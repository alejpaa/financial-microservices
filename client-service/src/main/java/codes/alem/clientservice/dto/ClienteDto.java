package codes.alem.clientservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
    name = "Cliente",
    description = "Información básica de un cliente del sistema financiero"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {

    @Schema(
        description = "Nombres del cliente",
        example = "Juan Carlos",
        required = true,
        maxLength = 100
    )
    private String nombres;

    @Schema(
        description = "Apellidos del cliente",
        example = "Pérez García",
        required = true,
        maxLength = 100
    )
    private String apellidos;

    @Schema(
        description = "Tipo de documento de identidad del cliente",
        example = "DNI",
        allowableValues = {"DNI", "Pasaporte", "Carnet de Extranjería", "Cédula"},
        required = true
    )
    private String tipoDocumento;

    @Schema(
        description = "Número del documento de identidad",
        example = "12345678",
        required = true,
        maxLength = 50
    )
    private String numeroDocumento;
}
