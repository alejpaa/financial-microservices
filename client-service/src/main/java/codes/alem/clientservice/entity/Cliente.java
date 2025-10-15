package codes.alem.clientservice.entity;

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
@Table("clientes")
public class Cliente {

    @Id
    private UUID id;

    @Column("codigo_unico")
    private String codigoUnico;

    @Column("nombres")
    private String nombres;

    @Column("apellidos")
    private String apellidos;

    @Column("tipo_documento_id")
    private Short tipoDocumentoId;

    @Column("numero_documento")
    private String numeroDocumento;

    @Column("fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @Column("fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;
}
