package odin.lightdefy

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

// This should do fine, but if you ever feel fancy maybe try using GCM (an authenticated encryption mode)
// If this ever breaks unexpectedly, check changes in crypto defaults for SecretKeySpec and NodeJS crypto
fun decrypt(ciphertext: String, key: String): String {
    val keyb = key.toByteArray(Charsets.UTF_8)
    val md = MessageDigest.getInstance("MD5")
    val thedigest = md.digest(keyb)
    val skey = SecretKeySpec(thedigest, "AES")
    val dcipher = Cipher.getInstance("AES")
    dcipher.init(Cipher.DECRYPT_MODE, skey)

    val clearbyte = dcipher.doFinal(toByte(ciphertext))
    return String(clearbyte)
}

fun toByte(hexString: String): ByteArray {
    val len = hexString.length / 2
    val result = ByteArray(len)
    for (i in 0 until len) {
        result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).toByte()
    }
    return result
}

fun encrypt(plaintext: String, key: String): String {
    throw NotImplementedError()
}

private fun hashString(type: String, input: String): String {
    val hexChars = "0123456789ABCDEF"
    val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(hexChars[i shr 4 and 0x0f])
        result.append(hexChars[i and 0x0f])
    }

    return result.toString()
}