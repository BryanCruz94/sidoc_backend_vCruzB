package ec.mil.ejercito.cedmt.sidoc.dto;

public class DocEjercitoResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String nombre_tipo;
    private String categoria;
    private String subcategoria;
    private String descripcion;
    private char estado;
    private char publicado;
    private String anioPublicacion;
    private String enlaceDescarga;
    private String urlImagen;

    public DocEjercitoResponseDTO() {
    }

    public DocEjercitoResponseDTO(Long id, String codigo, String nombre, String nombre_tipo, String categoria,
                                  String subcategoria,String descripcion, char estado, char publicado,
                                  String anioPublicacion, String enlaceDescarga, String urlImagen ) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.nombre_tipo = nombre_tipo;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
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

    public String getNombre_tipo() {
        return nombre_tipo;
    }

    public void setNombre_tipo(String nombre_tipo) {
        this.nombre_tipo = nombre_tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
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
