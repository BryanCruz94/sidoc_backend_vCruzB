package ec.mil.ejercito.cedmt.sidoc.repository;

import ec.mil.ejercito.cedmt.sidoc.dto.DocEjercitoResponseDTO;
import ec.mil.ejercito.cedmt.sidoc.dto.ManualResponseToEditDTO;
import ec.mil.ejercito.cedmt.sidoc.dto.ManualsToChatDTO;
import ec.mil.ejercito.cedmt.sidoc.model.Manual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Long> {

    @Query(value = "SELECT new ec.mil.ejercito.cedmt.sidoc.dto.DocEjercitoResponseDTO(" +
            "m.id, " +
            "m.codigo, " +
            "m.nombre, " +
            "t.nombre_tipo, " +
            "c.nombre, " +
            "s.nombre, " +
            "m.descripcion, " +
            "m.estado, " +
            "m.publicado, " +
            "CAST(YEAR(m.anioPublicacion) AS string), " +
            "m.enlaceDescarga, " +
            "m.urlImagen) " +
            "FROM Manual m " +
            "JOIN m.tipo t " +
            "JOIN m.subcategoria s " +
            "JOIN s.categoria c " +
            "WHERE m.estado = '1'" )
    List<DocEjercitoResponseDTO> findActiveManuals();

    @Query("SELECT m FROM Manual m WHERE m.id = :id AND m.estado = '1'")
    Optional<Manual> findByIdDescarga(@Param("id") Long id);

    @Query(value = "SELECT new ec.mil.ejercito.cedmt.sidoc.dto.ManualResponseToEditDTO(" +
            "m.id, " +
            "m.codigo, " +
            "m.nombre, " +
            "t.id, " +           // Cambiado de t.nombre_tipo a t.id
            "c.id, " +           // Cambiado de c.nombre a c.id
            "s.id, " +           // Cambiado de s.nombre a s.id
            "m.descripcion, " +
            "m.estado, " +
            "m.publicado, " +
            "CAST(m.anioPublicacion AS string), " +
            "m.enlaceDescarga, " +
            "m.urlImagen) " +
            "FROM Manual m " +
            "JOIN m.tipo t " +
            "JOIN m.subcategoria s " +
            "JOIN s.categoria c " +
            "WHERE m.id = :idManual" +
            " AND m.estado = '1' " )
    ManualResponseToEditDTO findManualById(@Param("idManual") Long idManual);

    @Query(value = "SELECT new ec.mil.ejercito.cedmt.sidoc.dto.ManualsToChatDTO(" +
            "m.nombre, " +
            "c.nombre, " +
            "s.nombre, " +
            "m.descripcion, " +
            "CAST(m.anioPublicacion AS string)) " +
            "FROM Manual m " +
            "JOIN m.tipo t " +
            "JOIN m.subcategoria s " +
            "JOIN s.categoria c " +
            "WHERE m.estado = '1' AND m.publicado = '1' ")
    List<ManualsToChatDTO> findManualsToChat();

    //INICIO CONTEO DE MANUALES
    @Query("SELECT COUNT(m) FROM Manual m WHERE m.estado = '1' ")
    Long countTotalManuals();



}