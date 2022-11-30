object KotlinLibs {
    private const val COROUTINES_VERSION = "1.6.4"

    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${GradlePluginsVersions.KOTLIN_PLUGIN}"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
}

object AndroidX {
    const val CORE = "androidx.core:core-ktx:1.3.2"
    const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.0"
    const val FRAGMENT = "androidx.fragment:fragment-ktx:1.4.0"
    const val ANNOTATIONS = "androidx.annotation:annotation:1.2.0"
    const val PREFERENCE = "androidx.preference:preference:1.1.1"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.2.1"
}

object MaterialComponents {
    const val LIBRARY = "com.google.android.material:material:1.6.1"
}

object UI {
    const val VIEWBINDING_PROPERTY_DELEGATE =
        "com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.4.6"
}

object Tools {
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:1.1.1"

}

object Testing {
    private const val JUNIT_VERSION = "4.13.2"
    private const val MOCKITO_VERSION = "1.10.19"
    private const val POWERMOCK_VERSION = "1.6.4"

    const val JUNIT = "junit:junit:$JUNIT_VERSION"
    const val MOCKITO_ALL = "org.mockito:mockito-all:$MOCKITO_VERSION"
    const val POWERMOCK_API = "org.powermock:powermock-api-mockito:$POWERMOCK_VERSION"
    const val POWERMOCK_MODULE_JUNIT = "org.powermock:powermock-module-junit4:$POWERMOCK_VERSION"
}