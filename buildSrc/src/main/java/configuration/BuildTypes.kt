package configuration

sealed class BuildTypes(val name: String, val ribbonColor: String) {

    // Optional build type for development needs
    object DEBUG : BuildTypes("debug", "#b4f4511e")

    // Release build for uploading to app store
    object RELEASE : BuildTypes("release", "")

}