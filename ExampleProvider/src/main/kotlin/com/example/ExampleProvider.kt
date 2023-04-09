package com.example.dizilla2

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class Dizilla2Plugin: Plugin() {
override fun load(context: Context) {
// Tüm sağlayıcılar bu şekilde eklenmelidir. Lütfen sağlayıcılar listesini doğrudan düzenlemeyin.
registerMainAPI(Dizilla2())
}
}
