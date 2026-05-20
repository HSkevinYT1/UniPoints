import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.awt.geom.Ellipse2D;
import java.io.File;

public class Ranking extends JFrame {

    public Ranking() {

        setTitle("Top Jugadores");
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // PANEL PRINCIPAL
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(11, 15, 20));
        panelPrincipal.setLayout(new BorderLayout());

        // HEADER PANEL (Atrás + Título)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        headerPanel.setOpaque(false);

        JButton btnVolver = new JButton("← Volver");
        btnVolver.setBackground(new Color(18, 24, 33));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 16));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            new MainMenu();
            Ranking.this.dispose();
        });
        headerPanel.add(btnVolver);

        JLabel titulo = new JLabel("Ranking Global");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        try {
            ImageIcon rawTrophy = new ImageIcon("Icons/trophy.png");
            Image imgTrophy = rawTrophy.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            titulo.setIcon(new ImageIcon(imgTrophy));
            titulo.setIconTextGap(15);
        } catch (Exception e) {
            // fallback
        }
        headerPanel.add(titulo);

        panelPrincipal.add(headerPanel, BorderLayout.NORTH);

        // CONTENIDO
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(11, 15, 20));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // Obtener ranking dinámico
        List<Usuario> ranking = Usuario.getRankingGlobal();
        int limite = Math.min(ranking.size(), 12);

        // =========================
        // TOP 3 (Columnas Podio)
        // =========================
        JPanel top3 = new JPanel(new GridLayout(1, 3, 20, 0));
        top3.setBackground(new Color(11, 15, 20));
        top3.setBorder(new EmptyBorder(10, 20, 20, 20));

        Usuario primero = ranking.size() > 0 ? ranking.get(0) : null;
        Usuario segundo = ranking.size() > 1 ? ranking.get(1) : null;
        Usuario tercero = ranking.size() > 2 ? ranking.get(2) : null;

        // Podio: Plata (2º), Oro (1º), Bronce (3º)
        if (segundo != null) {
            top3.add(crearTopJugador(
                    "2º",
                    segundo.getNombre(),
                    String.format("%,.0f UP", segundo.getSaldo()),
                    new Color(192, 192, 192),
                    segundo.getFotoPerfil()
            ));
        } else {
            top3.add(new JPanel() {{ setOpaque(false); }});
        }

        if (primero != null) {
            top3.add(crearTopJugador(
                    "1º",
                    primero.getNombre(),
                    String.format("%,.0f UP", primero.getSaldo()),
                    new Color(255, 215, 0),
                    primero.getFotoPerfil()
            ));
        } else {
            top3.add(new JPanel() {{ setOpaque(false); }});
        }

        if (tercero != null) {
            top3.add(crearTopJugador(
                    "3º",
                    tercero.getNombre(),
                    String.format("%,.0f UP", tercero.getSaldo()),
                    new Color(205, 127, 50),
                    tercero.getFotoPerfil()
            ));
        } else {
            top3.add(new JPanel() {{ setOpaque(false); }});
        }

        contenido.add(top3);

        // =========================
        // RESTO DEL TOP (Jugadores 4 a 12)
        // =========================
        int maxPuntos = primero != null ? (int) primero.getSaldo() : 20000;
        if (maxPuntos <= 0) maxPuntos = 20000;

        for (int i = 3; i < limite; i++) {
            Usuario u = ranking.get(i);
            contenido.add(crearJugador(i + 1, u.getNombre(), (int) u.getSaldo(), maxPuntos, u.getFotoPerfil()));
        }

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(11, 15, 20));

        panelPrincipal.add(scroll, BorderLayout.CENTER);

        add(panelPrincipal);

        WindowPreserver.configurarVentana(this);
        setVisible(true);
    }

    // =========================
    // AVATAR COMPONENT GENERATOR
    // =========================
    private JComponent crearComponenteAvatar(String nombre, String fotoPerfilPath, int size, Color fallbackColor) {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean pintado = false;
                if (fotoPerfilPath != null && !fotoPerfilPath.isEmpty()) {
                    try {
                        File imgFile = new File(fotoPerfilPath);
                        if (imgFile.exists()) {
                            ImageIcon icon = new ImageIcon(fotoPerfilPath);
                            Image img = icon.getImage();
                            g2.setClip(new Ellipse2D.Double(0, 0, size, size));
                            g2.drawImage(img, 0, 0, size, size, null);
                            g2.setClip(null);
                            pintado = true;
                        }
                    } catch (Exception e) {
                        // ignore and fallback
                    }
                }

                if (!pintado) {
                    g2.setColor(fallbackColor != null ? fallbackColor : Color.GREEN);
                    g2.fillOval(0, 0, size, size);
                    
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, size / 2));
                    FontMetrics fm = g2.getFontMetrics();
                    String initial = (nombre != null && !nombre.isEmpty()) ? nombre.substring(0, 1).toUpperCase() : "?";
                    int x = (size - fm.stringWidth(initial)) / 2;
                    int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(initial, x, y);
                }

                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(size, size));
        avatarPanel.setMaximumSize(new Dimension(size, size));
        avatarPanel.setMinimumSize(new Dimension(size, size));
        avatarPanel.setOpaque(false);
        return avatarPanel;
    }

    // =========================
    // TOP 3 CARD CREATOR (TEXTO)
    // =========================
    private JPanel crearTopJugador(String puestoTxt, String nombre, String puntos, Color color, String fotoPerfilPath) {

        JPanel card = new JPanel();
        card.setBackground(new Color(18, 24, 33));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel puestoLabel = new JLabel(puestoTxt);
        puestoLabel.setForeground(color);
        puestoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        puestoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int avatarSize = (puestoTxt.equals("1º")) ? 80 : 65;
        JComponent avatar = crearComponenteAvatar(nombre, fotoPerfilPath, avatarSize, color);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        int nombreFontSize = (puestoTxt.equals("1º")) ? 22 : 18;
        JLabel nombreLabel = new JLabel(nombre);
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, nombreFontSize));
        nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int puntosFontSize = (puestoTxt.equals("1º")) ? 20 : 16;
        JLabel puntosLabel = new JLabel(puntos);
        puntosLabel.setForeground(color);
        puntosLabel.setFont(new Font("Arial", Font.BOLD, puntosFontSize));
        puntosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(puestoLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(avatar);
        card.add(Box.createVerticalStrut(10));
        card.add(nombreLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(puntosLabel);

        return card;
    }

    // =========================
    // JUGADORES ROW CREATOR
    // =========================
    private JPanel crearJugador(int posicion, String nombre, int puntos, int maxPuntos, String fotoPerfilPath) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(11, 15, 20));
        wrapper.setBorder(new EmptyBorder(0, 20, 15, 20));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(18, 24, 33));
        card.setBorder(new EmptyBorder(12, 20, 12, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridy = 0;

        // 1. Posición
        JLabel pos = new JLabel("#" + posicion);
        pos.setForeground(new Color(46, 204, 113));
        pos.setFont(new Font("Arial", Font.BOLD, 20));
        pos.setPreferredSize(new Dimension(50, 45));
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(pos, gbc);

        // 2. Avatar
        JComponent avatar = crearComponenteAvatar(nombre, fotoPerfilPath, 45, new Color(46, 204, 113));
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(avatar, gbc);

        // Espaciador
        gbc.gridx = 2;
        card.add(Box.createHorizontalStrut(15), gbc);

        // 3. Nombre
        JLabel nombreLabel = new JLabel(nombre);
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nombreLabel.setPreferredSize(new Dimension(220, 45));
        gbc.gridx = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(nombreLabel, gbc);

        // Espaciador
        gbc.gridx = 4;
        card.add(Box.createHorizontalStrut(20), gbc);

        // 4. Barra de Progreso
        JProgressBar barra = new JProgressBar();
        barra.setMaximum(maxPuntos);
        barra.setValue(puntos);
        barra.setForeground(new Color(46, 204, 113));
        barra.setBackground(new Color(30, 36, 45));
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(200, 10));
        gbc.gridx = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(barra, gbc);

        // Espaciador
        gbc.gridx = 6;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        card.add(Box.createHorizontalStrut(25), gbc);

        // 5. Puntos
        JLabel puntosLabel = new JLabel(String.format("%,d UP", puntos));
        puntosLabel.setForeground(new Color(46, 204, 113));
        puntosLabel.setFont(new Font("Arial", Font.BOLD, 18));
        puntosLabel.setPreferredSize(new Dimension(130, 45));
        puntosLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 7;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(puntosLabel, gbc);

        wrapper.add(card);

        return wrapper;
    }
}