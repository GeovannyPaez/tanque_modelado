package org.example.view;

import org.example.model.AperturaValvula;
import org.example.model.ModoEstado;
import org.example.model.NivelEstado;
import org.example.model.SeguridadEstado;

public class EstadoUI {
    private final NivelEstado nivel;
    private final ModoEstado modo;
    private final SeguridadEstado seguridad;
    private final AperturaValvula valvula;

    public EstadoUI(NivelEstado nivel, ModoEstado modo, SeguridadEstado seguridad, AperturaValvula valvula) {
        this.nivel = nivel;
        this.modo = modo;
        this.seguridad = seguridad;
        this.valvula = valvula;
    }

    public NivelEstado getNivel() {
        return nivel;
    }

    public ModoEstado getModo() {
        return modo;
    }

    public SeguridadEstado getSeguridad() {
        return seguridad;
    }

    public AperturaValvula getValvula() {
        return valvula;
    }
}
