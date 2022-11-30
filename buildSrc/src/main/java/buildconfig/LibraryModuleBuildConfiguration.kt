package buildconfig

import org.gradle.api.Project
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.BuildTypes
import org.gradle.api.NamedDomainObjectContainer

class LibraryModuleBuildConfiguration(
    project: Project,
    libraryEtension: LibraryExtension
) : BaseBuildConfiguration(project, libraryEtension) {

    override val manifestPlaceholders: Map<String, Any>? = null

    override fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>) =
        Unit

    override fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<BuildType>) {
        buildTypes.apply {
            getByName(BuildTypes.RELEASE.name).apply {
                isDebuggable = false
            }
        }
    }


}