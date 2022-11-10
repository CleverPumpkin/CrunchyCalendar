import GradlePluginsVersions.GRADLE_PLUGIN
import GradlePluginsVersions.KOTLIN_PLUGIN
import GradlePluginsVersions.NAV_VERSION

internal object GradlePluginsVersions {
    const val GRADLE_PLUGIN = "7.0.4"
    const val KOTLIN_PLUGIN = "1.6.10"
    const val NAV_VERSION = "2.4.2"
}

object GradlePlugins {
    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_PLUGIN"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_PLUGIN"
    const val NAV_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAV_VERSION"
}

object MavenPublish {
    const val MAVEN_PUBLISH_VERSION = "0.22.0"

    const val MAVEN_PUBLISH = "com.vanniktech:gradle-maven-publish-plugin:$MAVEN_PUBLISH_VERSION"
}