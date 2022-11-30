// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
    repositories {
        google()
        jcenter()
    }
}

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(GradlePlugins.GRADLE)
        classpath(GradlePlugins.KOTLIN)
        classpath(MavenPublish.MAVEN_PUBLISH)
    }

}

subprojects {
    enableInlineClasses()
    setJvmTarget()
    setKotlinLangLevel()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}


fun Project.enableInlineClasses() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            val args = kotlinOptions.freeCompilerArgs
            kotlinOptions.freeCompilerArgs = args + listOf("-XXLanguage:+InlineClasses")
        }
    }
}

fun Project.setJvmTarget() {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

fun Project.setKotlinLangLevel() {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            languageVersion = "1.6"
        }
    }
}