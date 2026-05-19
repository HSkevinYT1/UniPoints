import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Places extends JFrame {

    // ── Paleta (Consistente con MainMenu) ───────────────────
    static final Color BG_DARK = new Color(1, 3, 7);
    static final Color BG_CARD = new Color(6, 12, 15);
    static final Color BG_HEADER = new Color(1, 3, 7);
    static final Color GREEN = new Color(44, 243, 53);
    static final Color GREEN_DIM = new Color(30, 180, 38);
    static final Color TEXT_WHITE = new Color(240, 240, 240);
    static final Color TEXT_GRAY = new Color(150, 155, 145);
    static final Color GOLD = new Color(245, 166, 35);
    static final Color BORDER = new Color(25, 35, 30);

    // ── Fuentes ─────────────────────────────────────────────
    static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 26);
    static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
    static final Font FONT_SEMI = new Font("SansSerif", Font.BOLD, 13);
    static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 12);
    static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 13);
    static final Font FONT_POINTS = new Font("SansSerif", Font.BOLD, 16);

    // Estado de tabs y componentes
    private String activeTab = "Explorar";
    private JLabel pointsLbl;
    private JTextField searchField;
    private JPanel cardsRowPanel;

    // Estado persistente de lugares unidos (static para persistencia entre
    // transiciones)
    private static final java.util.Set<String> joinedPlaces = new java.util.HashSet<>();

    // Información interna de cada tarjeta para filtros en tiempo real
    private static class PlaceCardInfo {
        JPanel cardPanel;
        String name;
        String location;
        JButton joinBtn;
    }

    private final java.util.List<PlaceCardInfo> cardList = new java.util.ArrayList<>();

    public Places() {
        // Inicializar usuario de prueba si no hay una sesión activa (ej. si se ejecuta
        // esta ventana sola)
        if (Usuario.getUsuarioActual() == null) {
            Usuario testUser = new Usuario("Usuario de Prueba", "prueba@unab.cl", "prueba", "123");
            Usuario.registrarUsuario(testUser);
            Usuario.iniciarSesion("prueba", "123");
        }

        setTitle("Lugares – UNAB Points");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 850); // Redimensionado para ser super consistente con MainMenu (1300, 850)
        setMinimumSize(new Dimension(1100, 650));
        setBackground(BG_DARK);
        setLocationRelativeTo(null);

        // Preservar estado de pantalla completa
        boolean maximizado = false;
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            if (f.isVisible() && (f.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH) == java.awt.Frame.MAXIMIZED_BOTH) {
                maximizado = true;
                break;
            }
        }
        if (maximizado) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════
    // HEADER
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

        // Campana con badge
        JPanel bellPanel = makeBellWithBadge(3);

        // Avatar del usuario (idéntico a MainMenu, con popup de Cerrar sesión)
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
    // CONTENIDO PRINCIPAL
    // ════════════════════════════════════════════════════════
    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(28, 30, 24, 30));

        content.add(buildSectionHeader());
        content.add(Box.createVerticalStrut(20));
        content.add(buildTabsAndSearch());
        content.add(Box.createVerticalStrut(24));
        content.add(buildCardsRow());
        content.add(Box.createVerticalStrut(20));
        content.add(buildBottomBanner());

        return content;
    }

    // ── Encabezado de sección ────────────────────────────────
    private JPanel buildSectionHeader() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Ícono de lugar
        JLabel icon = new JLabel(makeLocationIcon(54));

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);

        JLabel title = new JLabel("Lugares");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_WHITE);

        JLabel sub = new JLabel("Visita lugares aliados y gana APUNAB");
        sub.setFont(FONT_REG);
        sub.setForeground(TEXT_GRAY);

        texts.add(title);
        texts.add(Box.createVerticalStrut(2));
        texts.add(sub);

        panel.add(icon);
        panel.add(texts);
        return panel;
    }

    // ── Tabs + buscador ──────────────────────────────────────
    private JPanel buildTabsAndSearch() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Tabs
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabs.setOpaque(false);

        String[] tabNames = { "Explorar", "Mis lugares" };
        for (String name : tabNames) {
            tabs.add(makeTab(name));
            tabs.add(Box.createHorizontalStrut(4));
        }

        // Buscador con diseño premium centrado verticalmente usando BorderLayout
        JPanel searchBox = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        searchBox.setOpaque(false);
        searchBox.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        searchBox.setPreferredSize(new Dimension(300, 38));
        searchBox.setMaximumSize(new Dimension(300, 38));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchIcon.setForeground(TEXT_GRAY);

        searchField = new JTextField("Buscar lugares...", 16);
        searchField.setOpaque(false);
        searchField.setBackground(new Color(0, 0, 0, 0)); // Evita que Windows pinte el fondo blanco por defecto
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setForeground(TEXT_GRAY);
        searchField.setFont(FONT_REG);
        searchField.setCaretColor(TEXT_WHITE);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Buscar lugares...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Buscar lugares...");
                    searchField.setForeground(TEXT_GRAY);
                }
            }
        });

        // Filtrado en tiempo real al escribir en el buscador
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                filterCards(searchField.getText());
            }
        });

        searchBox.add(searchIcon, BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        row.add(tabs, BorderLayout.WEST);
        row.add(searchBox, BorderLayout.EAST);
        return row;
    }

    // ── Fila de tarjetas (con FlowLayout para centrado fluido de tarjetas de
    // tamaño fijo) ──
    private JPanel buildCardsRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));
        cardsRowPanel = row;

        Object[][] places = {
                { "L", "Cafetería del L", "Campus El Limonar", new Color(0x1a5c1a), new Color(0xd4af37), false },
                { "CSU", "Cafetería CSU", "Campus El Bosque", new Color(0xb71c1c), new Color(0xCC2222), false },
                { "B", "Cafetería Bosque", "Campus El Bosque", new Color(0xffffff), new Color(0x2E7D32), true },
                { "C", "Cafetería Casona", "Campus La Casona", new Color(0xffffff), new Color(0x1B5E20), true },
                { "BANU", "Banú", "Campus El Bosque", new Color(0x1a1a1a), new Color(0x888888), false },
        };

        cardList.clear();

        for (Object[] p : places) {
            String pName = (String) p[1];
            String pLoc = (String) p[2];
            JPanel cardPanel = buildPlaceCard(
                    (String) p[0],
                    pName,
                    pLoc,
                    (Color) p[3],
                    (Color) p[4],
                    (boolean) p[5]);

            row.add(cardPanel);

            // Registrar metadatos de la tarjeta para búsquedas en tiempo real
            PlaceCardInfo info = new PlaceCardInfo();
            info.cardPanel = cardPanel;
            info.name = pName;
            info.location = pLoc;
            for (Component comp : cardPanel.getComponents()) {
                if (comp instanceof JButton) {
                    info.joinBtn = (JButton) comp;
                }
            }
            cardList.add(info);
        }

        // Ejecutar filtro inicial para sincronizar el estado persistente
        filterCards("");

        return row;
    }

    // Helper de filtrado dinámico
    private void filterCards(String searchQuery) {
        String query = searchQuery.toLowerCase().trim();
        for (PlaceCardInfo info : cardList) {
            boolean matchesTab = false;
            if (activeTab.equals("Explorar")) {
                matchesTab = true;
            } else if (activeTab.equals("Mis lugares")) {
                matchesTab = joinedPlaces.contains(info.name);
            }

            boolean matchesSearch = query.equals("buscar lugares...") || query.isEmpty()
                    || info.name.toLowerCase().contains(query)
                    || info.location.toLowerCase().contains(query);

            boolean shouldShow = matchesTab && matchesSearch;
            info.cardPanel.putClientProperty("shouldShow", shouldShow);

            for (Component comp : info.cardPanel.getComponents()) {
                comp.setVisible(shouldShow);
            }

            if (!shouldShow) {
                info.cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 26, 20));
            }

            info.cardPanel.repaint();
        }
        if (cardsRowPanel != null) {
            cardsRowPanel.revalidate();
            cardsRowPanel.repaint();
        }
    }

    // ── Tarjeta individual (con dimensiones uniformes y alternador dinámico de
    // unirse) ──
    private JPanel buildPlaceCard(String iconText, String name, String location,
            Color bgColor, Color accentColor, boolean isEmoji) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Boolean shouldShow = (Boolean) getClientProperty("shouldShow");
                if (shouldShow != null && !shouldShow) {
                    return; // No pintar el fondo si está filtrado
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Boolean shouldShow = (Boolean) getClientProperty("shouldShow");
                if (shouldShow != null && !shouldShow) {
                    return; // No pintar el borde si está filtrado
                }
                super.paintBorder(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 20, 26, 20));
        card.setPreferredSize(new Dimension(220, 420));
        card.setMaximumSize(new Dimension(220, 420));

        // Logo circular
        JLabel logo = new JLabel(makeCircleLogo(iconText, bgColor, accentColor, isEmoji, 90));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(22));

        // Nombre
        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(FONT_BOLD);
        nameLbl.setForeground(TEXT_WHITE);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLbl);

        card.add(Box.createVerticalStrut(8));

        // Ubicación
        JPanel locRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        locRow.setOpaque(false);
        locRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel pin = new JLabel("📍");
        pin.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel locLbl = new JLabel(location);
        locLbl.setFont(FONT_SMALL);
        locLbl.setForeground(TEXT_GRAY);
        locRow.add(pin);
        locRow.add(locLbl);
        card.add(locRow);

        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(22));

        // Botón Unirme / Darme de baja
        boolean isJoined = joinedPlaces.contains(name);
        JButton btn = makeGreenButton(isJoined ? "Darme de baja" : "Unirme");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.addActionListener(e -> {
            if (btn.getText().equals("Unirme")) {
                joinedPlaces.add(name);
                btn.setText("Darme de baja");
                JOptionPane.showMessageDialog(this,
                        "🎉 ¡Te has unido con éxito a " + name + "!",
                        "¡Unido con Éxito!",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                joinedPlaces.remove(name);
                btn.setText("Unirme");
                JOptionPane.showMessageDialog(this,
                        "❌ Te has dado de baja de " + name + ".",
                        "Baja Confirmada",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            filterCards(searchField != null ? searchField.getText() : "");
        });
        card.add(btn);

        // Hover sutil
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Boolean shouldShow = (Boolean) card.getClientProperty("shouldShow");
                if (shouldShow != null && !shouldShow) {
                    return; // Ignorar hover si está filtrado
                }
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(0x22, 0xC5, 0x5E, 80), 1, true),
                        BorderFactory.createEmptyBorder(29, 19, 25, 19)));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Boolean shouldShow = (Boolean) card.getClientProperty("shouldShow");
                if (shouldShow != null && !shouldShow) {
                    return; // Ignorar hover si está filtrado
                }
                card.setBorder(BorderFactory.createEmptyBorder(30, 20, 26, 20));
                card.repaint();
            }
        });

        return card;
    }

    // ── Banner inferior ──────────────────────────────────────
    private JPanel buildBottomBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);

        JLabel giftIcon = new JLabel(makeGiftIcon(42));

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);
        JLabel t1 = new JLabel("Gana puntos visitando estos lugares");
        t1.setFont(FONT_SEMI);
        t1.setForeground(TEXT_WHITE);
        JLabel t2 = new JLabel("Acumula UP por cada visita y actividad.");
        t2.setFont(FONT_SMALL);
        t2.setForeground(TEXT_GRAY);
        textBlock.add(t1);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(t2);

        left.add(giftIcon);
        left.add(textBlock);

        banner.add(left, BorderLayout.WEST);
        return banner;
    }

    // ════════════════════════════════════════════════════════
    // COMPONENTES / HELPERS
    // ════════════════════════════════════════════════════════

    private JButton makeTab(String name) {
        JButton btn = new JButton(name) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean active = name.equals(activeTab);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Actualizar fuente y color de forma dinámica según el estado activo
                setFont(active ? new Font("SansSerif", Font.BOLD, 14) : FONT_REG);
                setForeground(active ? GREEN : TEXT_GRAY);

                super.paintComponent(g);
                if (active) {
                    g2.setColor(GREEN);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(6, getHeight() - 2, getWidth() - 6, getHeight() - 2);
                }
                g2.dispose();
            }
        };
        btn.setFont(name.equals(activeTab) ? new Font("SansSerif", Font.BOLD, 14) : FONT_REG);
        btn.setForeground(name.equals(activeTab) ? GREEN : TEXT_GRAY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 8, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            activeTab = name;
            filterCards(searchField != null ? searchField.getText() : "");
            // Forzar el repintado del contenedor de tabs para refrescar visualmente todas
            // las pestañas
            if (btn.getParent() != null) {
                btn.getParent().repaint();
            }
        });
        return btn;
    }

    private JButton makeGreenButton(String text) {
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

                String btnText = getText();
                if (btnText.equals("Darme de baja")) {
                    if (!isEnabled()) {
                        g2.setColor(new Color(45, 25, 25));
                    } else {
                        g2.setColor(hover ? new Color(180, 40, 40) : new Color(220, 50, 50));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(Color.WHITE);
                } else if (btnText.equals("Unido")) {
                    g2.setColor(new Color(25, 45, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(TEXT_GRAY);
                } else {
                    if (!isEnabled()) {
                        g2.setColor(new Color(25, 45, 30));
                    } else {
                        g2.setColor(hover ? GREEN_DIM : GREEN);
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(Color.WHITE);
                }

                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(btnText)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(btnText, tx, ty);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

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

    // ── Logo circular ────────────────────────────────────────
    private ImageIcon makeCircleLogo(String text, Color bg, Color accent, boolean emoji, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo blanco o de color
        g2.setColor(bg);
        g2.fillOval(0, 0, size - 1, size - 1);

        // Borde
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(1, 1, size - 3, size - 3);

        // Texto / emoji
        if (emoji) {
            Font ef = new Font("Segoe UI Emoji", Font.PLAIN, size / 2);
            g2.setFont(ef);
        } else {
            int fs = text.length() > 2 ? size / 4 : size / 3;
            g2.setFont(new Font("SansSerif", Font.BOLD, fs));
        }
        g2.setColor(emoji ? accent : (bg.equals(Color.WHITE) || bg.getRed() > 180 ? Color.BLACK : Color.WHITE));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (size - fm.stringWidth(text)) / 2;
        int ty = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, tx, ty);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ── Moneda ───────────────────────────────────────────────
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
        return new ImageIcon(img);
    }

    // ── Campana con badge ────────────────────────────────────
    private JPanel makeBellWithBadge(int count) {
        JPanel p = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // círculo fondo
                g2.setColor(BG_CARD);
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(BORDER);
                g2.drawOval(0, 0, 41, 41);
                // campana
                g2.setColor(TEXT_WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                g2.drawString("🔔", 8, 28);
                // badge verde
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
                return new Dimension(44, 44);
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ── Avatar ───────────────────────────────────────────────
    private ImageIcon makeAvatarIcon(int size) {
        BufferedImage img = new BufferedImage(size + 4, size + 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean hasCustomPhoto = false;
        Image customImg = null;
        Usuario actual = Usuario.getUsuarioActual();
        if (actual != null && actual.getFotoPerfil() != null && !actual.getFotoPerfil().isEmpty()) {
            try {
                customImg = new ImageIcon(actual.getFotoPerfil()).getImage();
                hasCustomPhoto = true;
            } catch (Exception e) {
                // fall back to default
            }
        }

        if (hasCustomPhoto && customImg != null) {
            g2.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, size, size));
            g2.drawImage(customImg, 0, 0, size, size, null);
            g2.setClip(null);
        } else {
            // fondo gris
            g2.setColor(new Color(0x3A3A3A));
            g2.fillOval(0, 0, size, size);
            // silueta
            g2.setColor(new Color(0x888888));
            g2.fillOval(size / 4, size / 6, size / 2, size / 2);
            g2.fillOval(size / 8, (int) (size * 0.55), (int) (size * 0.75), size);
        }

        // borde verde
        g2.setColor(GREEN);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(1, 1, size - 2, size - 2);

        // punto verde online
        g2.setColor(GREEN);
        g2.fillOval(size - 10, size - 10, 10, 10);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ── Ícono ubicación ──────────────────────────────────────
    private ImageIcon makeLocationIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // fondo círculo verde oscuro
        g2.setColor(new Color(0x14532D));
        g2.fillOval(0, 0, size - 1, size - 1);
        g2.setColor(new Color(GREEN.getRed(), GREEN.getGreen(), GREEN.getBlue(), 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(0, 0, size - 1, size - 1);
        // pin
        g2.setColor(GREEN);
        int cx = size / 2, r = size / 5, py = (int) (size * 0.72);
        g2.fillOval(cx - r, size / 4, r * 2, r * 2);
        int[] xp = { cx - r + 2, cx + r - 2, cx };
        int[] yp = { size / 4 + r, size / 4 + r, py };
        g2.fillPolygon(xp, yp, 3);
        g2.setColor(new Color(0x14532D));
        g2.fillOval(cx - r / 2, size / 4 + r / 2, r, r);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ── Ícono regalo ─────────────────────────────────────────
    private ImageIcon makeGiftIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(GREEN);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
        g2.drawString("🎁", 0, size - 2);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ════════════════════════════════════════════════════════
    // MAIN
    // ════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Look & feel del sistema (o Nimbus si está disponible)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(Places::new);
    }
}