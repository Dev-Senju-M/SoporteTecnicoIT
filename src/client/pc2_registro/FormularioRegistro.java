package client.pc2_registro;

import shared.models.Ticket;
import shared.utils.Clasificador;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FormularioRegistro extends JFrame {

    private static final String HOST   = "localhost";
    private static final int    PUERTO = 5000;

    public FormularioRegistro() {
        setTitle("Registrar Ticket");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Campos ───────────────────────────────────────────────────────────
        JLabel lblDpi = crearLabel("DPI:");
        JTextField txtDpi = new JTextField();
        estilizarCampo(txtDpi);

        JLabel lblTipo = crearLabel("Tipo de problema:");
        String[] tipos = {"Hardware", "Software", "Red/Internet", "Servidor", "Accesos/Permisos", "Otro"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        cmbTipo.setBackground(new Color(60, 60, 60));
        cmbTipo.setForeground(Color.WHITE);

        JLabel lblMotivo = crearLabel("Motivo:");
        JTextField txtMotivo = new JTextField();
        estilizarCampo(txtMotivo);

        JLabel lblDestino = crearLabel("Será atendido en:");
        JLabel lblDestinoValor = new JLabel("PC3");
        lblDestinoValor.setForeground(new Color(0, 200, 255));
        lblDestinoValor.setFont(new Font("Arial", Font.BOLD, 13));

        // Actualizar destino al cambiar tipo
        cmbTipo.addActionListener(e -> {
            String tipo = (String) cmbTipo.getSelectedItem();
            lblDestinoValor.setText(Clasificador.getDestino(tipo));
        });

        // ── Botón enviar ─────────────────────────────────────────────────────
        JButton btnEnviar = new JButton("Enviar Ticket");
        btnEnviar.setBackground(new Color(0, 122, 204));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEnviar.addActionListener(e -> {
            String dpi           = txtDpi.getText().trim();
            String tipo          = (String) cmbTipo.getSelectedItem();
            String motivo        = txtMotivo.getText().trim();

            if (dpi.isEmpty() || motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor complete todos los campos.",
                        "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ticket ticket = new Ticket(dpi, motivo, tipo);

            try {
                Socket socket          = new Socket(HOST, PUERTO);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(ticket);
                out.flush();
                socket.close();
                JOptionPane.showMessageDialog(this,
                        "Ticket enviado correctamente.\nSerá atendido en: " + Clasificador.getDestino(tipo),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al conectar con el servidor:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblDpi);           panel.add(txtDpi);
        panel.add(lblTipo);          panel.add(cmbTipo);
        panel.add(lblMotivo);        panel.add(txtMotivo);
        panel.add(lblDestino);       panel.add(lblDestinoValor);
        panel.add(new JLabel());     panel.add(btnEnviar);

        add(panel);
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(new Color(60, 60, 60));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }
}