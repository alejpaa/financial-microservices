package codes.alem.bffservice.mapper;

import codes.alem.bffservice.dto.ClienteConProductosDto;
import codes.alem.bffservice.dto.ClienteDto;
import codes.alem.bffservice.dto.ProductoFinancieroDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "productosFinancieros", source = "productos")
    ClienteConProductosDto toClienteConProductos(ClienteDto cliente, List<ProductoFinancieroDto> productos);
}
