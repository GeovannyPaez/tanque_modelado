package org.example.model;

public class NivelAutomata {
    private NivelEstado estado = NivelEstado.MEDIO;

    public NivelEstado dispararEvento(EventoNivel evento) {
        switch (evento) {
            case eEntrada:
                estado = subir(estado);
                break;
            case eSalida:
                estado = bajar(estado);
                break;
            default:
                break;
        }
        return estado;
    }

    private NivelEstado subir(NivelEstado actual) {
        switch (actual) {
            case VACIO:
                return NivelEstado.BAJO;
            case BAJO:
                return NivelEstado.MEDIO;
            case MEDIO:
                return NivelEstado.ALTO;
            case ALTO:
                return NivelEstado.MAXIMO;
            case MAXIMO:
                return NivelEstado.DESBORDAMIENTO;
            case DESBORDAMIENTO:
            default:
                return NivelEstado.DESBORDAMIENTO;
        }
    }

    private NivelEstado bajar(NivelEstado actual) {
        switch (actual) {
            case DESBORDAMIENTO:
                return NivelEstado.MAXIMO;
            case MAXIMO:
                return NivelEstado.ALTO;
            case ALTO:
                return NivelEstado.MEDIO;
            case MEDIO:
                return NivelEstado.BAJO;
            case BAJO:
                return NivelEstado.VACIO;
            case VACIO:
            default:
                return NivelEstado.VACIO;
        }
    }

    public NivelEstado getEstado() {
        return estado;
    }
}
