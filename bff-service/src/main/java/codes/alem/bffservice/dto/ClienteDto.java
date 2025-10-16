package codes.alem.bffservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto {
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
}
