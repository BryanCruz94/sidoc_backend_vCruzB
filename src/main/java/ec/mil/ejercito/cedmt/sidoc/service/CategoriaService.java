package ec.mil.ejercito.cedmt.sidoc.service;

import ec.mil.ejercito.cedmt.sidoc.model.Categoria;
import ec.mil.ejercito.cedmt.sidoc.repository.CategoriaRepository;
import ec.mil.ejercito.cedmt.sidoc.dto.CategoriaListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaListDTO> getCategorias(){
        return categoriaRepository.findCategorias();
    }

}
