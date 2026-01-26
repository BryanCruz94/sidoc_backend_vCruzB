package ec.mil.ejercito.cedmt.sidoc.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DMAN_MANUALES")
public class Manual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DMAN_ID")
    private Long id;

    @Column(name = "DMAN_NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DMAN_CODIGO")
    private String codigo;

    @Column(name = "DMAN_OBSERVACIONES")
    private String observaciones;

    @Column(name = "DMAN_ANIO_PUB")
    private LocalDate anioPublicacion;

    @Column(name = "DMAN_ESTADO", nullable = false)
    private char estado;

    @Column(name = "DMAN_PUBLICADO", nullable = false)
    private char publicado;

    @Column(name = "DMAN_URL_IMAGEN")
    private String urlImagen;

    @Column(name = "DMAN_ENLACE_DESC")
    private String enlaceDescarga;

    @Column(name = "DMAN_DESCRIPCION", length = 3500)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "DMSUB_SUBCATEGORIAS_DMSB_ID")
    private Subcategoria subcategoria;

    @ManyToOne
    @JoinColumn(name = "DMAN_TIPO_ID")
    private Tipo tipo;

    @ManyToOne
    @JoinColumn(name = "DMAN_CATEGORIA_ID")
    private Categoria categoria;


    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(LocalDate anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public char getPublicado() {
        return publicado;
    }

    public void setPublicado(char publicado) {
        this.publicado = publicado;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getEnlaceDescarga() {
        return enlaceDescarga;
    }

    public void setEnlaceDescarga(String enlaceDescarga) {
        this.enlaceDescarga = enlaceDescarga;
    }

    public Subcategoria getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
