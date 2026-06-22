package org.example.model;

public class NivelAutomata {
    private int porcentaje = NivelEstado.MEDIO.getPorcentaje();

    public NivelEstado dispararEvento(EventoNivel evento) {
        switch (evento) {
            case eEntrada:
                aplicarCambio(10);
                break;
            case eSalida:
                aplicarCambio(-10);
                break;
            default:
                break;
        }
        return getEstado();
    }

    public NivelEstado aplicarCambio(int deltaPorcentaje) {
        porcentaje = Math.max(0, Math.min(110, porcentaje + deltaPorcentaje));
        return getEstado();
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public NivelEstado getEstado() {
        if (porcentaje <= 0) {
            return NivelEstado.VACIO;
        }
        if (porcentaje < 45) {
            return NivelEstado.BAJO;
        }
        if (porcentaje < 75) {
            return NivelEstado.MEDIO;
        }
        if (porcentaje < 100) {
            return NivelEstado.ALTO;
        }
        if (porcentaje == 100) {
            return NivelEstado.MAXIMO;
        }
        return NivelEstado.DESBORDAMIENTO;
    }

    public void reset() {
        porcentaje = NivelEstado.MEDIO.getPorcentaje();
    }
}
