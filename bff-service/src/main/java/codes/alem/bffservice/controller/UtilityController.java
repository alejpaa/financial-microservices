package codes.alem.bffservice.controller;

import codes.alem.bffservice.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
public class UtilityController {

    private final EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public String encryptCode(@RequestBody String codigoUnico) {
        return encryptionService.encrypt(codigoUnico);
    }
}
