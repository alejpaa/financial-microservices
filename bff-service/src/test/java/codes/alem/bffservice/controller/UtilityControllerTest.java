package codes.alem.bffservice.controller;

import codes.alem.bffservice.service.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilityControllerTest {

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private UtilityController controller;

    private String codigoUnico;
    private String codigoEncriptado;

    @BeforeEach
    void setUp() {
        codigoUnico = "CLI001";
        codigoEncriptado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.encrypted_test_code";
    }

    @Test
    void encryptCode_CuandoCodigoEsValido_DebeRetornarCodigoEncriptado() {
        // Given
        when(encryptionService.encrypt(codigoUnico)).thenReturn(codigoEncriptado);

        // When
        String resultado = controller.encryptCode(codigoUnico);

        // Then
        assertNotNull(resultado);
        assertEquals(codigoEncriptado, resultado);
        assertTrue(resultado.startsWith("eyJ")); // Verifica que tenga formato de token/encriptado
    }

    @Test
    void encryptCode_CuandoCodigoEsVacio_DebeRetornarEncriptacionVacia() {
        // Given
        String codigoVacio = "";
        String encriptacionVacia = "encrypted_empty";
        when(encryptionService.encrypt(codigoVacio)).thenReturn(encriptacionVacia);

        // When
        String resultado = controller.encryptCode(codigoVacio);

        // Then
        assertNotNull(resultado);
        assertEquals(encriptacionVacia, resultado);
    }

    @Test
    void encryptCode_CuandoCodigoEsNull_DebeRetornarNull() {
        // Given
        when(encryptionService.encrypt(null)).thenReturn(null);

        // When
        String resultado = controller.encryptCode(null);

        // Then
        assertNull(resultado);
    }

    @Test
    void encryptCode_CuandoCodigoTieneCaracteresEspeciales_DebeEncriptarCorrectamente() {
        // Given
        String codigoEspecial = "CLI-001_TEST@2024";
        String encriptadoEspecial = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.special_encrypted";
        when(encryptionService.encrypt(codigoEspecial)).thenReturn(encriptadoEspecial);

        // When
        String resultado = controller.encryptCode(codigoEspecial);

        // Then
        assertNotNull(resultado);
        assertEquals(encriptadoEspecial, resultado);
    }

    @Test
    void encryptCode_CuandoServicioLanzaExcepcion_DebePropagarExcepcion() {
        // Given
        when(encryptionService.encrypt(anyString()))
                .thenThrow(new RuntimeException("Error en encriptación"));

        // When & Then
        assertThrows(RuntimeException.class, () ->
                controller.encryptCode(codigoUnico));
    }

}
