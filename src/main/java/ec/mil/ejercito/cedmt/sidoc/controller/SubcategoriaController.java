package ec.mil.ejercito.cedmt.sidoc.controller;

        import ec.mil.ejercito.cedmt.sidoc.dto.SubCategoriaListDTO;
        import ec.mil.ejercito.cedmt.sidoc.service.SubcategoriaService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.CrossOrigin;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.List;

@RestController
@RequestMapping("/cedmt/sidoc/subcategorias")
public class SubcategoriaController {

    @Autowired
    private SubcategoriaService subCategoriaService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/list")
    public List<SubCategoriaListDTO> getSubcategorias() {
        return subCategoriaService.getSubcategorias();
    }
}
