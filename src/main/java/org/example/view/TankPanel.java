package org.example.view;

import org.example.model.NivelEstado;
import org.example.model.AperturaValvula;
import org.example.model.SeguridadEstado;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class TankPanel extends JPanel {
    private NivelEstado nivel = NivelEstado.MEDIO;
    private int nivelPorcentaje = NivelEstado.MEDIO.getPorcentaje();
    private double nivelActualMetros = 3.0;
    private double alturaMaximaMetros = 5.0;
    private SeguridadEstado seguridad = SeguridadEstado.NORMAL;
    private AperturaValvula valvula = AperturaValvula.CERRADA;
    private int setPoint = NivelEstado.MEDIO.getPorcentaje();
    private double porcentajeVisual = NivelEstado.MEDIO.getPorcentaje();
    private double porcentajeObjetivo = NivelEstado.MEDIO.getPorcentaje();

    public TankPanel() {
        setPreferredSize(new Dimension(500, 360));
        setBackground(new Color(245, 247, 250));
        Timer animTimer = new Timer(40, e -> animarNivel());
        animTimer.start();
    }

    public void actualizar(NivelEstado nivel, int nivelPorcentaje, double nivelActualMetros, double alturaMaximaMetros, SeguridadEstado seguridad, AperturaValvula valvula, int setPoint) {
        this.nivel = nivel;
        this.nivelPorcentaje = nivelPorcentaje;
        this.nivelActualMetros = nivelActualMetros;
        this.alturaMaximaMetros = alturaMaximaMetros;
        this.seguridad = seguridad;
        this.valvula = valvula;
        this.setPoint = setPoint;
        this.porcentajeObjetivo = nivelPorcentaje;
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

        int tankX = Math.max(130, (getWidth() / 2) - 185);
        int tankY = 70;
        int tankW = 110;
        int tankH = 220;

        drawAutoLine(g2, tankX, tankY, tankW);
        drawSafetyLoop(g2, tankX, tankY, tankW, tankH);
        drawInlet(g2, tankX, tankY);
        drawOutlet(g2, tankX, tankY, tankW, tankH);
        drawTank(g2, tankX, tankY, tankW, tankH);
        drawLevelTransmitter(g2, tankX, tankY, tankW, tankH);
        drawController(g2, tankX, tankY, tankW);
        drawNumericLevel(g2, tankX, tankY, tankW);
        drawStatusCard(g2, tankX, tankY);
        g2.dispose();
    }

    private void drawAutoLine(Graphics2D g2, int tankX, int tankY, int tankW) {
        int pipeY = tankY + 34;
        int valveX = tankX - 44;
        int controllerX = tankX + tankW + 162;
        int controllerY = tankY + 128;
        int signalY = tankY - 22;

        g2.setColor(new Color(75, 85, 99));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{5f, 5f}, 0f));
        g2.drawLine(valveX, pipeY - 18, valveX, signalY);
        g2.drawLine(valveX, signalY, controllerX, signalY);
        g2.drawLine(controllerX, signalY, controllerX, controllerY - 22);
        drawTextBadge(g2, "Lazo de Control", tankX + 8, signalY - 20, new Color(245, 247, 250), new Color(31, 41, 55));
    }

    private void drawSafetyLoop(Graphics2D g2, int tankX, int tankY, int tankW, int tankH) {
        Color loopColor = colorLazoSeguridad();
        Color fillColor = seguridad == SeguridadEstado.EMERGENCIA
                ? new Color(254, 202, 202)
                : seguridad == SeguridadEstado.ADVERTENCIA ? new Color(254, 243, 199) : new Color(243, 244, 246);

        int pipeY = tankY + 34;
        int valveX = tankX - 44;
        int sensorX = tankX + tankW + 52;
        int sensorY = tankY + 34;
        int sisX = tankX + tankW + 212;
        int sisY = tankY + 4;
        int safetyLaneY = tankY - 2;

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(loopColor);
        g2.drawLine(tankX + tankW + 2, tankY + 22, sensorX - 22, sensorY);

        g2.setColor(fillColor);
        g2.fillOval(sensorX - 24, sensorY - 24, 48, 48);
        g2.setColor(loopColor);
        g2.drawOval(sensorX - 24, sensorY - 24, 48, 48);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        drawCenteredString(g2, "LSHH", sensorX, sensorY - 3);
        drawCenteredString(g2, "100 %", sensorX, sensorY + 12);

        g2.setColor(fillColor);
        g2.fillRoundRect(sisX, sisY, 78, 38, 10, 10);
        g2.setColor(loopColor);
        g2.drawRoundRect(sisX, sisY, 78, 38, 10, 10);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString("SIS/ESD", sisX + 13, sisY + 24);

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{6f, 4f}, 0f));
        drawArrow(g2, sensorX + 24, sensorY, sisX, sensorY);
        drawElbowArrow(g2, sisX + 39, sisY, sisX + 39, safetyLaneY, valveX - 20, safetyLaneY);
        drawArrow(g2, valveX - 20, safetyLaneY, valveX, pipeY - 18);

        drawTextBadge(g2, "Lazo de Seguridad", sisX - 8, sisY - 22, new Color(245, 247, 250), loopColor);
    }

    private void drawInlet(Graphics2D g2, int tankX, int tankY) {
        int pipeY = tankY + 34;
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(125, 211, 252));
        g2.drawLine(tankX - 92, pipeY, tankX, pipeY);

        boolean abierta = valvula.isAbierta() && seguridad == SeguridadEstado.NORMAL;
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(abierta ? new Color(57, 255, 20) : new Color(107, 114, 128));
        Polygon left = new Polygon(new int[]{tankX - 44, tankX - 62, tankX - 62}, new int[]{pipeY, pipeY - 12, pipeY + 12}, 3);
        Polygon right = new Polygon(new int[]{tankX - 44, tankX - 26, tankX - 26}, new int[]{pipeY, pipeY - 12, pipeY + 12}, 3);
        g2.fillPolygon(left);
        g2.fillPolygon(right);
        g2.drawLine(tankX - 44, pipeY - 18, tankX - 44, pipeY - 6);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        g2.setColor(new Color(31, 41, 55));
        drawTextBadge(g2, abierta ? "Valvula Abierta" : "Valvula Cerrada", tankX - 86, pipeY + 34, new Color(245, 247, 250), new Color(31, 41, 55));
    }

    private void drawOutlet(Graphics2D g2, int tankX, int tankY, int tankW, int tankH) {
        int y = tankY + tankH + 26;
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(120, 113, 108));
        g2.drawLine(tankX + tankW / 2, tankY + tankH, tankX + tankW / 2, y);
        g2.drawLine(tankX + tankW / 2, y, tankX + tankW + 58, y);
        g2.drawLine(tankX + tankW + 58, y, tankX + tankW + 58, y + 16);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
        g2.setColor(new Color(100, 116, 139));
        drawTextBadge(g2, "Salida / consumo", tankX + tankW + 66, y + 20, new Color(245, 247, 250), new Color(100, 116, 139));
    }

    private void drawTank(Graphics2D g2, int x, int y, int w, int h) {
        RoundRectangle2D body = new RoundRectangle2D.Double(x, y, w, h, 28, 28);
        g2.setClip(body);

        g2.setColor(new Color(140, 140, 138));
        g2.fill(body);

        int pct = (int) Math.round(Math.min(porcentajeVisual, 110.0));
        int fillH = (int) Math.round((pct / 110.0) * h);
        int fillY = y + h - fillH;
        g2.setColor(colorAgua());
        g2.fillRect(x, fillY, w, Math.max(fillH, 0));

        g2.setClip(null);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(107, 114, 128));
        g2.draw(body);
    }

    private void drawLevelTransmitter(Graphics2D g2, int tankX, int tankY, int tankW, int tankH) {
        int x = tankX + tankW + 58;
        int top = tankY + 18;
        int bottom = tankY + tankH - 28;
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(75, 85, 99));
        g2.drawLine(tankX + tankW + 14, top, x, top);
        g2.drawLine(x, top, x, bottom);
        g2.drawLine(tankX + tankW + 14, bottom, x, bottom);

        int ly = tankY + 128;
        g2.setColor(new Color(125, 211, 252));
        g2.fillOval(x - 21, ly - 21, 42, 42);
        g2.setColor(new Color(14, 116, 144));
        g2.drawOval(x - 21, ly - 21, 42, 42);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString("LT", x - 8, ly + 4);
    }

    private void drawController(Graphics2D g2, int tankX, int tankY, int tankW) {
        int ltX = tankX + tankW + 58;
        int lcX = tankX + tankW + 162;
        int y = tankY + 128;
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f, 4f}, 0f));
        g2.setColor(new Color(55, 65, 81));
        g2.drawLine(ltX + 22, y, lcX - 20, y);
        g2.setStroke(new BasicStroke(1.5f));
        Polygon arrow = new Polygon(new int[]{lcX - 20, lcX - 28, lcX - 28}, new int[]{y, y - 5, y + 5}, 3);
        g2.fillPolygon(arrow);

        Color controllerColor = seguridad == SeguridadEstado.EMERGENCIA ? new Color(254, 202, 202) : new Color(187, 247, 208);
        g2.setColor(controllerColor);
        g2.fillOval(lcX - 22, y - 22, 44, 44);
        g2.setColor(new Color(22, 101, 52));
        g2.drawOval(lcX - 22, y - 22, 44, 44);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString("LC", lcX - 8, y + 4);

        g2.setColor(new Color(186, 230, 253));
        g2.fillRect(lcX + 48, y - 14, 98, 28);
        g2.setColor(new Color(15, 23, 42));
        g2.drawRect(lcX + 48, y - 14, 98, 28);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        g2.drawString("SP = " + setPoint + "%", lcX + 62, y + 4);
    }

    private void drawNumericLevel(Graphics2D g2, int tankX, int tankY, int tankW) {
        int x = tankX + tankW + 84;
        int y = tankY + 72;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        g2.setColor(new Color(31, 41, 55));
        g2.drawString("NIVEL NUMERICO", x, y);
        g2.setColor(new Color(186, 230, 253));
        g2.fillRect(x + 8, y + 12, 76, 26);
        g2.setColor(new Color(14, 116, 144));
        g2.drawRect(x + 8, y + 12, 76, 26);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString(Math.round(porcentajeVisual) + " %", x + 25, y + 30);
        g2.setColor(new Color(31, 41, 55));
        g2.drawString(String.format("%.2f m", nivelActualMetros), x + 22, y + 56);
    }

    private void drawStatusCard(Graphics2D g2, int tankX, int tankY) {
        int x = 12;
        int y = 12;
        g2.setColor(new Color(255, 255, 255));
        g2.fillRoundRect(x, y, 160, 46, 8, 8);
        g2.setColor(new Color(203, 213, 225));
        g2.drawRoundRect(x, y, 160, 46, 8, 8);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.setColor(new Color(31, 41, 55));
        g2.drawString("Nivel: " + nivel.name() + " (" + nivelPorcentaje + "%)", x + 10, y + 18);
        g2.drawString(String.format("Altura: %.2f / %.2f m", nivelActualMetros, alturaMaximaMetros), x + 10, y + 36);
    }

    private void drawTextBadge(Graphics2D g2, String text, int x, int baselineY, Color background, Color foreground) {
        Font font = g2.getFont();
        int width = g2.getFontMetrics(font).stringWidth(text);
        int height = g2.getFontMetrics(font).getHeight();
        int topY = baselineY - height + 3;

        g2.setColor(background);
        g2.fillRoundRect(x - 4, topY, width + 8, height, 6, 6);
        g2.setColor(foreground);
        g2.drawString(text, x, baselineY);
    }

    private void drawCenteredString(Graphics2D g2, String text, int centerX, int baselineY) {
        int width = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, centerX - (width / 2), baselineY);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowLength = 10;
        int arrowWidth = 5;
        int xA = (int) Math.round(x2 - arrowLength * Math.cos(angle) + arrowWidth * Math.sin(angle));
        int yA = (int) Math.round(y2 - arrowLength * Math.sin(angle) - arrowWidth * Math.cos(angle));
        int xB = (int) Math.round(x2 - arrowLength * Math.cos(angle) - arrowWidth * Math.sin(angle));
        int yB = (int) Math.round(y2 - arrowLength * Math.sin(angle) + arrowWidth * Math.cos(angle));
        g2.fillPolygon(new int[]{x2, xA, xB}, new int[]{y2, yA, yB}, 3);
    }

    private void drawElbowArrow(Graphics2D g2, int x1, int y1, int x2, int y2, int x3, int y3) {
        g2.drawLine(x1, y1, x2, y2);
        drawArrow(g2, x2, y2, x3, y3);
    }

    private Color colorLazoSeguridad() {
        if (seguridad == SeguridadEstado.EMERGENCIA || nivel == NivelEstado.DESBORDAMIENTO) {
            return new Color(220, 38, 38);
        }
        if (seguridad == SeguridadEstado.ADVERTENCIA || nivelPorcentaje >= 90 || nivel == NivelEstado.ALTO) {
            return new Color(217, 119, 6);
        }
        return new Color(75, 85, 99);
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
