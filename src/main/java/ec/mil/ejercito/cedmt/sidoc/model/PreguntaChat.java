package ec.mil.ejercito.cedmt.sidoc.model;


import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name = "DPRG_PREGUNTAS_CHAT")
public class PreguntaChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DPRG_ID")
    private Long id;

    @Column(name = "DPRG_PREGUNTA", nullable = false)
    private String pregunta;

    @Column(name = "DPRG_TIPO_PRE", nullable = false)
    private String tipoPregunta;

    @Column(name= "DPRG_TIEMPO_RESP", nullable = true)
    private Integer tiempoRespuesta;

    @Column(name= "DPRG_API_KEY", nullable = true)
    private String apiKey;

    @Column(name= "DPRG_CANT_TOKENS", nullable = true)
    private Integer cantTokens;

    public PreguntaChat() {
    }

    public PreguntaChat(Long id, String pregunta, String tipoPregunta, Integer tiempoRespuesta, String apiKey, Integer cantTokens ) {
        this.id = id;
        this.pregunta = pregunta;
        this.tipoPregunta = tipoPregunta;
        this.tiempoRespuesta = tiempoRespuesta;
        this.apiKey = apiKey;
        this.cantTokens = cantTokens;
    }

    public String getTipoPregunta() {
        return tipoPregunta;
    }

    public void setTipoPregunta(String tipoPregunta) {
        this.tipoPregunta = tipoPregunta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Integer getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(Integer tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getCantTokens() {
        return cantTokens;
    }

    public void setCantTokens(Integer cantTokens) {
        this.cantTokens = cantTokens;
    }
}
