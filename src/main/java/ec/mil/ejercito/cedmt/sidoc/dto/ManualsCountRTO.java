package ec.mil.ejercito.cedmt.sidoc.dto;

import java.util.ArrayList;

public class ManualsCountRTO {
    public int totalManuals;
    public ArrayList<Long> totalCategoriasManuals;
    public ArrayList<Long> totalSubcategoriasManuals;
    public ArrayList<Long> totalTiposManuals;

    public ManualsCountRTO() {
    }

    public ManualsCountRTO(int totalManuals, ArrayList<Long> totalCategoriasManuals, ArrayList<Long> totalSubcategoriasManuals, ArrayList<Long> totalTiposManuals) {
        this.totalManuals = totalManuals;
        this.totalCategoriasManuals = totalCategoriasManuals;
        this.totalSubcategoriasManuals = totalSubcategoriasManuals;
        this.totalTiposManuals = totalTiposManuals;
    }

    public int getTotalManuals() {
        return totalManuals;
    }

    public void setTotalManuals(int totalManuals) {
        this.totalManuals = totalManuals;
    }

    public ArrayList<Long> getTotalCategoriasManuals() {
        return totalCategoriasManuals;
    }

    public void setTotalCategoriasManuals(ArrayList<Long> totalCategoriasManuals) {
        this.totalCategoriasManuals = totalCategoriasManuals;
    }

    public ArrayList<Long> getTotalSubcategoriasManuals() {
        return totalSubcategoriasManuals;
    }

    public void setTotalSubcategoriasManuals(ArrayList<Long> totalSubcategoriasManuals) {
        this.totalSubcategoriasManuals = totalSubcategoriasManuals;
    }

    public ArrayList<Long> getTotalTiposManuals() {
        return totalTiposManuals;
    }

    public void setTotalTiposManuals(ArrayList<Long> totalTiposManuals) {
        this.totalTiposManuals = totalTiposManuals;
    }
}
