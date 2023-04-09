import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson
import com.lagradost.cloudstream3.utils.Episode
import com.lagradost.cloudstream3.utils.NewEpisodesResult
import com.lagradost.cloudstream3.utils.SubtitleFile
import com.lagradost.cloudstream3.utils.TvType
import org.jsoup.nodes.Element

class Dizilla2 : MainAPI() {
    override var name: String = "dizilla2"
    override var mainUrl: String = "https://dizilla2.org"
    override var lang: String = "tr"
    override val hasMainPage: Boolean = false
    override val hasQuickSearch: Boolean = true
    override val hasDownloadSupport: Boolean = false
    override val supportedTypes: Set<TvType> = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun search(query: String): List<SearchResponse> {
        val response = app.get("$mainUrl/?s=$query")
        val result = ArrayList<SearchResponse>()

        response.document.select(".g-w-search > div").forEach {
            val title = it.selectFirst("h3 a")?.text()
            val href = fixUrl(mainUrl, it.selectFirst("h3 a")?.attr("href"))
            val posterUrl = fixUrl(mainUrl, it.selectFirst("img")?.attr("src"))
            if (title != null && href != null) {
                if (it.hasClass("g-post-type-movie")) {
                    result.add(
                        newMovieSearchResponse(
                            title = title,
                            url = href,
                            type = TvType.Movie
                        ) {
                            posterUrl = posterUrl
                        }
                    )
                } else if (it.hasClass("g-post-type-series")) {
                    result.add(
                        newTvSeriesSearchResponse(
                            title = title,
                            url = href,
                            type = TvType.TvSeries
                        ) {
                            posterUrl = posterUrl
                        }
                    )
                }
            }
        }

        return result
    }

    override suspend fun load(url: String): LoadResponse? {
        val response = app.get(url)
        val imdbRatingRegex = "IMDB: ([0-9.]+)".toRegex()
        val imdbRatingStr = response.document.selectFirst(".m-p-horizontal > div:nth-child(5)")?.text()?.let {
            imdbRatingRegex.find(it)?.groupValues?.getOrNull(1)
        }
        val rating = imdbRatingStr?.toFloat()

        return when {
            response.document.selectFirst("#playlist-items") != null -> {
                val episodes = response.document.select("#playlist-items li").mapNotNull { elem ->
                    val episodeHref = fixUrl(mainUrl, elem.selectFirst("a")?.attr("href"))
                    val name = elem.selectFirst("span")?.text()
                    if (name == null || episodeHref == null) return@mapNotNull null
                    val episodeNum = Regex("(\\d+(?=\\.))").find(name)?.value?.toIntOrNull() ?: 0
                    Episode(
                        episodeHref,
                        name,
                        1,
                        episodeNum
                    )
                }
                newTvSeriesLoadResponse(
                    response.document.selectFirst("h1.entry-title")?.text(),
                    url,
                    TvType.TvSeries,
                    episodes
                ) {
                    this.rating = rating
                    this.posterUrl = fixUrl(mainUrl, response.document.selectFirst(".wp-post-image")?.attr("src"))
                    this.plot = response.document.selectFirst(".tab-content .m-p-horizontal > div:nth-child(4)")?.text()
                    this.tags = listOf(
                        response.document.selectFirst(".m-p-horizontal > div:nth-child(2) a:nth-child(1)")?.text() ?: "",
                        response.document.selectFirst(".m-p-horizontal > div:nth-child(3) a")?.text() ?: ""
                    )
                }
            }
            else -> {
                newMovieLoadResponse(
                    response.document.selectFirst("h1.entry-title")?.text(),
                    url,
                    TvType.Movie,
                    url
                ) {
                    this.rating = rating
                    this.posterUrl =
                        fixUrl(mainUrl, response.document.selectFirst(".wp-post-image")?.attr("src"))
                    this.plot = response.document.selectFirst(".tab-content .m-p-horizontal > div:nth-child(2)")?.text()
                    this.tags =
                        listOf(response.document.selectFirst(".m-p-horizontal > div:nth-child(4) a")?.text() ?: "")
