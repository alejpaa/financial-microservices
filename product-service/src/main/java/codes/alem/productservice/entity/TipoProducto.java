package codes.alem.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("tipos_producto")
public class TipoProducto {

    @Id
    private Short id;

    @Column("codigo")
    private String codigo;

    @Column("nombre")
    private String nombre;
}
