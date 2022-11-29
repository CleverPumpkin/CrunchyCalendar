import buildconfig.AppModuleBuildConfiguration
import com.project.starter.easylauncher.plugin.EasyLauncherConfig

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.starter.easylauncher") version "5.1.2"
}

android {
    AppModuleBuildConfiguration(project, appExtension = this).configure()
}

easylauncher {
    buildTypes {
        create(configuration.BuildTypes.DEBUG.name) {
            configure(
                listOf("@mipmap/ic_launcher_debug"),
                configuration.BuildTypes.DEBUG.ribbonColor
            )
        }
        create(configuration.BuildTypes.RELEASE.name) {
            enable(false)
        }
    }
}

dependencies {
    coreLibraryDesugaring(Tools.DESUGAR_JDK_LIBS)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Core modules
    implementation(project(":crunchycalendar"))
    implementation(KotlinLibs.STDLIB)
    implementation(AndroidX.APPCOMPAT)
    implementation(AndroidX.RECYCLER_VIEW)
    implementation(MaterialComponents.LIBRARY)
    implementation(UI.VIEWBINDING_PROPERTY_DELEGATE)
}

fun EasyLauncherConfig.configure(icons: List<String>, ribbonColor: String) {
    setIconNames(icons)
    filters(
        chromeLike(
            label = configuration.ApplicationVersions.APP_VERSION_NAME,
            labelPadding = 13,
            textSizeRatio = 0.12f,
            ribbonColor = ribbonColor
        )
    )
}

