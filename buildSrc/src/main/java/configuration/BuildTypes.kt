package configuration

sealed class BuildTypes(val name: String) {

    // Optional build type for development needs
    object DEBUG : BuildTypes("debug")

    // Release build for uploading to app store
    object RELEASE : BuildTypes("release")

}