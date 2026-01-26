package ec.mil.ejercito.cedmt.sidoc.dto;

import ec.mil.ejercito.cedmt.sidoc.model.Manual;

import java.time.LocalDate;

public class ManualNewResponseDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String observaciones;
    private LocalDate anioPublicacion;
    private char estado;
    private char publicado;
    private String urlImagen;
    private String enlaceDescarga;
    private String descripcion;

    private Long subcategoriaId;
    private String subcategoriaNombre;

    private Long categoriaId;
    private String categoriaNombre;

    private Long tipoId;
    private String tipoNombre;

    // Constructor
    public ManualNewResponseDTO(Manual manual) {
        this.id = manual.getId();
        this.nombre = manual.getNombre();
        this.codigo = manual.getCodigo();
        this.observaciones = manual.getDescripcion();
        this.anioPublicacion = manual.getAnioPublicacion();
        this.estado = manual.getEstado();
        this.publicado = manual.getPublicado();
        this.urlImagen = manual.getUrlImagen();
        this.enlaceDescarga = manual.getEnlaceDescarga();
        this.descripcion = manual.getDescripcion();

        if (manual.getCategoria() != null){
            this.categoriaId = manual.getCategoria().getId();
            this.subcategoriaNombre = manual.getCategoria().getNombre();
        }

        if (manual.getSubcategoria() != null) {
            this.subcategoriaId = manual.getSubcategoria().getId();
            this.subcategoriaNombre = manual.getSubcategoria().getNombre();
        }

        if (manual.getTipo() != null) {
            this.tipoId = manual.getTipo().getId();
            this.tipoNombre = manual.getTipo().getNombre_tipo();
        }
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getSubcategoriaId() {
        return subcategoriaId;
    }

    public void setSubcategoriaId(Long subcategoriaId) {
        this.subcategoriaId = subcategoriaId;
    }

    public String getSubcategoriaNombre() {
        return subcategoriaNombre;
    }

    public void setSubcategoriaNombre(String subcategoriaNombre) {
        this.subcategoriaNombre = subcategoriaNombre;
    }

    public Long getTipoId() {
        return tipoId;
    }

    public void setTipoId(Long tipoId) {
        this.tipoId = tipoId;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }
}
