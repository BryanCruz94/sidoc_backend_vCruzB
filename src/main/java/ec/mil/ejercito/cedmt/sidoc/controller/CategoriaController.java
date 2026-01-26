package ec.mil.ejercito.cedmt.sidoc.controller;


import ec.mil.ejercito.cedmt.sidoc.dto.CategoriaListDTO;
import ec.mil.ejercito.cedmt.sidoc.model.Categoria;
import ec.mil.ejercito.cedmt.sidoc.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cedmt/sidoc/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/list")
    public List<CategoriaListDTO> getCategorias() {
        return categoriaService.getCategorias();
    }

}
