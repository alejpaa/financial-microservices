package codes.alem.bffservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(
    name = "ClienteConProductos",
    description = "Respuesta consolidada que incluye información del cliente y todos sus productos financieros"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteConProductosDto {

    @Schema(
        description = "Nombres del cliente",
        example = "Juan Carlos",
        required = true
    )
    private String nombres;

    @Schema(
        description = "Apellidos del cliente",
        example = "Pérez García",
        required = true
    )
    private String apellidos;

    @Schema(
        description = "Tipo de documento de identidad",
        example = "DNI",
        allowableValues = {"DNI", "Pasaporte", "Carnet de Extranjería", "Cédula"},
        required = true
    )
    private String tipoDocumento;

    @Schema(
        description = "Número del documento de identidad",
        example = "12345678",
        required = true
    )
    private String numeroDocumento;

    @Schema(
        description = "Lista de todos los productos financieros del cliente",
        required = true,
        implementation = ProductoFinancieroDto.class
    )
    private List<ProductoFinancieroDto> productosFinancieros;
}
