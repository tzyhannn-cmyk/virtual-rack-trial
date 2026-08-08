package com.dsp220.pro
import android.content.Context
import android.provider.Settings
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object LicenseManager {

    // ⚠️ SANGAT PENTING: Ganti string ini dengan kata rahasia Anda sendiri!
    // Kunci ini harus SAMA PERSIS dengan kunci di script generator laptop Anda.
    private const val SECRET_KEY = "KunciRahasiaSuperAmanMilikSaya123!"

    /**
     * Mengambil ID unik perangkat Android secara otomatis.
     */
    fun getDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "UNKNOWN_DEVICE"
        
        // Mengubah ID menjadi hash SHA-256 dan mengambil 8 karakter awal agar rapi
        return hashString(androidId).substring(0, 8).uppercase()
    }

    /**
     * Menghasilkan License Key berdasarkan Device ID menggunakan HMAC-SHA256.
     */
    fun generateLicenseKey(deviceId: String): String {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "HmacSHA256")
            mac.init(secretKeySpec)
            
            val hmacBytes = mac.doFinal(deviceId.trim().uppercase().toByteArray())
            val fullHex = hmacBytes.joinToString("") { "%02X".format(it) }
            
            // Format keluaran: XXXX-XXXX-XXXX
            val rawKey = fullHex.substring(0, 12)
            "${rawKey.substring(0, 4)}-${rawKey.substring(4, 8)}-${rawKey.substring(8, 12)}"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Memverifikasi apakah License Key yang dimasukkan pengguna valid untuk perangkat ini.
     */
    fun verifyLicense(context: Context, inputKey: String): Boolean {
        val currentDeviceId = getDeviceId(context)
        val expectedKey = generateLicenseKey(currentDeviceId)
        
        val cleanInput = inputKey.replace("-", "").replace(" ", "").trim().uppercase()
        val cleanExpected = expectedKey.replace("-", "").trim().uppercase()

        return cleanInput == cleanExpected
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
