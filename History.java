import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class History extends JFrame {

    // ── Paleta (Consistente con Places/MainMenu) ───────────
    static final Color BG_DARK = new Color(1, 3, 7);
    static final Color BG_CARD = new Color(6, 12, 15);
    static final Color BG_HEADER = new Color(1, 3, 7);
    static final Color GREEN = new Color(44, 243, 53);
    static final Color TEXT_WHITE = new Color(240, 240, 240);
    static final Color TEXT_GRAY = new Color(150, 155, 145);
    static final Color GOLD = new Color(245, 166, 35);
    static final Color BORDER = new Color(25, 35, 30);

    static final Font FONT_POINTS = new Font("SansSerif", Font.BOLD, 16);

    private JLabel pointsLbl;
    private JPanel panelActividades;
    private static History instancia;
    private final List<JPanel> actividades = new ArrayList<>();

    public History() {
        instancia = this;
        setTitle("Actividad reciente");
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // PANEL PRINCIPAL
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(BG_DARK);
        panelPrincipal.setLayout(new BorderLayout());

        // AGREGAR HEADER SUPERIOR CONSISTENTE
        panelPrincipal.add(buildHeader(), BorderLayout.NORTH);

        // PANEL ACTIVIDADES (CON SCROLL)
        panelActividades = new JPanel();
        panelActividades.setBackground(BG_DARK);
        panelActividades.setLayout(new BoxLayout(panelActividades, BoxLayout.Y_AXIS));
        panelActividades.setBorder(new EmptyBorder(25, 40, 25, 40));

        // TÍTULO DE LA SECCIÓN (Dentro del scroll para un look moderno)
        JLabel seccionTitulo = new JLabel("Actividad reciente");
        seccionTitulo.setForeground(Color.WHITE);
        seccionTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        seccionTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        seccionTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccionTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        panelActividades.add(seccionTitulo);


        // ACTIVIDADES DINÁMICAS
        agregarActividad(
                "Ganaste 500 puntos en Bingo.",
                "+500",
                "Hace 5 min",
                new Color(0, 255, 100));

        agregarActividad(
                "Perdiste 200 en Ruleta.",
                "-200",
                "Hace 20 min",
                Color.RED);

        agregarActividad(
                "Ganaste 300 puntos en Penales.",
                "+300",
                "Hace 1 hora",
                new Color(0, 255, 100));

        agregarActividad(
                "Ganaste 150 puntos en Lucky Spin.",
                "+150",
                "Hace 2 horas",
                new Color(0, 255, 100));

        JScrollPane scroll = new JScrollPane(panelActividades);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);

        panelPrincipal.add(scroll, BorderLayout.CENTER);

        add(panelPrincipal);
        WindowPreserver.configurarVentana(this);
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════
    // HEADER SUPERIOR PREMIUM
    // ════════════════════════════════════════════════════════
    private JPanel buildHeader() {
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
            dispose();
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
        JPanel bellPanel = CampanaNotificaciones.crear(History.this);

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

                    Shape clip = new java.awt.geom.Ellipse2D.Float(2, 2, 38, 38);
                    g2.setClip(clip);
                    g2.drawImage(icon.getImage(), 2, 2, 38, 38, null);
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
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
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
                    dispose();
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

    // ════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES DE ESTILADO CONSISTENTE
    // ════════════════════════════════════════════════════════
    private JButton makeIconButton(String text, int size) {
        JButton btn = new JButton(text) {
            boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
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

    private ImageIcon makeCoinIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
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

    private JPanel makeBellWithBadge(int count) {
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

    // METODO PARA CREAR CARDS
    private JPanel crearActividad(String texto, String puntos, String tiempo, Color color) {
        JPanel card = new JPanel();
        card.setBackground(new Color(18, 24, 33));
        card.setMaximumSize(new Dimension(820, 90)); // Aumentado ligeramente para alinearse con el nuevo ancho
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ICONO
        JLabel icono = new JLabel("⬤");
        icono.setForeground(color);
        icono.setFont(new Font("Arial", Font.BOLD, 30));

        // TEXTO
        JLabel descripcion = new JLabel(texto);
        descripcion.setForeground(Color.WHITE);
        descripcion.setFont(new Font("Arial", Font.PLAIN, 18));

        // PANEL IZQUIERDO
        JPanel izquierda = new JPanel();
        izquierda.setBackground(new Color(18, 24, 33));
        izquierda.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        izquierda.add(icono);
        izquierda.add(descripcion);

        // PANEL DERECHO
        JPanel derecha = new JPanel();
        derecha.setBackground(new Color(18, 24, 33));
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));

        JLabel puntosLabel = new JLabel(puntos);
        puntosLabel.setForeground(color);
        puntosLabel.setFont(new Font("Arial", Font.BOLD, 22));
        puntosLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel tiempoLabel = new JLabel(tiempo);
        tiempoLabel.setForeground(Color.GRAY);
        tiempoLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        derecha.add(puntosLabel);
        derecha.add(tiempoLabel);

        // AGREGAR
        card.add(izquierda, BorderLayout.WEST);
        card.add(derecha, BorderLayout.EAST);

        // ESPACIO ENTRE CARDS
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.setBorder(new EmptyBorder(0, 0, 15, 0));
        wrapper.add(card);

        return wrapper;
    }



    // ════════════════════════════════════════════════════════
    // MÉTODOS DINÁMICOS PARA ACTUALIZAR EL HISTORIAL
    // ════════════════════════════════════════════════════════

    public void agregarActividad(String descripcion, String puntos, String tiempo, Color color) {
        JPanel actividad = crearActividad(descripcion, puntos, tiempo, color);

        actividades.add(0, actividad);

        // Insertar después del título
        panelActividades.add(actividad, 1);

        panelActividades.revalidate();
        panelActividades.repaint();
    }

    public static void agregarActividadTiempoReal(String descripcion, int puntos) {
        if (instancia == null)
            return;

        String textoPuntos = (puntos >= 0 ? "+" : "") + puntos;
        Color color = puntos >= 0 ? new Color(0, 255, 100) : Color.RED;

        SwingUtilities.invokeLater(() -> {
            instancia.agregarActividad(
                    descripcion,
                    textoPuntos,
                    "Ahora mismo",
                    color);

            // Actualizar saldo visual
            try {
                Usuario actual = Usuario.getUsuarioActual();
                if (actual != null) {
                    double nuevoSaldo = actual.getSaldo();
                    instancia.pointsLbl.setText(String.format("%,.0f ", nuevoSaldo));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new History().setVisible(true);
        });
    }
}