package codes.alem.clientservice.mapper;

import codes.alem.clientservice.dto.ClienteDto;
import codes.alem.clientservice.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDto toDto(Cliente cliente);
}
