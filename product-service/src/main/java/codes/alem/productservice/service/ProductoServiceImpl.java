package codes.alem.productservice.service;

import codes.alem.productservice.dto.ProductoDto;
import codes.alem.productservice.mapper.ProductoMapper;
import codes.alem.productservice.repository.ProductoRepository;
import codes.alem.productservice.repository.TipoProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final TipoProductoRepository tipoProductoRepository;
    
    @Override
    public Flux<ProductoDto> findByCodigoUnico(String codigoUnico) {
        return productoRepository.findByCodigoUnicoCliente(codigoUnico)
                .flatMap(p -> tipoProductoRepository.findById(p.getTipoProductoId())
                        .map(tp -> {
                            ProductoDto dto = productoMapper.toDto(p);
                            dto.setTipoProducto(tp.getNombre());
                            return dto;
                        })
                );
    }
}
