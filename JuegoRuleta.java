import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class JuegoRuleta extends JFrame {

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

    // Componentes visuales de la ruleta
    private JPanel[] panelesSectores = new JPanel[8];
    private final String[] PREMIOS_NOMBRES = {"X0 (Perder)", "X1 (Retorno)", "X1.5", "JACKPOT X5", "X0 (Perder)", "X1 (Retorno)", "X2", "X0.5 (Mitad)"};
    private final double[] MULTIPLICADORES = {0.0, 1.0, 1.5, 5.0, 0.0, 1.0, 2.0, 0.5};

    // Paleta de Colores
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_ROJO_FUEGO = new Color(230, 40, 40);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    public JuegoRuleta() {
        // Cargar datos reales del usuario logueado para consistencia global
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        } else {
            this.nombreJugador = "Invitado";
        }

        // Configuración de la Ventana Principal
        setTitle("Unab Points - Ruleta");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        // 1. BARRA SUPERIOR (HEADER)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (APUESTAS)
        add(crearPanelApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (LA RULETA)
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
            VentanaJuegos.crearVentanaLimpia();
            JuegoRuleta.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Ruleta ", SwingConstants.LEFT);
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

        JLabel lblGananciaTitulo = new JLabel("Premio Máximo (Jackpot)", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaMax = new JLabel(String.format("%,d UP", montoApuesta * 5), SwingConstants.CENTER);
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
        JPanel panelPrincipalJuego = new JPanel(new BorderLayout(10, 10));
        panelPrincipalJuego.setBackground(COLOR_FONDO_PRINCIPAL);
        panelPrincipalJuego.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "RULETA DE LA FORTUNA",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        // Diseñamos una cuadrícula circular simulada de 3x3 (dejando el centro vacío o como indicador)
        JPanel gridRuleta = new JPanel(new GridLayout(3, 3, 10, 10));
        gridRuleta.setBackground(COLOR_FONDO_PANEL);
        gridRuleta.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Mapeo manual de las posiciones de la cuadrícula 3x3 a los 8 sectores externos
        // Posiciones: 0=TopLeft, 1=TopCenter, 2=TopRight, 3=MidRight, 4=BotRight, 5=BotCenter, 6=BotLeft, 7=MidLeft
        for (int i = 0; i < 8; i++) {
            panelesSectores[i] = new JPanel(new GridBagLayout());
            panelesSectores[i].setBackground(COLOR_FONDO_PRINCIPAL);
            panelesSectores[i].setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));

            JLabel lblPremio = new JLabel(PREMIOS_NOMBRES[i]);
            lblPremio.setFont(new Font("Arial", Font.BOLD, 13));
            if (PREMIOS_NOMBRES[i].contains("JACKPOT")) {
                lblPremio.setForeground(COLOR_VERDE_ACENTO);
            } else if (PREMIOS_NOMBRES[i].contains("X0")) {
                lblPremio.setForeground(COLOR_ROJO_FUEGO);
            } else {
                lblPremio.setForeground(COLOR_TEXTO);
            }
            panelesSectores[i].add(lblPremio);
        }

        // Panel del centro que sirve de flecha indicadora
        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setBackground(COLOR_FONDO_PRINCIPAL);
        JLabel lblCentro = new JLabel("🍀");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 36));
        panelCentro.add(lblCentro);

        // Colocar los componentes en el orden correcto del GridLayout 3x3
        gridRuleta.add(panelesSectores[0]); // Fila 1 - Col 1
        gridRuleta.add(panelesSectores[1]); // Fila 1 - Col 2
        gridRuleta.add(panelesSectores[2]); // Fila 1 - Col 3
        gridRuleta.add(panelesSectores[7]); // Fila 2 - Col 1
        gridRuleta.add(panelCentro);        // Fila 2 - Col 2 (Centro)
        gridRuleta.add(panelesSectores[3]); // Fila 2 - Col 3
        gridRuleta.add(panelesSectores[6]); // Fila 3 - Col 1
        gridRuleta.add(panelesSectores[5]); // Fila 3 - Col 2
        gridRuleta.add(panelesSectores[4]); // Fila 3 - Col 3

        panelPrincipalJuego.add(gridRuleta, BorderLayout.CENTER);
        return panelPrincipalJuego;
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
                + "<br><b style='color: #FFFFFF;'>1.</b> Selecciona tu monto de apuesta usando los controles.<br><br>"
                + "<b style='color: #FFFFFF;'>2.</b> Presiona el botón <b style='color: #00E62A;'>¡GIRAR!</b> para iniciar la ruleta.<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> La luz recorrerá los multiplicadores de forma aleatoria.<br><br>"
                + "<b style='color: #FFFFFF;'>4.</b> Dependiendo de dónde se detenga, ganarás un multiplicador de tu apuesta. "
                + "¡Apunta al <b style='color: #00E62A;'>JACKPOT X5</b>!<br><br>"
                + "<b style='color: #00E62A;'>¡Mucha Suerte!</b>"
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
        lblGananciaMax.setText(String.format("%,d UP", montoApuesta * 5));
    }

    private void setControlesHabilitados(boolean habilitado) {
        btnGirar.setEnabled(habilitado);
        btnMas.setEnabled(habilitado);
        btnMenos.setEnabled(habilitado);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(habilitado);
        }
    }

    // LÓGICA DE JUEGO ACTUALIZADA (Lucky Spin Mecánica)
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

        // Descontar saldo inicial
        monedasJugador -= montoApuesta;
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        // Hilo de animación para el giro de la ruleta
        Thread hiloRuleta = new Thread(() -> {
            Random rand = new Random();

            // Pasos totales que dará la ruleta (entre 24 y 40 pasos para aleatoriedad)
            int pasosTotales = 24 + rand.nextInt(16);
            int delay = 60; // Retardo inicial en milisegundos
            int sectorActual = 0;

            for (int i = 0; i < pasosTotales; i++) {
                sectorActual = i % 8;
                final int sectorIluminado = sectorActual;

                // Animación: Iluminar el cuadro actual en el hilo de UI
                SwingUtilities.invokeLater(() -> {
                    // Limpiar todos los fondos primero
                    for (int j = 0; j < 8; j++) {
                        panelesSectores[j].setBackground(COLOR_FONDO_PRINCIPAL);
                    }
                    // Resaltar el seleccionado con color de acento
                    panelesSectores[sectorIluminado].setBackground(new Color(255, 215, 0)); // Color Dorado de Giro
                });

                try {
                    // Simular efecto de desaceleración al final del giro
                    if (i > pasosTotales - 6) {
                        delay += 70;
                    }
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            final int resultadoFinalIndex = sectorActual;

            // Evaluar resultados en la interfaz
            SwingUtilities.invokeLater(() -> {
                evaluarResultado(resultadoFinalIndex);
            });
        });

        hiloRuleta.start();
    }

    private void evaluarResultado(int sectorGanador) {
        // Restaurar el color llamativo en el sector ganador
        for (int j = 0; j < 8; j++) {
            panelesSectores[j].setBackground(COLOR_FONDO_PRINCIPAL);
        }
        panelesSectores[sectorGanador].setBackground(COLOR_VERDE_ACENTO);

        double multiplicador = MULTIPLICADORES[sectorGanador];
        int premioFinal = (int) (montoApuesta * multiplicador);

        if (multiplicador > 1.0) { // Victorias (Ganancia neta)
            if (JuegosGanadosSeguidos < 3) {
                JuegosGanadosSeguidos++;
            }
            monedasJugador += premioFinal;

            String mensaje = "🎉 ¡Felicidades! La ruleta cayó en " + PREMIOS_NOMBRES[sectorGanador] + ".\n" +
                    "Ganaste: " + premioFinal + " UP.";
            if (multiplicador == 5.0) {
                mensaje = "🔥 ¡¡BRUTAL!! ¡TE LLEVASTE EL JACKPOT X5!! 🔥\nGanaste: " + premioFinal + " UP.";
            }

            JOptionPane.showMessageDialog(this, mensaje, "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);

        } else if (multiplicador == 1.0) { // Empate (Devolución)
            monedasJugador += premioFinal;
            JOptionPane.showMessageDialog(this,
                    "⚖️ Recuperaste tu apuesta. Cayó en " + PREMIOS_NOMBRES[sectorGanador] + ".\nRetornas: " + premioFinal + " UP.",
                    "Empate", JOptionPane.INFORMATION_MESSAGE);

        } else if (multiplicador == 0.5) { // Pérdida Parcial
            monedasJugador += premioFinal;
            JOptionPane.showMessageDialog(this,
                    "📉 ¡Casi! La ruleta cayó en " + PREMIOS_NOMBRES[sectorGanador] + ".\nSalvaste: " + premioFinal + " UP.",
                    "Pérdida Parcial", JOptionPane.WARNING_MESSAGE);
            JuegosGanadosSeguidos = 0;

        } else { // Pérdida Total (X0)
            JOptionPane.showMessageDialog(this,
                    "💸 Mala suerte. Cayó en " + PREMIOS_NOMBRES[sectorGanador] + ".\n¡Inténtalo de nuevo!",
                    "Suerte para la próxima",
                    JOptionPane.WARNING_MESSAGE);
            JuegosGanadosSeguidos = 0;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoRuleta().setVisible(true);
        });
    }
}