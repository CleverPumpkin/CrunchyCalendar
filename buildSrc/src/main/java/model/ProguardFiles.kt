package model

import java.io.File

class ProguardFiles(
    /**
     * Expected to be one of "proguard-android.txt" or "proguard-android-optimize.txt".
     */
    private val defaultProguardFile: File,

    /**
     * Project-level proguard rules file, usually "proguard-rules.pro".
     */
    private val proguardRulesFile: File
) {

    val asArray: Array<File>
        get() = arrayOf(defaultProguardFile, proguardRulesFile)

}