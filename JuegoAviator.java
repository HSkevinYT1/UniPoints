import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class JuegoAviator extends JFrame {

    // Logros / Estadísticas
    private boolean Logro = false;
    private int JuegosGanadosSeguidos = 0;

    // Jugador
    private int monedasJugador = 1200;
    private String nombreJugador = "juan cacorro";

    // Variables del Juego del Cohete (Crash)
    private int montoApuesta = 500;
    private boolean juegoEnCurso = false;
    private boolean seRetiro = false;
    
    private double multiplicadorActual = 1.00;
    private double puntoDeExplosion = 1.00;
    private Timer timerVuelo;

    // Elementos de la Interfaz UI
    private JLabel lblMonedas;
    private JTextField txtMontoApuesta;
    private JLabel lblGananciaPotencial;
    private JButton btnAccion; // Cambia dinámicamente entre "Apostar" y "Retirarse"
    private JButton btnMenos, btnMas; 
    private JButton[] btnRapidos = new JButton[4]; 
    private JLabel lblMultiplicadorPantalla;
    private PanelGraficoCohete panelGraficoCohete;

    // Paleta de Colores basada en tu UI original
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_ROJO_CRASH = new Color(230, 0, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    public JuegoAviator() {
        // Cargar datos reales del usuario logueado para consistencia global
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        } else {
            this.nombreJugador = "Invitado";
        }

        // Configuración de la Ventana Principal
        setTitle("Unab Points - Aviator Casino");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        // 1. BARRA SUPERIOR (HEADER)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (APUESTAS)
        add(crearPanelApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (EL JUEGO DEL COHETE)
        add(crearPanelJuego(), BorderLayout.CENTER);

        // 4. PANEL DERECHO (CÓMO JUGAR)
        add(crearPanelComoJugar(), BorderLayout.EAST);
        WindowPreserver.configurarVentana(this);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_FONDO_PRINCIPAL);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.setBackground(COLOR_FONDO_PANEL);
        btnVolver.setForeground(COLOR_TEXTO);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnVolver.addActionListener(e -> {
            if (juegoEnCurso && !seRetiro) {
                timerVuelo.stop(); // Seguridad por si intenta salir en pleno vuelo
            }
            VentanaJuegos.crearVentanaLimpia();
            JuegoAviator.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Aviator Crash Casino", SwingConstants.LEFT);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelIzquierdoHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelIzquierdoHeader.setBackground(COLOR_FONDO_PRINCIPAL);
        panelIzquierdoHeader.add(btnVolver);
        panelIzquierdoHeader.add(lblTitulo);

        lblMonedas = new JLabel(String.format("%,d UP", monedasJugador), SwingConstants.CENTER);
        lblMonedas.setOpaque(true);
        lblMonedas.setBackground(COLOR_FONDO_PANEL);
        lblMonedas.setForeground(COLOR_VERDE_ACENTO);
        lblMonedas.setFont(new Font("Arial", Font.BOLD, 14));
        lblMonedas.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_ACENTO, 1, true));
        lblMonedas.setPreferredSize(new Dimension(120, 35));

        header.add(panelIzquierdoHeader, BorderLayout.WEST);
        header.add(lblMonedas, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelApuestas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(240, 0));

        JLabel lblApuesta = new JLabel("Apuesta");
        lblApuesta.setForeground(COLOR_TEXTO);
        lblApuesta.setFont(new Font("Arial", Font.BOLD, 16));
        lblApuesta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMonto = new JLabel("Monto:");
        lblMonto.setForeground(COLOR_TEXTO_MUTED);
        lblMonto.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelMontoCtrl = new JPanel(new BorderLayout(5, 0));
        panelMontoCtrl.setBackground(COLOR_FONDO_PANEL);
        panelMontoCtrl.setMaximumSize(new Dimension(210, 30));
        panelMontoCtrl.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtMontoApuesta = new JTextField(String.valueOf(montoApuesta));
        txtMontoApuesta.setBackground(COLOR_FONDO_PRINCIPAL);
        txtMontoApuesta.setForeground(COLOR_TEXTO);
        txtMontoApuesta.setCaretColor(COLOR_TEXTO);
        txtMontoApuesta.setHorizontalAlignment(JTextField.CENTER);
        txtMontoApuesta.setEditable(false);
        txtMontoApuesta.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        btnMenos = new JButton("-");
        btnMenos.setBackground(COLOR_FONDO_PRINCIPAL);
        btnMenos.setForeground(COLOR_TEXTO);
        btnMenos.setFocusPainted(false);
        btnMenos.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        btnMenos.addActionListener(e -> modificarApuesta(-50));

        btnMas = new JButton("+");
        btnMas.setBackground(COLOR_FONDO_PRINCIPAL);
        btnMas.setForeground(COLOR_TEXTO);
        btnMas.setFocusPainted(false);
        btnMas.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        btnMas.addActionListener(e -> modificarApuesta(50));

        panelMontoCtrl.add(btnMenos, BorderLayout.WEST);
        panelMontoCtrl.add(txtMontoApuesta, BorderLayout.CENTER);
        panelMontoCtrl.add(btnMas, BorderLayout.EAST);

        JPanel panelBotonesRapidos = new JPanel(new GridLayout(1, 4, 5, 0));
        panelBotonesRapidos.setBackground(COLOR_FONDO_PANEL);
        panelBotonesRapidos.setMaximumSize(new Dimension(210, 30));
        panelBotonesRapidos.setAlignmentX(Component.LEFT_ALIGNMENT);

        int[] montosRapidos = {100, 250, 500, 1000};
        for (int i = 0; i < montosRapidos.length; i++) {
            int m = montosRapidos[i];
            btnRapidos[i] = new JButton(String.valueOf(m));
            btnRapidos[i].setFont(new Font("Arial", Font.PLAIN, 10));
            btnRapidos[i].setBackground(COLOR_FONDO_PRINCIPAL);
            btnRapidos[i].setForeground(COLOR_TEXTO_MUTED);
            btnRapidos[i].setFocusPainted(false);
            btnRapidos[i].setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            btnRapidos[i].addActionListener(e -> {
                if (!juegoEnCurso) {
                    montoApuesta = m;
                    txtMontoApuesta.setText(String.valueOf(montoApuesta));
                    actualizarGananciaPotencial();
                }
            });
            panelBotonesRapidos.add(btnRapidos[i]);
        }

        JPanel panelGanancia = new JPanel(new GridLayout(2, 1));
        panelGanancia.setBackground(COLOR_FONDO_PRINCIPAL);
        panelGanancia.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));
        panelGanancia.setMaximumSize(new Dimension(210, 60));
        panelGanancia.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGananciaTitulo = new JLabel("Ganancia Estimada", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaPotencial = new JLabel(String.format("%,d UP", montoApuesta), SwingConstants.CENTER);
        lblGananciaPotencial.setForeground(COLOR_VERDE_ACENTO);
        lblGananciaPotencial.setFont(new Font("Arial", Font.BOLD, 14));
        panelGanancia.add(lblGananciaTitulo);
        panelGanancia.add(lblGananciaPotencial);

        btnAccion = new JButton("Apostar");
        btnAccion.setBackground(COLOR_VERDE_ACENTO);
        btnAccion.setForeground(COLOR_FONDO_PRINCIPAL);
        btnAccion.setFont(new Font("Arial", Font.BOLD, 16));
        btnAccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAccion.setMaximumSize(new Dimension(210, 40));
        btnAccion.setFocusPainted(false);
        btnAccion.addActionListener(e -> manejarAccionBoton());

        panel.add(lblApuesta);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMonto);
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelMontoCtrl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(panelBotonesRapidos);
        panel.add(Box.createVerticalGlue());
        panel.add(panelGanancia);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnAccion);

        return panel;
    }

    private JPanel crearPanelJuego() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "ÁREA DE LANZAMIENTO",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        lblMultiplicadorPantalla = new JLabel("1.00x", SwingConstants.CENTER);
        lblMultiplicadorPantalla.setFont(new Font("Arial", Font.BOLD, 64));
        lblMultiplicadorPantalla.setForeground(COLOR_TEXTO);
        lblMultiplicadorPantalla.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panel.add(lblMultiplicadorPantalla, BorderLayout.NORTH);

        panelGraficoCohete = new PanelGraficoCohete();
        panel.add(panelGraficoCohete, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelComoJugar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(220, 0));

        JLabel lblTitulo = new JLabel("Cómo Jugar");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String reglasHTML = "<html><body style='width: 150px; color: #8A8F99; font-family: Arial; font-size: 11px;'>"
                + "<br><b style='color: #FFFFFF;'>1.</b> Define el monto de tus UP a arriesgar.<br><br>"
                + "<b style='color: #00E62A;'>2.</b> Presiona 'Apostar' para lanzar el cohete.<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> El multiplicador subirá continuamente.<br><br>"
                + "<b style='color: #00E62A;'>4.</b> Pulsa 'Retirarse' antes de que el cohete explote para asegurar tu premio.<br><br>"
                + "<b style='color: #E6002A;'>¡Si explota antes de retirarte, pierdes todo!</b>"
                + "</body></html>";

        JLabel lblReglas = new JLabel(reglasHTML);
        lblReglas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(lblReglas);

        return panel;
    }

    private void modificarApuesta(int valor) {
        if (juegoEnCurso) return;
        if (montoApuesta + valor >= 50) { 
            montoApuesta += valor;
            txtMontoApuesta.setText(String.valueOf(montoApuesta));
            actualizarGananciaPotencial();
        }
    }

    private void actualizarGananciaPotencial() {
        long estimado = Math.round(montoApuesta * multiplicadorActual);
        lblGananciaPotencial.setText(String.format("%,d UP", estimado));
    }

    private void setControlesHabilitados(boolean habilitado) {
        btnMas.setEnabled(habilitado);
        btnMenos.setEnabled(habilitado);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(habilitado);
        }
    }

    private void manejarAccionBoton() {
        if (!juegoEnCurso) {
            iniciarVueloCohete();
        } else if (!seRetiro) {
            retirarseYCobrar();
        }
    }

    private void iniciarVueloCohete() {
        if (montoApuesta > monedasJugador) {
            JOptionPane.showMessageDialog(this,
                    "❌ No tienes suficientes UP para realizar esta apuesta.",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        juegoEnCurso = true;
        seRetiro = false;
        multiplicadorActual = 1.00;
        setControlesHabilitados(false);

        monedasJugador -= montoApuesta;
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        btnAccion.setText("Retirarse");
        btnAccion.setBackground(COLOR_VERDE_ACENTO);
        lblMultiplicadorPantalla.setForeground(COLOR_TEXTO);

        Random rand = new Random();
        if (rand.nextInt(100) < 5) { 
            puntoDeExplosion = 1.00; 
        } else {
            puntoDeExplosion = 1.00 + (rand.nextDouble() * 5.0) * (rand.nextDouble() * 2.0);
            puntoDeExplosion = Math.round(puntoDeExplosion * 100.0) / 100.0;
        }

        timerVuelo = new Timer(60, e -> {
            double incremento = 0.01 + (multiplicadorActual * 0.002);
            multiplicadorActual += incremento;
            multiplicadorActual = Math.round(multiplicadorActual * 100.0) / 100.0;

            lblMultiplicadorPantalla.setText(String.format("%.2fx", multiplicadorActual));
            actualizarGananciaPotencial();
            panelGraficoCohete.actualizarPosicion(multiplicadorActual);

            if (multiplicadorActual >= puntoDeExplosion) {
                finalizarPorCrash();
            }
        });
        timerVuelo.start();
    }

    private void retirarseYCobrar() {
        seRetiro = true;
        btnAccion.setEnabled(false); 
        btnAccion.setText("¡Cobrado!");
        btnAccion.setBackground(COLOR_TEXTO_MUTED);

        int premio = (int) Math.round(montoApuesta * multiplicadorActual);
        monedasJugador += premio;

        if (JuegosGanadosSeguidos < 3) {
            JuegosGanadosSeguidos++;
        }
        System.out.println("El numero de Victorias consecutivas es: " + JuegosGanadosSeguidos);

        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }
    }

    private void finalizarPorCrash() {
        timerVuelo.stop();
        juegoEnCurso = false;
        lblMultiplicadorPantalla.setForeground(COLOR_ROJO_CRASH);
        lblMultiplicadorPantalla.setText("💥 CRASH " + String.format("%.2fx", puntoDeExplosion));
        panelGraficoCohete.explotar();

        if (!seRetiro) {
            JuegosGanadosSeguidos = 0;
            System.out.println("El numero de Victorias es: " + JuegosGanadosSeguidos);
            JOptionPane.showMessageDialog(this,
                    "💸 ¡El cohete explotó! Perdiste tus " + montoApuesta + " UP.",
                    "💥 Boom",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            int premio = (int) Math.round(montoApuesta * multiplicadorActual);
            JOptionPane.showMessageDialog(this,
                    "🎉 ¡Excelente escape! Te retiraste a tiempo.\nGanaste: " + premio + " UP.",
                    "¡Victoria!",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        btnAccion.setText("Apostar");
        btnAccion.setBackground(COLOR_VERDE_ACENTO);
        btnAccion.setEnabled(true);
        setControlesHabilitados(true);
        actualizarGananciaPotencial();
    }

    private class PanelGraficoCohete extends JPanel {
        private int coheteX = 50;
        private int coheteY = 450;
        private boolean haExplotado = false;
        private double multReferencia = 1.0;

        public PanelGraficoCohete() {
            setBackground(COLOR_FONDO_PRINCIPAL);
            setBorder(BorderFactory.createLineBorder(COLOR_FONDO_PANEL, 2));
        }

        public void actualizarPosicion(double multiplicador) {
            this.multReferencia = multiplicador;
            this.haExplotado = false;

            int anchoMax = getWidth() - 120;
            int altoMax = getHeight() - 100;

            coheteX = 50 + (int) (Math.min(multiplicador - 1.0, 5.0) / 5.0 * anchoMax);
            coheteY = (getHeight() - 80) - (int) (Math.min(Math.pow(multiplicador - 1.0, 1.3), 5.0) / 5.0 * altoMax);

            if (coheteX > getWidth() - 80) coheteX = getWidth() - 80;
            if (coheteY < 50) coheteY = 50;

            repaint();
        }

        public void explotar() {
            this.haExplotado = true;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(COLOR_FONDO_PANEL);
            for (int i = 0; i < getWidth(); i += 60) {
                g2.drawLine(i, 0, i, getHeight());
            }
            for (int j = 0; j < getHeight(); j += 60) {
                g2.drawLine(0, j, getWidth(), j);
            }

            if (juegoEnCurso || haExplotado) {
                g2.setStroke(new BasicStroke(3.0f));
                g2.setColor(COLOR_VERDE_ACENTO);
                //g2.drawQuadTo(50, getHeight() - 80, coheteX + 15, coheteY + 15);

                if (!haExplotado) {
                    g2.setColor(new Color(240, 240, 245));
                    g2.fillOval(coheteX, coheteY, 35, 20); 
                    
                    g2.setColor(COLOR_ROJO_CRASH);
                    int[] xPunta = {coheteX + 30, coheteX + 45, coheteX + 30};
                    int[] yPunta = {coheteY, coheteY + 10, coheteY + 20};
                    g2.fillPolygon(xPunta, yPunta, 3); 
                    
                    g2.setColor(Color.ORANGE);
                    g2.fillOval(coheteX - 15, coheteY + 3, 18, 14);
                } else {
                    g2.setColor(COLOR_ROJO_CRASH);
                    g2.fillOval(coheteX - 10, coheteY - 10, 55, 55);
                    g2.setColor(Color.ORANGE);
                    g2.fillOval(coheteX - 2, coheteY - 2, 38, 38);
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(coheteX + 5, coheteY + 5, 22, 22);
                }
            } else {
                g2.setFont(new Font("Arial", Font.ITALIC, 16));
                g2.setColor(COLOR_TEXTO_MUTED);
                g2.drawString("🚀 Cohete listo en plataforma. Introduce tu apuesta para despegar...", 40, getHeight() / 2);
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoAviator().setVisible(true);
        });
    }
}