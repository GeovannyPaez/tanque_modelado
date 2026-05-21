package org.example;

import org.example.controller.SimulacionController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulacionController controller = new SimulacionController();
            controller.start();
        });
    }
}
