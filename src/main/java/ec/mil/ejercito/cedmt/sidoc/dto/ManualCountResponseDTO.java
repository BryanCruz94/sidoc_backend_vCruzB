package ec.mil.ejercito.cedmt.sidoc.dto;

import java.util.Map;

public class ManualCountResponseDTO {
    private Long totalManuals;
    private Map<String, Long> categoryCounts;
    private Map<String, Long> subcategoryCounts;
    private Map<String, Long> typeCounts;
    private Map<Integer, Long> yearCounts;

    // Getters y Setters
    public Long getTotalManuals() {
        return totalManuals;
    }

    public Map<Integer, Long> getYearCounts() {
        return yearCounts;
    }

    public void setYearCounts(Map<Integer, Long> yearCounts) {
        this.yearCounts = yearCounts;
    }

    public void setTotalManuals(Long totalManuals) {
        this.totalManuals = totalManuals;
    }

    public Map<String, Long> getCategoryCounts() {
        return categoryCounts;
    }

    public void setCategoryCounts(Map<String, Long> categoryCounts) {
        this.categoryCounts = categoryCounts;
    }

    public Map<String, Long> getSubcategoryCounts() {
        return subcategoryCounts;
    }

    public void setSubcategoryCounts(Map<String, Long> subcategoryCounts) {
        this.subcategoryCounts = subcategoryCounts;
    }

    public Map<String, Long> getTypeCounts() {
        return typeCounts;
    }

    public void setTypeCounts(Map<String, Long> typeCounts) {
        this.typeCounts = typeCounts;
    }
}