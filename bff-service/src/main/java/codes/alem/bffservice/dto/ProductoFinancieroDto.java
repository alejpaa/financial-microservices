package codes.alem.bffservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(
    name = "ProductoFinancieroBFF",
    description = "Información detallada de un producto financiero del cliente en el contexto del BFF"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoFinancieroDto {

    @Schema(
        description = "Tipo de producto financiero",
        example = "Cuenta de Ahorro",
        allowableValues = {"Cuenta de Ahorro", "Cuenta Corriente", "Tarjeta de Crédito", "Tarjeta de Débito", "Préstamo", "Depósito a Plazo", "Inversión"},
        required = true
    )
    private String tipoProducto;

    @Schema(
        description = "Nombre comercial específico del producto",
        example = "Ahorro Premium Plus",
        required = true,
        maxLength = 100
    )
    private String nombre;

    @Schema(
        description = "Saldo actual del producto financiero en la moneda base del sistema",
        example = "5000.50",
        required = true,
        minimum = "0",
        type = "number",
        format = "decimal"
    )
    private BigDecimal saldo;
}
