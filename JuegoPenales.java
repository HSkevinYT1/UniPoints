import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class JuegoPenales extends JFrame {

    private int monedasJugador = 1200;
    private String nombreJugador = "juan cacorro";

    // Lógica Avanzada de Penales
    private int montoApuesta = 200;
    private int penalActual = 1;
    private int totalTirosTanda = 5; // Modificable dinámicamente (3, 5, 10, 20)
    private int golesAnotados = 0;
    private boolean juegoEnCurso = false;
    private final Random random = new Random();

    // Nombre de las 9 zonas del arco
    private final String[] nombresZonas = {
            "Ángulo Izquierdo", "Centro Alto", "Ángulo Derecho",
            "Medio Izquierdo",  "Centro Medio", "Medio Derecho",
            "Abajo Izquierdo",  "Raso Centro",  "Abajo Derecho"
    };

    // Al cubrir 3 áreas el portero, subimos los multiplicadores para recompensar el riesgo
    private final double[] multiplicadoresZonas = {
            3.5, 2.2, 3.5,
            2.8, 1.8, 2.8,
            3.0, 1.6, 3.0
    };

    // Componentes UI
    private JLabel lblMonedas;
    private JLabel lblHistorialTanda;
    private JLabel lblEstadoRonda;
    private JLabel lblPorteroAccion;
    private JTextField txtMontoApuesta;
    private JComboBox<Integer> comboCantidadTiros; // Selector de tiros
    private JButton btnApostar;
    private JButton btnMenos, btnMas;
    private JButton[] btnZonasArco = new JButton[9];

    // Paleta de Colores
    private final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);
    private final Color COLOR_ROJO = new Color(255, 50, 50);

    public JuegoPenales() {
        if (Usuario.getUsuarioActual() != null) {
            this.monedasJugador = (int) Usuario.getUsuarioActual().getSaldo();
            this.nombreJugador = Usuario.getUsuarioActual().getNombre();
        }

        setTitle("Unab Points - Penalty Shootout PRO");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);
        setLayout(new BorderLayout(15, 15));

        // 1. BARRA SUPERIOR
        add(crearHeader(), BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (CONTROL DE APUESTAS Y TIROS)
        add(crearPanelControlApuestas(), BorderLayout.WEST);

        // 3. PANEL CENTRAL (EL ARCO INTERACTIVO)
        add(crearPanelPorteria(), BorderLayout.CENTER);

        // 4. PANEL DERECHO (ESTADÍSTICAS Y REGLAS)
        add(crearPanelInfo(), BorderLayout.EAST);
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
            JuegoPenales.this.dispose();
        });

        JLabel lblTitulo = new JLabel("  " + nombreJugador + " - Portero de 3 Áreas 🧤⚽", SwingConstants.LEFT);
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
        lblMonedas.setPreferredSize(new Dimension(135, 35));

        header.add(panelIzquierdoHeader, BorderLayout.WEST);
        header.add(lblMonedas, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelControlApuestas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(240, 0));

        JLabel lblApuesta = new JLabel("Configurar Tanda");
        lblApuesta.setForeground(COLOR_TEXTO);
        lblApuesta.setFont(new Font("Arial", Font.BOLD, 16));
        lblApuesta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMonto = new JLabel("Riesgo por tiro:");
        lblMonto.setForeground(COLOR_TEXTO_MUTED);
        lblMonto.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Controles de valor de apuesta
        JPanel panelMontoCtrl = new JPanel(new BorderLayout(5, 0));
        panelMontoCtrl.setBackground(COLOR_FONDO_PANEL);
        panelMontoCtrl.setMaximumSize(new Dimension(210, 30));
        panelMontoCtrl.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtMontoApuesta = new JTextField(String.valueOf(montoApuesta));
        txtMontoApuesta.setBackground(COLOR_FONDO_PRINCIPAL);
        txtMontoApuesta.setForeground(COLOR_TEXTO);
        txtMontoApuesta.setHorizontalAlignment(JTextField.CENTER);
        txtMontoApuesta.setEditable(false);

        btnMenos = new JButton("-");
        btnMenos.setBackground(COLOR_FONDO_PRINCIPAL);
        btnMenos.setForeground(COLOR_TEXTO);
        btnMenos.setFocusPainted(false);
        btnMenos.addActionListener(e -> modificarApuesta(-50));

        btnMas = new JButton("+");
        btnMas.setBackground(COLOR_FONDO_PRINCIPAL);
        btnMas.setForeground(COLOR_TEXTO);
        btnMas.setFocusPainted(false);
        btnMas.addActionListener(e -> modificarApuesta(50));

        panelMontoCtrl.add(btnMenos, BorderLayout.WEST);
        panelMontoCtrl.add(txtMontoApuesta, BorderLayout.CENTER);
        panelMontoCtrl.add(btnMas, BorderLayout.EAST);

        // NUEVO: Selector de cantidad de tiros configurables
        JLabel lblTirosTanda = new JLabel("Longitud de la Tanda:");
        lblTirosTanda.setForeground(COLOR_TEXTO_MUTED);
        lblTirosTanda.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        lblTirosTanda.setAlignmentX(Component.LEFT_ALIGNMENT);

        Integer[] opcionesTiros = {3, 5, 10, 20};
        comboCantidadTiros = new JComboBox<>(opcionesTiros);
        comboCantidadTiros.setSelectedItem(5); // Por defecto 5
        comboCantidadTiros.setBackground(COLOR_FONDO_PRINCIPAL);
        comboCantidadTiros.setForeground(COLOR_TEXTO);
        comboCantidadTiros.setMaximumSize(new Dimension(210, 35));
        comboCantidadTiros.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboCantidadTiros.addActionListener(e -> {
            if (!juegoEnCurso) {
                totalTirosTanda = (int) comboCantidadTiros.getSelectedItem();
                inicializarHistorialVacio();
            }
        });

        btnApostar = new JButton("Iniciar Tanda");
        btnApostar.setBackground(COLOR_VERDE_ACENTO);
        btnApostar.setForeground(COLOR_FONDO_PRINCIPAL);
        btnApostar.setFont(new Font("Arial", Font.BOLD, 14));
        btnApostar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnApostar.setMaximumSize(new Dimension(210, 40));
        btnApostar.setFocusPainted(false);
        btnApostar.addActionListener(e -> iniciarNuevaTanda());

        panel.add(lblApuesta); panel.add(Box.createVerticalStrut(5));
        panel.add(lblMonto); panel.add(Box.createVerticalStrut(5));
        panel.add(panelMontoCtrl); panel.add(Box.createVerticalStrut(10));
        panel.add(lblTirosTanda);
        panel.add(comboCantidadTiros);
        panel.add(Box.createVerticalGlue());
        panel.add(btnApostar);

        return panel;
    }

    private JPanel crearPanelPorteria() {
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(COLOR_FONDO_PRINCIPAL);

        JPanel panelMarcador = new JPanel(new GridLayout(2, 1));
        panelMarcador.setBackground(COLOR_FONDO_PANEL);
        panelMarcador.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        lblEstadoRonda = new JLabel("Configura la tanda y presiona 'Iniciar Tanda'", SwingConstants.CENTER);
        lblEstadoRonda.setFont(new Font("Arial", Font.BOLD, 15));
        lblEstadoRonda.setForeground(COLOR_TEXTO);

        lblPorteroAccion = new JLabel("¡Cuidado! El arquero ahora puede tapar 3 zonas al mismo tiempo.", SwingConstants.CENTER);
        lblPorteroAccion.setFont(new Font("Arial", Font.ITALIC, 13));
        lblPorteroAccion.setForeground(COLOR_VERDE_ACENTO);

        panelMarcador.add(lblEstadoRonda);
        panelMarcador.add(lblPorteroAccion);

        JPanel gridArco = new JPanel(new GridLayout(3, 3, 12, 12));
        gridArco.setBackground(new Color(24, 34, 44));
        gridArco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 6),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        for (int i = 0; i < 9; i++) {
            final int indexZona = i;
            btnZonasArco[i] = new JButton("<html><center>" + nombresZonas[i] + "<br><font color='#00E62A'>x" + multiplicadoresZonas[i] + "</font></center></html>");
            btnZonasArco[i].setFont(new Font("Arial", Font.BOLD, 13));
            btnZonasArco[i].setBackground(COLOR_FONDO_PRINCIPAL);
            btnZonasArco[i].setForeground(COLOR_TEXTO);
            btnZonasArco[i].setFocusPainted(false);
            btnZonasArco[i].setEnabled(false);

            btnZonasArco[i].addActionListener(e -> ejecutarDisparo(indexZona));
            gridArco.add(btnZonasArco[i]);
        }

        panelCentral.add(panelMarcador, BorderLayout.NORTH);
        panelCentral.add(gridArco, BorderLayout.CENTER);

        return panelCentral;
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(230, 0));

        JLabel lblTitulo = new JLabel("Marcador Global");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHistorialTanda = new JLabel("⚪ ⚪ ⚪ ⚪ ⚪", SwingConstants.LEFT);
        lblHistorialTanda.setFont(new Font("Arial", Font.BOLD, 18));
        lblHistorialTanda.setForeground(COLOR_TEXTO_MUTED);
        lblHistorialTanda.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        lblHistorialTanda.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblReglasTitulo = new JLabel("Reglas de Desafío:");
        lblReglasTitulo.setForeground(COLOR_TEXTO);
        lblReglasTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblReglasTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String instruccionesHTML = "<html><body style='width: 160px; color: #8A8F99; font-family: Arial; font-size: 11px;'>"
                + "<br><b style='color:#FF3232;'>🚨 REGLA DE LAS 3 ÁREAS:</b><br>"
                + "Al patear, el portero se estirará y ocupará <b>3 zonas contiguas o separadas</b> al azar. Si tu tiro va a cualquiera de esas 3, será atajado.<br><br>"
                + "• Los multiplicadores aumentaron de valor para equilibrar la ventaja del arquero.<br><br>"
                + "• Si anotas en más del <b>60%</b> de los tiros que elegiste para tu tanda, duplicarás tu recompensa final de campeonato."
                + "</body></html>";

        JLabel lblInstrucciones = new JLabel(instruccionesHTML);
        lblInstrucciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(lblHistorialTanda);
        panel.add(lblReglasTitulo);
        panel.add(lblInstrucciones);

        // Inicializar los círculos según la selección por defecto
        inicializarHistorialVacio();

        return panel;
    }

    private void inicializarHistorialVacio() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalTirosTanda; i++) {
            sb.append("⚪ ");
        }
        lblHistorialTanda.setText(sb.toString().trim());
    }

    private void modificarApuesta(int valor) {
        if (juegoEnCurso) return;
        if (montoApuesta + valor >= 50) {
            montoApuesta += valor;
            txtMontoApuesta.setText(String.valueOf(montoApuesta));
        }
    }

    private void iniciarNuevaTanda() {
        if (montoApuesta * totalTirosTanda > monedasJugador) {
            JOptionPane.showMessageDialog(this,
                    "❌ Saldo insuficiente para respaldar la tanda completa seleccionada de " + totalTirosTanda + " tiros (" + (montoApuesta * totalTirosTanda) + " UP).",
                    "Saldo Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        juegoEnCurso = true;
        penalActual = 1;
        golesAnotados = 0;

        btnApostar.setEnabled(false);
        btnMas.setEnabled(false);
        btnMenos.setEnabled(false);
        comboCantidadTiros.setEnabled(false); // Bloquear cambios durante el partido

        inicializarHistorialVacio();
        lblEstadoRonda.setText("Penal 1/" + totalTirosTanda + " - ¡Elige dónde patear!");
        lblPorteroAccion.setText("El arquero y sus dos defensas cubren la línea...");

        for (JButton boton : btnZonasArco) {
            boton.setEnabled(true);
            boton.setBackground(COLOR_FONDO_PRINCIPAL);
        }
    }

    private void ejecutarDisparo(int zonaSeleccionada) {
        for (JButton boton : btnZonasArco) boton.setEnabled(false);

        // LÓGICA: El portero ahora elige 3 zonas diferentes y únicas para tapar
        ArrayList<Integer> universoZonas = new ArrayList<>();
        for (int i = 0; i < 9; i++) universoZonas.add(i);
        Collections.shuffle(universoZonas);

        // Tomamos las primeras 3 de la lista mezclada
        int zonaPortero1 = universoZonas.get(0);
        int zonaPortero2 = universoZonas.get(1);
        int zonaPortero3 = universoZonas.get(2);

        new Thread(() -> {
            try {
                lblEstadoRonda.setText("⚡ ¡Disparo potente hacia el arco...!");
                Thread.sleep(600);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                // Verificamos si cayó en cualquiera de las 3 zonas atajadas
                boolean atajado = (zonaSeleccionada == zonaPortero1 ||
                        zonaSeleccionada == zonaPortero2 ||
                        zonaSeleccionada == zonaPortero3);

                boolean esGol = !atajado;

                // Mostrar en rojo las 3 zonas que tapó el arquero al mismo tiempo
                btnZonasArco[zonaPortero1].setBackground(COLOR_ROJO);
                btnZonasArco[zonaPortero2].setBackground(COLOR_ROJO);
                btnZonasArco[zonaPortero3].setBackground(COLOR_ROJO);

                if (esGol) {
                    golesAnotados++;
                    int gananciasTiro = (int) (montoApuesta * multiplicadoresZonas[zonaSeleccionada]);
                    monedasJugador += gananciasTiro;

                    lblEstadoRonda.setText("⚽ ¡GOOOOOLAZO! Superaste las 3 zonas cubiertas (+" + gananciasTiro + " UP)");
                    lblPorteroAccion.setText("El arquero no pudo cubrir los espacios vacíos.");
                    btnZonasArco[zonaSeleccionada].setBackground(COLOR_VERDE_ACENTO);
                } else {
                    monedasJugador -= montoApuesta;
                    lblEstadoRonda.setText("❌ ¡ATAJADO! Tu tiro fue directo a las manos del muro defensivo.");
                    lblPorteroAccion.setText("Esa zona estaba completamente bloqueada.");
                }

                lblMonedas.setText(String.format("%,d UP", monedasJugador));
                if (Usuario.getUsuarioActual() != null) {
                    Usuario.getUsuarioActual().setSaldo(monedasJugador);
                }

                actualizarIconosTanda(esGol);

                new Thread(() -> {
                    try {
                        Thread.sleep(1800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SwingUtilities.invokeLater(() -> avanzarOFinalizarTanda());
                }).start();
            });
        }).start();
    }

    private void actualizarIconosTanda(boolean ultimoFueGol) {
        String[] iconos = lblHistorialTanda.getText().split(" ");
        if (penalActual <= iconos.length) {
            iconos[penalActual - 1] = ultimoFueGol ? "⚽" : "❌";
        }
        lblHistorialTanda.setText(String.join(" ", iconos));
    }

    private void avanzarOFinalizarTanda() {
        if (penalActual < totalTirosTanda) {
            penalActual++;
            lblEstadoRonda.setText("Penal " + penalActual + "/" + totalTirosTanda + " - ¡Elige tu zona!");
            lblPorteroAccion.setText("El arquero vuelve a posicionarse.");

            for (JButton boton : btnZonasArco) {
                boton.setBackground(COLOR_FONDO_PRINCIPAL);
                boton.setEnabled(true);
            }
        } else {
            juegoEnCurso = false;

            // Requisito dinámico: Haber anotado en el 60% o más de los tiros totales configurados
            double efectividadMinima = totalTirosTanda * 0.6;

            if (golesAnotados >= efectividadMinima) {
                int bonoCampeon = montoApuesta * (totalTirosTanda / 2 + 1);
                monedasJugador += bonoCampeon;
                lblMonedas.setText(String.format("%,d UP", monedasJugador));
                if (Usuario.getUsuarioActual() != null) {
                    Usuario.getUsuarioActual().setSaldo(monedasJugador);
                }
                JOptionPane.showMessageDialog(this,
                        "🏆 ¡SÚPER CAMPEÓN DE LA TANDA! 🏆\nLograste " + golesAnotados + " goles en una tanda de " + totalTirosTanda + " tiros.\nRecibes un mega bono de: " + bonoCampeon + " UP.",
                        "Fin del Partido", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "📉 Fin de la tanda.\nAnotaste " + golesAnotados + " goles de " + totalTirosTanda + ".\nNo lograste superar el 60% de efectividad.",
                        "Fin del Partido", JOptionPane.WARNING_MESSAGE);
            }

            lblEstadoRonda.setText("Tanda finalizada. Elige una nueva configuración de juego.");
            lblPorteroAccion.setText("El arco está libre.");

            btnApostar.setEnabled(true);
            btnMas.setEnabled(true);
            btnMenos.setEnabled(true);
            comboCantidadTiros.setEnabled(true);

            for (JButton boton : btnZonasArco) {
                boton.setBackground(COLOR_FONDO_PRINCIPAL);
                boton.setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoPenales().setVisible(true);
        });
    }
}