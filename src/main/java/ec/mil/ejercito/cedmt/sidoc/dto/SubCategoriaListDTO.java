package ec.mil.ejercito.cedmt.sidoc.dto;

public class SubCategoriaListDTO {
    private Long id;
    private String subCategoria;
    private Long categoriaID;


    public SubCategoriaListDTO() {
    }

    public SubCategoriaListDTO(Long id, String subCategoria, Long categoriaID) {
        this.id = id;
        this.subCategoria = subCategoria;
        this.categoriaID = categoriaID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubCategoria() {
        return subCategoria;
    }

    public void setSubCategoria(String subCategoria) {
        this.subCategoria = subCategoria;
    }

    public Long getCategoriaID() {
        return categoriaID;
    }

    public void setCategoriaID(Long categoriaID) {
        this.categoriaID = categoriaID;
    }
}
