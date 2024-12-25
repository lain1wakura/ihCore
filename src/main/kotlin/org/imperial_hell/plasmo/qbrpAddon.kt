//package org.imperial_hell.plasmo
//
//import su.plo.voice.api.addon.AddonInitializer
//import su.plo.voice.api.addon.AddonLoaderScope
//import su.plo.voice.api.addon. InjectPlasmoVoice
//import su.plo.voice.api.addon.annotation.Addon
//import su.plo.voice.api.addon.injectPlasmoVoice
//import su.plo.voice.api.server.PlasmoVoiceServer
//
//
//@Addon(
//    // An addon id must start with a lowercase letter and may contain only lowercase letters, digits, hyphens, and underscores.
//    // It should be between 4 and 32 characters long.
//    id = "pv-addon-qbrp",
//    name = "Hello World Add-on",
//    version = "1.0.0",
//    authors = ["Plasmo"],
//    scope = AddonLoaderScope.ANY
//)
//class qbrpAddon : AddonInitializer {
//
//    @InjectPlasmoVoice
//    private lateinit var voiceServer: PlasmoVoiceServer
//
//    override fun onAddonInitialize() {
//        voiceServer.sourceLineManager.createBuilder(
//            this,
//            "ambient", // name
//            "pv.activation.ambient", // translation key
//            "plasmovoice:textures/icons/speaker_priority.png", // icon resource location
//            10 // weight
//        ).build()
//
//        println("Addon initialized")
//    }
//}