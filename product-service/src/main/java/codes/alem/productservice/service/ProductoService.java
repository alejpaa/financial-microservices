package codes.alem.productservice.service;

import codes.alem.productservice.dto.ProductoDto;
import reactor.core.publisher.Flux;

public interface ProductoService {
    Flux<ProductoDto> findByCodigoUnico(String codigoUnico);
}
