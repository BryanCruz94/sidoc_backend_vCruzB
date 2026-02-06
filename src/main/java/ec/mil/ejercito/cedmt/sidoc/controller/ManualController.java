package ec.mil.ejercito.cedmt.sidoc.controller;

import ec.mil.ejercito.cedmt.sidoc.dto.DocEjercitoResponseDTO;
import ec.mil.ejercito.cedmt.sidoc.dto.*;
import ec.mil.ejercito.cedmt.sidoc.service.ManualService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cedmt/sidoc/manuales")
public class ManualController {

    @Autowired
    private ManualService manualService;


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/list")
    public List<DocEjercitoResponseDTO> getActiveManuals() {
        return manualService.getActiveManuals();
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/subirManual")
    public ResponseEntity<ManualNewResponseDTO> subirManual(
            @RequestParam("nombre") String nombre,
            @RequestParam("codigo") String codigo,
            @RequestParam("anioPublicacion") String anioPublicacion,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "subcategoriaId", required = false) Long subcategoriaId,
            @RequestParam(value = "tipoId", required = false) Long tipoId,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "portada", required = false) MultipartFile portada) {

        ManualRequestNewDTO manualRequest = new ManualRequestNewDTO();
        manualRequest.setNombre(nombre);
        manualRequest.setCodigo(codigo);
        manualRequest.setAnioPublicacion(anioPublicacion);
        manualRequest.setDescripcion(descripcion);
        manualRequest.setArchivo(archivo);

        // Asignar portada solo si fue enviada
        if (portada != null && !portada.isEmpty()) {
            manualRequest.setPortada(portada);
        } else {
            manualRequest.setPortada(null);
        }

        // Asignar categoría únicamente si es diferente de null
        if(categoriaId != null){
            manualRequest.setCategoriaId(categoriaId);
        }

        manualRequest.setSubcategoriaId(subcategoriaId);

        // Tipo: asignar tipo "SIN TIPO" si no viene
        manualRequest.setTipoId(tipoId != null ? tipoId : 5L);

        ManualNewResponseDTO manualResponse = manualService.subirManual(manualRequest);
        return ResponseEntity.ok(manualResponse);
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/manualParaEditar/{id}")
    public ResponseEntity<ManualResponseToEditDTO> manualParaEditar(@PathVariable Long id) {
        try {
            ManualResponseToEditDTO manual = manualService.manualParaEditar(id);
            return ResponseEntity.ok(manual);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/editarManual")
    public ResponseEntity<ManualNewResponseDTO> editarManual(
            @RequestParam("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("codigo") String codigo,
            @RequestParam("anioPublicacion") String anioPublicacion,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value= "subcategoriaId",required = false) Long subcategoriaId,
            @RequestParam(value= "publicado",required = false) char publicado,
            @RequestParam(value= "tipoId",required = false) Long tipoId,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "portada", required = false) MultipartFile portada) throws IOException {

        ManualRequestEditDTO manualRequest = new ManualRequestEditDTO();
        manualRequest.setId(id);
        manualRequest.setNombre(nombre);
        manualRequest.setCodigo(codigo);
        manualRequest.setAnioPublicacion(anioPublicacion);
        manualRequest.setDescripcion(descripcion);
        manualRequest.setPublicado(publicado);
        manualRequest.setSubcategoriaId(subcategoriaId);// Puede ser null
        manualRequest.setTipoId(tipoId);// Puede ser null
        manualRequest.setArchivo(archivo); // Puede ser null
        manualRequest.setPortada(portada); // Puede ser null

        // Llamar al servicio para editar el manual
        ManualNewResponseDTO  manualResponse = manualService.editarManual(manualRequest);

        // Retornar el manual actualizado
        return ResponseEntity.ok(manualResponse);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/descargarManual/{id}/{usuario}/{cedula}")
    public ResponseEntity<byte[]> descargarManual(@PathVariable Long id, @PathVariable String usuario, @PathVariable String cedula, HttpServletRequest request) {
        try {

            // Capturar la IP del cliente
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }


            // Obtener el contenido del archivo PDF en binario desde el servicio
            byte[] pdfContent = manualService.obtenerArchivoPdf(id, usuario, ip, cedula);



            if (pdfContent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=manual.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/eliminarManual/{id}")
    public ResponseEntity<String> eliminarManual(@PathVariable Long id) {
        try {
            manualService.eliminarManual(id);
            return ResponseEntity.ok("Manual eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el manual");
        }
    }


}
