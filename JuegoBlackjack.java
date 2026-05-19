import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JuegoBlackjack extends JFrame {

    // Datos principales del jugador y estado de la ronda.
    private int monedasJugador = 500;
    private String nombreJugador = "Invitado";
    private int montoApuesta = 100;
    private boolean rondaEnCurso = false;
    private boolean animacionEnCurso = false;

    // Mazo y manos usadas durante la partida.
    private final List<Carta> mazo = new ArrayList<>();
    private final List<Carta> manoJugador = new ArrayList<>();
    private final List<Carta> manoDealer = new ArrayList<>();

    // Componentes visuales que cambian durante el juego.
    private JLabel lblMonedas;
    private JLabel lblEstado;
    private JLabel lblPuntosJugador;
    private JLabel lblPuntosDealer;
    private JLabel lblGananciaPotencial;
    private JTextField txtMontoApuesta;
    private JPanel panelCartasJugador;
    private JPanel panelCartasDealer;
    private JButton btnRepartir;
    private JButton btnPedir;
    private JButton btnPlantarse;
    private JButton btnMas;
    private JButton btnMenos;
    private final JButton[] btnRapidos = new JButton[4];

    // Paleta de colores del juego.
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);
    private final Color COLOR_ROJO = new Color(230, 70, 70);
    private final Color COLOR_DORADO = new Color(245, 166, 35);

    // Configura la ventana principal del Blackjack.
    public JuegoBlackjack() {
        if (Usuario.getUsuarioActual() != null) {
            monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            nombreJugador = Usuario.getUsuarioActual().getNombre();
        }

        setTitle("Unab Points - Blackjack");
        setSize(1050, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelApuestas(), BorderLayout.WEST);
        add(crearPanelJuego(), BorderLayout.CENTER);
        add(crearPanelComoJugar(), BorderLayout.EAST);

        crearNuevoMazo();
        actualizarEstadoControles();
    }

    // Crea la barra superior con boton de volver, nombre y saldo.
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
            JuegoBlackjack.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Blackjack", SwingConstants.LEFT);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelIzquierdo.setBackground(COLOR_FONDO_PRINCIPAL);
        panelIzquierdo.add(btnVolver);
        panelIzquierdo.add(lblTitulo);

        lblMonedas = new JLabel(String.format("%,d UP", monedasJugador), SwingConstants.CENTER);
        lblMonedas.setOpaque(true);
        lblMonedas.setBackground(COLOR_FONDO_PANEL);
        lblMonedas.setForeground(COLOR_VERDE_ACENTO);
        lblMonedas.setFont(new Font("Arial", Font.BOLD, 14));
        lblMonedas.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_ACENTO, 1, true));
        lblMonedas.setPreferredSize(new Dimension(120, 35));

        header.add(panelIzquierdo, BorderLayout.WEST);
        header.add(lblMonedas, BorderLayout.EAST);
        return header;
    }

    // Crea el panel lateral donde se controla la apuesta y las acciones.
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

        JPanel panelMonto = new JPanel(new BorderLayout(5, 0));
        panelMonto.setBackground(COLOR_FONDO_PANEL);
        panelMonto.setMaximumSize(new Dimension(210, 30));
        panelMonto.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtMontoApuesta = new JTextField(String.valueOf(montoApuesta));
        txtMontoApuesta.setBackground(COLOR_FONDO_PRINCIPAL);
        txtMontoApuesta.setForeground(COLOR_TEXTO);
        txtMontoApuesta.setCaretColor(COLOR_TEXTO);
        txtMontoApuesta.setHorizontalAlignment(JTextField.CENTER);
        txtMontoApuesta.setEditable(false);
        txtMontoApuesta.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        btnMenos = crearBotonPlano("-");
        btnMenos.addActionListener(e -> modificarApuesta(-50));
        btnMas = crearBotonPlano("+");
        btnMas.addActionListener(e -> modificarApuesta(50));

        panelMonto.add(btnMenos, BorderLayout.WEST);
        panelMonto.add(txtMontoApuesta, BorderLayout.CENTER);
        panelMonto.add(btnMas, BorderLayout.EAST);

        JPanel panelRapidos = new JPanel(new GridLayout(1, 4, 5, 0));
        panelRapidos.setBackground(COLOR_FONDO_PANEL);
        panelRapidos.setMaximumSize(new Dimension(210, 30));
        panelRapidos.setAlignmentX(Component.LEFT_ALIGNMENT);

        int[] montosRapidos = {100, 250, 500, 1000};
        for (int i = 0; i < montosRapidos.length; i++) {
            int monto = montosRapidos[i];
            btnRapidos[i] = crearBotonPlano(String.valueOf(monto));
            btnRapidos[i].setFont(new Font("Arial", Font.PLAIN, 10));
            btnRapidos[i].addActionListener(e -> {
                montoApuesta = monto;
                txtMontoApuesta.setText(String.valueOf(montoApuesta));
                actualizarGananciaPotencial();
            });
            panelRapidos.add(btnRapidos[i]);
        }

        JPanel panelGanancia = new JPanel(new GridLayout(2, 1));
        panelGanancia.setBackground(COLOR_FONDO_PRINCIPAL);
        panelGanancia.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));
        panelGanancia.setMaximumSize(new Dimension(210, 60));
        panelGanancia.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGananciaTitulo = new JLabel("Ganancia normal x2", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaPotencial = new JLabel(String.format("%,d UP", montoApuesta * 2), SwingConstants.CENTER);
        lblGananciaPotencial.setForeground(COLOR_VERDE_ACENTO);
        lblGananciaPotencial.setFont(new Font("Arial", Font.BOLD, 14));
        panelGanancia.add(lblGananciaTitulo);
        panelGanancia.add(lblGananciaPotencial);

        btnRepartir = crearBotonPrincipal("Repartir");
        btnRepartir.addActionListener(e -> iniciarRonda());
        btnPedir = crearBotonAccion("Pedir carta");
        btnPedir.addActionListener(e -> pedirCartaJugador());
        btnPlantarse = crearBotonAccion("Plantarse");
        btnPlantarse.addActionListener(e -> turnoDealer());

        panel.add(lblApuesta);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMonto);
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelMonto);
        panel.add(Box.createVerticalStrut(8));
        panel.add(panelRapidos);
        panel.add(Box.createVerticalGlue());
        panel.add(panelGanancia);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnRepartir);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPedir);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPlantarse);
        return panel;
    }

    // Crea la mesa central donde aparecen las cartas del jugador y del dealer.
    private JPanel crearPanelJuego() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "MESA DE BLACKJACK",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        lblEstado = new JLabel("Presiona Repartir para empezar", SwingConstants.CENTER);
        lblEstado.setForeground(COLOR_DORADO);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 18));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mesa = new JPanel(new GridLayout(2, 1, 0, 14));
        mesa.setBackground(COLOR_FONDO_PANEL);
        mesa.setBorder(BorderFactory.createEmptyBorder(10, 18, 18, 18));

        panelCartasDealer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelCartasDealer.setBackground(COLOR_FONDO_PRINCIPAL);
        panelCartasJugador = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelCartasJugador.setBackground(COLOR_FONDO_PRINCIPAL);

        lblPuntosDealer = crearTituloMano("Dealer: 0");
        lblPuntosJugador = crearTituloMano("Jugador: 0");

        mesa.add(crearPanelMano(lblPuntosDealer, panelCartasDealer));
        mesa.add(crearPanelMano(lblPuntosJugador, panelCartasJugador));

        panel.add(lblEstado, BorderLayout.NORTH);
        panel.add(mesa, BorderLayout.CENTER);
        return panel;
    }

    // Crea el panel lateral con las reglas basicas del Blackjack.
    private JPanel crearPanelComoJugar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(230, 0));

        JLabel lblTitulo = new JLabel("Como jugar");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String reglasHTML = "<html><body style='width: 160px; color: #8A8F99; font-family: Arial; font-size: 11px;'>"
                + "<br><b style='color: #FFFFFF;'>1.</b> Apuesta y presiona Repartir.<br><br>"
                + "<b style='color: #FFFFFF;'>2.</b> Pide cartas para acercarte a 21 sin pasarte.<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> Plantate para que juegue el dealer.<br><br>"
                + "<b style='color: #FFFFFF;'>4.</b> Blackjack inicial paga x2.5. Victoria normal paga x2.<br><br>"
                + "<b style='color: #00E62A;'>21 es la meta.</b>"
                + "</body></html>";

        JLabel lblReglas = new JLabel(reglasHTML);
        lblReglas.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(lblReglas);
        return panel;
    }

    // Crea el titulo de cada mano con su puntaje.
    private JLabel crearTituloMano(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(COLOR_TEXTO);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return label;
    }

    // Agrupa el titulo y las cartas de una mano.
    private JPanel crearPanelMano(JLabel titulo, JPanel cartas) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO_PRINCIPAL);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1, true));
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(cartas, BorderLayout.CENTER);
        return panel;
    }

    // Crea botones pequeños usados en la apuesta.
    private JButton crearBotonPlano(String text) {
        JButton boton = new JButton(text);
        boton.setBackground(COLOR_FONDO_PRINCIPAL);
        boton.setForeground(COLOR_TEXTO_MUTED);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        boton.setMargin(new Insets(2, 2, 2, 2));
        return boton;
    }

    // Crea el boton principal para iniciar la ronda.
    private JButton crearBotonPrincipal(String text) {
        JButton boton = new JButton(text);
        boton.setBackground(COLOR_VERDE_ACENTO);
        boton.setForeground(COLOR_FONDO_PRINCIPAL);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(210, 40));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return boton;
    }

    // Crea botones de accion durante la ronda.
    private JButton crearBotonAccion(String text) {
        JButton boton = new JButton(text);
        boton.setBackground(COLOR_FONDO_PRINCIPAL);
        boton.setForeground(COLOR_TEXTO);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(210, 38));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1, true));
        return boton;
    }

    // Aumenta o disminuye la apuesta mientras no hay ronda activa.
    private void modificarApuesta(int valor) {
        if (rondaEnCurso) return;
        int nuevoMonto = montoApuesta + valor;
        if (nuevoMonto >= 50) {
            montoApuesta = nuevoMonto;
            txtMontoApuesta.setText(String.valueOf(montoApuesta));
            actualizarGananciaPotencial();
        }
    }

    // Actualiza el texto de la ganancia posible.
    private void actualizarGananciaPotencial() {
        lblGananciaPotencial.setText(String.format("%,d UP", montoApuesta * 2));
    }

    // Inicia una ronda, descuenta la apuesta y reparte las cartas iniciales.
    private void iniciarRonda() {
        if (montoApuesta > monedasJugador) {
            JOptionPane.showMessageDialog(this, "No tienes suficientes UP para realizar esta apuesta.",
                    "Saldo insuficiente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rondaEnCurso = true;
        monedasJugador -= montoApuesta;
        guardarSaldo();

        manoJugador.clear();
        manoDealer.clear();
        if (mazo.size() < 15) crearNuevoMazo();

        lblEstado.setForeground(COLOR_DORADO);
        lblEstado.setText("Repartiendo cartas...");
        renderizarManos(false);
        actualizarEstadoControles();

        Carta jugador1 = robarCarta();
        Carta dealer1 = robarCarta();
        Carta jugador2 = robarCarta();
        Carta dealer2 = robarCarta();
        animarRepartoInicial(jugador1, dealer1, jugador2, dealer2);
    }

    // Agrega una carta al jugador y revisa si se pasa o llega a 21.
    private void pedirCartaJugador() {
        if (!rondaEnCurso || animacionEnCurso) return;
        manoJugador.add(robarCarta());
        renderizarManos(false);

        animacionEnCurso = true;
        actualizarEstadoControles();
        Timer revisarCarta = new Timer(420, e -> {
            ((Timer) e.getSource()).stop();
            animacionEnCurso = false;
            int puntos = calcularPuntos(manoJugador);
            if (puntos > 21) {
                finalizarRonda("Te pasaste de 21. Pierdes la apuesta.", 0, false);
            } else if (puntos == 21) {
                turnoDealer();
            } else {
                actualizarEstadoControles();
            }
        });
        revisarCarta.setRepeats(false);
        revisarCarta.start();
    }

    // Ejecuta el turno del dealer, pidiendo cartas hasta llegar minimo a 17.
    private void turnoDealer() {
        if (!rondaEnCurso || animacionEnCurso) return;

        lblEstado.setForeground(COLOR_DORADO);
        lblEstado.setText("Turno del dealer...");
        animacionEnCurso = true;
        renderizarManos(true);
        actualizarEstadoControles();

        Timer dealerTimer = new Timer(620, null);
        dealerTimer.addActionListener(e -> {
            if (calcularPuntos(manoDealer) < 17) {
                manoDealer.add(robarCarta());
                renderizarManos(true);
                return;
            }

            dealerTimer.stop();
            animacionEnCurso = false;
            evaluarResultadoFinal();
        });
        dealerTimer.setInitialDelay(520);
        dealerTimer.start();
    }

    // Anima el reparto inicial carta por carta.
    private void animarRepartoInicial(Carta jugador1, Carta dealer1, Carta jugador2, Carta dealer2) {
        animacionEnCurso = true;
        Carta[] cartas = {jugador1, dealer1, jugador2, dealer2};
        boolean[] paraJugador = {true, false, true, false};
        final int[] indice = {0};

        Timer timer = new Timer(360, null);
        timer.addActionListener(e -> {
            if (indice[0] >= cartas.length) {
                timer.stop();
                animacionEnCurso = false;
                lblEstado.setText("Tu turno: pide carta o plantate");

                if (calcularPuntos(manoJugador) == 21) {
                    int premio = (int) (montoApuesta * 2.5);
                    finalizarRonda("Blackjack! Ganaste " + premio + " UP.", premio, true);
                } else {
                    renderizarManos(false);
                    actualizarEstadoControles();
                }
                return;
            }

            if (paraJugador[indice[0]]) {
                manoJugador.add(cartas[indice[0]]);
            } else {
                manoDealer.add(cartas[indice[0]]);
            }
            renderizarManos(false);
            indice[0]++;
        });
        timer.setInitialDelay(120);
        timer.start();
    }

    // Compara los puntajes finales y decide si gana jugador, dealer o hay empate.
    private void evaluarResultadoFinal() {
        int puntosJugador = calcularPuntos(manoJugador);
        int puntosDealer = calcularPuntos(manoDealer);

        if (puntosDealer > 21 || puntosJugador > puntosDealer) {
            finalizarRonda("Ganaste la mano. Recibes " + (montoApuesta * 2) + " UP.", montoApuesta * 2, true);
        } else if (puntosJugador == puntosDealer) {
            finalizarRonda("Empate. Recuperas tu apuesta.", montoApuesta, true);
        } else {
            finalizarRonda("Gana el dealer. Pierdes la apuesta.", 0, false);
        }
    }

    // Cierra la ronda, entrega premios si corresponde y actualiza la interfaz.
    private void finalizarRonda(String mensaje, int premio, boolean resultadoPositivo) {
        rondaEnCurso = false;
        monedasJugador += premio;
        guardarSaldo();
        renderizarManos(true);
        lblEstado.setForeground(resultadoPositivo ? COLOR_VERDE_ACENTO : COLOR_ROJO);
        lblEstado.setText(mensaje);
        actualizarEstadoControles();
    }

    // Habilita o bloquea botones segun el estado de la ronda y animaciones.
    private void actualizarEstadoControles() {
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        btnRepartir.setEnabled(!rondaEnCurso && !animacionEnCurso);
        btnPedir.setEnabled(rondaEnCurso && !animacionEnCurso);
        btnPlantarse.setEnabled(rondaEnCurso && !animacionEnCurso);
        btnMas.setEnabled(!rondaEnCurso && !animacionEnCurso);
        btnMenos.setEnabled(!rondaEnCurso && !animacionEnCurso);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(!rondaEnCurso && !animacionEnCurso);
        }
    }

    // Guarda el saldo en el usuario actual para mantenerlo entre ventanas.
    private void guardarSaldo() {
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }
    }

    // Crea y mezcla un mazo completo de 52 cartas.
    private void crearNuevoMazo() {
        mazo.clear();
        String[] palos = {"C", "D", "T", "P"};
        String[] rangos = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        for (String palo : palos) {
            for (String rango : rangos) {
                mazo.add(new Carta(rango, palo));
            }
        }
        Collections.shuffle(mazo);
    }

    // Toma una carta del mazo y lo regenera si queda vacio.
    private Carta robarCarta() {
        if (mazo.isEmpty()) crearNuevoMazo();
        return mazo.remove(0);
    }

    // Calcula el puntaje de una mano manejando el As como 1 u 11.
    private int calcularPuntos(List<Carta> mano) {
        int total = 0;
        int ases = 0;
        for (Carta carta : mano) {
            if ("A".equals(carta.rango)) {
                ases++;
                total += 11;
            } else if ("JQK".contains(carta.rango)) {
                total += 10;
            } else {
                total += Integer.parseInt(carta.rango);
            }
        }
        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        return total;
    }

    // Redibuja las cartas y actualiza los puntajes visibles.
    private void renderizarManos(boolean mostrarDealer) {
        panelCartasJugador.removeAll();
        panelCartasDealer.removeAll();

        for (Carta carta : manoJugador) {
            panelCartasJugador.add(crearCartaVisual(carta, true));
        }
        for (int i = 0; i < manoDealer.size(); i++) {
            boolean visible = mostrarDealer || i == 0 || !rondaEnCurso;
            panelCartasDealer.add(crearCartaVisual(manoDealer.get(i), visible));
        }

        lblPuntosJugador.setText("Jugador: " + calcularPuntos(manoJugador));
        lblPuntosDealer.setText(mostrarDealer || !rondaEnCurso ? "Dealer: " + calcularPuntos(manoDealer) : "Dealer: ?");

        panelCartasJugador.revalidate();
        panelCartasJugador.repaint();
        panelCartasDealer.revalidate();
        panelCartasDealer.repaint();
    }

    // Crea el componente visual de una carta visible o boca abajo.
    private JPanel crearCartaVisual(Carta carta, boolean visible) {
        return new CartaPanel(carta, visible);
    }

    // Modelo simple de una carta del mazo.
    private static class Carta {
        private final String rango;
        private final String palo;

        Carta(String rango, String palo) {
            this.rango = rango;
            this.palo = palo;
        }

        boolean esRoja() {
            return "D".equals(palo) || "C".equals(palo);
        }

        // Convierte el palo interno en el simbolo visual de la carta.
        String simbolo() {
            switch (palo) {
                case "C": return "♥";
                case "D": return "♦";
                case "T": return "♣";
                case "P": return "♠";
                default: return palo;
            }
        }
    }

    // Componente encargado de dibujar y animar cada carta.
    private class CartaPanel extends JPanel {
        private final Carta carta;
        private final boolean visible;
        private float progreso = 0.15f;

        // Prepara una animacion corta de entrada.
        CartaPanel(Carta carta, boolean visible) {
            this.carta = carta;
            this.visible = visible;
            setOpaque(false);
            setPreferredSize(new Dimension(86, 118));

            Timer entrada = new Timer(18, e -> {
                progreso += 0.08f;
                if (progreso >= 1f) {
                    progreso = 1f;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            entrada.start();
        }

        // Dibuja la carta con sombra y escala animada.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            float escala = 0.85f + (0.15f * progreso);
            int cw = (int) (76 * escala);
            int ch = (int) (108 * escala);
            int x = (w - cw) / 2;
            int y = (h - ch) / 2 + (int) ((1f - progreso) * 18);

            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillRoundRect(x + 5, y + 7, cw, ch, 14, 14);

            if (visible) {
                pintarFrente(g2, x, y, cw, ch);
            } else {
                pintarReverso(g2, x, y, cw, ch);
            }
            g2.dispose();
        }

        // Dibuja el frente de la carta con rango, palo y esquinas.
        private void pintarFrente(Graphics2D g2, int x, int y, int w, int h) {
            Color suitColor = carta.esRoja() ? COLOR_ROJO : new Color(22, 22, 24);

            g2.setColor(new Color(248, 248, 242));
            g2.fillRoundRect(x, y, w, h, 14, 14);
            g2.setColor(new Color(210, 210, 205));
            g2.drawRoundRect(x, y, w - 1, h - 1, 14, 14);

            g2.setColor(suitColor);
            g2.setFont(new Font("Serif", Font.BOLD, 18));
            g2.drawString(carta.rango, x + 8, y + 22);
            g2.setFont(new Font("Serif", Font.BOLD, 17));
            g2.drawString(carta.simbolo(), x + 9, y + 40);

            g2.setFont(new Font("Serif", Font.BOLD, 42));
            FontMetrics fm = g2.getFontMetrics();
            String simbolo = carta.simbolo();
            g2.drawString(simbolo, x + (w - fm.stringWidth(simbolo)) / 2, y + (h + fm.getAscent()) / 2 - 4);

            Graphics2D corner = (Graphics2D) g2.create();
            corner.rotate(Math.PI, x + w / 2.0, y + h / 2.0);
            corner.setColor(suitColor);
            corner.setFont(new Font("Serif", Font.BOLD, 18));
            corner.drawString(carta.rango, x + 8, y + 22);
            corner.setFont(new Font("Serif", Font.BOLD, 17));
            corner.drawString(carta.simbolo(), x + 9, y + 40);
            corner.dispose();
        }

        // Dibuja el reverso de la carta con estilo verde UP.
        private void pintarReverso(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(new Color(8, 35, 18));
            g2.fillRoundRect(x, y, w, h, 14, 14);
            g2.setColor(COLOR_VERDE_ACENTO);
            g2.drawRoundRect(x, y, w - 1, h - 1, 14, 14);

            g2.setColor(new Color(44, 243, 53, 45));
            for (int i = 10; i < w; i += 14) {
                g2.drawLine(x + i, y + 8, x + w - 8, y + h - i);
                g2.drawLine(x + 8, y + i, x + w - i, y + h - 8);
            }

            g2.setColor(COLOR_VERDE_ACENTO);
            g2.setFont(new Font("SansSerif", Font.BOLD, 26));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("UP", x + (w - fm.stringWidth("UP")) / 2, y + h / 2 + 10);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JuegoBlackjack().setVisible(true));
    }
}
