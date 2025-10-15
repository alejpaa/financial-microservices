package codes.alem.clientservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
}
