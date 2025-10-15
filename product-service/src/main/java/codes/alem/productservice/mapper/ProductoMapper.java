package codes.alem.productservice.mapper;

import codes.alem.productservice.dto.ProductoDto;
import codes.alem.productservice.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(source = "nombreProducto", target = "nombre")
    ProductoDto toDto(Producto producto);
}
