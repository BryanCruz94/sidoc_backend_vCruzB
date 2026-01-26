package ec.mil.ejercito.cedmt.sidoc.service;


import ec.mil.ejercito.cedmt.sidoc.dto.TipoListDTO;
import ec.mil.ejercito.cedmt.sidoc.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoService {
    @Autowired
    private TipoRepository tipoRepository;

    public List<TipoListDTO> getTipos(){
        return tipoRepository.getTiposList();
    }

}
