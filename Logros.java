import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Logros {

    // Paleta de Colores
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private static final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private static final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    private static JLabel pointsLbl;

    // Clase interna para el manejo de logros (usada por GestorLogros)
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
        // 1. Instanciamos el JFrame tradicional para forzar la aparición en la barra de
        // tareas
        JFrame ventana = new JFrame();
        ventana.setTitle("Perfil de Usuario");
        ventana.setSize(900, 600); // Tamaño inicial base equilibrado
        ventana.setMinimumSize(new Dimension(800, 550)); // Evita que el usuario la rompa al achicarla demasiado
        ventana.setLocationRelativeTo(null);

        // Al cerrarse esta ventana independiente, volvemos a dar foco o control (puedes
        // cambiarlo a EXIT_ON_CLOSE si deseas cerrar todo)
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- VARIABLES PARAMETRIZADAS DEL USUARIO ---
        String nombreUsuario = "Invitado";
        int numeroMonedas = 500;
        ImageIcon fotoUsuario = null;

        int victorias = 0;
        int derrotas = 0;

        VentanaLogros actual = VentanaLogros.getUsuarioActual();
        if (actual != null) {
            nombreUsuario = actual.getNombre();
            numeroMonedas = (int) actual.getSaldo();
            victorias = actual.getVictorias();
            derrotas = actual.getDerrotas();

            String rutaFoto = actual.getFotoPerfil();
            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                try {
                    ImageIcon tempIcon = new ImageIcon(rutaFoto);
                    Image img = tempIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                    fotoUsuario = new ImageIcon(img);
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

        // --- CONTENEDOR PRINCIPAL FLUIDO ---
        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);

        // AGREGAR HEADER SUPERIOR CONSISTENTE
        contenedorPrincipal.add(buildHeader(ventana), BorderLayout.NORTH);

        // CONTENEDOR CONTENIDO CON PADDING
        JPanel panelContenido = new JPanel(new BorderLayout(25, 20));
        panelContenido.setBackground(COLOR_FONDO_PRINCIPAL);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        contenedorPrincipal.add(panelContenido, BorderLayout.CENTER);

        // ==========================================
        // SECCIÓN CENTRAL: CUERPO DIVIDIDO EN DOS (LOGROS | ESTADÍSTICAS)
        // ==========================================
        // Usamos un Grid con 1 fila y 2 columnas distribuidas proporcionalmente (50% y
        // 50%)
        // Esto permite que al estirar la ventana, ambos paneles se expandan
        // proporcionalmente.
        JPanel panelCuerpoCajas = new JPanel(new GridLayout(1, 2, 40, 0));
        panelCuerpoCajas.setOpaque(false);

        // PANEL IZQUIERDO: CONTENEDOR LOGROS
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setOpaque(false);

        JLabel lblTituloLogros = new JLabel("Logros", SwingConstants.CENTER);
        lblTituloLogros.setFont(new Font("Arial", Font.BOLD, 15));
        lblTituloLogros.setForeground(COLOR_TEXTO);
        lblTituloLogros.setBackground(COLOR_VERDE_ACENTO);
        lblTituloLogros.setOpaque(true);
        lblTituloLogros.setPreferredSize(new Dimension(380, 40));
        panelIzquierdo.add(lblTituloLogros, BorderLayout.NORTH);

        // Subcontenedor elástico para meter las filas de logros
        JPanel panelFilasContenedor = new JPanel();
        panelFilasContenedor.setLayout(new BoxLayout(panelFilasContenedor, BoxLayout.Y_AXIS));
        panelFilasContenedor.setOpaque(false);

        for (Logro logro : listaLogros) {
            // Cada logro usa BorderLayout para que la descripción cubra todo lo ancho
            // dinámicamente
            JPanel panelFilaLogro = new JPanel(new BorderLayout(5, 5));
            panelFilaLogro.setOpaque(false);
            panelFilaLogro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65)); // Forzar expansión a lo ancho

            JLabel lblDesc = new JLabel(logro.getDescripcion(), SwingConstants.CENTER);
            lblDesc.setForeground(COLOR_TEXTO);
            lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
            lblDesc.setOpaque(true);
            lblDesc.setBackground(COLOR_FONDO_PANEL);
            lblDesc.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1, true));
            lblDesc.setPreferredSize(new Dimension(100, 28));

            JPanel panelControlesLogro = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panelControlesLogro.setOpaque(false);

            JButton btnReclamar = new JButton();
            btnReclamar.setFont(new Font("Arial", Font.BOLD, 11));
            btnReclamar.setFocusPainted(false);
            btnReclamar.setPreferredSize(new Dimension(110, 22));

            JLabel lblRegalo = new JLabel("🎁");
            lblRegalo.setFont(new Font("Arial", Font.PLAIN, 16));

            if (logro.isReclamado()) {
                btnReclamar.setText("Reclamado");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                btnReclamar.setEnabled(false);
                lblRegalo.setForeground(COLOR_TEXTO_MUTED);
            } else if (logro.isCompletado()) {
                btnReclamar.setText("Reclamar");
                btnReclamar.setBackground(COLOR_VERDE_ACENTO);
                btnReclamar.setForeground(COLOR_FONDO_PRINCIPAL);
                btnReclamar.setEnabled(true);
                lblRegalo.setForeground(COLOR_VERDE_ACENTO);
            } else {
                btnReclamar.setText("Reclamar");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                btnReclamar.setEnabled(false);
                lblRegalo.setForeground(COLOR_TEXTO_MUTED);
            }

            btnReclamar.addActionListener(e -> {
                logro.marcarComoReclamado();
                btnReclamar.setEnabled(false);
                btnReclamar.setText("Reclamado");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                lblRegalo.setForeground(COLOR_TEXTO_MUTED);

                int recompensa = logro.getRecompensaMonedas();
                VentanaLogros actualUser = VentanaLogros.getUsuarioActual();
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

            panelControlesLogro.add(btnReclamar);
            panelControlesLogro.add(lblRegalo);

            panelFilaLogro.add(lblDesc, BorderLayout.NORTH);
            panelFilaLogro.add(panelControlesLogro, BorderLayout.CENTER);

            panelFilasContenedor.add(panelFilaLogro);
            panelFilasContenedor.add(Box.createVerticalStrut(8));
        }

        // Agregamos un JScrollPane invisible por si la resolución es baja o se achica
        // demasiado verticalmente
        JScrollPane scrollLogros = new JScrollPane(panelFilasContenedor);
        scrollLogros.setBorder(null);
        scrollLogros.setOpaque(false);
        scrollLogros.getViewport().setOpaque(false);
        panelIzquierdo.add(scrollLogros, BorderLayout.CENTER);

        // PANEL DERECHO: CONTENEDOR ESTADÍSTICAS
        JPanel panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(new BoxLayout(panelEstadisticas, BoxLayout.Y_AXIS));
        panelEstadisticas.setBackground(COLOR_FONDO_PANEL);
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));

        panelEstadisticas.add(crearFilaMetrica("Partidas jugadas:", String.valueOf(partidasJugadas)));
        panelEstadisticas.add(Box.createVerticalStrut(35));
        panelEstadisticas.add(crearFilaMetrica("Victorias:", String.valueOf(victorias)));
        panelEstadisticas.add(Box.createVerticalStrut(35));
        panelEstadisticas.add(crearFilaMetrica("Derrotas:", String.valueOf(derrotas)));
        panelEstadisticas.add(Box.createVerticalStrut(35));
        panelEstadisticas.add(crearFilaMetrica("Porcentaje V/D:", porcentajeVD));

        // Metemos ambas secciones en la cuadrícula fluida
        panelCuerpoCajas.add(panelIzquierdo);
        panelCuerpoCajas.add(panelEstadisticas);

        panelContenido.add(panelCuerpoCajas, BorderLayout.CENTER);

        // Sin botón inferior para maximizar el espacio vertical de forma idéntica a Places y Chat

        ventana.add(contenedorPrincipal);
        ventana.setVisible(true);
    }

    private static JPanel crearFilaMetrica(String titulo, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Escala horizontal infinita

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_VERDE_ACENTO);

        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("Arial", Font.BOLD, 15));
        lblValor.setForeground(COLOR_TEXTO);

        fila.add(lblTitulo, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.CENTER);
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
        VentanaLogros actual = VentanaLogros.getUsuarioActual();
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

        // Campana con badge
        JPanel bellPanel = makeBellWithBadge(3);

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
                    String fotoPath = (VentanaLogros.getUsuarioActual() != null)
                            ? VentanaLogros.getUsuarioActual().getFotoPerfil()
                            : "Icons/UserDefaultpfp.png";
                    ImageIcon icon = new ImageIcon(fotoPath);
                    Image img = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);

                    // Clip circular
                    Shape clip = new java.awt.geom.Ellipse2D.Float(2, 2, 38, 38);
                    g2.setClip(clip);
                    g2.drawImage(img, 2, 2, 38, 38, null);
                    g2.setClip(null);

                    // Borde verde online premium
                    g2.setColor(GREEN);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(2, 2, 37, 37);
                } catch (Exception e) {
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
                    VentanaLogros.cerrarSesion();
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
        return new ImageIcon(img);
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