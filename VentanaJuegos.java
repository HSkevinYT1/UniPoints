import javax.swing.*;
import java.awt.*;

public class VentanaJuegos {

    private static int cantidadMonedas = 12000;

    public static void main(String[] args) {
        crearVentanaLimpia();
    }

    public static void crearVentanaLimpia() {
        JFrame ventana = new JFrame();
        ventana.setTitle("Unab Points");
        ventana.setSize(1300, 850);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cambiado a EXIT para cerrar todo

        // Preservar estado de pantalla completa
        boolean maximizado = false;
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            if (f.isVisible() && (f.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH) == java.awt.Frame.MAXIMIZED_BOTH) {
                maximizado = true;
                break;
            }
        }
        if (maximizado) {
            ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(new Color(13, 13, 13));

        contenedorPrincipal.add(buildHeader(ventana), BorderLayout.NORTH);

        // El cuerpo ahora es un JScrollPane por si añades más juegos
        JScrollPane scroll = new JScrollPane(crearCuerpo());
        scroll.setBorder(null);
        contenedorPrincipal.add(scroll, BorderLayout.CENTER);

        ventana.add(contenedorPrincipal);
        ventana.setVisible(true);
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
        JLabel pointsLbl = new JLabel(String.format("%,.0f ", saldo));
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
                    String fotoPath = (Usuario.getUsuarioActual() != null)
                            ? Usuario.getUsuarioActual().getFotoPerfil()
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
        avatarPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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

                logoutItem.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent me) {
                        logoutItem.setBackground(new Color(30, 45, 30));
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent me) {
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

    private static JPanel crearCuerpo() {
        // Panel con margen para que no toque los bordes
        JPanel contenedorCuerpo = new JPanel(new BorderLayout());
        contenedorCuerpo.setBackground(new Color(20, 20, 20));
        contenedorCuerpo.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Título de la sección
        JLabel lblSeccion = new JLabel("Todos");
        lblSeccion.setForeground(Color.WHITE);
        lblSeccion.setFont(new Font("Arial", Font.BOLD, 22));
        lblSeccion.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contenedorCuerpo.add(lblSeccion, BorderLayout.NORTH);

        // Cuadrícula de juegos: 2 filas, 4 columnas, 20px de espacio
        JPanel rejillaJuegos = new JPanel(new GridLayout(2, 4, 20, 20));
        rejillaJuegos.setOpaque(false);

        // AÑADIMOS LOS JUEGOS
        // -----------------------------------------------------------------------------------------
        // 1. Carreras
        rejillaJuegos.add(crearCardJuego("Carreras","Icons/RaceGame.png", new Color(44, 243, 53), e -> {
            /* [REDIFRECCIÓN JUEGO CARRERAS] */
            JuegoCarrera segundaVentana = new JuegoCarrera();
            segundaVentana.setVisible(true);
            SwingUtilities.getWindowAncestor((Component)e.getSource()).dispose();
        }));

        // 2. Bingo
        rejillaJuegos.add(crearCardJuego("Bingo","Icons/BingoGame.png", new Color(120, 43, 244), e -> {
            JuegoBingo bingo = new JuegoBingo();
            bingo.setVisible(true);
            SwingUtilities.getWindowAncestor((Component)e.getSource()).dispose();
        }));

        // 3. Penales
        rejillaJuegos.add(crearCardJuego("Penales","Icons/GolGame.png", new Color(26, 60, 169), e -> {
            /* [REDIRECCIÓN JUEGO PENALES] */
        }));

        // 4. Lucky Spin
        rejillaJuegos.add(crearCardJuego("Lucky Spin","Icons/LuckySpinGame.png", new Color(161, 7, 9), e -> {
            /* [REDIRECCIÓN LUCKY SPIN] */
        }));

        // 5. Dados
        rejillaJuegos.add(crearCardJuego("Dados","Icons/DiceGame.png", new Color(245, 192, 10), e -> {
            /* [REDIRECCIÓN DADOS] */
        }));

        // 6. Aviator
        rejillaJuegos.add(crearCardJuego("Aviator","Icons/AviatorGame.png", new Color(7, 176, 188), e -> {
            /* [REDIRECCIÓN AVIATOR] */
        }));

        // 7. Ruleta
        rejillaJuegos.add(crearCardJuego("Ruleta","Icons/RouletteGame.png", new Color(255, 46, 126), e -> {
            /* [REDIRECCIÓN RULETA] */
        }));

        // 8. Blackjack
        rejillaJuegos.add(crearCardJuego("Blackjack","Icons/BlackJackGame.png", new Color(252, 103, 1), e -> {
            JuegoBlackjack blackjack = new JuegoBlackjack();
            blackjack.setVisible(true);
            SwingUtilities.getWindowAncestor((Component)e.getSource()).dispose();
        }));
        // -----------------------------------------------------------------------------------------

        contenedorCuerpo.add(rejillaJuegos, BorderLayout.CENTER);
        return contenedorCuerpo;
    }

    private static JPanel crearCardJuego(String titulo,String rutaImg, Color colorFondo, java.awt.event.ActionListener accion ) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(colorFondo);
        card.setPreferredSize(new Dimension(250, 300));

        // Área de la imagen (Centro)
        // [INDICACIÓN: AQUÍ DEBES PONER EL LLAMADO A LA IMAGEN SEGÚN EL TÍTULO]
        // Ejemplo: JLabel imgLabel = new JLabel(new ImageIcon("img/" + titulo.toLowerCase() + ".png"));
        JLabel lblImagen = new JLabel(new ImageIcon(new ImageIcon(rutaImg).getImage().getScaledInstance(308, 310, Image.SCALE_SMOOTH)));
        lblImagen.setForeground(new Color(0, 0, 0, 100)); // Texto temporal
        card.add(lblImagen, BorderLayout.CENTER);

        // Panel inferior de la Card (Título y Botón Jugar)
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(0, 0, 0, 80)); // Fondo oscuro traslúcido
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblNombre = new JLabel(titulo);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnJugar = new JButton("Jugar");
        btnJugar.setBackground(new Color(34, 197, 94));
        btnJugar.setForeground(Color.WHITE);
        btnJugar.setFocusPainted(false);
        btnJugar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnJugar.addActionListener(accion); // Ejecuta la redirección pasada por parámetro

        panelInferior.add(lblNombre, BorderLayout.WEST);
        panelInferior.add(btnJugar, BorderLayout.EAST);

        card.add(panelInferior, BorderLayout.SOUTH);
        return card;
    }

    private static void estilizarBotónVerde(JButton boton) {
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBackground(new Color(34, 197, 94));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
}
