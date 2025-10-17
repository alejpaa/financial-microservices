package codes.alem.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
    name = "ProductoFinanciero",
    description = "Información de un producto financiero del cliente (cuenta, tarjeta, etc.)"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto {

    @Schema(
        description = "Tipo de producto financiero",
        example = "Cuenta de Ahorro",
        allowableValues = {"Cuenta de Ahorro", "Cuenta Corriente", "Tarjeta de Crédito", "Tarjeta de Débito", "Préstamo", "Depósito a Plazo"},
        required = true
    )
    private String tipoProducto;

    @Schema(
        description = "Nombre específico del producto",
        example = "Ahorro Premium",
        required = true,
        maxLength = 100
    )
    private String nombre;

    @Schema(
        description = "Saldo actual del producto en la moneda base del sistema",
        example = "5000.50",
        required = true,
        minimum = "0"
    )
    private Double saldo;
}
