package client.pc2_registro;

import javax.swing.*;
import java.awt.*;

public class VentanaPC2 extends JFrame {

    public VentanaPC2() {
        setTitle("PC2 - Mesa de Ayuda | Help Desk");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        // ── Panel principal ──────────────────────────────────────────────────
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Título ───────────────────────────────────────────────────────────
        JLabel titulo = new JLabel("SISTEMA DE SOPORTE TÉCNICO IT", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 200, 255));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel subtitulo = new JLabel("PC2 - Registro de Tickets", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(Color.LIGHT_GRAY);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 30, 30));
        headerPanel.add(titulo, BorderLayout.NORTH);
        headerPanel.add(subtitulo, BorderLayout.SOUTH);

        // ── Botones ──────────────────────────────────────────────────────────
        JButton btnRegistrar = new JButton("📋  Registrar Nuevo Ticket");
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegistrar.setBackground(new Color(0, 122, 204));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setPreferredSize(new Dimension(0, 50));

        JButton btnHistorial = new JButton("🔍  Consultar Historial");
        btnHistorial.setFont(new Font("Arial", Font.BOLD, 13));
        btnHistorial.setBackground(new Color(40, 167, 69));
        btnHistorial.setForeground(Color.WHITE);
        btnHistorial.setFocusPainted(false);
        btnHistorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHistorial.setPreferredSize(new Dimension(0, 50));

        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Arial", Font.PLAIN, 11));
        btnSalir.setBackground(new Color(60, 60, 60));
        btnSalir.setForeground(Color.LIGHT_GRAY);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ── Acciones ─────────────────────────────────────────────────────────
        btnRegistrar.addActionListener(e -> {
            new FormularioRegistro().setVisible(true);
        });

        btnHistorial.addActionListener(e -> {
            new VentanaHistorial().setVisible(true);
        });

        btnSalir.addActionListener(e -> System.exit(0));

        // ── Layout de botones ────────────────────────────────────────────────
        JPanel botonesPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        botonesPanel.setBackground(new Color(30, 30, 30));
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnHistorial);
        botonesPanel.add(btnSalir);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(botonesPanel, BorderLayout.CENTER);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPC2().setVisible(true);
        });
    }
}