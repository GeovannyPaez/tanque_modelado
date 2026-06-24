package org.example.view;

import org.example.model.AperturaValvula;
import org.example.model.ModoEstado;
import org.example.model.NivelEstado;
import org.example.model.SeguridadEstado;

public class EstadoUI {
    private final NivelEstado nivel;
    private final int nivelPorcentaje;
    private final ModoEstado modo;
    private final SeguridadEstado seguridad;
    private final AperturaValvula valvula;
    private final AperturaValvula valvulaSeguridad;
    private final long tick;
    private final boolean simulando;
    private final int setPoint;
    private final boolean consumoActivo;
    private final double alturaMaximaMetros;
    private final double nivelActualMetros;
    private final boolean fallaAutomaticaActiva;
    private final boolean eventoSeguridadAutomaticoActivado;
    private final boolean emergenciaPorAlturaMaxima;

    public EstadoUI(NivelEstado nivel, int nivelPorcentaje, ModoEstado modo, SeguridadEstado seguridad, AperturaValvula valvula, AperturaValvula valvulaSeguridad, long tick, boolean simulando, int setPoint, boolean consumoActivo, double alturaMaximaMetros, double nivelActualMetros, boolean fallaAutomaticaActiva, boolean eventoSeguridadAutomaticoActivado, boolean emergenciaPorAlturaMaxima) {
        this.nivel = nivel;
        this.nivelPorcentaje = nivelPorcentaje;
        this.modo = modo;
        this.seguridad = seguridad;
        this.valvula = valvula;
        this.valvulaSeguridad = valvulaSeguridad;
        this.tick = tick;
        this.simulando = simulando;
        this.setPoint = setPoint;
        this.consumoActivo = consumoActivo;
        this.alturaMaximaMetros = alturaMaximaMetros;
        this.nivelActualMetros = nivelActualMetros;
        this.fallaAutomaticaActiva = fallaAutomaticaActiva;
        this.eventoSeguridadAutomaticoActivado = eventoSeguridadAutomaticoActivado;
        this.emergenciaPorAlturaMaxima = emergenciaPorAlturaMaxima;
    }

    public NivelEstado getNivel() {
        return nivel;
    }

    public int getNivelPorcentaje() {
        return nivelPorcentaje;
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

    public AperturaValvula getValvulaSeguridad() {
        return valvulaSeguridad;
    }

    public long getTick() {
        return tick;
    }

    public boolean isSimulando() {
        return simulando;
    }

    public int getSetPoint() {
        return setPoint;
    }

    public boolean isConsumoActivo() {
        return consumoActivo;
    }

    public double getAlturaMaximaMetros() {
        return alturaMaximaMetros;
    }

    public double getNivelActualMetros() {
        return nivelActualMetros;
    }

    public boolean isFallaAutomaticaActiva() {
        return fallaAutomaticaActiva;
    }

    public boolean isEventoSeguridadAutomaticoActivado() {
        return eventoSeguridadAutomaticoActivado;
    }

    public boolean isEmergenciaPorAlturaMaxima() {
        return emergenciaPorAlturaMaxima;
    }
}
