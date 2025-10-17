package codes.alem.bffservice.controller;

import codes.alem.bffservice.service.EncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Utilidades",
    description = "Endpoints utilitarios para operaciones auxiliares como encriptación de códigos de cliente"
)
@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
public class UtilityController {

    private final EncryptionService encryptionService;

    @Operation(
        summary = "Encriptar código único de cliente",
        description = "Encripta un código único de cliente para ser usado de forma segura en las consultas del BFF. " +
                     "Este endpoint es útil para testing y desarrollo, permitiendo generar códigos encriptados " +
                     "que luego pueden ser usados en el endpoint principal del BFF."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Código encriptado exitosamente",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string"),
                examples = @ExampleObject(
                    name = "Código encriptado",
                    value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb2RpZ28iOiJDTEkwMDEifQ.encrypted_signature"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Código único inválido o vacío",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de validación",
                    value = """
                    {
                        "error": "Bad Request",
                        "message": "El código único es requerido y no puede estar vacío"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno durante la encriptación",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de encriptación",
                    value = """
                    {
                        "error": "Internal Server Error",
                        "message": "Error al encriptar el código único"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/encrypt")
    public String encryptCode(
            @Parameter(
                description = "Código único del cliente que se desea encriptar. Debe ser un código válido " +
                             "que corresponda a un cliente existente en el sistema.",
                example = "CLI001",
                required = true,
                schema = @Schema(type = "string", minLength = 1)
            )
            @RequestBody String codigoUnico) {
        return encryptionService.encrypt(codigoUnico);
    }
}
