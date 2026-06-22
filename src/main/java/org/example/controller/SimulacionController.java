package org.example.controller;

import org.example.model.AperturaValvula;
import org.example.model.EventoModo;
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
    private static final int CONSUMO_POR_TICK = 3;
    private static final int BANDA_CONTROL_AUTOMATICO = 15;

    private final NivelAutomata nivelAutomata = new NivelAutomata();
    private final ModoAutomata modoAutomata = new ModoAutomata();
    private final SeguridadAutomata seguridadAutomata = new SeguridadAutomata();
    private final MainFrame view = new MainFrame();

    private AperturaValvula aperturaActual = AperturaValvula.CERRADA;
    private AperturaValvula aperturaManualSeleccionada = AperturaValvula.CERRADA;
    private int setPoint = NivelEstado.MEDIO.getPorcentaje();
    private double alturaMaximaMetros = 5.0;
    private boolean actualizandoSetPointDesdeControlador = false;
    private boolean actualizandoAlturaDesdeControlador = false;
    private boolean llenandoAutomatico = false;
    private boolean fallaAutomaticaActiva = false;
    private boolean eventoSeguridadAutomaticoActivado = false;
    private boolean emergenciaPorAlturaMaxima = false;
    private boolean consumoActivo = true;
    private long tick = 0;

    private final Timer timer;

    public SimulacionController() {
        timer = new Timer(TICK_MS, e -> ejecutarTick());
        wireActions();
        renderizar();
        log("Sistema inicializado.");
    }

    public void start() {
        timer.start();
        renderizar();
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

        view.getIniciarBtn().addActionListener(e -> {
            timer.start();
            log("simulacion iniciada");
            renderizar();
        });

        view.getPausarBtn().addActionListener(e -> {
            timer.stop();
            log("simulacion pausada");
            renderizar();
        });

        view.getReiniciarBtn().addActionListener(e -> reiniciarSistema());
        view.getConsumoBtn().addActionListener(e -> alternarConsumo());
        view.getAplicarSetPointBtn().addActionListener(e -> aplicarSetPointDesdeVista(true));
        view.addSetPointChangeListener(e -> aplicarSetPointDesdeVista(false));
        view.addAlturaTanqueChangeListener(e -> aplicarAlturaTanqueDesdeVista());

        view.getAlternarValvulaBtn().addActionListener(e -> alternarValvulaManual());
        view.getSimularFallaAutomaticaBtn().addActionListener(e -> simularFallaAutomatica());

        view.getEmergenciaBtn().addActionListener(e -> {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eStop);
            aperturaActual = AperturaValvula.CERRADA;
            fallaAutomaticaActiva = false;
            eventoSeguridadAutomaticoActivado = false;
            emergenciaPorAlturaMaxima = false;
            logEvento("eStop", estado.name());
            renderizar();
        });

        view.getResetBtn().addActionListener(e -> {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.reset);
            eventoSeguridadAutomaticoActivado = false;
            emergenciaPorAlturaMaxima = false;
            logEvento("reset", estado.name());
            renderizar();
        });
    }

    private void reiniciarSistema() {
        nivelAutomata.reset();
        modoAutomata.reset();
        seguridadAutomata.reset();
        aperturaActual = AperturaValvula.CERRADA;
        aperturaManualSeleccionada = AperturaValvula.CERRADA;
        setPoint = NivelEstado.MEDIO.getPorcentaje();
        alturaMaximaMetros = 5.0;
        llenandoAutomatico = false;
        fallaAutomaticaActiva = false;
        eventoSeguridadAutomaticoActivado = false;
        emergenciaPorAlturaMaxima = false;
        consumoActivo = true;
        actualizandoSetPointDesdeControlador = true;
        view.getSetPointSpinner().setValue(Integer.valueOf(setPoint));
        actualizandoSetPointDesdeControlador = false;
        actualizandoAlturaDesdeControlador = true;
        view.getAlturaTanqueSpinner().setValue(Double.valueOf(alturaMaximaMetros));
        actualizandoAlturaDesdeControlador = false;
        tick = 0;
        view.limpiarGrafica();
        log("sistema reiniciado");
        renderizar();
    }

    private void simularFallaAutomatica() {
        if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA
                || modoAutomata.getEstado() != ModoEstado.AUTOMATICO) {
            return;
        }

        fallaAutomaticaActiva = true;
        eventoSeguridadAutomaticoActivado = false;
        llenandoAutomatico = true;
        aperturaActual = AperturaValvula.ABIERTA;
        log("Falla simulada: válvula atascada abierta");
        renderizar();
    }

    private void aplicarAlturaTanqueDesdeVista() {
        if (actualizandoAlturaDesdeControlador) {
            return;
        }

        double nuevaAltura = view.getAlturaTanqueSeleccionada();
        if (nuevaAltura <= 0.0) {
            log("altura de tanque invalida: debe ser mayor que cero");
            return;
        }

        if (Double.compare(nuevaAltura, alturaMaximaMetros) == 0) {
            return;
        }

        alturaMaximaMetros = nuevaAltura;
        log(String.format("altura maxima del tanque configurada: %.2f m", alturaMaximaMetros));
        renderizar();
    }

    private void alternarConsumo() {
        consumoActivo = !consumoActivo;
        log(consumoActivo ? "consumo activado" : "consumo desactivado");
        renderizar();
    }

    private void aplicarSetPointDesdeVista(boolean registrarLog) {
        if (actualizandoSetPointDesdeControlador) {
            return;
        }

        int nuevoSetPoint = view.getSetPointSeleccionado();
        if (nuevoSetPoint == setPoint && !registrarLog) {
            return;
        }

        setPoint = nuevoSetPoint;
        recalcularAperturaAutomatica();
        if (registrarLog) {
            log("set point configurado desde la vista: " + setPoint + "%");
        }
        renderizar();
    }

    private void alternarValvulaManual() {
        aperturaManualSeleccionada = aperturaManualSeleccionada.isAbierta()
                ? AperturaValvula.CERRADA
                : AperturaValvula.ABIERTA;
        aperturaActual = aperturaManualSeleccionada;
        log("Seleccion manual de valvula: " + aperturaManualSeleccionada.getEtiqueta());
        renderizar();
    }

    private void ejecutarTick() {
        tick++;

        if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA) {
            log("tick bloqueado por emergencia");
            renderizar();
            return;
        }

        if (modoAutomata.getEstado() == ModoEstado.AUTOMATICO && fallaAutomaticaActiva) {
            aperturaActual = AperturaValvula.ABIERTA;
        } else if (modoAutomata.getEstado() == ModoEstado.AUTOMATICO) {
            aperturaActual = decidirAperturaAutomatica(nivelAutomata.getPorcentaje(), setPoint);
        } else {
            aperturaActual = aperturaManualSeleccionada;
        }

        NivelEstado previo = nivelAutomata.getEstado();
        int consumo = consumoActivo ? CONSUMO_POR_TICK : 0;
        int cambioNeto = flujoEntrada(aperturaActual) - consumo;
        NivelEstado nuevo = nivelAutomata.aplicarCambio(cambioNeto);
        log("flujo neto " + cambioNeto + "% -> nivel " + nivelAutomata.getPorcentaje() + "% (" + nuevo.name() + ")");
        evaluarFallaAutomaticaPorNivel();
        if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA) {
            aperturaActual = AperturaValvula.CERRADA;
            renderizar();
            return;
        }
        evaluarSeguridadPorNivel(previo, nuevo);

        if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA) {
            aperturaActual = AperturaValvula.CERRADA;
            renderizar();
            return;
        }

        renderizar();
    }

    private AperturaValvula decidirAperturaAutomatica(int nivelPorcentaje, int setPointActual) {
        int limiteInferior = Math.max(5, setPointActual - BANDA_CONTROL_AUTOMATICO);

        if (nivelPorcentaje <= limiteInferior) {
            llenandoAutomatico = true;
        } else if (nivelPorcentaje >= setPointActual) {
            llenandoAutomatico = false;
        }

        if (!llenandoAutomatico) {
            return AperturaValvula.CERRADA;
        }

        return AperturaValvula.ABIERTA;
    }

    private int flujoEntrada(AperturaValvula apertura) {
        return apertura.isAbierta() ? 8 : 0;
    }

    private void evaluarSeguridadPorNivel(NivelEstado previo, NivelEstado actual) {
        if (nivelAutomata.getPorcentaje() >= 100) {
            activarEmergenciaPorAlturaMaxima();
        } else if (nivelAutomata.getPorcentaje() >= 90) {
            actualizarSeguridad(EventoSeguridad.eA, "ADVERTENCIA: NIVEL ALTO");
        } else if (seguridadAutomata.getEstado() == SeguridadEstado.ADVERTENCIA) {
            actualizarSeguridad(EventoSeguridad.reset, "OPERACION NORMAL");
        } else if (previo != NivelEstado.VACIO && actual == NivelEstado.VACIO) {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eV);
            logEvento("eV", estado.name());
            aperturaActual = AperturaValvula.CERRADA;
        } else if (previo != NivelEstado.DESBORDAMIENTO && actual == NivelEstado.DESBORDAMIENTO) {
            SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eD);
            logEvento("eD", estado.name());
            aperturaActual = AperturaValvula.CERRADA;
        }
    }

    private void evaluarFallaAutomaticaPorNivel() {
        if (!fallaAutomaticaActiva || nivelAutomata.getPorcentaje() < 100) {
            return;
        }

        activarEmergenciaPorAlturaMaxima();
        eventoSeguridadAutomaticoActivado = true;
        log("Evento automático de seguridad activado");
        if (eventoSeguridadAutomaticoActivado) {
            return;
        }

        SeguridadEstado estado = seguridadAutomata.dispararEvento(EventoSeguridad.eD);
        aperturaActual = AperturaValvula.CERRADA;
        llenandoAutomatico = false;
        fallaAutomaticaActiva = false;
        eventoSeguridadAutomaticoActivado = true;
        timer.stop();
        logEvento("Evento automático de seguridad activado", estado.name());
        log("alarma activada: nivel al 100%, válvula cerrada y llenado detenido");
    }

    private void activarEmergenciaPorAlturaMaxima() {
        if (seguridadAutomata.getEstado() == SeguridadEstado.EMERGENCIA) {
            return;
        }

        actualizarSeguridad(EventoSeguridad.eD, "EMERGENCIA: RIESGO DE DESBORDAMIENTO");
        aperturaActual = AperturaValvula.CERRADA;
        llenandoAutomatico = false;
        fallaAutomaticaActiva = false;
        emergenciaPorAlturaMaxima = true;
        timer.stop();
        log("alarma activada: nivel al 100%, válvula cerrada y llenado detenido");
    }

    private void actualizarSeguridad(EventoSeguridad evento, String descripcion) {
        SeguridadEstado anterior = seguridadAutomata.getEstado();
        SeguridadEstado actual = seguridadAutomata.dispararEvento(evento);
        if (actual != anterior) {
            log("seguridad: " + anterior.name() + " -> " + actual.name() + " (" + descripcion + ")");
        }
    }

    private void recalcularAperturaAutomatica() {
        if (seguridadAutomata.getEstado() != SeguridadEstado.EMERGENCIA
                && modoAutomata.getEstado() == ModoEstado.AUTOMATICO) {
            aperturaActual = decidirAperturaAutomatica(nivelAutomata.getPorcentaje(), setPoint);
        }
    }

    private void renderizar() {
        double nivelActualMetros = (nivelAutomata.getPorcentaje() / 100.0) * alturaMaximaMetros;
        EstadoUI dto = new EstadoUI(
                nivelAutomata.getEstado(),
                nivelAutomata.getPorcentaje(),
                modoAutomata.getEstado(),
                seguridadAutomata.getEstado(),
                aperturaActual,
                tick,
                timer.isRunning(),
                setPoint,
                consumoActivo,
                alturaMaximaMetros,
                nivelActualMetros,
                fallaAutomaticaActiva,
                eventoSeguridadAutomaticoActivado,
                emergenciaPorAlturaMaxima
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
