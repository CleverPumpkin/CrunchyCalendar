import buildconfig.AppModuleBuildConfiguration

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    AppModuleBuildConfiguration(project, appExtension = this).configure()
}

dependencies {
    coreLibraryDesugaring(Tools.DESUGAR_JDK_LIBS)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Core modules
    implementation(project(":crunchycalendar"))
    implementation(KotlinLibs.STDLIB)
    implementation(AndroidX.APPCOMPAT)
    implementation(AndroidX.RECYCLER_VIEW)
    implementation(UI.VIEWBINDING_PROPERTY_DELEGATE)
}

