package ec.mil.ejercito.cedmt.sidoc.controller;



import ec.mil.ejercito.cedmt.sidoc.dto.TipoListDTO;
import ec.mil.ejercito.cedmt.sidoc.service.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cedmt/sidoc/tipos")
public class TipoController {

    @Autowired
    private TipoService tipoService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/list")
    public List<TipoListDTO> getTipos() {
        return tipoService.getTipos();
    }

}
