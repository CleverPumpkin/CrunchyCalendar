package model

import com.android.builder.model.ApiVersion

class SdkApiVersion(val sdkVersion: Int) : ApiVersion {

    override fun getCodename(): String? = null

    override fun getApiLevel(): Int = sdkVersion

    override fun getApiString(): String = sdkVersion.toString()

}