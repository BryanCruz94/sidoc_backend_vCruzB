package ec.mil.ejercito.cedmt.sidoc.dto;

public class ManualsToChatDTO {
    private String nombre;
    private String categoria;
    private String subcategoria;
    private String descripcion;
    private String anioPublicacion;

    public ManualsToChatDTO() {
    }

    public ManualsToChatDTO(String nombre, String categoria, String subcategoria, String descripcion, String anioPublicacion) {

        this.nombre = nombre;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.anioPublicacion = anioPublicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(String anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }
}
