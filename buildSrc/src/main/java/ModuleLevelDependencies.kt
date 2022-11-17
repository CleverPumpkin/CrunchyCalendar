object KotlinLibs {
    private const val COROUTINES_VERSION = "1.6.4"

    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${GradlePluginsVersions.KOTLIN_PLUGIN}"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
}

object AndroidX {
    const val CORE = "androidx.core:core-ktx:1.3.2"
    const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.0"
    const val FRAGMENT = "androidx.fragment:fragment-ktx:1.4.0"
    const val ANNOTATIONS = "androidx.annotation:annotation:1.2.0"
    const val PREFERENCE = "androidx.preference:preference:1.1.1"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.2.1"
}

object UI {
    const val VIEWBINDING_PROPERTY_DELEGATE = "com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.4.6"
}

object Tools {
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:1.1.1"

}

object Compose {
    const val VERSION = "1.2.0-beta03"

    //UI
    const val UI = "androidx.compose.ui:ui:$VERSION"

    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    const val FOUNDATION = "androidx.compose.foundation:foundation:$VERSION"

    // Material design icons
    const val ICONS_CORE = "androidx.compose.material:material-icons-core:$VERSION"
    const val ICONS_EXTENDED = "androidx.compose.material:material-icons-extended:$VERSION"

    //ConstraintLayout
    const val CONSTRAINT = "androidx.constraintlayout:constraintlayout-compose:1.0.0"

    // Integration with activities
    const val ACTIVITY = "androidx.activity:activity-compose:1.4.0"

    // Compose Material Design
    const val MATERIAL = "androidx.compose.material:material:$VERSION"
    const val MATERIAL_YOU = "androidx.compose.material3:material3:1.0.0-alpha12"

    // Animations
    const val ANIMATIONS = "androidx.compose.animation:animation:$VERSION"

    //CODE
    const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"

    // Integration with observables
    const val RUNTIME_LIVEDATA = "androidx.compose.runtime:runtime-livedata:$VERSION"
    const val RUNTIME_RXJAVA = "androidx.compose.runtime:runtime-rxjava2:$VERSION"

    // Tooling support (Previews, etc.)
    const val TOOLING = "androidx.compose.ui:ui-tooling:$VERSION"

    // Integration with ViewModels
    const val VIEWMODELS = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"

    // Accompanist
    object ACCOMPANIST {
        private const val VERSION = "0.24.9-beta"

        const val SWIPE_REFRESH = "com.google.accompanist:accompanist-swiperefresh:$VERSION"
        const val PAGER = "com.google.accompanist:accompanist-pager:$VERSION"
        const val PAGER_INDICATORS = "com.google.accompanist:accompanist-pager-indicators:$VERSION"
    }

    const val LOTTIE_COMPOSE = "com.airbnb.android:lottie-compose:5.2.0"
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