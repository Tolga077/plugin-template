// use an integer for version numbers
version = 1


cloudstream {
    // All of these properties are optional, you can safely remove them
    language = "en"

    description = "Lorem Ipsum"
    authors = listOf("Sir Aguacata (Aka KillerDogeEmpire)")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // will be 3 if unspecified
    tvTypes = listOf(
        "Anime",
        "AnimeMovie",
        "OVA",
    )

    iconUrl = "https://www.google.com/s2/favicons?domain=animixplay.to&sz=%size%"
}