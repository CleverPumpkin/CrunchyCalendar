package buildconfig

import org.gradle.api.Project
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.JavaCompileOptions
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.Config
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer

abstract class BaseBuildConfiguration(
    private val project: Project,
    private val baseExtension: BaseExtension
) {

    abstract val manifestPlaceholders: Map<String, Any>?

    open fun configure() {
        with(baseExtension) {
            compileSdkVersion(Config.compileSdk)

            defaultConfig {
                minSdk = Config.minSdk.apiLevel
                targetSdk = Config.targetSdk.apiLevel

                configureJavaCompileOptions(javaCompileOptions)
                vectorDrawables.useSupportLibrary = true
                manifestPlaceholders.let(::addManifestPlaceholders)

                javaCompileOptions {
                    annotationProcessorOptions {
                        argument("room.schemaLocation", "${project.projectDir.absolutePath}/schemas")
                    }
                }
            }

            lintOptions {
                isAbortOnError = true
                fatal("StopShip")
            }

            sourceSets {
                getByName("main").java.srcDirs("src/main/kotlin")
                getByName("debug").java.srcDirs("src/debug/kotlin")
                getByName("release").java.srcDirs("src/release/kotlin")
            }

            compileOptions {
                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            viewBinding.isEnabled = true

            configureSigningConfigs(signingConfigs)
            configureBuildTypes(buildTypes)


        }
    }

    open fun configureJavaCompileOptions(javaCompileOptions: JavaCompileOptions) {
        javaCompileOptions.apply {
            annotationProcessorOptions {
                argument("dagger.gradle.incremental", "true")
            }
        }
    }

    abstract fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>)

    abstract fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<BuildType>)

}