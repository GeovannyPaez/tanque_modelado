package org.example.model;

public enum NivelEstado {
    VACIO(0),
    BAJO(30),
    MEDIO(60),
    ALTO(90),
    MAXIMO(100),
    DESBORDAMIENTO(110);

    private final int porcentaje;

    NivelEstado(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getPorcentaje() {
        return porcentaje;
    }
}
