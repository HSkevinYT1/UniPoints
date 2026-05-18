import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

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

    // FUENTES
    private static final Font FONT_LOGO = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_WELCOME = new Font("SansSerif", Font.BOLD, 38);
    private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 17);
    private static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 15);
    private static final Font FONT_BADGE = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_ARROW = new Font("SansSerif", Font.PLAIN, 16);

    private String userName;

    public MainMenu() {
        Usuario actual = Usuario.getUsuarioActual();
        this.userName = (actual != null) ? actual.getNombre().split(" ")[0] : "Usuario";

        setTitle("Unab Points - Menú Principal");
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

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
                        400f,
                        new float[] { 0f, 0.4f, 1f },
                        new Color[] {
                                new Color(20, 80, 20, 60),
                                new Color(15, 50, 15, 25),
                                new Color(0, 0, 0, 0)
                        });
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Texto "UP" fantasma en el fondo
                g2.setFont(new Font("SansSerif", Font.BOLD, 200));
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

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ─── HEADER ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 30, 12, 30));

        // Sección derecha: Saldo + Notificaciones + Avatar
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setOpaque(false);

        // Badge de saldo UP
        Usuario actual = Usuario.getUsuarioActual();
        double saldo = (actual != null) ? actual.getSaldo() : 0;
        JPanel saldoBadge = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 38);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 30, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(50, 65, 45));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                // Círculo UP pequeño
                g2.setColor(new Color(200, 170, 50));
                g2.fillOval(8, 7, 24, 24);
                g2.setColor(new Color(30, 25, 10));
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("UP", 13, 23);

                // Texto saldo
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(FONT_BADGE);
                String saldoText = String.format("%,.0f UP", saldo);
                g2.drawString(saldoText, 40, 24);
                g2.dispose();
            }
        };
        saldoBadge.setOpaque(false);

        // Campana de notificaciones
        JPanel bellPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(38, 38);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Icono campana (simplificada)
                g2.setColor(TEXT_SECONDARY);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Cuerpo campana
                g2.drawArc(8, 6, 22, 22, 0, 180);
                g2.drawLine(8, 17, 8, 26);
                g2.drawLine(30, 17, 30, 26);
                g2.drawLine(8, 26, 30, 26);
                // Badana
                g2.drawLine(16, 29, 22, 29);

                // Badge rojo con número
                g2.setColor(new Color(230, 50, 50));
                g2.fillOval(23, 2, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("3", 28, 14);
                g2.dispose();
            }
        };
        bellPanel.setOpaque(false);
        bellPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Avatar del usuario
        JPanel avatarPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
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
                    Image img = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);

                    // Clip circular
                    Shape clip = new Ellipse2D.Float(2, 2, 38, 38);
                    g2.setClip(clip);
                    g2.drawImage(img, 2, 2, 38, 38, null);
                    g2.setClip(null);

                    // Borde
                    g2.setColor(new Color(60, 70, 55));
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
                JPopupMenu popup = new JPopupMenu();
                popup.setBackground(new Color(15, 20, 15));
                popup.setBorder(BorderFactory.createLineBorder(new Color(50, 65, 45), 1));

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

    // ─── CONTENIDO PRINCIPAL ──────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 60, 40, 60));

        // Bienvenida
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomePanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel welcomeLabel = new JLabel("¡Bienvenido, " + userName + "! \uD83D\uDC4B");
        welcomeLabel.setFont(FONT_WELCOME);
        welcomeLabel.setForeground(TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Elige una opción para comenzar");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(subtitleLabel);

        content.add(welcomePanel);

        // Espacio para mantener las tarjetas en su posición original
        content.add(Box.createVerticalStrut(75));

        // Grilla de tarjetas
        JPanel gridPanel = new JPanel(new GridLayout(2, 4, 22, 22));
        gridPanel.setOpaque(false);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPanel.setMaximumSize(new Dimension(1180, 460));

        // Fila 1
        gridPanel.add(buildMenuCard("Chat", "chat", () -> {
            new Chat();
            dispose();
        }));
        gridPanel.add(buildMenuCard("Minijuegos", "gamepad", () -> {
            VentanaJuegos.crearVentanaLimpia();
            dispose();
        }));
        gridPanel.add(buildMenuCard("Ranking", "trophy", null));
        gridPanel.add(buildMenuCard("Historial", "clock", null));

        // Fila 2
        gridPanel.add(buildMenuCard("Logros", "medal", null));
        gridPanel.add(buildMenuCard("Lugares", "coins", null));
        gridPanel.add(buildMenuCard("Calculadora de notas", "bell", null));
        gridPanel.add(buildMenuCard("Perfil", "profile", null));

        content.add(gridPanel);

        return content;
    }

    // ─── TARJETA DE MENÚ ──────────────────────────────────────────────────────
    private JPanel buildMenuCard(String title, String iconType, Runnable action) {
        boolean[] hovered = { false };

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo de la tarjeta
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde
                if (hovered[0]) {
                    g2.setColor(BORDER_HOVER);
                    g2.setStroke(new BasicStroke(1.8f));
                } else {
                    g2.setColor(BORDER_CARD);
                    g2.setStroke(new BasicStroke(1.2f));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                // Brillo inferior verde sutil cuando hover
                if (hovered[0]) {
                    GradientPaint glowBottom = new GradientPaint(
                            0, getHeight() - 30, new Color(30, 120, 30, 40),
                            0, getHeight(), new Color(30, 120, 30, 0));
                    g2.setPaint(glowBottom);
                    g2.fillRoundRect(0, getHeight() - 30, getWidth(), 30, 20, 20);
                }

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(30, 20, 20, 20));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered[0] = true;
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered[0] = false;
                card.repaint();
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

        // Icono central
        // Icono central (imagen PNG)
        String fileName = "";
        switch (iconType) {
            case "chat":
                fileName = "Icons/ChatAccess.png";
                break;
            case "gamepad":
                fileName = "Icons/MinigamesAccess.png";
                break;
            case "trophy":
                fileName = "Icons/RankingAccess.png";
                break;
            case "clock":
                fileName = "Icons/HistoryAccess.png";
                break;
            case "medal":
                fileName = "Icons/AchievementAccess.png";
                break;
            case "coins":
                fileName = "Icons/PlaceAccess.png";
                break;
            case "bell":
                fileName = "Icons/CalculatorAccess.png";
                break;
            case "profile":
                fileName = "Icons/ProfileAccess.png";
                break;
            default:
                fileName = "Icons/UserDefaultpfp.png";
                break;
        }

        JLabel iconLabel;
        try {
            ImageIcon icon = new ImageIcon(fileName);
            Image scaledImage = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaledImage));
        } catch (Exception e) {
            iconLabel = new JLabel();
        }
        iconLabel.setPreferredSize(new Dimension(80, 80));

        JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconWrapper.setOpaque(false);
        iconWrapper.add(iconLabel);

        // Nombre de la tarjeta
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(FONT_CARD_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(8, 0, 4, 0));

        // Flecha de redirección
        JLabel arrowLabel = new JLabel("→", SwingConstants.CENTER);
        arrowLabel.setFont(FONT_ARROW);
        arrowLabel.setForeground(GREEN_MAIN);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(titleLabel);
        bottomPanel.add(arrowLabel);

        card.add(iconWrapper, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    // ─── MAIN ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
