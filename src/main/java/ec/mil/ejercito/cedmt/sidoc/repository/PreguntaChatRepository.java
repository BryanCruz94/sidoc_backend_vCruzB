package ec.mil.ejercito.cedmt.sidoc.repository;

import ec.mil.ejercito.cedmt.sidoc.model.PreguntaChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaChatRepository extends JpaRepository<PreguntaChat, Long> {
    PreguntaChat findByPregunta(String pregunta);
}
