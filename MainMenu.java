import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;

public class MainMenu extends JFrame {

    // COLORES
    private static final Color BG_DARK = new Color(1, 3, 7);
    private static final Color BG_CARD = new Color(6, 12, 15);
    private static final Color BORDER_CARD = new Color(40, 55, 35);
    private static final Color BORDER_HOVER = new Color(44, 243, 53);
    private static final Color GREEN_MAIN = new Color(44, 243, 53);
    private static final Color GREEN_DARK = new Color(30, 180, 38);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 155, 145);
    private static final Color HEADER_BG = new Color(14, 16, 12);

    // FUENTES BASE (Se escalan dinámicamente)
    private static final Font FONT_WELCOME_BASE = new Font("SansSerif", Font.BOLD, 38);
    private static final Font FONT_SUBTITLE_BASE = new Font("SansSerif", Font.PLAIN, 17);
    private static final Font FONT_CARD_TITLE_BASE = new Font("SansSerif", Font.BOLD, 15);
    private static final Font FONT_ARROW_BASE = new Font("SansSerif", Font.PLAIN, 16);

    private String userName;
    private float currentScale = 1.0f;

    // Componentes para escala dinámica
    private JLabel welcomeLabel;
    private JLabel subtitleLabel;
    private JPanel welcomePanel;
    private JPanel content;
    private JPanel gridPanel;
    private GridLayout gridLayout;
    private JPanel headerPanel;
    private JPanel rightPanel;
    
    // Lista de tarjetas para escala dinámica
    private List<MenuCardPanel> cardsList = new ArrayList<>();
    private JPanel bellPanel;

    public MainMenu() {
        Usuario actual = Usuario.getUsuarioActual();
        this.userName = (actual != null) ? actual.getNombre().split(" ")[0] : "Usuario";

        setTitle("Unab Points - Menú Principal");
        setSize(1300, 850);
        setMinimumSize(new Dimension(1300, 850));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Preservar tamaño/maximizado
        WindowPreserver.configurarVentana(this);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo oscuro base
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Resplandor verde sutil en el centro
                RadialGradientPaint glow = new RadialGradientPaint(
                        new Point2D.Float(getWidth() / 2f, getHeight() / 2.5f),
                        400f * currentScale,
                        new float[] { 0f, 0.4f, 1f },
                        new Color[] {
                                new Color(20, 80, 20, 60),
                                new Color(15, 50, 15, 25),
                                new Color(0, 0, 0, 0)
                        });
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Texto "UP" fantasma en el fondo
                g2.setFont(new Font("SansSerif", Font.BOLD, (int)(200 * currentScale)));
                g2.setColor(new Color(30, 40, 28, 30));
                FontMetrics fm = g2.getFontMetrics();
                String watermark = "UP";
                int wx = (getWidth() - fm.stringWidth(watermark)) / 2;
                int wy = (int) (getHeight() * 0.42);
                g2.drawString(watermark, wx, wy);

                g2.dispose();
            }
        };
        root.setOpaque(false);

        headerPanel = buildHeader();
        welcomePanel = buildWelcomePanel();
        content = buildContent();

        root.add(headerPanel, BorderLayout.NORTH);
        
        // Panel contenedor para la zona de contenido (Bienvenida arriba + Accesos centrados)
        JPanel mainAreaPanel = new JPanel(new BorderLayout());
        mainAreaPanel.setOpaque(false);
        
        // Bienvenida arriba a la izquierda
        mainAreaPanel.add(welcomePanel, BorderLayout.NORTH);
        
        // Accesos centrados perfectamente
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(content);
        mainAreaPanel.add(centerContainer, BorderLayout.CENTER);
        
        root.add(mainAreaPanel, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);

        // Control dinámico de escala tipo CanvasScaler de Unity
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                float scaleX = getWidth() / 1300f;
                float scaleY = getHeight() / 850f;
                currentScale = Math.min(scaleX, scaleY);
                if (currentScale < 0.8f) currentScale = 0.8f;
                if (currentScale > 1.6f) currentScale = 1.6f; // Límite para mantener la elegancia

                // Escalar etiquetas de bienvenida
                if (welcomeLabel != null) {
                    welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(38 * currentScale)));
                }
                if (subtitleLabel != null) {
                    subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(17 * currentScale)));
                }

                // Escalar bordes y espaciados
                if (welcomePanel != null) {
                    welcomePanel.setBorder(new EmptyBorder((int)(10 * currentScale), (int)(60 * currentScale), 0, (int)(60 * currentScale)));
                }
                if (content != null) {
                    content.setBorder(new EmptyBorder(0, (int)(60 * currentScale), (int)(40 * currentScale), (int)(60 * currentScale)));
                }
                if (gridLayout != null && gridPanel != null) {
                    int gap = (int)(22 * currentScale);
                    gridLayout.setHgap(gap);
                    gridLayout.setVgap(gap);
                    gridPanel.setMaximumSize(new Dimension((int)(1180 * currentScale), (int)(460 * currentScale)));
                    gridPanel.setPreferredSize(new Dimension((int)(1180 * currentScale), (int)(460 * currentScale)));
                }
                if (headerPanel != null) {
                    headerPanel.setBorder(new EmptyBorder((int)(18 * currentScale), (int)(30 * currentScale), (int)(12 * currentScale), (int)(30 * currentScale)));
                }
                if (rightPanel != null) {
                    FlowLayout fl = (FlowLayout) rightPanel.getLayout();
                    fl.setHgap((int)(16 * currentScale));
                }

                // Escalar individualmente cada tarjeta
                for (MenuCardPanel card : cardsList) {
                    card.scaleCard(currentScale);
                }

                revalidate();
                repaint();
            }
        });
    }

    // ─── HEADER ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 30, 12, 30));

        // Sección derecha: Saldo + Notificaciones + Avatar
        rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setOpaque(false);

        // Badge de saldo UP
        Usuario actual = Usuario.getUsuarioActual();
        double saldo = (actual != null) ? actual.getSaldo() : 0;
        JPanel saldoBadge = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension((int)(140 * currentScale), (int)(38 * currentScale));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 30, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int)(20 * currentScale), (int)(20 * currentScale));
                g2.setColor(new Color(50, 65, 45));
                g2.setStroke(new BasicStroke(1.2f * currentScale));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, (int)(20 * currentScale), (int)(20 * currentScale));

                // Círculo UP pequeño
                g2.setColor(new Color(200, 170, 50));
                g2.fillOval((int)(8 * currentScale), (int)(7 * currentScale), (int)(24 * currentScale), (int)(24 * currentScale));
                g2.setColor(new Color(30, 25, 10));
                g2.setFont(new Font("SansSerif", Font.BOLD, (int)(10 * currentScale)));
                g2.drawString("UP", (int)(13 * currentScale), (int)(23 * currentScale));

                // Texto saldo
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("SansSerif", Font.BOLD, (int)(14 * currentScale)));
                String saldoText = String.format("%,.0f UP", saldo);
                g2.drawString(saldoText, (int)(40 * currentScale), (int)(24 * currentScale));
                g2.dispose();
            }
        };
        saldoBadge.setOpaque(false);

        // Campana de notificaciones (global)
        bellPanel = CampanaNotificaciones.crear(this);

        // Avatar del usuario
        JPanel avatarPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension((int)(42 * currentScale), (int)(42 * currentScale));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Cargar foto de perfil
                try {
                    String fotoPath = (Usuario.getUsuarioActual() != null)
                            ? Usuario.getUsuarioActual().getFotoPerfil()
                            : "Icons/UserDefaultpfp.png";
                    ImageIcon icon = new ImageIcon(fotoPath);
                    int avatarSize = (int)(38 * currentScale);
                    int offset = (int)(2 * currentScale);
                    Image img = icon.getImage().getScaledInstance(avatarSize, avatarSize, Image.SCALE_SMOOTH);

                    // Clip circular
                    Shape clip = new Ellipse2D.Float(offset, offset, avatarSize, avatarSize);
                    g2.setClip(clip);
                    g2.drawImage(img, offset, offset, avatarSize, avatarSize, null);
                    g2.setClip(null);

                    // Borde
                    g2.setColor(new Color(60, 70, 55));
                    g2.setStroke(new BasicStroke(1.5f * currentScale));
                    g2.drawOval(offset, offset, avatarSize - 1, avatarSize - 1);
                } catch (Exception e) {
                    int avatarSize = (int)(38 * currentScale);
                    int offset = (int)(2 * currentScale);
                    g2.setColor(new Color(40, 50, 38));
                    g2.fillOval(offset, offset, avatarSize, avatarSize);
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

                // Efecto hover para el JMenuItem
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

                // Mostrar alineado a la derecha debajo de la foto de perfil
                int px = avatarPanel.getWidth() - popup.getPreferredSize().width;
                int py = avatarPanel.getHeight() + 4;
                popup.show(avatarPanel, px, py);
            }
        });

        rightPanel.add(saldoBadge);
        rightPanel.add(bellPanel);
        rightPanel.add(avatarPanel);

        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ─── BIENVENIDA ───────────────────────────────────────────────────────────
    private JPanel buildWelcomePanel() {
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(new EmptyBorder(10, 60, 0, 60));

        welcomeLabel = new JLabel("¡Bienvenido, " + userName + "! 👋");
        welcomeLabel.setFont(FONT_WELCOME_BASE);
        welcomeLabel.setForeground(TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLabel = new JLabel("Elige una opción para comenzar");
        subtitleLabel.setFont(FONT_SUBTITLE_BASE);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(subtitleLabel);
        return welcomePanel;
    }

    // ─── CONTENIDO DE TARJETAS ────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 60, 40, 60));

        // Grilla de tarjetas
        gridLayout = new GridLayout(2, 4, 22, 22);
        gridPanel = new JPanel(gridLayout);
        gridPanel.setOpaque(false);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPanel.setMaximumSize(new Dimension(1180, 460));
        gridPanel.setPreferredSize(new Dimension(1180, 460));

        // Fila 1
        gridPanel.add(addCardToList(new MenuCardPanel("Chat", "chat", () -> {
            new Chat();
            dispose();
        })));
        gridPanel.add(addCardToList(new MenuCardPanel("Minijuegos", "gamepad", () -> {
            VentanaJuegos.crearVentanaLimpia();
            dispose();
        })));
        gridPanel.add(addCardToList(new MenuCardPanel("Ranking", "trophy", null)));
        gridPanel.add(addCardToList(new MenuCardPanel("Historial", "clock", () -> {
            new History();
            dispose();
        })));

        // Fila 2
        gridPanel.add(addCardToList(new MenuCardPanel("Logros", "medal", () -> {
            Logros.mostrarVentanaLogros();
            dispose();
        })));
        gridPanel.add(addCardToList(new MenuCardPanel("Lugares", "coins", () -> {
            new Places();
            dispose();
        })));
        gridPanel.add(addCardToList(new MenuCardPanel("Calculadora de notas", "bell", null)));
        gridPanel.add(addCardToList(new MenuCardPanel("Perfil", "profile", null)));

        mainContent.add(gridPanel);

        return mainContent;
    }

    private MenuCardPanel addCardToList(MenuCardPanel card) {
        cardsList.add(card);
        return card;
    }

    // ─── CLASE INTERNA PARA TARJETAS DINÁMICAS ──────────────────────────────
    private class MenuCardPanel extends JPanel {
        private String title;
        private String iconType;
        private JLabel iconLabel;
        private JLabel titleLabel;
        private JLabel arrowLabel;
        private JPanel iconWrapper;
        private JPanel bottomPanel;
        private boolean hovered = false;

        public MenuCardPanel(String title, String iconType, Runnable action) {
            this.title = title;
            this.iconType = iconType;

            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (action != null) {
                        action.run();
                    } else {
                        JOptionPane.showMessageDialog(MainMenu.this,
                                "Próximamente: " + title,
                                "En desarrollo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            // Icono central (imagen PNG)
            String fileName = getIconPath(iconType);
            iconLabel = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(fileName);
                Image scaledImage = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                // fallback
            }
            iconLabel.setPreferredSize(new Dimension(80, 80));

            iconWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            iconWrapper.setOpaque(false);
            iconWrapper.add(iconLabel);

            // Nombre de la tarjeta
            titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(FONT_CARD_TITLE_BASE);
            titleLabel.setForeground(TEXT_PRIMARY);

            // Flecha de redirección
            arrowLabel = new JLabel("→", SwingConstants.CENTER);
            arrowLabel.setFont(FONT_ARROW_BASE);
            arrowLabel.setForeground(GREEN_MAIN);

            bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            bottomPanel.setOpaque(false);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            bottomPanel.add(titleLabel);
            bottomPanel.add(arrowLabel);

            add(iconWrapper, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            scaleCard(currentScale);
        }

        private String getIconPath(String type) {
            switch (type) {
                case "chat": return "Icons/ChatAccess.png";
                case "gamepad": return "Icons/MinigamesAccess.png";
                case "trophy": return "Icons/RankingAccess.png";
                case "clock": return "Icons/HistoryAccess.png";
                case "medal": return "Icons/AchievementAccess.png";
                case "coins": return "Icons/PlaceAccess.png";
                case "bell": return "Icons/CalculatorAccess.png";
                case "profile": return "Icons/ProfileAccess.png";
                default: return "Icons/UserDefaultpfp.png";
            }
        }

        public void scaleCard(float scale) {
            setBorder(new EmptyBorder((int)(30 * scale), (int)(20 * scale), (int)(20 * scale), (int)(20 * scale)));
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(15 * scale)));
            arrowLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(16 * scale)));
            titleLabel.setBorder(new EmptyBorder((int)(8 * scale), 0, (int)(4 * scale), 0));

            int iconSize = (int)(75 * scale);
            if (iconSize < 30) iconSize = 30;

            try {
                String fileName = getIconPath(iconType);
                ImageIcon icon = new ImageIcon(fileName);
                Image scaledImage = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                // ignore
            }
            iconLabel.setPreferredSize(new Dimension(iconSize + 5, iconSize + 5));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo de la tarjeta
            g2.setColor(BG_CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int)(20 * currentScale), (int)(20 * currentScale));

            // Borde
            if (hovered) {
                g2.setColor(BORDER_HOVER);
                g2.setStroke(new BasicStroke(1.8f * currentScale));
            } else {
                g2.setColor(BORDER_CARD);
                g2.setStroke(new BasicStroke(1.2f * currentScale));
            }
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, (int)(20 * currentScale), (int)(20 * currentScale));

            // Brillo inferior verde sutil cuando hover
            if (hovered) {
                GradientPaint glowBottom = new GradientPaint(
                        0, getHeight() - (int)(30 * currentScale), new Color(30, 120, 30, 40),
                        0, getHeight(), new Color(30, 120, 30, 0));
                g2.setPaint(glowBottom);
                g2.fillRoundRect(0, getHeight() - (int)(30 * currentScale), getWidth(), (int)(30 * currentScale), (int)(20 * currentScale), (int)(20 * currentScale));
            }

            g2.dispose();
        }
    }


    // ─── MAIN ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
