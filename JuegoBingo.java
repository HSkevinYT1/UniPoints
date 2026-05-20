import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class JuegoBingo extends JFrame {

    private int monedasJugador = 1200;
    private String nombreJugador = "juan cacorro";

    // Variables del juego
    private int montoApuesta = 500;
    private boolean juegoEnCurso = false;

    // Lógica del Bingo
    private int[] cartonNumeros = new int[9]; // Cartón de 3x3 (9 números)
    private boolean[] cartonMarcados = new boolean[9];
    private ArrayList<Integer> tombola = new ArrayList<>();
    private final int BALOTAS_BASE = 60; // NUEVO MÍNIMO: 60 balotas base
    private int balotasRondaActual = 60; // Balotas asignadas para la partida actual

    // Elementos de la Interfaz UI
    private JLabel lblMonedas;
    private JTextField txtMontoApuesta;
    private JLabel lblGananciaPotencial;
    private JButton btnApostar;
    private JButton btnMenos, btnMas;
    private JButton btnBalotasExtra;
    private JButton[] btnRapidos = new JButton[4];
    private JLabel lblBalotaActual;
    private JLabel lblBalotasContador;
    private JPanel panelCarton;
    private JLabel[] lblCeldasCarton = new JLabel[9];

    // Paleta de Colores
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);
    private final Color COLOR_AZUL_BOTON = new Color(0, 150, 255);

    public JuegoBingo() {
        // Cargar datos reales del usuario logueado para consistencia global
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        } else {
            this.monedasJugador = 500;
            this.nombreJugador = "Invitado";
        }

        setTitle("Unab Points - Casino Bingo");
        setSize(1000, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(10, 10));

        // Generar un cartón inicial visual
        generarNuevoCarton();

        // 1. BARRA SUPERIOR (HEADER)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (APUESTAS Y EXTRAS)
        add(crearPanelApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (EL CARTÓN Y LA TÓMBOLA)
        add(crearPanelJuego(), BorderLayout.CENTER);

        // 4. PANEL DERECHO (CÓMO JUGAR)
        add(crearPanelComoJugar(), BorderLayout.EAST);

        // Preservar tamaño/maximizado
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
            JuegoBingo.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Casino Bingo", SwingConstants.LEFT);
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

        JLabel lblApuesta = new JLabel("Compra de Cartón");
        lblApuesta.setForeground(COLOR_TEXTO);
        lblApuesta.setFont(new Font("Arial", Font.BOLD, 16));
        lblApuesta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMonto = new JLabel("Precio:");
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
            btnRapidos[i].setMargin(new Insets(2, 2, 2, 2));
            btnRapidos[i].addActionListener(e -> {
                montoApuesta = m;
                txtMontoApuesta.setText(String.valueOf(montoApuesta));
                actualizarGananciaPotencial();
            });
            panelBotonesRapidos.add(btnRapidos[i]);
        }

        JLabel lblExtras = new JLabel("Ventaja:");
        lblExtras.setForeground(COLOR_TEXTO_MUTED);
        lblExtras.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnBalotasExtra = new JButton("+5 Balotas Extra (50 UP)");
        btnBalotasExtra.setBackground(COLOR_AZUL_BOTON);
        btnBalotasExtra.setForeground(COLOR_TEXTO);
        btnBalotasExtra.setFont(new Font("Arial", Font.BOLD, 12));
        btnBalotasExtra.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBalotasExtra.setMaximumSize(new Dimension(210, 35));
        btnBalotasExtra.setFocusPainted(false);
        btnBalotasExtra.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnBalotasExtra.addActionListener(e -> comprarBalotasExtra());

        JPanel panelGanancia = new JPanel(new GridLayout(2, 1));
        panelGanancia.setBackground(COLOR_FONDO_PRINCIPAL);
        panelGanancia.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));
        panelGanancia.setMaximumSize(new Dimension(210, 60));
        panelGanancia.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGananciaTitulo = new JLabel("Premio Bingo x3", SwingConstants.CENTER);
        lblGananciaTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblGananciaPotencial = new JLabel(String.format("%,d UP", montoApuesta * 3), SwingConstants.CENTER);
        lblGananciaPotencial.setForeground(COLOR_VERDE_ACENTO);
        lblGananciaPotencial.setFont(new Font("Arial", Font.BOLD, 14));
        panelGanancia.add(lblGananciaTitulo);
        panelGanancia.add(lblGananciaPotencial);

        btnApostar = new JButton("Jugar Bingo");
        btnApostar.setBackground(COLOR_VERDE_ACENTO);
        btnApostar.setForeground(COLOR_FONDO_PRINCIPAL);
        btnApostar.setFont(new Font("Arial", Font.BOLD, 16));
        btnApostar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnApostar.setMaximumSize(new Dimension(210, 40));
        btnApostar.setFocusPainted(false);
        btnApostar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnApostar.addActionListener(e -> iniciarBingo());

        panel.add(lblApuesta); panel.add(Box.createVerticalStrut(5));
        panel.add(lblMonto); panel.add(Box.createVerticalStrut(5));
        panel.add(panelMontoCtrl); panel.add(Box.createVerticalStrut(8));
        panel.add(panelBotonesRapidos); panel.add(Box.createVerticalStrut(15));
        panel.add(lblExtras); panel.add(Box.createVerticalStrut(5));
        panel.add(btnBalotasExtra);
        panel.add(Box.createVerticalGlue());
        panel.add(panelGanancia); panel.add(Box.createVerticalStrut(15));
        panel.add(btnApostar);

        return panel;
    }

    private JPanel crearPanelJuego() {
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(COLOR_FONDO_PRINCIPAL);

        JPanel panelTombola = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTombola.setBackground(COLOR_FONDO_PANEL);
        panelTombola.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelTombola.setPreferredSize(new Dimension(0, 80));

        lblBalotaActual = new JLabel("--", SwingConstants.CENTER);
        lblBalotaActual.setFont(new Font("Arial", Font.BOLD, 36));
        lblBalotaActual.setForeground(COLOR_VERDE_ACENTO);
        lblBalotaActual.setOpaque(true);
        lblBalotaActual.setBackground(COLOR_FONDO_PRINCIPAL);
        lblBalotaActual.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_ACENTO, 2, true));

        lblBalotasContador = new JLabel("Balotas en juego: " + balotasRondaActual, SwingConstants.CENTER);
        lblBalotasContador.setFont(new Font("Arial", Font.BOLD, 14));
        lblBalotasContador.setForeground(COLOR_TEXTO);

        panelTombola.add(lblBalotasContador);
        panelTombola.add(lblBalotaActual);

        panelCarton = new JPanel(new GridLayout(3, 3, 10, 10));
        panelCarton.setBackground(COLOR_FONDO_PANEL);
        panelCarton.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO_MUTED), "TU CARTÓN DE BINGO",
                0, 0, new Font("Arial", Font.BOLD, 14), COLOR_TEXTO));

        for (int i = 0; i < 9; i++) {
            lblCeldasCarton[i] = new JLabel(String.valueOf(cartonNumeros[i]), SwingConstants.CENTER);
            lblCeldasCarton[i].setFont(new Font("Arial", Font.BOLD, 24));
            lblCeldasCarton[i].setForeground(COLOR_TEXTO);
            lblCeldasCarton[i].setOpaque(true);
            lblCeldasCarton[i].setBackground(COLOR_FONDO_PRINCIPAL);
            lblCeldasCarton[i].setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1));
            panelCarton.add(lblCeldasCarton[i]);
        }

        panelCentral.add(panelTombola, BorderLayout.NORTH);
        panelCentral.add(panelCarton, BorderLayout.CENTER);

        return panelCentral;
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

        // REGLAS ACTUALIZADAS (MÍNIMO 60 BALOTAS Y SIN TOPE MÁXIMO)
        String reglasHTML = "<html><body style='width: 150px; color: #8A8F99; font-family: Arial; font-size: 11px;'>"
                + "<br><b style='color: #FFFFFF;'>1.</b> Selecciona el valor de tu cartón.<br><br>"
                + "<b style='color: #0096FF;'>¿Quieres más ventaja?</b> Presiona '+5 Balotas Extra' por <b style='color: #FFFFFF;'>50 UP</b> todas las veces que quieras para sumar más tiros a la tómbola en esa ronda.<br><br>"
                + "<b style='color: #FFFFFF;'>2.</b> Presiona 'Jugar Bingo'. La tómbola sacará las balotas configuradas (Mínimo <b style='color: #FFFFFF;'>60</b>).<br><br>"
                + "<b style='color: #FFFFFF;'>3.</b> Si completas los 9 números de tu cartón antes de terminar, ¡haces BINGO y <b>triplicas (x3)</b> tu apuesta!<br><br>"
                + "<b style='color: #00E62A;'>¡Mucha suerte!</b>"
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
        lblGananciaPotencial.setText(String.format("%,d UP", montoApuesta * 3));
    }

    private void comprarBalotasExtra() {
        if (juegoEnCurso) return;

        if (monedasJugador < 50) {
            JOptionPane.showMessageDialog(this,
                    "❌ No tienes suficientes UP para añadir más balotas (Costo: 50 UP).",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // RESTRICCIÓN DE TOPE DE 60 ELIMINADA TOTALMENTE.
        monedasJugador -= 50;
        balotasRondaActual += 5;

        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }
        lblBalotasContador.setText("Balotas en juego: " + balotasRondaActual);
    }

    private void setControlesHabilitados(boolean habilitado) {
        btnApostar.setEnabled(habilitado);
        btnMas.setEnabled(habilitado);
        btnMenos.setEnabled(habilitado);
        btnBalotasExtra.setEnabled(habilitado);
        for (JButton btn : btnRapidos) {
            if (btn != null) btn.setEnabled(habilitado);
        }
    }

    private void generarNuevoCarton() {
        Random rand = new Random();
        ArrayList<Integer> listaNumeros = new ArrayList<>();
        // El universo de números para generar el cartón sigue siendo del 1 al 100
        // para dar dinamismo si se juegan muchas balotas consecutivas.
        while (listaNumeros.size() < 9) {
            int num = rand.nextInt(100) + 1;
            if (!listaNumeros.contains(num)) {
                listaNumeros.add(num);
            }
        }
        Collections.sort(listaNumeros);

        for (int i = 0; i < 9; i++) {
            cartonNumeros[i] = listaNumeros.get(i);
            cartonMarcados[i] = false;
        }
    }

    private void actualizarCartonVisual() {
        for (int i = 0; i < 9; i++) {
            lblCeldasCarton[i].setText(String.valueOf(cartonNumeros[i]));
            if (cartonMarcados[i]) {
                lblCeldasCarton[i].setBackground(COLOR_VERDE_ACENTO);
                lblCeldasCarton[i].setForeground(COLOR_FONDO_PRINCIPAL);
            } else {
                lblCeldasCarton[i].setBackground(COLOR_FONDO_PRINCIPAL);
                lblCeldasCarton[i].setForeground(COLOR_TEXTO);
            }
        }
    }

    private boolean verificarBingo() {
        for (boolean marcado : cartonMarcados) {
            if (!marcado) return false;
        }
        return true;
    }

    private void iniciarBingo() {
        if (montoApuesta > monedasJugador) {
            JOptionPane.showMessageDialog(this,
                    "❌ No tienes suficientes UP para comprar este cartón.",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (juegoEnCurso) return;

        juegoEnCurso = true;
        setControlesHabilitados(false);

        monedasJugador -= montoApuesta;
        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }

        generarNuevoCarton();
        actualizarCartonVisual();

        int balotasDisponibles = balotasRondaActual;
        lblBalotasContador.setText("Balotas restantes: " + balotasDisponibles);

        // La tómbola ahora genera dinámicamente suficientes números únicos (hasta 120)
        // para soportar si el jugador decide comprar muchas balotas extra en la ronda.
        tombola.clear();
        int maxRangoTombola = Math.max(100, balotasDisponibles + 10);
        for (int i = 1; i <= maxRangoTombola; i++) tombola.add(i);
        Collections.shuffle(tombola);

        Thread hiloBingo = new Thread(() -> {
            boolean bingoAlcanzado = false;
            int restantes = balotasDisponibles;

            while (restantes > 0 && !bingoAlcanzado) {
                try {
                    Thread.sleep(400); // Velocidad de juego optimizada ligeramente a 0.4s por balota
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                int balotaSada = tombola.remove(0);
                restantes--;

                for (int i = 0; i < 9; i++) {
                    if (cartonNumeros[i] == balotaSada) {
                        cartonMarcados[i] = true;
                    }
                }

                bingoAlcanzado = verificarBingo();

                final int numBalota = balotaSada;
                final int tempRestantes = restantes;
                SwingUtilities.invokeLater(() -> {
                    lblBalotaActual.setText(String.valueOf(numBalota));
                    lblBalotasContador.setText("Balotas restantes: " + tempRestantes);
                    actualizarCartonVisual();
                });
            }

            final boolean gano = bingoAlcanzado;
            SwingUtilities.invokeLater(() -> evaluarResultado(gano));
        });

        hiloBingo.start();
    }

    private void evaluarResultado(boolean gano) {
        if (gano) {
            int premio = montoApuesta * 3;
            monedasJugador += premio;
            JOptionPane.showMessageDialog(this,
                    "🎉 ¡¡BINGO!! 🎉\nLograste tachar todo tu cartón.\nRecibes: " + premio + " UP.",
                    "¡Victoria!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "💸 Se acabaron las balotas.\nNo lograste completar tu cartón esta vez.",
                    "Fin de la ronda",
                    JOptionPane.WARNING_MESSAGE);
        }

        // Restablecer el nuevo valor base mínimo (60) para el próximo turno
        balotasRondaActual = BALOTAS_BASE;
        lblBalotasContador.setText("Balotas en juego: " + balotasRondaActual);

        lblMonedas.setText(String.format("%,d UP", monedasJugador));
        if (Usuario.getUsuarioActual() != null) {
            Usuario.getUsuarioActual().setSaldo(monedasJugador);
        }
        lblBalotaActual.setText("--");
        juegoEnCurso = false;
        setControlesHabilitados(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoBingo().setVisible(true);
        });
    }
}