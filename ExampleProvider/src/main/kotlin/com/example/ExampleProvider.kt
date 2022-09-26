package com.example

import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse


class AnimixplayProvider: MainAPI() {  // all providers must be an instance of MainAPI

    override var name = "Animixplay"
    override var mainUrl = "https://animixplay.to"
    // enable this when your provider has a main page
    override val hasMainPage = true
    override val hasChromecastSupport = true
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.Anime)
    override val hasQuickSearch = true

    override var lang = "en"

    // this function gets called when you search for something
    override suspend fun search(query: String): List<SearchResponse> {
        return listOf<SearchResponse>()
    }
}