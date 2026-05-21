package org.example.view;

import org.example.model.NivelEstado;
import org.example.model.SeguridadEstado;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class TankPanel extends JPanel {
    private NivelEstado nivel = NivelEstado.MEDIO;
    private SeguridadEstado seguridad = SeguridadEstado.NORMAL;
    private double porcentajeVisual = NivelEstado.MEDIO.getPorcentaje();
    private double porcentajeObjetivo = NivelEstado.MEDIO.getPorcentaje();

    public TankPanel() {
        setPreferredSize(new Dimension(300, 360));
        setBackground(new Color(245, 247, 250));
        Timer animTimer = new Timer(40, e -> animarNivel());
        animTimer.start();
    }

    public void actualizar(NivelEstado nivel, SeguridadEstado seguridad) {
        this.nivel = nivel;
        this.seguridad = seguridad;
        this.porcentajeObjetivo = nivel.getPorcentaje();
    }

    private void animarNivel() {
        double delta = porcentajeObjetivo - porcentajeVisual;
        if (Math.abs(delta) < 0.15) {
            porcentajeVisual = porcentajeObjetivo;
        } else {
            porcentajeVisual += Math.signum(delta) * 0.7;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 90;
        int y = 30;
        int w = 120;
        int h = 280;

        g2.setColor(new Color(55, 65, 81));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        int pct = (int) Math.round(Math.min(porcentajeVisual, 110.0));
        int fillH = (int) Math.round((pct / 110.0) * h);
        int fillY = y + h - fillH;
        g2.setColor(colorAgua());
        g2.fillRoundRect(x + 2, fillY + 2, w - 3, Math.max(fillH - 3, 0), 10, 10);

        drawMarca(g2, x, y, w, h, 0, "0%");
        drawMarca(g2, x, y, w, h, 30, "30%");
        drawMarca(g2, x, y, w, h, 60, "60%");
        drawMarca(g2, x, y, w, h, 90, "90%");
        drawMarca(g2, x, y, w, h, 100, "100%");

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.setColor(new Color(31, 41, 55));
        g2.drawString("NIVEL: " + nivel.name(), 80, 335);
        g2.dispose();
    }

    private void drawMarca(Graphics2D g2, int x, int y, int w, int h, int porcentaje, String texto) {
        int py = y + h - (int) Math.round((porcentaje / 110.0) * h);
        g2.setColor(new Color(107, 114, 128));
        g2.drawLine(x + w + 5, py, x + w + 16, py);
        g2.drawString(texto, x + w + 20, py + 4);
    }

    private Color colorAgua() {
        if (seguridad == SeguridadEstado.EMERGENCIA || nivel == NivelEstado.DESBORDAMIENTO) {
            return new Color(220, 38, 38);
        }
        if (nivel == NivelEstado.BAJO || nivel == NivelEstado.ALTO) {
            return new Color(245, 158, 11);
        }
        return new Color(37, 99, 235);
    }
}
