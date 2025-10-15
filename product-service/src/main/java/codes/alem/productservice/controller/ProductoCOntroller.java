package codes.alem.productservice.controller;

import codes.alem.productservice.dto.ProductoDto;
import codes.alem.productservice.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/productos")
@RequiredArgsConstructor
public class ProductoCOntroller {

    private final ProductoService productoService;

    @GetMapping("/{codigoUnico}")
    public Flux<ProductoDto> findByCodigoUnico(@PathVariable String codigoUnico) {
        return productoService.findByCodigoUnico(codigoUnico);
    }
}
