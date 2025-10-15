package codes.alem.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("productos")
public class Producto {
    @Id
    private UUID id;

    @Column("codigo_unico_cliente")
    private String codigoUnicoCliente;

    @Column("tipo_producto_id")
    private Short tipoProductoId;

    @Column("nombre_producto")
    private String nombreProducto;

    @Column("numero_producto")
    private String numeroProducto;

    @Column("saldo")
    private Double saldo;

    @Column("fecha_creacion")
    private OffsetDateTime fechaCreacion;
}
