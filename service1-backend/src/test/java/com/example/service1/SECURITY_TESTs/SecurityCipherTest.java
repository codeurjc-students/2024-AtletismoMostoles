package com.example.service1.SECURITY_TESTs;

import com.example.service1.Security.JWT.SecurityCipher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityCipherTest {

    @Test
    void encryptDecrypt_roundTrip() {
        String original = "Texto de prueba 123!@#";
        String encrypted = SecurityCipher.encrypt(original);
        assertNotNull(encrypted, "El resultado cifrado no debe ser null");
        assertNotEquals(original, encrypted, "El cifrado debe cambiar el texto");
        String decrypted = SecurityCipher.decrypt(encrypted);
        assertEquals(original, decrypted, "El descifrado debe devolver el texto original");
    }

    @Test
    void encrypt_nullReturnsNull() {
        assertNull(SecurityCipher.encrypt(null));
    }

    @Test
    void decrypt_nullReturnsNull() {
        assertNull(SecurityCipher.decrypt(null));
    }

    @Test
    void encrypt_ivRandomness() {
        String sample = "foo";
        String e1 = SecurityCipher.encrypt(sample);
        String e2 = SecurityCipher.encrypt(sample);
        assertNotEquals(e1, e2, "Cada cifrado debería usar un IV distinto");
    }
}
