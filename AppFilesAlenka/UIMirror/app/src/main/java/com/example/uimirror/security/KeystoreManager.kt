package com.example.uimirror.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object KeystoreManager {
    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "EncryptedPersonDBKey" // The unique key alias for your database encryption key

    /**
     * Retrieves the encryption key from the Android Keystore or generates it if it doesn't already exist.
     */
    fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }

        // Check if the key alias exists in the Keystore
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // Generate a new encryption key
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
            val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256) // Use a 256-bit key for strong encryption
                .build()

            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        }

        // Retrieve the encryption key from the Keystore
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    /**
     * Retrieves the encryption passphrase derived from the secret key.
     */
    fun getPassphrase(): ByteArray {
        return getOrCreateKey().toString().toByteArray()
    }
}