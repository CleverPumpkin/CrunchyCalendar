import GradlePluginsVersions.GRADLE_PLUGIN
import GradlePluginsVersions.KOTLIN_PLUGIN

internal object GradlePluginsVersions {
    const val GRADLE_PLUGIN = "8.2.2"
    const val KOTLIN_PLUGIN = "1.9.22"
}

object GradlePlugins {
    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_PLUGIN"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_PLUGIN"
}

object MavenPublish {
    const val MAVEN_PUBLISH_VERSION = "0.22.0"

    const val MAVEN_PUBLISH = "com.vanniktech:gradle-maven-publish-plugin:$MAVEN_PUBLISH_VERSION"
}