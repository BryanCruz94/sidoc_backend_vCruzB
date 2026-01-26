package ec.mil.ejercito.cedmt.sidoc.dto;

public class ManualResponseToEditDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Long tipoId;          // Cambiado de String a Long
    private Long categoriaId;     // Cambiado de String a Long
    private Long subcategoriaId;  // Cambiado de String a Long
    private String descripcion;
    private char estado;
    private char publicado;
    private String anioPublicacion;
    private String enlaceDescarga;
    private String urlImagen;


    public ManualResponseToEditDTO() {
    }

    public ManualResponseToEditDTO(Long id, String codigo, String nombre, Long tipoId, Long categoriaId,
                                   Long subcategoriaId, String descripcion, char estado, char publicado,
                                   String anioPublicacion, String enlaceDescarga, String urlImagen ) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoId = tipoId;
        this.categoriaId = categoriaId;
        this.subcategoriaId = subcategoriaId;
        this.descripcion = descripcion;
        this.estado = estado;
        this.publicado = publicado;
        this.anioPublicacion = anioPublicacion;
        this.enlaceDescarga = enlaceDescarga;
        this.urlImagen = urlImagen;

    }

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTipoId() {
        return tipoId;
    }

    public void setTipoId(Long tipoId) {
        this.tipoId = tipoId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getSubcategoriaId() {
        return subcategoriaId;
    }

    public void setSubcategoriaId(Long subcategoriaId) {
        this.subcategoriaId = subcategoriaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(String anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getEnlaceDescarga() {
        return enlaceDescarga;
    }

    public void setEnlaceDescarga(String enlaceDescarga) {
        this.enlaceDescarga = enlaceDescarga;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}




