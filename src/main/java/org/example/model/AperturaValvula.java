package org.example.model;

public enum AperturaValvula {
    CERRADA(false, "Cerrada"),
    ABIERTA(true, "Abierta");

    private final boolean abierta;
    private final String etiqueta;

    AperturaValvula(boolean abierta, String etiqueta) {
        this.abierta = abierta;
        this.etiqueta = etiqueta;
    }

    public boolean isAbierta() {
        return abierta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
