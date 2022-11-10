package configuration

import model.SdkApiVersion

object SdkVersions {

    val MIN_SDK_VERSION = SdkApiVersion(21)

    val TARGET_SDK_VERSION = SdkApiVersion(33)

    val COMPILE_SDK_VERSION = TARGET_SDK_VERSION.sdkVersion

}