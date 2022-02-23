package com.example.suportstudy.until

import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

object AESHelper {
     var TAG = "AESCrypt"

    //AESCrypt-ObjC uses CBC and PKCS7Padding
     var AES_MODE = "AES/CBC/PKCS7Padding"
     var CHARSET:String   = "UTF-8"

    //AESCrypt-ObjC uses SHA-256 (and so a 256-bit key)
    private val HASH_ALGORITHM = "SHA-256"

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private val ivBytes = byteArrayOf(
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00
    )

    //togglable log option (please turn off in live!)
    var DEBUG_LOG_ENABLED = false


    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    private fun generateKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val bytes = password.toByteArray(charset("UTF-8"))
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        log("SHA-256 key ", key)
        return SecretKeySpec(key, "AES")
    }


    @Throws(GeneralSecurityException::class)
    fun encrypt(password: String, message: String): String? {
        return try {
            val key = generateKey(password)
            log("message", message)
            val cipherText = encrypt(key, ivBytes, message.toByteArray(charset(CHARSET)))

            //NO_WRAP is important as was getting \n at the end
            val encoded = Base64.encodeToString(cipherText, Base64.NO_WRAP)
            log("Base64.NO_WRAP", encoded)
            encoded
        } catch (e: UnsupportedEncodingException) {
            if (DEBUG_LOG_ENABLED) Log.e(TAG, "UnsupportedEncodingException ", e)
            throw GeneralSecurityException(e)
        }
    }


    @Throws(GeneralSecurityException::class)
    fun encrypt(key: SecretKeySpec?, iv: ByteArray?, message: ByteArray?): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        val cipherText = cipher.doFinal(message)
        log("cipherText", cipherText)
        return cipherText
    }


    @Throws(GeneralSecurityException::class)
    fun decrypt(password: String, base64EncodedCipherText: String): String {
        return try {
            val key = generateKey(password)
            log("base64EncodedCipherText", base64EncodedCipherText)
            val decodedCipherText = Base64.decode(base64EncodedCipherText, Base64.NO_WRAP)
            log("decodedCipherText", decodedCipherText)
            val decryptedBytes = decrypt(key, ivBytes, decodedCipherText)
            log("decryptedBytes", decryptedBytes)
            var message = String(decryptedBytes, charset(CHARSET))
            return message
        } catch (e: UnsupportedEncodingException) {
            if (DEBUG_LOG_ENABLED) Log.e(TAG, "UnsupportedEncodingException ", e)
            throw GeneralSecurityException(e)
        }
    }




    @Throws(GeneralSecurityException::class)
    fun decrypt(key: SecretKeySpec?, iv: ByteArray?, decodedCipherText: ByteArray?): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        val decryptedBytes = cipher.doFinal(decodedCipherText)
        log("decryptedBytes", decryptedBytes)
        return decryptedBytes
    }


    private fun log(what: String, bytes: ByteArray) {
        if (DEBUG_LOG_ENABLED) Log.d(TAG, what + "[" + bytes.size + "] [" + bytesToHex(bytes) + "]")
    }

    private fun log(what: String, value: String) {
        if (DEBUG_LOG_ENABLED) Log.d(TAG, what + "[" + value.length + "] [" + value + "]")
    }


    /**
     * Converts byte array to hexidecimal useful for logging and fault finding
     *
     * @param bytes
     * @return
     */
    fun bytesToHex(bytes: ByteArray): String? {
        val hexArray = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'
        )
        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = (bytes[j] and 0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}