import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Perfil {

    private static final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private static final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private static final Color COLOR_ITEM_FONDO = new Color(30, 36, 48);
    private static final Color COLOR_VERDE_ACENTO = new Color(44, 243, 53);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    private static JLabel pointsLbl;
    private static JPanel bannerPanel;
    private static JPanel avatarPanelCenter;

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

    public static void mostrarVentanaPerfil() {
        JFrame ventana = new JFrame();
        ventana.setTitle("Mi Perfil");
        ventana.setSize(1300, 850);
        ventana.setMinimumSize(new Dimension(900, 600));
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Usuario actual = Usuario.getUsuarioActual();
        if (actual == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.");
            return;
        }

        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);
        contenedorPrincipal.add(buildHeader(ventana), BorderLayout.NORTH);

        // ── PANEL CENTRAL con scroll
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(COLOR_FONDO_PRINCIPAL);
        scrollContent.setBorder(new EmptyBorder(30, 40, 40, 40));

        // ══════════════════════════════════════════════════════════
        // CONTENEDOR CENTRAL DINÁMICO (Responsive Fullscreen)
        // ══════════════════════════════════════════════════════════
        JPanel wrapperPrincipal = new JPanel();
        wrapperPrincipal.setLayout(new BoxLayout(wrapperPrincipal, BoxLayout.Y_AXIS));
        wrapperPrincipal.setOpaque(false);
        wrapperPrincipal.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        wrapperPrincipal.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ══════════════════════════════════════════════════════════
        // ZONA HERO: Banner + Avatar + Botones
        // ══════════════════════════════════════════════════════════
        final int AVATAR_SIZE  = 130;
        final int AVATAR_HALF  = AVATAR_SIZE / 2;
        final int BANNER_H     = 220;
        final int HERO_H       = BANNER_H + AVATAR_HALF + 10;

        JPanel heroPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // — Banner —
                try {
                    String bp = actual.getBanner();
                    if (bp == null || bp.isEmpty()) bp = "Banners/Night_Banner.png";
                    ImageIcon icon = ImageLoader.load(bp);
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), BANNER_H, 20, 20));
                    g2.drawImage(icon.getImage(), 0, 0, getWidth(), BANNER_H, null);
                    g2.setClip(null);
                } catch (Exception ex) {
                    g2.setColor(new Color(30, 45, 60));
                    g2.fillRoundRect(0, 0, getWidth(), BANNER_H, 20, 20);
                }
                g2.dispose();
            }
        };
        heroPanel.setOpaque(false);
        heroPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, HERO_H));
        heroPanel.setPreferredSize(new Dimension(0, HERO_H));
        heroPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón editar banner
        RoundedButton btnEditarBanner = new RoundedButton("✏️ Editar Banner", 15);
        btnEditarBanner.setBackground(new Color(0, 0, 0, 160));
        btnEditarBanner.setForeground(Color.WHITE);
        btnEditarBanner.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEditarBanner.addActionListener(e -> editarImagen(ventana, actual, true));

        // Botón cambiar foto
        RoundedButton btnEditarAvatar = new RoundedButton("📷 Cambiar Foto", 15);
        btnEditarAvatar.setBackground(COLOR_FONDO_PANEL);
        btnEditarAvatar.setForeground(COLOR_TEXTO);
        btnEditarAvatar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEditarAvatar.addActionListener(e -> editarImagen(ventana, actual, false));

        // Avatar circular superpuesto
        avatarPanelCenter = new JPanel() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(AVATAR_SIZE + 10, AVATAR_SIZE + 10); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo oscuro detrás del avatar
                g2.setColor(COLOR_FONDO_PRINCIPAL);
                g2.fillOval(2, 2, AVATAR_SIZE + 5, AVATAR_SIZE + 5);
                
                // Imagen
                String fp = actual.getFotoPerfil();
                if (fp == null || fp.isEmpty()) fp = "Icons/UserDefaultpfp.png";
                ImageIcon icon = ImageLoader.load(fp);
                if (icon.getIconWidth() == -1) {
                    icon = ImageLoader.load("Icons/UserDefaultpfp.png");
                }
                Shape clip = new Ellipse2D.Float(5, 5, AVATAR_SIZE, AVATAR_SIZE);
                g2.setClip(clip);
                g2.drawImage(icon.getImage(), 5, 5, AVATAR_SIZE, AVATAR_SIZE, null);
                g2.setClip(null);

                // Borde verde
                g2.setColor(COLOR_VERDE_ACENTO);
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(5, 5, AVATAR_SIZE - 1, AVATAR_SIZE - 1);
                g2.dispose();
            }
        };
        avatarPanelCenter.setOpaque(false);
        avatarPanelCenter.setBounds(30, BANNER_H - AVATAR_HALF, AVATAR_SIZE + 10, AVATAR_SIZE + 10);

        heroPanel.add(avatarPanelCenter);
        heroPanel.add(btnEditarBanner);
        heroPanel.add(btnEditarAvatar);

        // Posicionamiento dinámico de botones
        heroPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = heroPanel.getWidth();
                btnEditarBanner.setBounds(w - 185, 15, 150, 34);
                btnEditarAvatar.setBounds(w - 185, BANNER_H + 15, 150, 34);
            }
        });

        // ══════════════════════════════════════════════════════════
        // INFO NOMBRES
        // ══════════════════════════════════════════════════════════
        JPanel infoHeader = new JPanel(new BorderLayout());
        infoHeader.setOpaque(false);
        infoHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        infoHeader.setPreferredSize(new Dimension(0, 80));
        infoHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);
        textCol.setBorder(new EmptyBorder(5, 35, 0, 0)); // Alinear con el avatar

        JLabel lblNombreBig = new JLabel(actual.getNombre());
        lblNombreBig.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblNombreBig.setForeground(COLOR_TEXTO);

        JLabel lblUsernameBig = new JLabel("@" + actual.getUsername());
        lblUsernameBig.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblUsernameBig.setForeground(COLOR_TEXTO_MUTED);

        textCol.add(lblNombreBig);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(lblUsernameBig);
        
        infoHeader.add(textCol, BorderLayout.WEST);

        // ══════════════════════════════════════════════════════════
        // TARJETAS DE INFORMACIÓN (Grid Horizontal)
        // ══════════════════════════════════════════════════════════
        JPanel sectionTitle = new JPanel(new BorderLayout());
        sectionTitle.setOpaque(false);
        sectionTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionTitle.setBorder(new EmptyBorder(0, 35, 0, 0));
        
        JLabel lblInfoTitle = new JLabel("Información de Registro");
        lblInfoTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblInfoTitle.setForeground(COLOR_TEXTO_MUTED);
        sectionTitle.add(lblInfoTitle, BorderLayout.WEST);

        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 25, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        cardsContainer.setPreferredSize(new Dimension(0, 120));
        cardsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsContainer.setBorder(new EmptyBorder(0, 35, 0, 35));

        cardsContainer.add(crearTarjetaInfo("👤  Nombre Completo", actual.getNombre()));
        cardsContainer.add(crearTarjetaInfo("🏷️  Username", "@" + actual.getUsername()));
        cardsContainer.add(crearTarjetaInfo("📧  Correo Electrónico", actual.getCorreo()));

        // Agregar todo al wrapper
        wrapperPrincipal.add(heroPanel);
        wrapperPrincipal.add(Box.createVerticalStrut(5));
        wrapperPrincipal.add(infoHeader);
        wrapperPrincipal.add(Box.createVerticalStrut(35));
        wrapperPrincipal.add(sectionTitle);
        wrapperPrincipal.add(Box.createVerticalStrut(15));
        wrapperPrincipal.add(cardsContainer);

        scrollContent.add(wrapperPrincipal);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 65, 75);
                this.trackColor = COLOR_FONDO_PRINCIPAL;
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
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        contenedorPrincipal.add(scrollPane, BorderLayout.CENTER);

        ventana.add(contenedorPrincipal);
        WindowPreserver.configurarVentana(ventana);
        ventana.setVisible(true);
    }

    private static JPanel crearFilaInfo(String titulo, String valor) {
        RoundedPanel fila = new RoundedPanel(15, COLOR_ITEM_FONDO);
        fila.setLayout(new BorderLayout(15, 0));
        fila.setBorder(new EmptyBorder(12, 20, 12, 20));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblValor.setForeground(COLOR_TEXTO);

        fila.add(lblTitulo, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.EAST);
        return fila;
    }

    private static JPanel crearTarjetaInfo(String titulo, String valor) {
        RoundedPanel tarjeta = new RoundedPanel(15, COLOR_FONDO_PANEL);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO_MUTED);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblValor.setForeground(COLOR_TEXTO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(lblValor);
        return tarjeta;
    }

    private static void editarImagen(JFrame padre, Usuario actual, boolean esBanner) {
        if (esBanner) {
            mostrarSelectorBanners(padre, actual);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Foto de Perfil");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(padre);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Path targetDir = Paths.get("Profile Pictures");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                
                String extension = "";
                int i = selectedFile.getName().lastIndexOf('.');
                if (i > 0) extension = selectedFile.getName().substring(i);
                
                String newFileName = actual.getUsername() + "_PFP" + extension;
                Path targetPath = targetDir.resolve(newFileName);
                
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                actual.setFotoPerfil(targetPath.toString().replace("\\", "/"));
                if (avatarPanelCenter != null) avatarPanelCenter.repaint();
                padre.repaint();
                
                JOptionPane.showMessageDialog(padre, "¡Foto actualizada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(padre, "Error al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void mostrarSelectorBanners(JFrame padre, Usuario actual) {
        JDialog dialog = new JDialog(padre, "Selecciona tu Banner", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(padre);
        dialog.getContentPane().setBackground(COLOR_FONDO_PRINCIPAL);

        JPanel panelBanners = new JPanel(new GridLayout(0, 2, 20, 20));
        panelBanners.setBackground(COLOR_FONDO_PRINCIPAL);
        panelBanners.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] bannerFiles = {
            "Castle_Banner.png",
            "Japon_Banner.png",
            "Night_Banner.png",
            "Spider_Banner.png",
            "Underwater_Banner.png"
        };
        
        for (String fileName : bannerFiles) {
            String fullPath = "Banners/" + fileName;
            JPanel bannerContainer = new JPanel(new BorderLayout(0, 10));
            bannerContainer.setOpaque(false);
            
            JButton btnBanner = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    try {
                        ImageIcon icon = ImageLoader.load(fullPath);
                        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                        g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
                    } catch (Exception e) {}
                    g2.dispose();
                }
            };
            btnBanner.setPreferredSize(new Dimension(350, 150));
            btnBanner.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBanner.setBorderPainted(false);
            btnBanner.setContentAreaFilled(false);
            
            btnBanner.addActionListener(e -> {
                actual.setBanner(fullPath);
                if (bannerPanel != null) bannerPanel.repaint();
                dialog.dispose();
                JOptionPane.showMessageDialog(padre, "¡Banner actualizado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            });

            JLabel lblName = new JLabel(fileName.replace("_Banner", "").replace(".png", "").replace(".jpg", ""), SwingConstants.CENTER);
            lblName.setForeground(COLOR_TEXTO);
            lblName.setFont(new Font("SansSerif", Font.BOLD, 14));
            
            bannerContainer.add(btnBanner, BorderLayout.CENTER);
            bannerContainer.add(lblName, BorderLayout.SOUTH);
            panelBanners.add(bannerContainer);
        }

        JScrollPane scroll = new JScrollPane(panelBanners);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    // ════════════════════════════════════════════════════════
    // HEADER SUPERIOR PREMIUM UNIFICADO
    // ════════════════════════════════════════════════════════
    private static JPanel buildHeader(JFrame ventana) {
        Color BG_HEADER = new Color(1, 3, 7);
        Color BG_CARD = new Color(6, 12, 15);
        Color BORDER = new Color(25, 35, 30);
        Color GREEN = new Color(44, 243, 53);
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

        JButton backBtn = makeIconButton("←", 32);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        backBtn.addActionListener(e -> {
            new MainMenu();
            ventana.dispose();
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(backBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

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
        if (actual != null) saldo = actual.getSaldo();

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

        JPanel bellPanel = CampanaNotificaciones.crear(ventana);

        JPanel avatarPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(42, 42); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    String fotoPath = (Usuario.getUsuarioActual() != null) ? Usuario.getUsuarioActual().getFotoPerfil() : "Icons/UserDefaultpfp.png";
                    if (fotoPath == null || fotoPath.isEmpty()) fotoPath = "Icons/UserDefaultpfp.png";
                    ImageIcon icon = ImageLoader.load(fotoPath);
                    Image img = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
                    Shape clip = new java.awt.geom.Ellipse2D.Float(2, 2, 38, 38);
                    g2.setClip(clip);
                    g2.drawImage(img, 2, 2, 38, 38, null);
                    g2.setClip(null);
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
                    @Override public void mouseEntered(MouseEvent me) { logoutItem.setBackground(new Color(30, 45, 30)); }
                    @Override public void mouseExited(MouseEvent me) { logoutItem.setBackground(new Color(15, 20, 15)); }
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
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
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
        g2.drawString("UP", (size - fm.stringWidth("UP")) / 2, (size + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
        return ImageLoader.load(img);
    }
}
