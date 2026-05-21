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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final TankPanel tankPanel = new TankPanel();
    private final JLabel nivelLabel = new JLabel("Nivel: MEDIO");
    private final JLabel modoLabel = new JLabel("Modo: AUTOMATICO");
    private final JLabel seguridadLabel = new JLabel("Seguridad: NORMAL");
    private final JLabel valvulaLabel = new JLabel("Valvula: 0%");
    private final JLabel bannerLabel = new JLabel("OPERACION NORMAL", SwingConstants.CENTER);

    private final JRadioButton autoBtn = new JRadioButton("AUTOMATICO");
    private final JRadioButton manualBtn = new JRadioButton("MANUAL");
    private final JButton abrir33Btn = new JButton("Abrir 33%");
    private final JButton abrir66Btn = new JButton("Abrir 66%");
    private final JButton abrir100Btn = new JButton("Abrir 100%");
    private final JButton cerrarBtn = new JButton("Cerrar");
    private final JButton emergenciaBtn = new JButton("EMERGENCIA");
    private final JButton resetBtn = new JButton("RESET");

    private final JTextArea logArea = new JTextArea(9, 60);
    private final Map<String, JLabel> sensores = new LinkedHashMap<String, JLabel>();

    public MainFrame() {
        setTitle("Simulador de Control de Nivel de Tanque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildPanelSensores(), BorderLayout.WEST);
        add(buildPanelCentro(), BorderLayout.CENTER);
        add(buildPanelEstado(), BorderLayout.EAST);
        add(buildPanelInferior(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildPanelSensores() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setPreferredSize(new Dimension(220, 0));
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
        led.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        sensores.put(nombre, led);
        parent.add(led);
    }

    private JPanel buildPanelCentro() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(tankPanel, BorderLayout.CENTER);
        bannerLabel.setOpaque(true);
        bannerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        bannerLabel.setBackground(new Color(220, 252, 231));
        bannerLabel.setForeground(new Color(22, 101, 52));
        panel.add(bannerLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPanelEstado() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        nivelLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        modoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        seguridadLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        valvulaLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        panel.add(nivelLabel);
        panel.add(modoLabel);
        panel.add(seguridadLabel);
        panel.add(valvulaLabel);
        return panel;
    }

    private JPanel buildPanelInferior() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        JPanel controls = new JPanel(new GridLayout(2, 1, 6, 6));
        controls.setBorder(BorderFactory.createTitledBorder("Controles"));

        JPanel modoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modoPanel.add(new JLabel("Modo: "));
        ButtonGroup group = new ButtonGroup();
        group.add(autoBtn);
        group.add(manualBtn);
        autoBtn.setSelected(true);
        modoPanel.add(autoBtn);
        modoPanel.add(manualBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(abrir33Btn);
        actionPanel.add(abrir66Btn);
        actionPanel.add(abrir100Btn);
        actionPanel.add(cerrarBtn);
        emergenciaBtn.setBackground(new Color(220, 38, 38));
        emergenciaBtn.setForeground(Color.WHITE);
        actionPanel.add(emergenciaBtn);
        resetBtn.setBackground(new Color(22, 163, 74));
        resetBtn.setForeground(Color.WHITE);
        actionPanel.add(resetBtn);

        controls.add(modoPanel);
        controls.add(actionPanel);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        root.add(controls, BorderLayout.NORTH);
        root.add(logPanel, BorderLayout.CENTER);
        return root;
    }

    public void render(EstadoUI estado) {
        NivelEstado nivel = estado.getNivel();
        ModoEstado modo = estado.getModo();
        SeguridadEstado seguridad = estado.getSeguridad();
        AperturaValvula valvula = estado.getValvula();

        nivelLabel.setText("Nivel: " + nivel.name());
        modoLabel.setText("Modo: " + modo.name());
        seguridadLabel.setText("Seguridad: " + seguridad.name());
        valvulaLabel.setText("Valvula: " + valvula.getEtiqueta());
        tankPanel.actualizar(nivel, seguridad);

        updateSensores(nivel);
        boolean manualEnabled = modo == ModoEstado.MANUAL && seguridad == SeguridadEstado.NORMAL;
        abrir33Btn.setEnabled(manualEnabled);
        abrir66Btn.setEnabled(manualEnabled);
        abrir100Btn.setEnabled(manualEnabled);
        cerrarBtn.setEnabled(manualEnabled);
        autoBtn.setEnabled(seguridad == SeguridadEstado.NORMAL);
        manualBtn.setEnabled(seguridad == SeguridadEstado.NORMAL);
        resetBtn.setVisible(seguridad == SeguridadEstado.EMERGENCIA);

        if (seguridad == SeguridadEstado.EMERGENCIA || nivel == NivelEstado.DESBORDAMIENTO) {
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

    private void updateSensores(NivelEstado nivel) {
        int pct = nivel.getPorcentaje();
        setSensor("Vacio (0%)", nivel == NivelEstado.VACIO);
        setSensor("30%", pct >= 30);
        setSensor("60%", pct >= 60);
        setSensor("90%", pct >= 90);
        setSensor("100%", pct >= 100);
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

    public JRadioButton getAutoBtn() {
        return autoBtn;
    }

    public JRadioButton getManualBtn() {
        return manualBtn;
    }

    public JButton getAbrir33Btn() {
        return abrir33Btn;
    }

    public JButton getAbrir66Btn() {
        return abrir66Btn;
    }

    public JButton getAbrir100Btn() {
        return abrir100Btn;
    }

    public JButton getCerrarBtn() {
        return cerrarBtn;
    }

    public JButton getEmergenciaBtn() {
        return emergenciaBtn;
    }

    public JButton getResetBtn() {
        return resetBtn;
    }
}
