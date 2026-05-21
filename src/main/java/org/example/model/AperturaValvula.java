package org.example.model;

public enum AperturaValvula {
    CERRADA(0, "0%"),
    ABIERTO_33(33, "33%"),
    ABIERTO_66(66, "66%"),
    ABIERTO_100(100, "100%");

    private final int porcentaje;
    private final String etiqueta;

    AperturaValvula(int porcentaje, String etiqueta) {
        this.porcentaje = porcentaje;
        this.etiqueta = etiqueta;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
