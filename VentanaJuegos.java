import javax.swing.*;
import java.awt.*;

public class VentanaJuegos {

    private static int cantidadMonedas = 12000;

    public static void main(String[] args) {
        crearVentanaLimpia();
    }


    private static void crearVentanaLimpia() {
        JFrame ventana = new JFrame();

        // --- TUS ESPECIFICACIONES ---
        ventana.setTitle("Unab Points");
        ventana.setSize(1300, 850);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // -----------------------------

        // Contenedor principal
        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(new Color(13, 13, 13));

        // Ensamblaje de partes
        contenedorPrincipal.add(crearEncabezado(), BorderLayout.NORTH);
        contenedorPrincipal.add(crearCuerpo(), BorderLayout.CENTER);

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

        // Botón para cerrar la ventana (X) ya que quitamos el OK
        JButton btnCerrar = new JButton("X");
        btnCerrar.setForeground(Color.GRAY);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> System.exit(0));

        panelDerecho.add(lblSaldo);
        panelDerecho.add(btnRecargar);
        panelDerecho.add(btnCerrar);

        return panelDerecho;
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

    private static JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(new Color(20, 20, 20));
        cuerpo.setPreferredSize(new Dimension(800, 500));

        // Panel contenedor vertical para apilar el texto y el botón
        JPanel contenedorCentral = new JPanel();
        contenedorCentral.setLayout(new BoxLayout(contenedorCentral, BoxLayout.Y_AXIS));
        contenedorCentral.setOpaque(false);

        // A. El texto de bienvenida

        // B. LLAMADA AL MÉTODO DEL BOTÓN
        // Aquí es donde invocas el método creado arriba
        JButton btnJugar = crearBotonJuego("¡JUGAR AHORA!", new Color(34, 197, 94));
        btnJugar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJugar.addActionListener(e ->{
            JuegoCarrera segundaVentana = new JuegoCarrera();
            segundaVentana.setVisible(true);
            Window ventanaActual = SwingUtilities.getWindowAncestor(btnJugar);
            ventanaActual.dispose();
        });

        contenedorCentral.add(Box.createVerticalStrut(20)); // Espacio entre texto y botón
        contenedorCentral.add(btnJugar);

        // Añadimos el contenedor al cuerpo principal (centrado por el GridBagLayout)
        cuerpo.add(contenedorCentral);

        return cuerpo;
    }

    //Método para generar botones personalizados para el cuerpo
    private static JButton crearBotonJuego(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);

        // Aplicamos el estilo "Gaming"
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);

        // Bordes internos para que el botón tenga buen tamaño
        boton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Cambiar el cursor al pasar por encima
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

}