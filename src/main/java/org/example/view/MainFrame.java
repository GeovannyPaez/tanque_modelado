package org.example.view;

import org.example.model.AperturaValvula;
import org.example.model.ModoEstado;
import org.example.model.NivelEstado;
import org.example.model.SeguridadEstado;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final TankPanel tankPanel = new TankPanel();
    private final ResponseChartPanel chartPanel = new ResponseChartPanel();
    private final JLabel nivelLabel = new JLabel("Nivel: MEDIO");
    private final JLabel modoLabel = new JLabel("Modo: AUTOMATICO");
    private final JLabel seguridadLabel = new JLabel("Seguridad: NORMAL");
    private final JLabel valvulaLabel = new JLabel("Valvula control: Cerrada");
    private final JLabel valvulaSeguridadLabel = new JLabel("Valvula seguridad: Abierta");
    private final JLabel setPointLabel = new JLabel("Set Point: 60%");
    private final JLabel consumoLabel = new JLabel("Consumo: ACTIVO");
    private final JLabel alturaTanqueLabel = new JLabel("Altura tanque: 5.00 m");
    private final JLabel nivelMetrosLabel = new JLabel("Nivel actual: 3.00 m");
    private final JLabel tiempoLabel = new JLabel("Tiempo: 0 ticks");
    private final JLabel simulacionLabel = new JLabel("Simulacion: EN EJECUCION");
    private final JLabel bannerLabel = new JLabel("OPERACION NORMAL", SwingConstants.CENTER);

    private final JRadioButton autoBtn = new JRadioButton("AUTOMATICO");
    private final JRadioButton manualBtn = new JRadioButton("MANUAL");
    private final JSpinner setPointSpinner = new JSpinner(new SpinnerNumberModel(60, 0, 100, 5));
    private final JSpinner alturaTanqueSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.1, 1000.0, 0.1));
    private final JButton aplicarSetPointBtn = new JButton("Aplicar SP");
    private final JButton iniciarBtn = new JButton("Iniciar");
    private final JButton pausarBtn = new JButton("Pausar");
    private final JButton reiniciarBtn = new JButton("Reiniciar");
    private final JButton consumoBtn = new JButton("Desactivar consumo");
    private final JButton alternarValvulaBtn = new JButton("Abrir valvula");
    private final JButton simularFallaAutomaticaBtn = new JButton("Simular falla automática");
    private final JButton emergenciaBtn = new JButton("EMERGENCIA");
    private final JButton resetBtn = new JButton("RESET");

    private final JTextArea logArea = new JTextArea(8, 60);
    private final Map<String, JLabel> sensores = new LinkedHashMap<String, JLabel>();
    private long ultimoTickGraficado = -1;
    private boolean avisoEmergenciaMostrado = false;

    public MainFrame() {
        setTitle("Simulador de Control de Nivel de Tanque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildPanelSensores(), BorderLayout.WEST);
        add(buildPanelCentro(), BorderLayout.CENTER);
        add(buildPanelEstado(), BorderLayout.EAST);
        add(buildPanelInferior(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildPanelSensores() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.setPreferredSize(new Dimension(135, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Sensores de Nivel"));
        crearSensor(panel, "Vacio (0%)");
        crearSensor(panel, "30%");
        crearSensor(panel, "60%");
        crearSensor(panel, "90%");
        crearSensor(panel, "100%");
        return panel;
    }

    private void crearSensor(JPanel parent, String nombre) {
        JLabel led = new JLabel(sensorText(nombre, false));
        led.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        sensores.put(nombre, led);
        parent.add(led);
    }

    private JPanel buildPanelCentro() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(tankPanel, BorderLayout.CENTER);

        JPanel processControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        processControls.add(consumoBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        bottomPanel.add(processControls, BorderLayout.NORTH);
        bannerLabel.setOpaque(true);
        bannerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        bannerLabel.setBackground(new Color(220, 252, 231));
        bannerLabel.setForeground(new Color(22, 101, 52));
        bottomPanel.add(bannerLabel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPanelEstado() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Indicadores de Estado"));
        JLabel[] labels = {nivelLabel, nivelMetrosLabel, alturaTanqueLabel, modoLabel, seguridadLabel, valvulaLabel, valvulaSeguridadLabel, setPointLabel, consumoLabel, tiempoLabel, simulacionLabel};
        for (JLabel label : labels) {
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            panel.add(label);
        }
        return panel;
    }

    private JPanel buildPanelInferior() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        JPanel controls = new JPanel(new GridLayout(1, 3, 8, 8));

        setPointSpinner.setPreferredSize(new Dimension(70, 24));
        alturaTanqueSpinner.setPreferredSize(new Dimension(80, 24));

        JPanel automaticoPanel = new JPanel(new BorderLayout(4, 4));
        automaticoPanel.setBorder(BorderFactory.createTitledBorder("Panel de Control Automatico"));
        ButtonGroup group = new ButtonGroup();
        group.add(autoBtn);
        group.add(manualBtn);
        autoBtn.setSelected(true);

        JPanel automaticoRows = new JPanel(new GridLayout(0, 1, 2, 2));
        JPanel modoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        modoRow.add(autoBtn);
        modoRow.add(manualBtn);

        JPanel setPointRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setPointRow.add(new JLabel("Set Point (%):"));
        setPointRow.add(setPointSpinner);
        setPointRow.add(aplicarSetPointBtn);

        JPanel alturaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        alturaRow.add(new JLabel("Altura tanque (m):"));
        alturaRow.add(alturaTanqueSpinner);

        JPanel fallaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        fallaRow.add(simularFallaAutomaticaBtn);

        automaticoRows.add(modoRow);
        automaticoRows.add(setPointRow);
        automaticoRows.add(alturaRow);
        automaticoRows.add(fallaRow);
        automaticoPanel.add(automaticoRows, BorderLayout.CENTER);

        JPanel manualPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        manualPanel.setBorder(BorderFactory.createTitledBorder("Panel de Control Manual"));
        manualPanel.add(alternarValvulaBtn);

        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operationPanel.setBorder(BorderFactory.createTitledBorder("Botones de Operacion"));
        operationPanel.add(iniciarBtn);
        operationPanel.add(pausarBtn);
        operationPanel.add(reiniciarBtn);
        emergenciaBtn.setBackground(new Color(220, 38, 38));
        emergenciaBtn.setForeground(Color.WHITE);
        operationPanel.add(emergenciaBtn);
        resetBtn.setBackground(new Color(22, 163, 74));
        resetBtn.setForeground(Color.WHITE);
        operationPanel.add(resetBtn);

        controls.add(automaticoPanel);
        controls.add(manualPanel);
        controls.add(operationPanel);

        JPanel dataPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Grafica de Respuesta"));
        dataPanel.add(chartPanel);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        dataPanel.add(logPanel);

        root.add(controls, BorderLayout.NORTH);
        root.add(dataPanel, BorderLayout.CENTER);
        return root;
    }

    public void render(EstadoUI estado) {
        NivelEstado nivel = estado.getNivel();
        ModoEstado modo = estado.getModo();
        SeguridadEstado seguridad = estado.getSeguridad();
        AperturaValvula valvula = estado.getValvula();
        AperturaValvula valvulaSeguridad = estado.getValvulaSeguridad();

        nivelLabel.setText("Nivel: " + nivel.name() + " (" + estado.getNivelPorcentaje() + "%)");
        nivelMetrosLabel.setText(String.format("Nivel actual: %.2f m", estado.getNivelActualMetros()));
        alturaTanqueLabel.setText(String.format("Altura tanque: %.2f m", estado.getAlturaMaximaMetros()));
        modoLabel.setText("Modo: " + modo.name());
        seguridadLabel.setText("Seguridad: " + seguridad.name());
        valvulaLabel.setText("Valvula control: " + valvula.getEtiqueta());
        valvulaSeguridadLabel.setText("Valvula seguridad: " + valvulaSeguridad.getEtiqueta());
        setPointLabel.setText("Set Point: " + estado.getSetPoint() + "%");
        consumoLabel.setText("Consumo: " + (estado.isConsumoActivo() ? "ACTIVO" : "INACTIVO"));
        tiempoLabel.setText("Tiempo: " + estado.getTick() + " ticks");
        simulacionLabel.setText("Simulacion: " + (estado.isSimulando() ? "EN EJECUCION" : "PAUSADA"));
        tankPanel.actualizar(nivel, estado.getNivelPorcentaje(), estado.getNivelActualMetros(), estado.getAlturaMaximaMetros(), seguridad, valvula, valvulaSeguridad, estado.getSetPoint());

        if (estado.getTick() != ultimoTickGraficado) {
            chartPanel.addSample(estado.getTick(), estado.getNivelPorcentaje());
            ultimoTickGraficado = estado.getTick();
        }

        updateSensores(nivel, estado.getNivelPorcentaje());
        boolean emergencia = seguridad == SeguridadEstado.EMERGENCIA;
        boolean operacionPermitida = !emergencia;
        boolean manualEnabled = modo == ModoEstado.MANUAL && operacionPermitida && !estado.isFallaAutomaticaActiva();
        alternarValvulaBtn.setEnabled(manualEnabled);
        alternarValvulaBtn.setText(valvula.isAbierta() ? "Cerrar valvula" : "Abrir valvula");
        autoBtn.setEnabled(operacionPermitida && !estado.isFallaAutomaticaActiva());
        manualBtn.setEnabled(operacionPermitida && !estado.isFallaAutomaticaActiva());
        setPointSpinner.setEnabled(operacionPermitida);
        alturaTanqueSpinner.setEnabled(operacionPermitida);
        aplicarSetPointBtn.setEnabled(operacionPermitida);
        simularFallaAutomaticaBtn.setEnabled(modo == ModoEstado.AUTOMATICO
                && operacionPermitida
                && !estado.isFallaAutomaticaActiva());
        consumoBtn.setText(estado.isConsumoActivo() ? "Desactivar consumo" : "Activar consumo");
        consumoBtn.setEnabled(operacionPermitida);
        iniciarBtn.setEnabled(operacionPermitida && !estado.isSimulando());
        pausarBtn.setEnabled(operacionPermitida && estado.isSimulando());
        emergenciaBtn.setEnabled(operacionPermitida);
        resetBtn.setVisible(emergencia);

        if (actualizarBarraSeguridad(estado, seguridad)) {
            return;
        }

        if (estado.isEventoSeguridadAutomaticoActivado()) {
            bannerLabel.setText("Evento automático de seguridad activado");
            bannerLabel.setBackground(new Color(254, 202, 202));
            bannerLabel.setForeground(new Color(153, 27, 27));
        } else if (estado.isFallaAutomaticaActiva()) {
            bannerLabel.setText("FALLA AUTOMATICA: VALVULA ATASCADA ABIERTA");
            bannerLabel.setBackground(new Color(254, 243, 199));
            bannerLabel.setForeground(new Color(146, 64, 14));
        } else if (seguridad == SeguridadEstado.EMERGENCIA || nivel == NivelEstado.DESBORDAMIENTO) {
            bannerLabel.setText("EMERGENCIA ACTIVA");
            bannerLabel.setBackground(new Color(254, 202, 202));
            bannerLabel.setForeground(new Color(153, 27, 27));
        } else if (nivel == NivelEstado.BAJO || nivel == NivelEstado.ALTO) {
            bannerLabel.setText("ALERTA DE NIVEL");
            bannerLabel.setBackground(new Color(254, 243, 199));
            bannerLabel.setForeground(new Color(146, 64, 14));
        } else {
            bannerLabel.setText("OPERACION NORMAL");
            bannerLabel.setBackground(new Color(220, 252, 231));
            bannerLabel.setForeground(new Color(22, 101, 52));
        }
    }

    private void updateSensores(NivelEstado nivel, int pct) {
        setSensor("Vacio (0%)", nivel == NivelEstado.VACIO);
        setSensor("30%", pct >= 30);
        setSensor("60%", pct >= 60);
        setSensor("90%", pct >= 90);
        setSensor("100%", pct >= 100);
    }

    private boolean actualizarBarraSeguridad(EstadoUI estado, SeguridadEstado seguridad) {
        if (seguridad == SeguridadEstado.EMERGENCIA) {
            bannerLabel.setText("EMERGENCIA: RIESGO DE DESBORDAMIENTO");
            bannerLabel.setBackground(new Color(254, 202, 202));
            bannerLabel.setForeground(new Color(153, 27, 27));
            mostrarAvisoEmergenciaSiAplica(estado);
        } else if (seguridad == SeguridadEstado.ADVERTENCIA) {
            avisoEmergenciaMostrado = false;
            bannerLabel.setText("ADVERTENCIA: NIVEL ALTO");
            bannerLabel.setBackground(new Color(254, 243, 199));
            bannerLabel.setForeground(new Color(146, 64, 14));
        } else {
            avisoEmergenciaMostrado = false;
            bannerLabel.setText("OPERACIÓN NORMAL");
            bannerLabel.setBackground(new Color(220, 252, 231));
            bannerLabel.setForeground(new Color(22, 101, 52));
        }
        return true;
    }

    private void mostrarAvisoEmergenciaSiAplica(EstadoUI estado) {
        if (!estado.isEmergenciaPorAlturaMaxima() || avisoEmergenciaMostrado) {
            return;
        }

        avisoEmergenciaMostrado = true;
        JOptionPane.showMessageDialog(
                this,
                "El nivel del tanque alcanzó la altura máxima permitida. Se cerró la válvula de seguridad independiente.",
                "Emergencia de seguridad",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void setSensor(String key, boolean on) {
        JLabel label = sensores.get(key);
        if (label != null) {
            label.setText(sensorText(key, on));
        }
    }

    private String sensorText(String nombre, boolean on) {
        return (on ? "[ON] " : "[OFF] ") + nombre;
    }

    public void addLog(String line) {
        logArea.insert(line + "\n", 0);
    }

    public void limpiarGrafica() {
        ultimoTickGraficado = -1;
        chartPanel.clear();
    }

    public JRadioButton getAutoBtn() {
        return autoBtn;
    }

    public JRadioButton getManualBtn() {
        return manualBtn;
    }

    public JSpinner getSetPointSpinner() {
        return setPointSpinner;
    }

    public JSpinner getAlturaTanqueSpinner() {
        return alturaTanqueSpinner;
    }

    public JButton getAplicarSetPointBtn() {
        return aplicarSetPointBtn;
    }

    public void addSetPointChangeListener(ChangeListener listener) {
        setPointSpinner.addChangeListener(listener);
    }

    public void addAlturaTanqueChangeListener(ChangeListener listener) {
        alturaTanqueSpinner.addChangeListener(listener);
    }

    public int getSetPointSeleccionado() {
        try {
            setPointSpinner.commitEdit();
        } catch (ParseException ignored) {
            setPointSpinner.setValue(Integer.valueOf(60));
        }
        return ((Number) setPointSpinner.getValue()).intValue();
    }

    public double getAlturaTanqueSeleccionada() {
        try {
            alturaTanqueSpinner.commitEdit();
        } catch (ParseException ignored) {
            alturaTanqueSpinner.setValue(Double.valueOf(5.0));
        }
        double altura = ((Number) alturaTanqueSpinner.getValue()).doubleValue();
        if (altura <= 0.0) {
            alturaTanqueSpinner.setValue(Double.valueOf(5.0));
            return 5.0;
        }
        return altura;
    }

    public JButton getIniciarBtn() {
        return iniciarBtn;
    }

    public JButton getPausarBtn() {
        return pausarBtn;
    }

    public JButton getReiniciarBtn() {
        return reiniciarBtn;
    }

    public JButton getConsumoBtn() {
        return consumoBtn;
    }

    public JButton getAlternarValvulaBtn() {
        return alternarValvulaBtn;
    }

    public JButton getSimularFallaAutomaticaBtn() {
        return simularFallaAutomaticaBtn;
    }

    public JButton getEmergenciaBtn() {
        return emergenciaBtn;
    }

    public JButton getResetBtn() {
        return resetBtn;
    }
}
