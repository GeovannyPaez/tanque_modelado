package org.example.model;

public class ModoAutomata {
    private ModoEstado estado = ModoEstado.AUTOMATICO;

    public ModoEstado dispararEvento(EventoModo evento) {
        if (evento == EventoModo.set_auto) {
            estado = ModoEstado.AUTOMATICO;
        } else if (evento == EventoModo.set_manual) {
            estado = ModoEstado.MANUAL;
        }
        return estado;
    }

    public ModoEstado getEstado() {
        return estado;
    }

    public void reset() {
        estado = ModoEstado.AUTOMATICO;
    }
}
