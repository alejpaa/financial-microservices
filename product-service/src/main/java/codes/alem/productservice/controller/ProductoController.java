package codes.alem.productservice.controller;

import codes.alem.productservice.dto.ProductoDto;
import codes.alem.productservice.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "Productos Financieros", description = "API para gestión de productos financieros de clientes")
@RestController
@RequestMapping("api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Operation(
        summary = "Obtener productos financieros por código único de cliente",
        description = "Busca todos los productos financieros (cuentas de ahorro, tarjetas de crédito, etc.) " +
                     "asociados a un cliente específico utilizando su código único. " +
                     "Retorna una lista de productos con información detallada incluyendo tipo, nombre y saldo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Productos encontrados exitosamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductoDto.class)),
                examples = @ExampleObject(
                    name = "Lista de productos",
                    value = """
                    [
                        {
                            "tipoProducto": "Cuenta de Ahorro",
                            "nombreProducto": "Ahorro Premium",
                            "numeroProducto": "4001234567890123",
                            "saldo": 5000.50
                        },
                        {
                            "tipoProducto": "Tarjeta de Crédito",
                            "nombreProducto": "Visa Gold",
                            "numeroProducto": "4002987654321098",
                            "saldo": 2500.75
                        }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "204",
            description = "No se encontraron productos para el cliente especificado",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Código único de cliente inválido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de validación",
                    value = """
                    {
                        "error": "Bad Request",
                        "message": "El código único del cliente es requerido y debe tener un formato válido"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error interno",
                    value = """
                    {
                        "error": "Internal Server Error",
                        "message": "Error al consultar los productos financieros"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{codigoUnico}")
    public Flux<ProductoDto> findByCodigoUnico(
        @Parameter(
            description = "Código único del cliente para consultar sus productos financieros",
            example = "CLI001",
            required = true
        )
        @PathVariable String codigoUnico) {
        return productoService.findByCodigoUnico(codigoUnico);
    }
}
