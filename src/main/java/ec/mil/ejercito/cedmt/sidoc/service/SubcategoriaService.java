package ec.mil.ejercito.cedmt.sidoc.service;

import ec.mil.ejercito.cedmt.sidoc.dto.SubCategoriaListDTO;
import ec.mil.ejercito.cedmt.sidoc.repository.SubcategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubcategoriaService {

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    public List<SubCategoriaListDTO> getSubcategorias(){
        return subcategoriaRepository.getSubcategoriasList();
    }
}
