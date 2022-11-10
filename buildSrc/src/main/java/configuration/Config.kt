package configuration

import com.android.builder.core.DefaultApiVersion
import com.android.builder.model.ApiVersion

object Config {

    const val APPLICATION_ID = "ru.cleverpumpkin.crunchycalendar"

    val VERSION_CODE = ApplicationVersions.APP_VERSION_CODE

    val VERSION_NAME = ApplicationVersions.APP_VERSION_NAME

    const val buildToolsVersion = "31.0.0"

    val minSdk: ApiVersion = DefaultApiVersion(21)

    val targetSdk: ApiVersion = DefaultApiVersion(33)

    val compileSdk = targetSdk.apiLevel

    // Analytics

}