package ec.mil.ejercito.cedmt.sidoc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "DMDOW_DESCARGAS")
public class Descarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DMDW_ID")
    private Long id;

    @Column(name = "DMDW_FECHA", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "DMDW_IP", nullable = false)
    private String ip;

    @Column(name = "DMDW_USUARIO_ID")
    private String usuarioId;

    @ManyToOne
    @JoinColumn(name = "DMDW_MANUAL_ID", nullable = false)
    private Manual manual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Manual getManual() {
        return manual;
    }

    public void setManual(Manual manual) {
        this.manual = manual;
    }
}