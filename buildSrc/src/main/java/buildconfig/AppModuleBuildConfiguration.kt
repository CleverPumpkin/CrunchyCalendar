package buildconfig

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.ApplicationVersions
import configuration.BuildTypes
import configuration.Config
import configuration.SigningConfigs
import model.ProguardFiles
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File
import java.util.Collections.emptyMap
import java.io.FileInputStream
import java.util.Properties

class AppModuleBuildConfiguration(
    private val project: Project,
    private val appExtension: BaseAppModuleExtension
) : BaseBuildConfiguration(project, appExtension) {

    override val manifestPlaceholders: Map<String, Any> = emptyMap()

    override fun configure() {
        super.configure()
        with(appExtension) {
            defaultConfig {
                setupMainArguments(this)
            }
        }
    }

    private fun setupMainArguments(defaultConfig: ApplicationDefaultConfig) {
        defaultConfig.apply {
            applicationId = Config.APPLICATION_ID
            versionCode = Config.VERSION_CODE
            versionName = Config.VERSION_NAME
        }
    }

    override fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<BuildType>) {
        val projectProguardFiles = buildProguardFiles().asArray

        buildTypes.apply {
            getByName(BuildTypes.DEBUG.name).apply {
                isDebuggable = true
                isMinifyEnabled = false
                applicationIdSuffix = ".debug"
                versionNameSuffix = ApplicationVersions.VERSION_NAME_SUFFIX
            }

            getByName(BuildTypes.RELEASE.name).apply {
                isDebuggable = false
                isMinifyEnabled = true
                signingConfig = appExtension.signingConfigs.getByName(SigningConfigs.Release.NAME)
                proguardFiles(*projectProguardFiles)
                setMatchingFallbacks(BuildTypes.RELEASE.name)
            }
        }
    }

//    override fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>) {
//        val path = project.rootDir
//
//        val keystorePropertiesFile = File("$path/keystore.properties")
//        val keystoreProperties = Properties()
//        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
//
//        signingConfigs.maybeCreate(SigningConfigs.Release.NAME)
//            .setStoreFile(File(keystoreProperties.getProperty("uploadStoreFile")))
//            .setStorePassword(keystoreProperties.getProperty("uploadStorePassword"))
//            .setKeyAlias(keystoreProperties.getProperty("uploadKeyAlias"))
//            .setKeyPassword(keystoreProperties.getProperty("uploadKeyPassword"))
//
//    }

    override fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>) {
        val path = project.rootDir

//        signingConfigs.maybeCreate(SigningConfigs.Debug.NAME)
//            .setStoreFile(File("$path/${SigningConfigs.Debug.KEYSTORE_FILE}"))
//            .setStorePassword((SigningConfigs.Debug.KEYSTORE_FILE_PASSWORD))
//            .setKeyAlias(SigningConfigs.Debug.KEY_ALIAS)
//            .setKeyPassword(SigningConfigs.Debug.KEY_PASSWORD)

        signingConfigs.maybeCreate("upload")
            .setStoreFile(File("$path/keystore/keystore.jks"))
            .setStorePassword("Polo4321")
            .setKeyAlias("release")
            .setKeyPassword("Polo4321")

    }

    private fun buildProguardFiles(): ProguardFiles {
        val rulesFile = File("proguard-rules.pro")
        val defaultFile =
            com.android.build.gradle.ProguardFiles.getDefaultProguardFile(
                "proguard-android-optimize.txt",
                project.layout.buildDirectory
            )
        return ProguardFiles(
            proguardRulesFile = rulesFile,
            defaultProguardFile = defaultFile
        )
    }

}