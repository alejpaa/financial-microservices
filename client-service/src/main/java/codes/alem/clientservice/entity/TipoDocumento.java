package codes.alem.clientservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tipos_documento")
public class TipoDocumento {
    
    @Id
    private Short id;
    
    @Column("nombre")
    private String nombre;
    
    @Column("fecha_creacion")
    private OffsetDateTime fechaCreacion;
    
    @Column("fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;
}