package ec.mil.ejercito.cedmt.sidoc.repository;

import ec.mil.ejercito.cedmt.sidoc.dto.CategoriaListDTO;
import ec.mil.ejercito.cedmt.sidoc.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio para Categoria
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Obtener todas las categorías
    @Query("SELECT new ec.mil.ejercito.cedmt.sidoc.dto.CategoriaListDTO(c.id, c.nombre) FROM Categoria c")
    List<CategoriaListDTO> findCategorias();
}