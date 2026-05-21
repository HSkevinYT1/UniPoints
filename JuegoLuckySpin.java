import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class JuegoLuckySpin extends JFrame {

    // Logros
    private boolean Logro = false;
    private int JuegosGanadosSeguidos = 0;

    // Jugador
    private int monedasJugador = 1200;
    private String nombreJugador = "juan cacorro";
    private ImageIcon iconoJugador = null;

    // Variables del juego
    private int montoApuesta = 500;
    private boolean girando = false;

    // Elementos de la Interfaz UI
    private JLabel lblMonedas;
    private JTextField txtMontoApuesta;
    private JLabel lblGananciaMax;
    private JButton btnGirar;
    private JButton btnMenos, btnMas;
    private JButton[] btnRapidos = new JButton[4];

    // Casillas del Lucky Spin (Tragamonedas)
    private JLabel lblCasilla1;
    private JLabel lblCasilla2;
    private JLabel lblCasilla3;

    // Símbolos disponibles en el juego
    private final String[] SIMBOLOS = {"🍒", "🍋", "🍊", "🍇", "🔔", "💎", "7️⃣"};

    // Paleta de Colores basada en tu diseño
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    public JuegoLuckySpin() {
        // Cargar datos reales del usuario logueado para consistencia global
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        } else {
            this.nombreJugador = "Invitado";
        }

        // Configuración de la Ventana Principal
        setTitle("Unab Points - Lucky 3-Spin Slots");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        // 1. BARRA SUPERIOR (HEADER)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (APUESTAS)
        add(crearPanelApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (LAS 3 CASILLAS)
        add(crearPanelJuego(), BorderLayout.CENTER);

        // 4. PANEL DERECHO (CÓMO JUGAR Y TABLA DE PREMIOS)
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
            VentanaJuegos.crearVentanaLimpia();
            JuegoLuckySpin.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Lucky Spin de 3 Casillas", SwingConstants.LEFT);
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
            btnRapidos[i].addActionListener(e -> {
                montoApuesta = m;
                txtMontoApuesta.setText(String.valueOf(montoApuesta));
                actualizarGananciaMax();
            });
            panelBotonesRapidos.add(btnRapidos[i]);
        }

        JPanel panelGanancia = new JPanel(new GridLayout(2, 1));
        panelGanancia.setBackground(COLOR_FONDO_PRINCIPAL);
        panelGanancia.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));
        panelGanancia.setMaximumSize(new Dimension(210, 60));
        panelGanancia.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGananciaTitulo = new JLabel("Premio Máximo (777)", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaMax = new JLabel(String.format("%,d UP", montoApuesta * 10), SwingConstants.CENTER);
        lblGananciaMax.setForeground(COLOR_VERDE_ACENTO);
        lblGananciaMax.setFont(new Font("Arial", Font.BOLD, 14));
        panelGanancia.add(lblGananciaTitulo);
        panelGanancia.add(lblGananciaMax);

        btnGirar = new JButton("¡GIRAR!");
        btnGirar.setBackground(COLOR_VERDE_ACENTO);
        btnGirar.setForeground(COLOR_FONDO_PRINCIPAL);
        btnGirar.setFont(new Font("Arial", Font.BOLD, 16));
        btnGirar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGirar.setMaximumSize(new Dimension(210, 40));
        btnGirar.setFocusPainted(false);
        btnGirar.addActionListener(e -> iniciarGiro());

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
        panel.add(btnGirar);

        return panel;
    }

    private JPanel crearPanelJuego() {
        JPanel panelJuego = new JPanel(new GridBagLayout());
        panelJuego.setBackground(COLOR_FONDO_PANEL);
        panelJuego.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "MAQUINA TRAGAMONEDAS",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        // Subcontenedor para las 3 casillas alineadas horizontalmente
        JPanel panelCasillas = new JPanel(new GridLayout(1, 3, 25, 0));
        panelCasillas.setBackground(COLOR_FONDO_PANEL);

        // Inicializar las 3 casillas con un diseño elegante y grande
        lblCasilla1 = crearEstiloCasilla("🍒");
        lblCasilla2 = crearEstiloCasilla("🔔");
        lblCasilla3 = crearEstiloCasilla("7️⃣");

        panelCasillas.add(lblCasilla1);
        panelCasillas.add(lblCasilla2);
        panelCasillas.add(lblCasilla3);

        panelJuego.add(panelCasillas);
        return panelJuego;
    }

    private JLabel crearEstiloCasilla(String simboloInicial) {
        JLabel label = new JLabel(simboloInicial, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 72)); // Fuente compatible con emojis grandes
        label.setOpaque(true);
        label.setBackground(COLOR_FONDO_PRINCIPAL);
        label.setForeground(COLOR_TEXTO);
        label.setPreferredSize(new Dimension(160, 200));
        label.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 3, true));
        return label;
    }

    private JPanel crearPanelComoJugar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(240, 0));

        JLabel lblTitulo = new JLabel("Cómo Jugar");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String reglasHTML = "<html><body style='width: 170px; color: #8A8F99; font-family: Arial; font-size: 11px;'>"
                + "<br><b style='color: #FFFFFF;'>1.</b> Ajusta tu apuesta.<br><br>"
                + "<b style='color: #FFFFFF;'>2.</b> Presiona <b style='color: #00E62A;'>¡GIRAR!</b>.<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> Si las 3 casillas coinciden, ¡ganas!<br><br>"
                + "<b style='color: #FFFFFF; font-size: 12px;'>Tabla de Premios (3 Iguales):</b><br>"
                + "🍒 🍒 🍒  ->  <b>Apu x 2</b><br>"
                + "🍋 🍋 🍋  ->  <b>Apu x 3</b><br>"
                + "🍊 🍊 🍊  ->  <b>Apu x 4</b><br>"
                + "🍇 🍇 🍇  ->  <b>Apu x 5</b><br>"
                + "🔔 🔔 🔔  ->  <b>Apu x 6</b><br>"
                + "💎 💎 💎  ->  <b>Apu x 8</b><br>"
                + "7️⃣ 7️⃣ 7️⃣  ->  <b style='color: #00E62A;'>JACKPOT x 10</b><br><br>"
                + "💡 <i>Cualquier par idéntico (ej: 🍒 🍒 🔔) te devuelve la mitad de la apuesta (<b>x0.5</b>).</i>"
                + "</body></html>";

        JLabel lblReglas = new JLabel(reglasHTML);
        lblReglas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(lblReglas);

        return panel;
    }

    private void modificarApuesta(int valor) {
        if (girando) return;
        if (montoApuesta + valor >= 50) {
            montoApuesta += valor;
            txtMontoApuesta.setText(String.valueOf(montoApuesta));
            actualizarGananciaMax();
        }
    }

    private void actualizarGananciaMax() {
        lblGananciaMax.setText(String.format("%,d UP", montoApuesta * 10));
    }

    private void setControlesHabilitados(boolean habilitado) {
        btnGirar.setEnabled(habilitado);
        btnMas.setEnabled(habilitado);
        btnMenos.setEnabled(habilitado);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(habilitado);
        }
    }

    // JUGABILIDAD CAMBIADA TOTALMENTE: Simulación síncrona/asíncrona de rodillos
    private void iniciarGiro() {
        if (montoApuesta > monedasJugador) {
            JOptionPane.showMessageDialog(this,
                    "❌ No tienes suficientes UP para realizar esta apuesta.",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (girando) return;

        girando = true;
        setControlesHabilitados(false);

        // Descontar saldo
        monedasJugador -= montoApuesta;
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        // Hilo encargado del movimiento independiente de las casillas
        Thread hiloSlots = new Thread(() -> {
            Random rand = new Random();

            // Tiempos de giro para cada casilla (la 1 para rápido, la 2 medio, la 3 dura más)
            int ciclosCasilla1 = 15 + rand.nextInt(5);
            int ciclosCasilla2 = 25 + rand.nextInt(5);
            int ciclosCasilla3 = 35 + rand.nextInt(5);

            int maxCiclos = ciclosCasilla3;

            String res1 = "", res2 = "", res3 = "";

            for (int i = 0; i <= maxCiclos; i++) {
                try {
                    Thread.sleep(60); // Velocidad de la animación
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                // Mientras no alcance su ciclo límite, cambia aleatoriamente
                if (i <= ciclosCasilla1) {
                    res1 = SIMBOLOS[rand.nextInt(SIMBOLOS.length)];
                }
                if (i <= ciclosCasilla2) {
                    res2 = SIMBOLOS[rand.nextInt(SIMBOLOS.length)];
                }
                if (i <= ciclosCasilla3) {
                    res3 = SIMBOLOS[rand.nextInt(SIMBOLOS.length)];
                }

                // Actualizar interfaz gráficamente en tiempo de ejecución
                final String f1 = res1;
                final String f2 = res2;
                final String f3 = res3;

                SwingUtilities.invokeLater(() -> {
                    lblCasilla1.setText(f1);
                    lblCasilla2.setText(f2);
                    lblCasilla3.setText(f3);
                });
            }

            // Almacenar los resultados finales estables
            final String final1 = res1;
            final String final2 = res2;
            final String final3 = res3;

            // Enviar a evaluar al hilo principal de UI
            SwingUtilities.invokeLater(() -> {
                evaluarResultado(final1, final2, final3);
            });
        });

        hiloSlots.start();
    }

    private void evaluarResultado(String c1, String c2, String c3) {
        int premio = 0;
        double multiplicador = 0;
        boolean ganoClasico = false;

        // Caso 1: ¡LAS 3 CASILLAS SON IGUALES!
        if (c1.equals(c2) && AtlanticEqual(c2, c3)) {
            ganoClasico = true;
            switch (c1) {
                case "🍒": multiplicador = 2; break;
                case "🍋": multiplicador = 3; break;
                case "🍊": multiplicador = 4; break;
                case "🍇": multiplicador = 5; break;
                case "🔔": multiplicador = 6; break;
                case "💎": multiplicador = 8; break;
                case "7️⃣": multiplicador = 10; break;
            }
            premio = (int) (montoApuesta * multiplicador);
            monedasJugador += premio;

            if (JuegosGanadosSeguidos < 3) {
                JuegosGanadosSeguidos++;
            }

            String mensaje = "🎉 ¡BRUTAL! Tres iguales [" + c1 + c1 + c1 + "]\nMultiplicador x" + (int)multiplicador + "! Recibes: " + premio + " UP.";
            if (c1.equals("7️⃣")) {
                mensaje = "🔥 ¡¡JACKPOT MÁXIMO!! ¡777! 🔥\n¡Te llevas " + premio + " UP!";
            }
            JOptionPane.showMessageDialog(this, mensaje, "¡VICTORIA!", JOptionPane.INFORMATION_MESSAGE);

            // Caso 2: Al menos un par es idéntico (Premio de consuelo: recuperas el 50%)
        } else if (c1.equals(c2) || c2.equals(c3) || c1.equals(c3)) {
            multiplicador = 0.5;
            premio = (int) (montoApuesta * multiplicador);
            monedasJugador += premio;
            JuegosGanadosSeguidos = 0;
            JOptionPane.showMessageDialog(this,
                    "📉 ¡Casi! Conseguiste un par idéntico.\nSalvaste la mitad de tu apuesta: " + premio + " UP.",
                    "Premio de Consuelo", JOptionPane.WARNING_MESSAGE);

            // Caso 3: Todos diferentes (Pérdida)
        } else {
            JuegosGanadosSeguidos = 0;
            JOptionPane.showMessageDialog(this,
                    "💸 Perdiste. Ninguna casilla coincidió ["+c1+" "+c2+" "+c3+"].\n¡Inténtalo de nuevo!",
                    "Fin del giro",
                    JOptionPane.WARNING_MESSAGE);
        }

        System.out.println("El número de Victorias seguidas es: " + JuegosGanadosSeguidos);

        // Actualizar UI post-juego
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        girando = false;
        setControlesHabilitados(true);
    }

    // Método auxiliar simple para asegurar la transitividad de la comparación de las 3 variables sin fallas de sintaxis
    private boolean AtlanticEqual(String a, String b){
        return a.equals(b);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoLuckySpin().setVisible(true);
        });
    }
}