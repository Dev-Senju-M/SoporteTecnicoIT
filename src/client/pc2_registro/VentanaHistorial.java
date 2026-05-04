package client.pc2_registro;

import shared.models.Ticket;
import shared.structures.HashMapUsuarios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedList;

public class VentanaHistorial extends JFrame {

    private static HashMapUsuarios historial = new HashMapUsuarios();

    public VentanaHistorial() {
        setTitle("Consulta de Historial");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Búsqueda por DPI ─────────────────────────────────────────────────
        JPanel busquedaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busquedaPanel.setBackground(new Color(30, 30, 30));

        JLabel lblDpi = new JLabel("Ingrese DPI:");
        lblDpi.setForeground(Color.LIGHT_GRAY);
        lblDpi.setFont(new Font("Arial", Font.PLAIN, 13));

        JTextField txtDpi = new JTextField(15);
        txtDpi.setBackground(new Color(60, 60, 60));
        txtDpi.setForeground(Color.WHITE);
        txtDpi.setCaretColor(Color.WHITE);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(0, 122, 204));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        busquedaPanel.add(lblDpi);
        busquedaPanel.add(txtDpi);
        busquedaPanel.add(btnBuscar);

        // ── Tabla de resultados ──────────────────────────────────────────────
        String[] columnas = {"#", "Tipo", "Motivo", "Duración (min)", "Técnico", "Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = new JTable(modelo);
        tabla.setBackground(new Color(45, 45, 45));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(70, 70, 70));
        tabla.getTableHeader().setBackground(new Color(0, 122, 204));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setRowHeight(25);
        tabla.setSelectionBackground(new Color(0, 90, 160));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        // ── Acción buscar ────────────────────────────────────────────────────
        btnBuscar.addActionListener(e -> {
            String dpi = txtDpi.getText().trim();
            modelo.setRowCount(0); // Limpiar tabla

            if (dpi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un DPI.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LinkedList<Ticket> tickets = historial.get(dpi);

            if (tickets == null || tickets.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró historial para el DPI: " + dpi,
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int contador = 1;
            for (Ticket t : tickets) {
                modelo.addRow(new Object[]{
                        contador++,
                        t.getTipo(),
                        t.getMotivo(),
                        String.format("%.2f", t.getDuracionAtencionMinutos()),
                        t.getUsuarioAtendio() != null ? t.getUsuarioAtendio() : "Pendiente",
                        t.getFechaHoraAtencion()
                });
            }
        });

        panel.add(busquedaPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        add(panel);
    }

    public static void agregarAlHistorial(String dpi, Ticket ticket) {
        historial.agregarTicket(dpi, ticket);
    }
}