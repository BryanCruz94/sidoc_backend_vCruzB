package ec.mil.ejercito.cedmt.sidoc.controller;

import ec.mil.ejercito.cedmt.sidoc.config.ManualImageService;
import ec.mil.ejercito.cedmt.sidoc.repository.ManualRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cedmt/sidoc/manuales")
@CrossOrigin(origins = "http://localhost:4200")
public class ManualImageController {

    private final ManualRepository docEjercitoRepository;
    private final ManualImageService manualImageService;

    public ManualImageController(ManualRepository repo, ManualImageService imgSvc) {
        this.docEjercitoRepository = repo;
        this.manualImageService = imgSvc;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getManualImage(@PathVariable("id") Long id) {
        var manual = docEjercitoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manual no encontrado: " + id));

        String remotePath = manual.getUrlImagen();
        if (remotePath == null || remotePath.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = manualImageService.getImageBytes(remotePath);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }

        // Detecta tipo por extensión si es posible; cae en binario genérico si no.
        MediaType mediaType = MediaTypeFactory.getMediaType(remotePath)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                // Cache del navegador/edge: ajusta política a tu necesidad
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(data);
    }
}
