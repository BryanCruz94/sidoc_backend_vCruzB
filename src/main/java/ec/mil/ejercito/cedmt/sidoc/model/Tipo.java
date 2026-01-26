package ec.mil.ejercito.cedmt.sidoc.model;

import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "DMTYP_TIPOS")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DMTY_ID")
    private Long id;

    @Column(name = "DMTY_CODIGO", nullable = false)
    private String codigo;

    @Column(name = "DMTY_NOMBRE_TIPO", nullable = false)
    private String nombre_tipo;

    @Column(name = "DMTY_ESTADO", nullable = false)
    private char estado;

    @OneToMany(mappedBy = "tipo", cascade = CascadeType.ALL)
    private List<Manual> manuales;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre_tipo() {
        return nombre_tipo;
    }

    public void setNombre_tipo(String nombre_tipo) {
        this.nombre_tipo = nombre_tipo;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public List<Manual> getManuales() {
        return manuales;
    }

    public void setManuales(List<Manual> manuales) {
        this.manuales = manuales;
    }
}