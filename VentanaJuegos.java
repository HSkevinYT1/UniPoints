import javax.swing.*;
import java.awt.*;

public class VentanaJuegos {

    private static int cantidadMonedas = 12000;

    public static void main(String[] args) {
        crearVentanaLimpia();
    }

    private static void crearVentanaLimpia() {
        JFrame ventana = new JFrame();
        ventana.setTitle("Unab Points");
        ventana.setSize(1300, 850);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cambiado a EXIT para cerrar todo

        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(new Color(13, 13, 13));

        contenedorPrincipal.add(crearEncabezado(), BorderLayout.NORTH);

        // El cuerpo ahora es un JScrollPane por si añades más juegos
        JScrollPane scroll = new JScrollPane(crearCuerpo());
        scroll.setBorder(null);
        contenedorPrincipal.add(scroll, BorderLayout.CENTER);

        ventana.add(contenedorPrincipal);
        ventana.setVisible(true);
    }

    private static JPanel crearEncabezado() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(13, 13, 13));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitulo = new JLabel("Minijuegos");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        panelSuperior.add(crearSeccionUsuario(), BorderLayout.EAST);
        return panelSuperior;
    }

    private static JPanel crearSeccionUsuario() {
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelDerecho.setOpaque(false);

        JLabel lblSaldo = new JLabel(String.format("%, d", cantidadMonedas));
        lblSaldo.setForeground(new Color(200, 200, 200));
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnRecargar = new JButton("Recargar");
        estilizarBotónVerde(btnRecargar);

        panelDerecho.add(lblSaldo);
        panelDerecho.add(btnRecargar);
        return panelDerecho;
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
            /* [REDIRECCIÓN JUEGO BINGO] */
            // VentanaBingo v = new VentanaBingo();
            // v.setVisible(true);
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
            /* [REDIRECCIÓN BLACKJACK] */
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