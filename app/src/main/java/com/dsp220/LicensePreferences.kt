package com.dsp220.pro

import android.content.Context
import android.content.SharedPreferences

class LicensePreferences(context: Context) {

    private val prefs: SharedPreferences = 
        context.getSharedPreferences("app_license_prefs", Context.MODE_PRIVATE)

    fun saveLicenseKey(key: String) {
        prefs.edit().putString("saved_license_key", key).apply()
    }

    fun getSavedLicenseKey(): String? {
        return prefs.getString("saved_license_key", null)
    }

    fun isActivated(context: Context): Boolean {
        val savedKey = getSavedLicenseKey() ?: return false
        return LicenseManager.verifyLicense(context, savedKey)
    }
}
