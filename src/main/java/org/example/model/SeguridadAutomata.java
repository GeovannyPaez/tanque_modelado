package org.example.model;

public class SeguridadAutomata {
    private SeguridadEstado estado = SeguridadEstado.NORMAL;

    public SeguridadEstado dispararEvento(EventoSeguridad evento) {
        switch (evento) {
            case eA:
                if (estado != SeguridadEstado.EMERGENCIA) {
                    estado = SeguridadEstado.ADVERTENCIA;
                }
                break;
            case eD:
            case eV:
            case eStop:
                estado = SeguridadEstado.EMERGENCIA;
                break;
            case reset:
                estado = SeguridadEstado.NORMAL;
                break;
            default:
                break;
        }
        return estado;
    }

    public SeguridadEstado getEstado() {
        return estado;
    }

    public void reset() {
        estado = SeguridadEstado.NORMAL;
    }
}
