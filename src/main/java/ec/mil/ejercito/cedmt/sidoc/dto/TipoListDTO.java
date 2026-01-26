package ec.mil.ejercito.cedmt.sidoc.dto;

public class TipoListDTO {
    private Long id;
    private String codigo;
    private String nombre_tipo;

    public TipoListDTO() {
    }

    public TipoListDTO(Long id, String codigo, String nombre_tipo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre_tipo = nombre_tipo;
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

    public String getNombre_tipo() {
        return nombre_tipo;
    }

    public void setNombre_tipo(String nombre_tipo) {
        this.nombre_tipo = nombre_tipo;
    }
}
