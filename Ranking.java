import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;

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

        JLabel titulo = new JLabel("🏆 Ranking Global");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(titulo);

        panelPrincipal.add(headerPanel, BorderLayout.NORTH);

        // CONTENIDO
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(11, 15, 20));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // Obtener ranking dinámico
        List<Usuario> ranking = Usuario.getRankingGlobal();

        // =========================
        // TOP 3
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
                    "🥈",
                    segundo.getNombre(),
                    String.format("%,.0f UP", segundo.getSaldo()),
                    new Color(192, 192, 192)
            ));
        } else {
            top3.add(new JPanel() {{ setOpaque(false); }});
        }

        if (primero != null) {
            top3.add(crearTopJugador(
                    "🥇",
                    primero.getNombre(),
                    String.format("%,.0f UP", primero.getSaldo()),
                    new Color(255, 215, 0)
            ));
        } else {
            top3.add(new JPanel() {{ setOpaque(false); }});
        }

        if (tercero != null) {
            top3.add(crearTopJugador(
                    "🥉",
                    tercero.getNombre(),
                    String.format("%,.0f UP", tercero.getSaldo()),
                    new Color(205, 127, 50)
            ));
        } else {
            top3.add(new JPanel() {{ setOpaque(false); }});
        }

        contenido.add(top3);

        // =========================
        // RESTO DEL TOP
        // =========================
        int maxPuntos = primero != null ? (int) primero.getSaldo() : 20000;
        if (maxPuntos <= 0) maxPuntos = 20000;

        for (int i = 3; i < ranking.size(); i++) {
            Usuario u = ranking.get(i);
            contenido.add(crearJugador(i + 1, u.getNombre(), (int) u.getSaldo(), maxPuntos));
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
    // TOP 3 CARD CREATOR
    // =========================
    private JPanel crearTopJugador(String emoji, String nombre, String puntos, Color color) {

        JPanel card = new JPanel();
        card.setBackground(new Color(18, 24, 33));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel icono = new JLabel(emoji);
        icono.setFont(new Font("Arial", Font.PLAIN, 45));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        String initial = (nombre != null && !nombre.isEmpty()) ? nombre.substring(0, 1) : "?";
        JLabel avatar = new JLabel(initial);
        avatar.setOpaque(true);
        avatar.setBackground(color);
        avatar.setForeground(Color.BLACK);
        avatar.setPreferredSize(new Dimension(70, 70));
        avatar.setMaximumSize(new Dimension(70, 70));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 32));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nombreLabel = new JLabel(nombre);
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel puntosLabel = new JLabel(puntos);
        puntosLabel.setForeground(color);
        puntosLabel.setFont(new Font("Arial", Font.BOLD, 20));
        puntosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icono);
        card.add(Box.createVerticalStrut(10));
        card.add(avatar);
        card.add(Box.createVerticalStrut(15));
        card.add(nombreLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(puntosLabel);

        return card;
    }

    // =========================
    // JUGADORES 4+ ROW CREATOR
    // =========================
    private JPanel crearJugador(int posicion, String nombre, int puntos, int maxPuntos) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(11, 15, 20));
        wrapper.setBorder(new EmptyBorder(0, 20, 15, 20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(18, 24, 33));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        // IZQUIERDA
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        izquierda.setBackground(new Color(18, 24, 33));

        JLabel pos = new JLabel("#" + posicion);
        pos.setForeground(Color.GREEN);
        pos.setFont(new Font("Arial", Font.BOLD, 20));

        String initial = (nombre != null && !nombre.isEmpty()) ? nombre.substring(0, 1) : "?";
        JLabel avatar = new JLabel(initial);
        avatar.setOpaque(true);
        avatar.setBackground(Color.GREEN);
        avatar.setForeground(Color.BLACK);
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel nombreLabel = new JLabel(nombre);
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        izquierda.add(pos);
        izquierda.add(Box.createHorizontalStrut(15));
        izquierda.add(avatar);
        izquierda.add(Box.createHorizontalStrut(15));
        izquierda.add(nombreLabel);

        // CENTRO
        JProgressBar barra = new JProgressBar();
        barra.setMaximum(maxPuntos);
        barra.setValue(puntos);
        barra.setForeground(Color.GREEN);
        barra.setBackground(new Color(40, 40, 40));
        barra.setPreferredSize(new Dimension(250, 20));

        // DERECHA
        JLabel puntosLabel = new JLabel(String.format("%,d UP", puntos));
        puntosLabel.setForeground(Color.GREEN);
        puntosLabel.setFont(new Font("Arial", Font.BOLD, 18));

        card.add(izquierda, BorderLayout.WEST);
        card.add(barra, BorderLayout.CENTER);
        card.add(puntosLabel, BorderLayout.EAST);

        wrapper.add(card);

        return wrapper;
    }
}