import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Ranking extends JFrame {

    public Ranking() {

        setTitle("Top Jugadores");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // PANEL PRINCIPAL
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(11, 15, 20));
        panelPrincipal.setLayout(new BorderLayout());

        // TITULO
        JLabel titulo = new JLabel("🏆 Ranking Global");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setBorder(new EmptyBorder(20, 25, 20, 25));

        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // CONTENIDO
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(11, 15, 20));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // =========================
        // TOP 3
        // =========================

        JPanel top3 = new JPanel(new GridLayout(1, 3, 20, 0));
        top3.setBackground(new Color(11, 15, 20));
        top3.setBorder(new EmptyBorder(10, 20, 20, 20));

        top3.add(crearTopJugador(
                "🥈",
                "Kevin",
                "18.000 pts",
                new Color(192,192,192)
        ));

        top3.add(crearTopJugador(
                "🥇",
                "Juan",
                "20.000 pts",
                new Color(255,215,0)
        ));

        top3.add(crearTopJugador(
                "🥉",
                "Julian",
                "17.000 pts",
                new Color(205,127,50)
        ));

        contenido.add(top3);

        // =========================
        // RESTO DEL TOP
        // =========================

        contenido.add(crearJugador(4, "Alejandro", 15000));
        contenido.add(crearJugador(5, "Maria", 14000));
        contenido.add(crearJugador(6, "Camila", 13000));
        contenido.add(crearJugador(7, "Santiago", 12000));
        contenido.add(crearJugador(8, "Laura", 11000));
        contenido.add(crearJugador(9, "Andres", 10000));
        contenido.add(crearJugador(10, "Daniel", 9000));
        contenido.add(crearJugador(11, "Sebastian", 8500));
        contenido.add(crearJugador(12, "Valentina", 8000));

        JScrollPane scroll = new JScrollPane(contenido);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(11, 15, 20));

        panelPrincipal.add(scroll, BorderLayout.CENTER);

        add(panelPrincipal);

        // Preservar tamaño/maximizado
        WindowPreserver.configurarVentana(this);
    }


    // =========================
    // TOP 3
    // =========================

    private JPanel crearTopJugador(String emoji, String nombre, String puntos, Color color) {

        JPanel card = new JPanel();
        card.setBackground(new Color(18, 24, 33));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20,20,20,20));

        JLabel icono = new JLabel(emoji);
        icono.setFont(new Font("Arial", Font.PLAIN, 45));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel avatar = new JLabel(nombre.substring(0,1));
        avatar.setOpaque(true);
        avatar.setBackground(color);
        avatar.setForeground(Color.BLACK);
        avatar.setPreferredSize(new Dimension(70,70));
        avatar.setMaximumSize(new Dimension(70,70));
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
    // JUGADORES 4-12
    // =========================

    private JPanel crearJugador(int posicion, String nombre, int puntos) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(11,15,20));
        wrapper.setBorder(new EmptyBorder(0,20,15,20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(18,24,33));
        card.setBorder(new EmptyBorder(15,20,15,20));

        // IZQUIERDA
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        izquierda.setBackground(new Color(18,24,33));

        JLabel pos = new JLabel("#" + posicion);
        pos.setForeground(Color.GREEN);
        pos.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel avatar = new JLabel(nombre.substring(0,1));
        avatar.setOpaque(true);
        avatar.setBackground(Color.GREEN);
        avatar.setForeground(Color.BLACK);
        avatar.setPreferredSize(new Dimension(45,45));
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

        barra.setMaximum(20000);
        barra.setValue(puntos);

        barra.setForeground(Color.GREEN);
        barra.setBackground(new Color(40,40,40));

        barra.setPreferredSize(new Dimension(250,20));

        // DERECHA
        JLabel puntosLabel = new JLabel(puntos + " pts");
        puntosLabel.setForeground(Color.GREEN);
        puntosLabel.setFont(new Font("Arial", Font.BOLD, 18));

        card.add(izquierda, BorderLayout.WEST);
        card.add(barra, BorderLayout.CENTER);
        card.add(puntosLabel, BorderLayout.EAST);

        wrapper.add(card);

        return wrapper;
    }
}