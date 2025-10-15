package codes.alem.clientservice.service;

import codes.alem.clientservice.dto.ClienteDto;
import codes.alem.clientservice.entity.Cliente;
import codes.alem.clientservice.mapper.ClienteMapper;
import codes.alem.clientservice.repository.ClienteRepository;
import codes.alem.clientservice.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Override
    public Mono<ClienteDto> findByCodigoUnico(String codigoUnico) {
        return clienteRepository.findByCodigoUnico(codigoUnico).
                doOnNext(c -> log.info("Cliente encontrado: {}", c)).
                flatMap(c -> tipoDocumentoRepository.findById(c.getTipoDocumentoId())
                        .map(td -> {
                            ClienteDto dto = clienteMapper.toDto(c);
                            dto.setTipoDocumento(td.getNombre());
                            return dto;
                        }));

    }
    @Override
    public Mono<Cliente> findByCodigoUnico1(String codigoUnico) {
        return clienteRepository.findByCodigoUnico(codigoUnico);
    }
}
