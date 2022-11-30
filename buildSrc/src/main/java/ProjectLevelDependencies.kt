import GradlePluginsVersions.GRADLE_PLUGIN
import GradlePluginsVersions.KOTLIN_PLUGIN

internal object GradlePluginsVersions {
    const val GRADLE_PLUGIN = "7.0.4"
    const val KOTLIN_PLUGIN = "1.6.10"
}

object GradlePlugins {
    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_PLUGIN"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_PLUGIN"
}

object MavenPublish {
    const val MAVEN_PUBLISH_VERSION = "0.22.0"

    const val MAVEN_PUBLISH = "com.vanniktech:gradle-maven-publish-plugin:$MAVEN_PUBLISH_VERSION"
}