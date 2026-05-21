import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class JuegoDados extends JFrame {

    // Logros
    private boolean Logro = false;
    private int JuegosGanadosSeguidos = 0;

    // Jugador
    private int monedasJugador = 1200;
    private String nombreJugador = "juan cacorro";
    private ImageIcon iconoJugador = null;

    // Variables del juego
    private int montoApuesta = 500;
    private boolean lanzando = false;

    // Elementos de la Interfaz UI
    private JLabel lblMonedas;
    private JTextField txtMontoApuesta;
    private JLabel lblGananciaMax;
    private JButton btnLanzar;
    private JButton btnMenos, btnMas;
    private JButton[] btnRapidos = new JButton[4];

    // Casillas para los 3 dados
    private JLabel lblDado1;
    private JLabel lblDado2;
    private JLabel lblDado3;

    // Representación en texto/emoji de las caras del dado (1 al 6)
    private final String[] CARAS_DADOS = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};

    // Paleta de Colores
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    public JuegoDados() {
        // Cargar datos reales del usuario logueado para consistencia global
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        } else {
            this.nombreJugador = "Invitado";
        }

        // Configuración de la Ventana Principal
        setTitle("Unab Points - Casino Dados");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        // 1. BARRA SUPERIOR (HEADER)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (APUESTAS)
        add(crearPanelApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (LOS 3 DADOS)
        add(crearPanelJuego(), BorderLayout.CENTER);

        // 4. PANEL DERECHO (CÓMO JUGAR Y PREMIOS)
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
            JuegoDados.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - El Cubo de la Suerte", SwingConstants.LEFT);
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

        JLabel lblGananciaTitulo = new JLabel("Premio Máximo (Trío ⚅)", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaMax = new JLabel(String.format("%,d UP", montoApuesta * 8), SwingConstants.CENTER);
        lblGananciaMax.setForeground(COLOR_VERDE_ACENTO);
        lblGananciaMax.setFont(new Font("Arial", Font.BOLD, 14));
        panelGanancia.add(lblGananciaTitulo);
        panelGanancia.add(lblGananciaMax);

        btnLanzar = new JButton("¡LANZAR DADOS!");
        btnLanzar.setBackground(COLOR_VERDE_ACENTO);
        btnLanzar.setForeground(COLOR_FONDO_PRINCIPAL);
        btnLanzar.setFont(new Font("Arial", Font.BOLD, 16));
        btnLanzar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLanzar.setMaximumSize(new Dimension(210, 40));
        btnLanzar.setFocusPainted(false);
        btnLanzar.addActionListener(e -> iniciarLanzamiento());

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
        panel.add(btnLanzar);

        return panel;
    }

    private JPanel crearPanelJuego() {
        JPanel panelJuego = new JPanel(new GridBagLayout());
        panelJuego.setBackground(COLOR_FONDO_PANEL);
        panelJuego.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "MESA DE DADOS",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        JPanel panelDados = new JPanel(new GridLayout(1, 3, 30, 0));
        panelDados.setBackground(COLOR_FONDO_PANEL);

        // Diseñar las 3 cajas contenedoras de los dados
        lblDado1 = crearEstiloDado("⚀");
        lblDado2 = crearEstiloDado("⚀");
        lblDado3 = crearEstiloDado("⚀");

        panelDados.add(lblDado1);
        panelDados.add(lblDado2);
        panelDados.add(lblDado3);

        panelJuego.add(panelDados);
        return panelJuego;
    }

    private JLabel crearEstiloDado(String caraInicial) {
        JLabel label = new JLabel(caraInicial, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 110)); // Fuente ideal para dados limpios
        label.setOpaque(true);
        label.setBackground(COLOR_FONDO_PRINCIPAL);
        label.setForeground(COLOR_TEXTO);
        label.setPreferredSize(new Dimension(180, 180));
        label.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_ACENTO, 2, true));
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
                + "<br><b style='color: #FFFFFF;'>1.</b> Ajusta el valor de tu apuesta.<br><br>"
                + "<b style='color: #FFFFFF;'>2.</b> Presiona <b style='color: #00E62A;'>¡LANZAR DADOS!</b>.<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> Los 3 dados rodarán en la mesa.<br><br>"
                + "<b style='color: #FFFFFF; font-size: 12px;'>Tabla de Premios:</b><br>"
                + "🎲 <b>Trío de 6 (⚅⚅⚅):</b> <b style='color: #00E62A;'>Apu x 8</b><br>"
                + "🎲 <b>Cualquier otro Trío:</b> <b>Apu x 4</b><br>"
                + "🎲 <b>Cualquier Par:</b> <b>Apu x 1.5</b><br>"
                + "🎲 <b>Suma total de 7 u 11:</b> <b>Apu x 1.2</b><br><br>"
                + "💡 <i>Si no logras ninguna de estas combinaciones, pierdes la apuesta.</i>"
                + "</body></html>";

        JLabel lblReglas = new JLabel(reglasHTML);
        lblReglas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(lblReglas);

        return panel;
    }

    private void modificarApuesta(int valor) {
        if (lanzando) return;
        if (montoApuesta + valor >= 50) {
            montoApuesta += valor;
            txtMontoApuesta.setText(String.valueOf(montoApuesta));
            actualizarGananciaMax();
        }
    }

    private void actualizarGananciaMax() {
        lblGananciaMax.setText(String.format("%,d UP", montoApuesta * 8));
    }

    private void setControlesHabilitados(boolean habilitado) {
        btnLanzar.setEnabled(habilitado);
        btnMas.setEnabled(habilitado);
        btnMenos.setEnabled(habilitado);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(habilitado);
        }
    }

    // LÓGICA DE JUEGO MODIFICADA: Lanzamiento e inercia de dados
    private void iniciarLanzamiento() {
        if (montoApuesta > monedasJugador) {
            JOptionPane.showMessageDialog(this,
                    "❌ No tienes suficientes UP para realizar esta apuesta.",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lanzando) return;

        lanzando = true;
        setControlesHabilitados(false);

        // Descontar saldo
        monedasJugador -= montoApuesta;
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        // Hilo de animación para simular el giro de los dados
        Thread hiloDados = new Thread(() -> {
            Random rand = new Random();

            // Cantidad de rebotes/vueltas de cada dado de manera independiente
            int girosD1 = 12 + rand.nextInt(6);
            int girosD2 = 18 + rand.nextInt(6);
            int girosD3 = 24 + rand.nextInt(6);

            int maxGiros = girosD3;

            int valorD1 = 0, valorD2 = 0, valorD3 = 0;

            for (int i = 0; i <= maxGiros; i++) {
                try {
                    Thread.sleep(70); // Velocidad de actualización visual
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                // Mientras no se detenga su inercia, cambia la cara
                if (i <= girosD1) {
                    valorD1 = rand.nextInt(6); // Índice del 0 al 5
                }
                if (i <= girosD2) {
                    valorD2 = rand.nextInt(6);
                }
                if (i <= girosD3) {
                    valorD3 = rand.nextInt(6);
                }

                final int d1 = valorD1;
                final int d2 = valorD2;
                final int d3 = valorD3;

                SwingUtilities.invokeLater(() -> {
                    lblDado1.setText(CARAS_DADOS[d1]);
                    lblDado2.setText(CARAS_DADOS[d2]);
                    lblDado3.setText(CARAS_DADOS[d3]);
                });
            }

            // Guardar los valores numéricos reales (1 al 6)
            final int resultadoD1 = valorD1 + 1;
            final int resultadoD2 = valorD2 + 1;
            final int resultadoD3 = valorD3 + 1;

            // Procesar las reglas en la UI
            SwingUtilities.invokeLater(() -> {
                evaluarResultado(resultadoD1, resultadoD2, resultadoD3);
            });
        });

        hiloDados.start();
    }

    private void evaluarResultado(int d1, int d2, int d3) {
        int premio = 0;
        double multiplicador = 0;
        int sumaTotal = d1 + d2 + d3;
        String mensaje = "";
        String titulo = "Fin del lanzamiento";
        int tipoIcono = JOptionPane.INFORMATION_MESSAGE;

        // Regla 1: Trío de 6 (El Jackpot Máximo)
        if (d1 == 6 && d2 == 6 && d3 == 6) {
            multiplicador = 8;
            premio = montoApuesta * (int)multiplicador;
            mensaje = "🔥 ¡¡FANTÁSTICO!! ¡Sacaste Trío de Ases (6-6-6)! 🔥\nMultiplicador x8. ¡Ganas " + premio + " UP!";
            titulo = "¡JACKPOT!";

            // Regla 2: Cualquier otro Trío (ej: 3-3-3)
        } else if (d1 == d2 && d2 == d3) {
            multiplicador = 4;
            premio = montoApuesta * (int)multiplicador;
            mensaje = "🎉 ¡Excelente! Trío de [" + d1 + "].\nMultiplicador x4. Recibes: " + premio + " UP.";
            titulo = "¡Gran Victoria!";

            // Regla 3: Un par idéntico (ej: 4-4-1)
        } else if (d1 == d2 || d2 == d3 || d1 == d3) {
            multiplicador = 1.5;
            premio = (int)(montoApuesta * multiplicador);
            mensaje = "✨ ¡Bien hecho! Lograste un Par.\nMultiplicador x1.5. Recibes: " + premio + " UP.";
            titulo = "¡Ganador!";

            // Regla 4: Suma de la suerte (7 u 11)
        } else if (sumaTotal == 7 || sumaTotal == 11) {
            multiplicador = 1.2;
            premio = (int)(montoApuesta * multiplicador);
            mensaje = "🍀 ¡Suma de la suerte! Tus dados sumaron " + sumaTotal + ".\nMultiplicador x1.2. Recibes: " + premio + " UP.";
            titulo = "Suerte";

            // Regla 5: Pérdida total
        } else {
            mensaje = "💸 Mala suerte. La combinación no sumó nada especial [" + d1 + "-" + d2 + "-" + d3 + "] (Suma: " + sumaTotal + ").\n¡Prueba otra vez!";
            tipoIcono = JOptionPane.WARNING_MESSAGE;
        }

        // Procesamiento monetario y racha de victorias
        if (multiplicador > 0) {
            monedasJugador += premio;
            if (JuegosGanadosSeguidos < 3) {
                JuegosGanadosSeguidos++;
            }
        } else {
            JuegosGanadosSeguidos = 0;
        }

        System.out.println("El número de Victorias seguidas es: " + JuegosGanadosSeguidos);
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoIcono);

        // Actualizar UI post-juego
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        lanzando = false;
        setControlesHabilitados(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoDados().setVisible(true);
        });
    }
}