import buildconfig.LibraryModuleBuildConfiguration

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    LibraryModuleBuildConfiguration(project, libraryEtension = this).configure()

    buildFeatures.compose = true

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.VERSION
    }
}

dependencies {
    coreLibraryDesugaring(Tools.DESUGAR_JDK_LIBS)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //Compose
    implementation(Compose.FOUNDATION)
    implementation(Compose.UI)
    implementation(Compose.ACTIVITY)
    implementation(Compose.MATERIAL_YOU)
    implementation(Compose.ANIMATIONS)
    implementation(Compose.RUNTIME)
    implementation(Compose.VIEWMODELS)
    implementation(Compose.TOOLING)
    implementation(Compose.CONSTRAINT)
    implementation(Compose.ACCOMPANIST.PAGER)
    implementation(Compose.ACCOMPANIST.PAGER_INDICATORS)
    implementation(Compose.COIL_COMPOSE)

}
