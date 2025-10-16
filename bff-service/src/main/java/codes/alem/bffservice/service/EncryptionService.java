package codes.alem.bffservice.service;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EncryptionService {

    public String decrypt(String encryptedCode) {
        try {
            // Decodificación básica Base64 para el demo
            // En producción, usarías algoritmos de encriptación más robustos como AES
            return new String(Base64.getDecoder().decode(encryptedCode));
        } catch (Exception e) {
            throw new IllegalArgumentException("Código único inválido o mal formateado");
        }
    }

    public String encrypt(String plainCode) {
        try {
            return Base64.getEncoder().encodeToString(plainCode.getBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al encriptar el código");
        }
    }
}
