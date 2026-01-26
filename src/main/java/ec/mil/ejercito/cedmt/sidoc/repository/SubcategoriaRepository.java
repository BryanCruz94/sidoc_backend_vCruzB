package ec.mil.ejercito.cedmt.sidoc.repository;

import ec.mil.ejercito.cedmt.sidoc.dto.CategoriaListDTO;
import ec.mil.ejercito.cedmt.sidoc.dto.SubCategoriaListDTO;
import ec.mil.ejercito.cedmt.sidoc.model.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio para Subcategoria
@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    @Query("SELECT new ec.mil.ejercito.cedmt.sidoc.dto.SubCategoriaListDTO(s.id, s.nombre, s.categoria.id) FROM Subcategoria s")
    List<SubCategoriaListDTO> getSubcategoriasList();

}