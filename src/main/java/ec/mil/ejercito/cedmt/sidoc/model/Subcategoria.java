package ec.mil.ejercito.cedmt.sidoc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DMSUB_SUBCATEGORIAS")
public class Subcategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DMSB_ID")
    private Long id;

    @Column(name = "DMSB_NOMBRE", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "DCCAT_CATEGORIAS_DCCA_ID", nullable = false)
    private Categoria categoria;

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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
