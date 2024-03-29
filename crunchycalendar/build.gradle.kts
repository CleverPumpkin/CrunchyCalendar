import buildconfig.LibraryModuleBuildConfiguration
import configuration.ApplicationVersions
import kotlin.collections.listOf

plugins {
    id("com.android.library")
    id("kotlin-android")
}

ext {
    // Library package information.
    set("groupId", "ru.cleverpumpkin") // The group ID you want to add in `implementation` line

    set("artifactId", "crunchycalendar") // The artifact you want to add in `implementation` line

    set("libraryVersion", ApplicationVersions.APP_VERSION_NAME) // it is also your module name in Android Studio project

    set("libraryName", "CrunchyCalendar")

    set("libraryDescription", "Light, powerful and easy to use Calendar Widget with a number of features out of the box")

    set("siteUrl", "https://github.com/CleverPumpkin/CrunchyCalendar")

    set("gitUrl", "https://github.com/CleverPumpkin/CrunchyCalendar.git")

    set("licenseName", "MIT")
    set("licenseUrl", "https://opensource.org/licenses/MIT")
    set("allLicenses", listOf("MIT"))
}

android {
    namespace = "ru.cleverpumpkin.calendar"
    LibraryModuleBuildConfiguration(project, libraryEtension = this).configure()

    // Use resource prefix to avoid collisions with resources from other modules.
    resourcePrefix  = "calendar_"
}

dependencies {
    coreLibraryDesugaring(Tools.DESUGAR_JDK_LIBS)

    implementation(KotlinLibs.STDLIB)
    implementation(AndroidX.RECYCLER_VIEW)
    implementation(MaterialComponents.LIBRARY)
    implementation(KotlinLibs.COROUTINES_CORE)
    implementation(Testing.JUNIT)
//    implementation(Testing.MOCKITO_ALL)
    implementation(Testing.POWERMOCK_API)
    implementation(Testing.POWERMOCK_MODULE_JUNIT)

}

tasks.withType<Javadoc> {
    enabled = false
}

// This is the ready-to-use scripts to make uploading to mavenCentral easy.
apply(from = "../mavenCentral/mavenCentral.gradle")