package ec.mil.ejercito.cedmt.sidoc.repository;

import ec.mil.ejercito.cedmt.sidoc.dto.SubCategoriaListDTO;
import ec.mil.ejercito.cedmt.sidoc.dto.TipoListDTO;
import ec.mil.ejercito.cedmt.sidoc.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio para Tipo
@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {

    @Query("SELECT new ec.mil.ejercito.cedmt.sidoc.dto.TipoListDTO(t.id, t.codigo, t.nombre_tipo) FROM Tipo t")
    List<TipoListDTO> getTiposList();
}
