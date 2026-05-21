package org.example.controller;

import org.example.model.AperturaValvula;
import org.example.model.EventoModo;
import org.example.model.EventoNivel;
import org.example.model.EventoSeguridad;
import org.example.model.ModoAutomata;
import org.example.model.ModoEstado;
import org.example.model.NivelAutomata;
import org.example.model.NivelEstado;
import org.example.model.SeguridadAutomata;
import org.example.model.SeguridadEstado;
import org.example.view.EstadoUI;
import org.example.view.MainFrame;

import javax.swing.Timer;

public class SimulacionController {
    private static final int TICK_MS = 1200;
    private static final int OUTFLOW_EVERY_N_TICKS = 4;

    private final NivelAutomata nivelAutomata = new NivelAutomata();
    private final ModoAutomata modoAutomata = new ModoAutomata();
    private final SeguridadAutomata seguridadAutomata = new SeguridadAutomata();
    private final MainFrame view = new MainFrame();

    private AperturaValvula aperturaActual = AperturaValvula.CERRADA;
    private AperturaValvula aperturaManualSeleccionada = AperturaValvula.CERRADA;
    private long tick = 0;

    private final Timer timer;

    public SimulacionController() {
        wireActions();
        timer = new Timer(TICK_MS, e -> ejecutarTick());
        renderizar();
        log("Sistema inicializado.");
    }

    public void start() {
        timer.start();
    }

    private void wireActions() {
        view.getAutoBtn().addActionListener(e -> {
            ModoEstado nuevo = modoAutomata.dispararEvento(EventoModo.set_auto);
            logEvento("set_auto", nuevo.name());
            renderizar();
        });

        view.getManualBtn().addActionListener(e -> {
            ModoEstado nuevo = modoAutomata.dispararEvento(EventoModo.set_manual);
            logEvento("set_manual", nuevo.name());
            renderizar();
        });

        view.getAbrir33Btn().addActionListener(e -> seleccionarManual(AperturaValvula.ABIERTO_33));
        view.getAbrir66Btn().addActionListener(e -> seleccionarManual(AperturaValvula.ABIERTO_66));
        view.getAbrir100Btn().addActionListener(e -> seleccionarManual(AperturaValvula.ABIERTO_100));
        view.getCerrarBtn().addActionListener(e -> seleccionarManual(AperturaValvula.CERRADA));

        view.getEmergenciaBtn().addActionListener(e -> {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eStop);
            aperturaActual = AperturaValvula.CERRADA;
            logEvento("eStop", estado.name());
            renderizar();
        });

        view.getResetBtn().addActionListener(e -> {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.reset);
            logEvento("reset", estado.name());
            renderizar();
        });
    }

    private void seleccionarManual(AperturaValvula apertura) {
        aperturaManualSeleccionada = apertura;
        log("Seleccion manual de valvula: " + apertura.getEtiqueta());
    }

    private void ejecutarTick() {
        tick++;

        if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA) {
            log("tick bloqueado por emergencia");
            renderizar();
            return;
        }

        if (modoAutomata.getEstado() == ModoEstado.AUTOMATICO) {
            aperturaActual = decidirAperturaAutomatica(nivelAutomata.getEstado());
            if (debeDispararEntrada(aperturaActual, tick)) {
                NivelEstado previo = nivelAutomata.getEstado();
                NivelEstado nuevo = nivelAutomata.dispararEvento(EventoNivel.eEntrada);
                logEvento("eEntrada", nuevo.name());
                evaluarSeguridadPorNivel(previo, nuevo);
            }
        } else {
            aperturaActual = aperturaManualSeleccionada;
            if (debeDispararEntrada(aperturaActual, tick)) {
                NivelEstado previo = nivelAutomata.getEstado();
                NivelEstado nuevo = nivelAutomata.dispararEvento(EventoNivel.eEntrada);
                logEvento("eEntrada", nuevo.name());
                evaluarSeguridadPorNivel(previo, nuevo);
                if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA) {
                    aperturaActual = AperturaValvula.CERRADA;
                    renderizar();
                    return;
                }
            }
        }

        if (tick % OUTFLOW_EVERY_N_TICKS == 0) {
            NivelEstado previo = nivelAutomata.getEstado();
            NivelEstado nuevo = nivelAutomata.dispararEvento(EventoNivel.eSalida);
            logEvento("eSalida", nuevo.name());
            evaluarSeguridadPorNivel(previo, nuevo);
        }

        renderizar();
    }

    private AperturaValvula decidirAperturaAutomatica(NivelEstado nivel) {
        switch (nivel) {
            case VACIO:
                return AperturaValvula.ABIERTO_100;
            case BAJO:
                return AperturaValvula.ABIERTO_66;
            case MEDIO:
                return AperturaValvula.ABIERTO_33;
            case ALTO:
            case MAXIMO:
            case DESBORDAMIENTO:
            default:
                return AperturaValvula.CERRADA;
        }
    }

    private boolean debeDispararEntrada(AperturaValvula apertura, long tickActual) {
        switch (apertura) {
            case ABIERTO_100:
                return tickActual % 2 == 0;
            case ABIERTO_66:
                return tickActual % 3 == 0;
            case ABIERTO_33:
                return tickActual % 4 == 0;
            case CERRADA:
            default:
                return false;
        }
    }

    private void evaluarSeguridadPorNivel(NivelEstado previo, NivelEstado actual) {
        if (previo != NivelEstado.VACIO && actual == NivelEstado.VACIO) {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eV);
            logEvento("eV", estado.name());
            aperturaActual = AperturaValvula.CERRADA;
        } else if (previo != NivelEstado.DESBORDAMIENTO && actual == NivelEstado.DESBORDAMIENTO) {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eD);
            logEvento("eD", estado.name());
            aperturaActual = AperturaValvula.CERRADA;
        }
    }

    private void renderizar() {
        EstadoUI dto = new EstadoUI(
                nivelAutomata.getEstado(),
                modoAutomata.getEstado(),
                seguridadAutomata.getEstado(),
                aperturaActual
        );
        view.render(dto);
    }

    private void logEvento(String evento, String estado) {
        view.addLog("[tick " + tick + "] " + evento + " -> " + estado);
    }

    private void log(String mensaje) {
        view.addLog("[tick " + tick + "] " + mensaje);
    }
}
