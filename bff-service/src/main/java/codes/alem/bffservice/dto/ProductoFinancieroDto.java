package codes.alem.bffservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoFinancieroDto {
    private String tipoProducto;
    private String nombre;
    private BigDecimal saldo;
}
