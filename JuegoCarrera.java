import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class JuegoCarrera extends JFrame {

    // logros
    private boolean Logro = false;
    private int JuegosGanadosSeguidos = 0;

    //jugador
    private int monedasJugador = 1200;
    private String nombreJugador = "juan cacorro";
    private ImageIcon iconoJugador = null;

    // Variables del juego
    private int montoApuesta = 500;
    private int caballoSeleccionado = 1; // Por defecto el Caballo 1
    private boolean carreraEnCurso = false;

    // Elementos de la Interfaz UI
    private JLabel lblMonedas;
    private JTextField txtMontoApuesta;
    private JLabel lblGananciaPotencial;
    private JButton btnApostar;
    private JButton btnMenos, btnMas; // Añadidos para poder bloquearlos en carrera
    private JButton[] btnRapidos = new JButton[4]; // Añadidos para poder bloquearlos en carrera
    private JRadioButton rbCaballo1, rbCaballo2, rbCaballo3;
    private JProgressBar barCaballo1, barCaballo2, barCaballo3;

    // Paleta de Colores basada en el Login
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    public JuegoCarrera() {
        // Cargar datos reales del usuario logueado para consistencia global
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        } else {
            //this.monedasJugador = 500;
            this.nombreJugador = "Invitado";
        }

        // Configuración de la Ventana Principal
        setTitle("Unab Points - Carreras de Caballos");
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        // 1. BARRA SUPERIOR (HEADER)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (APUESTAS)
        add(crearPanelApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (EL JUEGO DEL CASINO)
        add(crearPanelJuego(), BorderLayout.CENTER);

        // 4. PANEL DERECHO (CÓMO JUGAR)
        add(crearPanelComoJugar(), BorderLayout.EAST);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_FONDO_PRINCIPAL);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Botón Volver
        JButton btnVolver = new JButton("< Volver");
        btnVolver.setBackground(COLOR_FONDO_PANEL);
        btnVolver.setForeground(COLOR_TEXTO);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnVolver.addActionListener(e -> {
            // 1. Llamamos directamente al método público y estático que arma tu menú
            VentanaJuegos.crearVentanaLimpia();

            // 2. Cerramos la ventana del juego de carreras de forma segura
            JuegoCarrera.this.dispose();
        });

        // Nombre del Juego e Info Jugador
        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Carrera de Caballos", SwingConstants.LEFT);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelIzquierdoHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelIzquierdoHeader.setBackground(COLOR_FONDO_PRINCIPAL);
        panelIzquierdoHeader.add(btnVolver);
        panelIzquierdoHeader.add(lblTitulo);

        // Contador de Monedas (Arriba Derecha)
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

        // Fila de control de monto (- y +)
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

        // Botones rápidos de apuesta
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
            btnRapidos[i].setMargin(new Insets(2, 2, 2, 2));
            btnRapidos[i].addActionListener(e -> {
                montoApuesta = m;
                txtMontoApuesta.setText(String.valueOf(montoApuesta));
                actualizarGananciaPotencial();
            });
            panelBotonesRapidos.add(btnRapidos[i]);
        }

        // Selección de Caballo
        JLabel lblSeleccion = new JLabel("Selecciona tu Caballo:");
        lblSeleccion.setForeground(COLOR_TEXTO);
        lblSeleccion.setFont(new Font("Arial", Font.BOLD, 12));
        lblSeleccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbCaballo1 = new JRadioButton("Caballo Rayo (1)", true);
        rbCaballo2 = new JRadioButton("Caballo Trueno (2)");
        rbCaballo3 = new JRadioButton("Caballo Centella (3)");

        ButtonGroup grupoCaballos = new ButtonGroup();
        grupoCaballos.add(rbCaballo1);
        grupoCaballos.add(rbCaballo2);
        grupoCaballos.add(rbCaballo3);

        ActionListener rbListener = e -> {
            if (rbCaballo1.isSelected()) caballoSeleccionado = 1;
            if (rbCaballo2.isSelected()) caballoSeleccionado = 2;
            if (rbCaballo3.isSelected()) caballoSeleccionado = 3;
        };
        rbCaballo1.addActionListener(rbListener);
        rbCaballo2.addActionListener(rbListener);
        rbCaballo3.addActionListener(rbListener);

        configurarRadioButton(rbCaballo1);
        configurarRadioButton(rbCaballo2);
        configurarRadioButton(rbCaballo3);

        // Panel de Ganancia
        JPanel panelGanancia = new JPanel(new GridLayout(2, 1));
        panelGanancia.setBackground(COLOR_FONDO_PRINCIPAL);
        panelGanancia.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));
        panelGanancia.setMaximumSize(new Dimension(210, 60));
        panelGanancia.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGananciaTitulo = new JLabel("Ganancia x2", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaPotencial = new JLabel(String.format("%,d UP", montoApuesta * 2), SwingConstants.CENTER);
        lblGananciaPotencial.setForeground(COLOR_VERDE_ACENTO);
        lblGananciaPotencial.setFont(new Font("Arial", Font.BOLD, 14));
        panelGanancia.add(lblGananciaTitulo);
        panelGanancia.add(lblGananciaPotencial);

        // Botón Apostar
        btnApostar = new JButton("Apostar");
        btnApostar.setBackground(COLOR_VERDE_ACENTO);
        btnApostar.setForeground(COLOR_FONDO_PRINCIPAL);
        btnApostar.setFont(new Font("Arial", Font.BOLD, 16));
        btnApostar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnApostar.setMaximumSize(new Dimension(210, 40));
        btnApostar.setFocusPainted(false);
        btnApostar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnApostar.addActionListener(e -> iniciarCarrera());

        // Organización en el contenedor vertical
        panel.add(lblApuesta); panel.add(Box.createVerticalStrut(5));
        panel.add(lblMonto); panel.add(Box.createVerticalStrut(5));
        panel.add(panelMontoCtrl); panel.add(Box.createVerticalStrut(8));
        panel.add(panelBotonesRapidos); panel.add(Box.createVerticalStrut(15));
        panel.add(lblSeleccion); panel.add(Box.createVerticalStrut(5));
        panel.add(rbCaballo1); panel.add(rbCaballo2); panel.add(rbCaballo3);
        panel.add(Box.createVerticalGlue());
        panel.add(panelGanancia); panel.add(Box.createVerticalStrut(15));
        panel.add(btnApostar);

        return panel;
    }

    private JPanel crearPanelJuego() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 20));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "PISTA DE CARRERAS",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        // Fila Caballo 1
        JPanel p1 = new JPanel(new BorderLayout(10, 0)); p1.setBackground(COLOR_FONDO_PANEL);
        JLabel name1 = new JLabel("🐎 C1: "); name1.setForeground(COLOR_TEXTO);
        barCaballo1 = crearProgresoCarrera();
        p1.add(name1, BorderLayout.WEST); p1.add(barCaballo1, BorderLayout.CENTER);

        // Fila Caballo 2
        JPanel p2 = new JPanel(new BorderLayout(10, 0)); p2.setBackground(COLOR_FONDO_PANEL);
        JLabel name2 = new JLabel("🐎 C2: "); name2.setForeground(COLOR_TEXTO);
        barCaballo2 = crearProgresoCarrera();
        p2.add(name2, BorderLayout.WEST); p2.add(barCaballo2, BorderLayout.CENTER);

        // Fila Caballo 3
        JPanel p3 = new JPanel(new BorderLayout(10, 0)); p3.setBackground(COLOR_FONDO_PANEL);
        JLabel name3 = new JLabel("🐎 C3: "); name3.setForeground(COLOR_TEXTO);
        barCaballo3 = crearProgresoCarrera();
        p3.add(name3, BorderLayout.WEST); p3.add(barCaballo3, BorderLayout.CENTER);

        panel.add(p1);
        panel.add(p2);
        panel.add(p3);

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
                + "<br><b style='color: #FFFFFF;'>1.</b> Selecciona el monto usando los controles.<br><br>"
                + "<b style='color: #FFFFFF;'>2.</b> Elige cuál de los 3 caballos crees que ganará.<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> Presiona 'Apostar'. Si tu caballo llega primero, <b>duplicas</b> lo invertido.<br><br>"
                + "<b style='color: #00E62A;'>¡Suerte!</b>"
                + "</body></html>";

        JLabel lblReglas = new JLabel(reglasHTML);
        lblReglas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(lblReglas);

        return panel;
    }

    // Auxiliares de Interfaz
    private void configurarRadioButton(JRadioButton rb) {
        rb.setBackground(COLOR_FONDO_PANEL);
        rb.setForeground(COLOR_TEXTO_MUTED);
        rb.setFocusPainted(false);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JProgressBar crearProgresoCarrera() {
        JProgressBar bar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo del carril oscuro
                g2.setColor(COLOR_FONDO_PRINCIPAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Borde sutil del carril
                g2.setColor(COLOR_TEXTO_MUTED);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                // Relleno de la barra de progreso (porcentaje de la carrera)
                double porcentaje = (double) getValue() / getMaximum();
                int progressWidth = (int) (getWidth() * porcentaje);
                if (progressWidth > 0) {
                    g2.setColor(COLOR_VERDE_ACENTO);
                    g2.fillRoundRect(0, 0, progressWidth, getHeight(), 12, 12);
                }

                // Dibujar texto del porcentaje en el centro
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String texto = getValue() + " %";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(texto)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                g2.setColor(porcentaje > 0.5 ? COLOR_FONDO_PRINCIPAL : COLOR_VERDE_ACENTO);
                g2.drawString(texto, tx, ty);

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder());
        return bar;
    }

    private void modificarApuesta(int valor) {
        if (carreraEnCurso) return;
        if (montoApuesta + valor >= 50) { // Mínimo de apuesta: 50 monedas
            montoApuesta += valor;
            txtMontoApuesta.setText(String.valueOf(montoApuesta));
            actualizarGananciaPotencial();
        }
    }

    private void actualizarGananciaPotencial() {
        lblGananciaPotencial.setText(String.format("%,d UP", montoApuesta * 2));
    }

    private void setControlesHabilitados(boolean habilitado) {
        btnApostar.setEnabled(habilitado);
        btnMas.setEnabled(habilitado);
        btnMenos.setEnabled(habilitado);
        rbCaballo1.setEnabled(habilitado);
        rbCaballo2.setEnabled(habilitado);
        rbCaballo3.setEnabled(habilitado);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(habilitado);
        }
    }

    // LÓGICA DE JUEGO (Simulación y Condicional de monedas)
    private void iniciarCarrera() {
        // --- CONDICIONAL SOLICITADO: No se puede apostar más de lo que se tiene ---
        if (montoApuesta > monedasJugador) {
            JOptionPane.showMessageDialog(this,
                    "❌ No tienes suficientes UP para realizar esta apuesta.",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (carreraEnCurso) return;

        carreraEnCurso = true;
        setControlesHabilitados(false); // Bloquea controles para evitar exploits

        // Descontar monedas de la apuesta inicial
        monedasJugador -= montoApuesta;
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        // Reiniciar pistas
        barCaballo1.setValue(0);
        barCaballo2.setValue(0);
        barCaballo3.setValue(0);

        // Hilo encargado del movimiento síncrono de los caballos
        Thread hiloCarrera = new Thread(() -> {
            Random rand = new Random();
            int c1 = 0, c2 = 0, c3 = 0;

            while (c1 < 100 && c2 < 100 && c3 < 100) {
                try {
                    Thread.sleep(80); // Velocidad de actualización de la animación
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                c1 += rand.nextInt(8);
                c2 += rand.nextInt(8);
                c3 += rand.nextInt(8);

                final int p1 = Math.min(c1, 100);
                final int p2 = Math.min(c2, 100);
                final int p3 = Math.min(c3, 100);

                SwingUtilities.invokeLater(() -> {
                    barCaballo1.setValue(p1);
                    barCaballo2.setValue(p2);
                    barCaballo3.setValue(p3);
                });
            }

            // Determinar Ganador
            int ganador = 1;
            if (c2 >= 100) ganador = 2;
            if (c3 >= 100) ganador = 3;

            final int caballoGanador = ganador;

            // Procesar resultado en el hilo UI
            SwingUtilities.invokeLater(() -> {
                evaluarResultado(caballoGanador);
            });
        });

        hiloCarrera.start();
    }

    private void evaluarResultado(int caballoGanador) {
        if (caballoSeleccionado == caballoGanador) {
            int premio = montoApuesta * 2;
            if(JuegosGanadosSeguidos < 3){
                JuegosGanadosSeguidos = JuegosGanadosSeguidos + 1;
                System.out.println("JuegosGanadosSeguidos");

            }
            System.out.println("El numero de Victorias es: " + JuegosGanadosSeguidos);

            monedasJugador += premio;
            JOptionPane.showMessageDialog(this,
                    "🎉 ¡Ganaste! El Caballo " + caballoGanador + " llegó primero.\nRecibes: " + premio + " UP.",
                    "¡Victoria!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "💸 Perdiste. El ganador fue el Caballo " + caballoGanador + ".\nMás suerte para la próxima.",
                    "Fin de la carrera",
                    JOptionPane.WARNING_MESSAGE);
            JuegosGanadosSeguidos = 0;
            System.out.println("El numero de Victorias es: " + JuegosGanadosSeguidos);
        }

        // Actualizar UI post-juego
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }
        carreraEnCurso = false;
        setControlesHabilitados(true); // Desbloquea controles
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoCarrera().setVisible(true);
        });
    }
}