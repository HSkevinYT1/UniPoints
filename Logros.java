import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Logros {

    // Paleta de Colores
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private static final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private static final Color COLOR_ITEM_FONDO = new Color(30, 36, 48);
    private static final Color COLOR_VERDE_ACENTO = new Color(44, 243, 53);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    private static JLabel pointsLbl;

    private static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return visibleRect.height;
        }
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static class RoundedButton extends JButton {
        private int radius;
        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            g2.setFont(getFont());
            g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, 
                (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    public static class Logro {
        private String descripcion;
        private int recompensaMonedas;
        private boolean completado;
        private boolean reclamado;

        public Logro(String descripcion, int recompensaMonedas) {
            this.descripcion = descripcion;
            this.recompensaMonedas = recompensaMonedas;
            this.completado = false;
            this.reclamado = false;
        }

        public void setCompletado(boolean completado) {
            this.completado = completado;
        }

        public boolean isCompletado() {
            return completado;
        }

        public boolean isReclamado() {
            return reclamado;
        }

        public void marcarComoReclamado() {
            this.reclamado = true;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public int getRecompensaMonedas() {
            return recompensaMonedas;
        }
    }

    // Cambiado a un JFrame dinámico e independiente
    public static void mostrarVentanaLogros() {

        JFrame ventana = new JFrame();
        ventana.setTitle("Perfil de Usuario");
        ventana.setSize(1300, 850);
        ventana.setMinimumSize(new Dimension(800, 550));
        ventana.setLocationRelativeTo(null);

        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- VARIABLES PARAMETRIZADAS DEL USUARIO ---
        String nombreUsuario = "Invitado";
        int numeroMonedas = 500;
        ImageIcon fotoUsuario = null;

        int victorias = 0;
        int derrotas = 0;

        Usuario actual = Usuario.getUsuarioActual();
        if (actual != null) {
            nombreUsuario = actual.getNombre();
            numeroMonedas = (int) actual.getSaldo();
            victorias = actual.getVictorias();
            derrotas = actual.getDerrotas();

            String rutaFoto = actual.getFotoPerfil();
            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                try {
                    ImageIcon tempIcon = ImageLoader.load(rutaFoto);
                    Image img = tempIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                    fotoUsuario = ImageLoader.load(img);
                } catch (Exception e) {
                    System.err.println("Error al cargar la foto de perfil en logros: " + e.getMessage());
                }
            }
        }

        int partidasJugadas = victorias + derrotas;
        String porcentajeVD;

        if (partidasJugadas > 0) {
            double tasa = ((double) victorias / partidasJugadas) * 100;
            porcentajeVD = String.format("%.1f%%", tasa);
        } else {
            porcentajeVD = "0.0%";
        }

        List<Logro> listaLogros = GestorLogros.getInstancia().getListaLogros();

        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);

        contenedorPrincipal.add(buildHeader(ventana), BorderLayout.NORTH);

        JPanel panelContenido = new JPanel(new BorderLayout(25, 20));
        panelContenido.setBackground(COLOR_FONDO_PRINCIPAL);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contenedorPrincipal.add(panelContenido, BorderLayout.CENTER);

        JPanel panelCuerpoCajas = new JPanel(new GridLayout(1, 2, 40, 0));
        panelCuerpoCajas.setOpaque(false);

        // PANEL IZQUIERDO: LOGROS
        RoundedPanel panelIzquierdo = new RoundedPanel(20, COLOR_FONDO_PANEL);
        panelIzquierdo.setLayout(new BorderLayout(0, 20));
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        JLabel lblTituloLogros = new JLabel("🌟 Mis Logros");
        lblTituloLogros.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTituloLogros.setForeground(COLOR_TEXTO);
        panelIzquierdo.add(lblTituloLogros, BorderLayout.NORTH);

        JPanel panelFilasContenedor = new ScrollablePanel();
        panelFilasContenedor.setLayout(new BoxLayout(panelFilasContenedor, BoxLayout.Y_AXIS));
        panelFilasContenedor.setOpaque(false);

        for (Logro logro : listaLogros) {
            RoundedPanel panelFilaLogro = new RoundedPanel(15, COLOR_ITEM_FONDO);
            panelFilaLogro.setLayout(new BorderLayout(15, 0));
            panelFilaLogro.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            panelFilaLogro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            JLabel lblDesc = new JLabel("<html><div style='width:100%;'><b>" + logro.getDescripcion() + "</b><br><font color='#8A8F99'>Recompensa: " + logro.getRecompensaMonedas() + " UP</font></div></html>");
            lblDesc.setForeground(COLOR_TEXTO);
            lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JPanel panelControlesLogro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            panelControlesLogro.setOpaque(false);

            RoundedButton btnReclamar = new RoundedButton("", 12);
            btnReclamar.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnReclamar.setPreferredSize(new Dimension(110, 32));

            JLabel lblRegalo = new JLabel("🎁");
            lblRegalo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

            if (logro.isReclamado()) {
                btnReclamar.setText("Reclamado");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                btnReclamar.setEnabled(false);
            } else if (logro.isCompletado()) {
                btnReclamar.setText("Reclamar");
                btnReclamar.setBackground(COLOR_VERDE_ACENTO);
                btnReclamar.setForeground(COLOR_FONDO_PRINCIPAL);
                btnReclamar.setEnabled(true);
            } else {
                btnReclamar.setText("Por completar");
                btnReclamar.setBackground(new Color(40, 45, 55));
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                btnReclamar.setEnabled(false);
            }

            btnReclamar.addActionListener(e -> {
                logro.marcarComoReclamado();
                btnReclamar.setEnabled(false);
                btnReclamar.setText("Reclamado");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);

                int recompensa = logro.getRecompensaMonedas();
                Usuario actualUser = Usuario.getUsuarioActual();
                if (actualUser != null) {
                    actualUser.setSaldo(actualUser.getSaldo() + recompensa);
                    if (pointsLbl != null) {
                        pointsLbl.setText(String.format("%,.0f ", actualUser.getSaldo()));
                    }
                }

                JOptionPane.showMessageDialog(ventana,
                        "¡Recompensas reclamadas! (+" + recompensa + " UP)",
                        "Recompensa Reclamada",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            JPanel btnWrapper = new JPanel(new GridBagLayout());
            btnWrapper.setOpaque(false);
            btnWrapper.add(lblRegalo);
            btnWrapper.add(Box.createHorizontalStrut(12));
            btnWrapper.add(btnReclamar);

            panelFilaLogro.add(lblDesc, BorderLayout.CENTER);
            panelFilaLogro.add(btnWrapper, BorderLayout.EAST);

            panelFilasContenedor.add(panelFilaLogro);
            panelFilasContenedor.add(Box.createVerticalStrut(12));
        }

        JScrollPane scrollLogros = new JScrollPane(panelFilasContenedor);
        scrollLogros.setBorder(null);
        scrollLogros.setOpaque(false);
        scrollLogros.getViewport().setOpaque(false);
        scrollLogros.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLogros.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 65, 75);
                this.trackColor = COLOR_FONDO_PANEL;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        scrollLogros.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        panelIzquierdo.add(scrollLogros, BorderLayout.CENTER);

        // PANEL DERECHO: CONTENEDOR ESTADÍSTICAS
        RoundedPanel panelEstadisticas = new RoundedPanel(20, COLOR_FONDO_PANEL);
        panelEstadisticas.setLayout(new BorderLayout(0, 20));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        JLabel lblTituloStats = new JLabel("📊 Estadísticas");
        lblTituloStats.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTituloStats.setForeground(COLOR_TEXTO);
        panelEstadisticas.add(lblTituloStats, BorderLayout.NORTH);

        JPanel statsContenedor = new JPanel();
        statsContenedor.setLayout(new BoxLayout(statsContenedor, BoxLayout.Y_AXIS));
        statsContenedor.setOpaque(false);

        statsContenedor.add(crearFilaMetrica("Partidas jugadas", String.valueOf(partidasJugadas), "🎮"));
        statsContenedor.add(Box.createVerticalStrut(15));
        statsContenedor.add(crearFilaMetrica("Victorias", String.valueOf(victorias), "🏆"));
        statsContenedor.add(Box.createVerticalStrut(15));
        statsContenedor.add(crearFilaMetrica("Derrotas", String.valueOf(derrotas), "💀"));
        statsContenedor.add(Box.createVerticalStrut(15));
        statsContenedor.add(crearFilaMetrica("Porcentaje V/D", porcentajeVD, "📈"));
        statsContenedor.add(Box.createVerticalStrut(15));
        statsContenedor.add(crearFilaMetrica("Saldo actual", String.format("%,.0f UP", (double) numeroMonedas), "💰"));

        panelEstadisticas.add(statsContenedor, BorderLayout.CENTER);

        // ─── BOTÓN EXPORTAR ──────────────────────────────────────────
        // Variables final para capturar en el lambda
        final String fNombreUsuario    = nombreUsuario;
        final String fUsername         = (actual != null) ? actual.getUsername() : "?";
        final String fCorreo           = (actual != null) ? actual.getCorreo()   : "?";
        final int    fPartidas         = partidasJugadas;
        final int    fVictorias        = victorias;
        final int    fDerrotas         = derrotas;
        final String fPorcentaje       = porcentajeVD;
        final int    fMonedas          = numeroMonedas;

        RoundedButton btnExportar = new RoundedButton("📄 Exportar Resumen", 15);
        btnExportar.setBackground(new Color(30, 36, 48));
        btnExportar.setForeground(new Color(44, 243, 53));
        btnExportar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnExportar.setPreferredSize(new Dimension(220, 42));
        btnExportar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnExportar.addActionListener(ev -> exportarResumen(ventana, listaLogros,
                fNombreUsuario, fUsername, fCorreo,
                fPartidas, fVictorias, fDerrotas, fPorcentaje, fMonedas));


        JPanel btnExportarWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnExportarWrapper.setOpaque(false);
        btnExportarWrapper.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        btnExportarWrapper.add(btnExportar);

        panelEstadisticas.add(btnExportarWrapper, BorderLayout.SOUTH);

        panelCuerpoCajas.add(panelIzquierdo);
        panelCuerpoCajas.add(panelEstadisticas);

        panelContenido.add(panelCuerpoCajas, BorderLayout.CENTER);

        // Sin botón inferior para maximizar el espacio vertical de forma idéntica a Places y Chat

        ventana.add(contenedorPrincipal);
        WindowPreserver.configurarVentana(ventana);
        ventana.setVisible(true);
    }

    // ─── EXPORTAR RESUMEN ──────────────────────────────────────────
    private static void exportarResumen(JFrame padre, List<Logros.Logro> logros,
            String nombre, String username, String correo,
            int partidas, int victorias, int derrotas, String porcentaje, int saldo) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Resumen de Logros");
        chooser.setSelectedFile(new java.io.File("Resumen_" + username + ".txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("Archivo de texto (.txt)", "txt"));

        int result = chooser.showSaveDialog(padre);
        if (result != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new java.io.File(archivo.getAbsolutePath() + ".txt");
        }

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("╔══════════════════════════════════════════════╗");
            pw.println("║          UNIPOINTS — RESUMEN DE LOGROS       ║");
            pw.println("╚══════════════════════════════════════════════╝");
            pw.println();
            pw.println("Generado el: " + fecha);
            pw.println();
            pw.println("── PERFIL ──────────────────────────────────────");
            pw.println("  Nombre:   " + nombre);
            pw.println("  Usuario:  @" + username);
            pw.println("  Correo:   " + correo);
            pw.println("  Saldo:    " + String.format("%,.0f UP", (double) saldo));
            pw.println();
            pw.println("── ESTADÍSTICAS ────────────────────────────────");
            pw.println("  Partidas jugadas:  " + partidas);
            pw.println("  Victorias:         " + victorias);
            pw.println("  Derrotas:          " + derrotas);
            pw.println("  Porcentaje V/D:    " + porcentaje);
            pw.println();
            pw.println("── LOGROS ──────────────────────────────────────");
            int completados = 0;
            for (int i = 0; i < logros.size(); i++) {
                Logros.Logro l = logros.get(i);
                String estado;
                if (l.isReclamado())      estado = "[RECLAMADO ✓]";
                else if (l.isCompletado()) estado = "[COMPLETADO — sin reclamar]";
                else                       estado = "[PENDIENTE]";
                if (l.isCompletado() || l.isReclamado()) completados++;
                pw.printf("  %d. %-45s %s (+%d UP)%n",
                        i + 1, l.getDescripcion(), estado, l.getRecompensaMonedas());
            }
            pw.println();
            pw.println("  Completados: " + completados + " / " + logros.size());
            pw.println();
            pw.println("════════════════════════════════════════════════");
            pw.println("  UniPoints — Universidad Andrés Bello");

            JOptionPane.showMessageDialog(padre,
                    "✅ Resumen exportado exitosamente en:\n" + archivo.getAbsolutePath(),
                    "Exportación completada", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(padre,
                    "Error al guardar el archivo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JPanel crearFilaMetrica(String titulo, String valor, String icono) {
        RoundedPanel fila = new RoundedPanel(15, COLOR_ITEM_FONDO);
        fila.setLayout(new BorderLayout(15, 0));
        fila.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierdo.setOpaque(false);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_TEXTO_MUTED);

        panelIzquierdo.add(lblIcono);
        panelIzquierdo.add(lblTitulo);

        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblValor.setForeground(COLOR_TEXTO);

        fila.add(panelIzquierdo, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.EAST);
        return fila;
    }

    // Método Main para probar de forma directa el redimensionamiento y el icono de
    // la barra de tareas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorLogros.getInstancia().cambiarEstadoLogro(0, true);
            GestorLogros.getInstancia().cambiarEstadoLogro(1, true);
            mostrarVentanaLogros();
        });
    }

    // ════════════════════════════════════════════════════════
    // HEADER SUPERIOR PREMIUM UNIFICADO (STATIC)
    // ════════════════════════════════════════════════════════
    private static JPanel buildHeader(JFrame ventana) {
        Color BG_HEADER = new Color(1, 3, 7);
        Color BG_CARD = new Color(6, 12, 15);
        Color BORDER = new Color(25, 35, 30);
        Color GREEN = new Color(44, 243, 53);
        Color GOLD = new Color(245, 166, 35);
        Font FONT_POINTS = new Font("SansSerif", Font.BOLD, 16);
        Color TEXT_WHITE = new Color(240, 240, 240);

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        // ← Botón atrás
        JButton backBtn = makeIconButton("←", 32);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        backBtn.addActionListener(e -> {
            new MainMenu();
            ventana.dispose();
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(backBtn);

        // Lado derecho: puntos + campana + avatar
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        // Chip de puntos
        JPanel pointsChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 25, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        pointsChip.setOpaque(false);
        pointsChip.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 14));

        double saldo = 500;
        Usuario actual = Usuario.getUsuarioActual();
        if (actual != null) {
            saldo = actual.getSaldo();
        }

        JLabel coinIcon = new JLabel(makeCoinIcon(22));
        pointsLbl = new JLabel(String.format("%,.0f ", saldo));
        pointsLbl.setFont(FONT_POINTS);
        pointsLbl.setForeground(TEXT_WHITE);
        JLabel upLbl = new JLabel("UP");
        upLbl.setFont(FONT_POINTS);
        upLbl.setForeground(GREEN);

        pointsChip.add(coinIcon);
        pointsChip.add(pointsLbl);
        pointsChip.add(upLbl);

        // Campana de notificaciones (global)
        JPanel bellPanel = CampanaNotificaciones.crear(ventana);

        // Avatar del usuario con popup de Cerrar sesión
        JPanel avatarPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                try {
                    String fotoPath = (Usuario.getUsuarioActual() != null)
                            ? Usuario.getUsuarioActual().getFotoPerfil()
                            : "Icons/UserDefaultpfp.png";
                    if (fotoPath == null || fotoPath.isEmpty()) fotoPath = "Icons/UserDefaultpfp.png";
                    ImageIcon icon = ImageLoader.load(fotoPath);
                    if (icon.getIconWidth() == -1) icon = ImageLoader.load("Icons/UserDefaultpfp.png");

                    // Clip circular
                    Shape clip = new java.awt.geom.Ellipse2D.Float(2, 2, 38, 38);
                    g2.setClip(clip);
                    g2.drawImage(icon.getImage(), 2, 2, 38, 38, null);
                    g2.setClip(null);

                    // Borde verde online premium
                    g2.setColor(GREEN);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(2, 2, 37, 37);
                } catch (Exception e) {
                    g2.setClip(null);
                    g2.setColor(new Color(40, 50, 38));
                    g2.fillOval(2, 2, 38, 38);
                }
                g2.dispose();
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        avatarPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu() {
                    @Override
                    protected void paintBorder(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(50, 65, 45));
                        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.dispose();
                    }
                };
                popup.setBackground(new Color(15, 20, 15));
                popup.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

                JMenuItem logoutItem = new JMenuItem("Cerrar sesión");
                logoutItem.setOpaque(true);
                logoutItem.setBackground(new Color(15, 20, 15));
                logoutItem.setForeground(new Color(240, 240, 240));
                logoutItem.setFont(new Font("SansSerif", Font.PLAIN, 14));
                logoutItem.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                logoutItem.setFocusPainted(false);
                logoutItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

                logoutItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent me) {
                        logoutItem.setBackground(new Color(30, 45, 30));
                    }

                    @Override
                    public void mouseExited(MouseEvent me) {
                        logoutItem.setBackground(new Color(15, 20, 15));
                    }
                });

                logoutItem.addActionListener(ae -> {
                    Usuario.cerrarSesion();
                    new Login();
                    ventana.dispose();
                });

                popup.add(logoutItem);

                int px = avatarPanel.getWidth() - popup.getPreferredSize().width;
                int py = avatarPanel.getHeight() + 4;
                popup.show(avatarPanel, px, py);
            }
        });

        right.add(pointsChip);
        right.add(bellPanel);
        right.add(avatarPanel);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private static JButton makeIconButton(String text, int size) {
        Color BG_CARD = new Color(6, 12, 15);
        Color BORDER = new Color(25, 35, 30);
        Color TEXT_WHITE = new Color(240, 240, 240);

        JButton btn = new JButton(text) {
            boolean hover = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(0x2A2A2A) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(TEXT_WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(size + 14, size + 4));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static ImageIcon makeCoinIcon(int size) {
        Color GOLD = new Color(245, 166, 35);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(GOLD);
        g2.fillOval(0, 0, size - 1, size - 1);
        g2.setColor(new Color(0xB8860B));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(0, 0, size - 1, size - 1);
        g2.setColor(new Color(0x7B5800));
        g2.setFont(new Font("SansSerif", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("UP", (size - fm.stringWidth("UP")) / 2,
                (size + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
        return ImageLoader.load(img);
    }

    private static JPanel makeBellWithBadge(int count) {
        Color BG_CARD = new Color(6, 12, 15);
        Color BORDER = new Color(25, 35, 30);
        Color TEXT_WHITE = new Color(240, 240, 240);
        Color GREEN = new Color(44, 243, 53);

        JPanel p = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(BORDER);
                g2.drawOval(0, 0, 41, 41);
                g2.setColor(TEXT_WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                g2.drawString("🔔", 8, 28);
                g2.setColor(GREEN);
                g2.fillOval(26, 2, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(count);
                g2.drawString(s, 34 - fm.stringWidth(s) / 2, 14);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }
        };
        p.setOpaque(false);
        return p;
    }
}