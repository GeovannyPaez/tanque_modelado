package org.example.view;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class ResponseChartPanel extends JPanel {
    private static final int MAX_SAMPLES = 80;
    private final List<Sample> samples = new ArrayList<Sample>();

    public ResponseChartPanel() {
        setPreferredSize(new Dimension(460, 190));
        setBackground(new Color(248, 250, 252));
    }

    public void addSample(long tiempo, int porcentaje) {
        samples.add(new Sample(tiempo, Math.max(0, Math.min(110, porcentaje))));
        while (samples.size() > MAX_SAMPLES) {
            samples.remove(0);
        }
        repaint();
    }

    public void clear() {
        samples.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int left = 58;
        int top = 20;
        int right = getWidth() - 22;
        int bottom = getHeight() - 44;
        int width = Math.max(1, right - left);
        int height = Math.max(1, bottom - top);

        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(left, top, width, height);
        g2.setColor(new Color(148, 163, 184));
        g2.drawRect(left, top, width, height);

        drawLevelAxis(g2, left, right, bottom, height);
        drawTimeAxis(g2, left, right, bottom, width);

        g2.setColor(new Color(15, 23, 42));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
        g2.drawString("Tiempo (ticks)", left + width / 2 - 34, getHeight() - 8);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Nivel (%)", -top - height / 2 - 24, 14);
        g2.rotate(Math.PI / 2);

        if (samples.size() < 2) {
            g2.setColor(new Color(100, 116, 139));
            g2.drawString("Esperando datos de simulacion...", left + 18, top + height / 2);
            g2.dispose();
            return;
        }

        Path2D path = new Path2D.Double();
        long firstTick = samples.get(0).tick;
        long lastTick = samples.get(samples.size() - 1).tick;
        long tickSpan = Math.max(1, lastTick - firstTick);

        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            double x = left + ((sample.tick - firstTick) / (double) tickSpan) * width;
            double y = bottom - (sample.porcentaje / 110.0) * height;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(14, 116, 144));
        g2.draw(path);

        Sample lastSample = samples.get(samples.size() - 1);
        int y = bottom - (int) Math.round((lastSample.porcentaje / 110.0) * height);
        g2.setColor(new Color(234, 88, 12));
        g2.fillOval(right - 5, y - 5, 10, 10);
        g2.dispose();
    }

    private void drawLevelAxis(Graphics2D g2, int left, int right, int bottom, int height) {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
        for (int pct = 0; pct <= 100; pct += 25) {
            int y = bottom - (int) Math.round((pct / 110.0) * height);
            g2.setColor(new Color(203, 213, 225));
            g2.drawLine(left, y, right, y);
            g2.setColor(new Color(71, 85, 105));
            g2.drawString(pct + "%", 18, y + 4);
        }
    }

    private void drawTimeAxis(Graphics2D g2, int left, int right, int bottom, int width) {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));

        long firstTick = samples.isEmpty() ? 0 : samples.get(0).tick;
        long lastTick = samples.isEmpty() ? 0 : samples.get(samples.size() - 1).tick;
        int divisions = 4;
        for (int i = 0; i <= divisions; i++) {
            int x = left + (int) Math.round((i / (double) divisions) * width);
            long tickValue = firstTick + Math.round(((lastTick - firstTick) * i) / (double) divisions);
            g2.setColor(new Color(203, 213, 225));
            g2.drawLine(x, bottom, x, bottom + 5);
            g2.setColor(new Color(71, 85, 105));
            String label = Long.toString(tickValue);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            int labelX = Math.max(left - 4, Math.min(right - labelWidth + 4, x - labelWidth / 2));
            g2.drawString(label, labelX, bottom + 20);
        }
    }

    private static class Sample {
        private final long tick;
        private final int porcentaje;

        private Sample(long tick, int porcentaje) {
            this.tick = tick;
            this.porcentaje = porcentaje;
        }
    }
}
