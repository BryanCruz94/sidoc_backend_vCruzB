package ec.mil.ejercito.cedmt.sidoc.repository;

import ec.mil.ejercito.cedmt.sidoc.model.Descarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Repositorio para Descarga
@Repository
public interface DescargaRepository extends JpaRepository<Descarga, Long> {
}