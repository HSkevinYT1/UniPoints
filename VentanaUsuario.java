import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaUsuario {

    // Paleta de Colores
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(11, 14, 20);
    private static final Color COLOR_FONDO_PANEL = new Color(22, 26, 35);
    private static final Color COLOR_VERDE_ACENTO = new Color(0, 230, 42);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_MUTED = new Color(138, 143, 153);

    // Clase interna para el manejo de logros (usada por GestorLogros)
    public static class Logro {
        private String descripcion;
        private int recompensaMonedas;
        private boolean completado;
        private boolean reclamado;

        public Logro(String descripcion, int recompensaMonedas) {
            this.descripcion = descripcion;
            this.recompensaMonedas = recompensaMonedas;
            this.completado = false;
            this.reclamado = false;
        }

        public void setCompletado(boolean completado) { this.completado = completado; }
        public boolean isCompletado() { return completado; }
        public boolean isReclamado() { return reclamado; }
        public void marcarComoReclamado() { this.reclamado = true; }
        public String getDescripcion() { return descripcion; }
        public int getRecompensaMonedas() { return recompensaMonedas; }
    }

    // Cambiado a un JFrame dinámico e independiente
    public static void mostrarVentanaLogros() {
        // 1. Instanciamos el JFrame tradicional para forzar la aparición en la barra de tareas
        JFrame ventana = new JFrame();
        ventana.setTitle("Perfil de Usuario");
        ventana.setSize(900, 600); // Tamaño inicial base equilibrado
        ventana.setMinimumSize(new Dimension(800, 550)); // Evita que el usuario la rompa al achicarla demasiado
        ventana.setLocationRelativeTo(null);

        // Al cerrarse esta ventana independiente, volvemos a dar foco o control (puedes cambiarlo a EXIT_ON_CLOSE si deseas cerrar todo)
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- VARIABLES PARAMETRIZADAS DEL USUARIO ---
        String nombreUsuario = "juan cacorro";
        int numeroMonedas = 12000;
        ImageIcon fotoUsuario = null;

        int victorias = 0;
        int derrotas = 0;
        int partidasJugadas = victorias + derrotas;
        String porcentajeVD;

        if (partidasJugadas > 0) {
            double tasa = ((double) victorias / partidasJugadas) * 100;
            porcentajeVD = String.format("%.1f%%", tasa);
        } else {
            porcentajeVD = "0.0%";
        }

        List<Logro> listaLogros = GestorLogros.getInstancia().getListaLogros();

        // --- CONTENEDOR PRINCIPAL FLUIDO ---
        JPanel contenedorPrincipal = new JPanel(new BorderLayout(25, 20));
        contenedorPrincipal.setBackground(COLOR_FONDO_PRINCIPAL);
        contenedorPrincipal.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // ==========================================
        // SECCIÓN SUPERIOR: ENCABEZADO (FLUIDO)
        // ==========================================
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JLabel lblMonedas = new JLabel("🪙 " + String.format("%,d", numeroMonedas));
        lblMonedas.setFont(new Font("Arial", Font.BOLD, 22));
        lblMonedas.setForeground(COLOR_TEXTO);

        JLabel lblUser = new JLabel(nombreUsuario + " 👤", SwingConstants.RIGHT);
        lblUser.setFont(new Font("Arial", Font.BOLD, 22));
        lblUser.setForeground(COLOR_TEXTO);
        if (fotoUsuario != null) lblUser.setIcon(fotoUsuario);

        panelHeader.add(lblMonedas, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        contenedorPrincipal.add(panelHeader, BorderLayout.NORTH);

        // ==========================================
        // SECCIÓN CENTRAL: CUERPO DIVIDIDO EN DOS (LOGROS | ESTADÍSTICAS)
        // ==========================================
        // Usamos un Grid con 1 fila y 2 columnas distribuidas proporcionalmente (50% y 50%)
        // Esto permite que al estirar la ventana, ambos paneles se expandan proporcionalmente.
        JPanel panelCuerpoCajas = new JPanel(new GridLayout(1, 2, 40, 0));
        panelCuerpoCajas.setOpaque(false);

        // PANEL IZQUIERDO: CONTENEDOR LOGROS
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setOpaque(false);

        JLabel lblTituloLogros = new JLabel("Logros", SwingConstants.CENTER);
        lblTituloLogros.setFont(new Font("Arial", Font.BOLD, 15));
        lblTituloLogros.setForeground(COLOR_TEXTO);
        lblTituloLogros.setBackground(COLOR_VERDE_ACENTO);
        lblTituloLogros.setOpaque(true);
        lblTituloLogros.setPreferredSize(new Dimension(380, 40));
        panelIzquierdo.add(lblTituloLogros, BorderLayout.NORTH);

        // Subcontenedor elástico para meter las filas de logros
        JPanel panelFilasContenedor = new JPanel();
        panelFilasContenedor.setLayout(new BoxLayout(panelFilasContenedor, BoxLayout.Y_AXIS));
        panelFilasContenedor.setOpaque(false);

        for (Logro logro : listaLogros) {
            // Cada logro usa BorderLayout para que la descripción cubra todo lo ancho dinámicamente
            JPanel panelFilaLogro = new JPanel(new BorderLayout(5, 5));
            panelFilaLogro.setOpaque(false);
            panelFilaLogro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65)); // Forzar expansión a lo ancho

            JLabel lblDesc = new JLabel(logro.getDescripcion(), SwingConstants.CENTER);
            lblDesc.setForeground(COLOR_TEXTO);
            lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
            lblDesc.setOpaque(true);
            lblDesc.setBackground(COLOR_FONDO_PANEL);
            lblDesc.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO_MUTED, 1, true));
            lblDesc.setPreferredSize(new Dimension(100, 28));

            JPanel panelControlesLogro = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panelControlesLogro.setOpaque(false);

            JButton btnReclamar = new JButton();
            btnReclamar.setFont(new Font("Arial", Font.BOLD, 11));
            btnReclamar.setFocusPainted(false);
            btnReclamar.setPreferredSize(new Dimension(110, 22));

            JLabel lblRegalo = new JLabel("🎁");
            lblRegalo.setFont(new Font("Arial", Font.PLAIN, 16));

            if (logro.isReclamado()) {
                btnReclamar.setText("Reclamado");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                btnReclamar.setEnabled(false);
                lblRegalo.setForeground(COLOR_TEXTO_MUTED);
            } else if (logro.isCompletado()) {
                btnReclamar.setText("Reclamar");
                btnReclamar.setBackground(COLOR_VERDE_ACENTO);
                btnReclamar.setForeground(COLOR_FONDO_PRINCIPAL);
                btnReclamar.setEnabled(true);
                lblRegalo.setForeground(COLOR_VERDE_ACENTO);
            } else {
                btnReclamar.setText("Reclamar");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                btnReclamar.setEnabled(false);
                lblRegalo.setForeground(COLOR_TEXTO_MUTED);
            }

            btnReclamar.addActionListener(e -> {
                logro.marcarComoReclamado();
                btnReclamar.setEnabled(false);
                btnReclamar.setText("Reclamado");
                btnReclamar.setBackground(COLOR_FONDO_PANEL);
                btnReclamar.setForeground(COLOR_TEXTO_MUTED);
                lblRegalo.setForeground(COLOR_TEXTO_MUTED);
                JOptionPane.showMessageDialog(ventana, "¡Recompensas reclamadas! (+" + logro.getRecompensaMonedas() + " 🪙)");
            });

            panelControlesLogro.add(btnReclamar);
            panelControlesLogro.add(lblRegalo);

            panelFilaLogro.add(lblDesc, BorderLayout.NORTH);
            panelFilaLogro.add(panelControlesLogro, BorderLayout.CENTER);

            panelFilasContenedor.add(panelFilaLogro);
            panelFilasContenedor.add(Box.createVerticalStrut(8));
        }

        // Agregamos un JScrollPane invisible por si la resolución es baja o se achica demasiado verticalmente
        JScrollPane scrollLogros = new JScrollPane(panelFilasContenedor);
        scrollLogros.setBorder(null);
        scrollLogros.setOpaque(false);
        scrollLogros.getViewport().setOpaque(false);
        panelIzquierdo.add(scrollLogros, BorderLayout.CENTER);

        // PANEL DERECHO: CONTENEDOR ESTADÍSTICAS
        JPanel panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(new BoxLayout(panelEstadisticas, BoxLayout.Y_AXIS));
        panelEstadisticas.setBackground(COLOR_FONDO_PANEL);
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));

        panelEstadisticas.add(crearFilaMetrica("Partidas jugadas:", String.valueOf(partidasJugadas)));
        panelEstadisticas.add(Box.createVerticalStrut(35));
        panelEstadisticas.add(crearFilaMetrica("Victorias:", String.valueOf(victorias)));
        panelEstadisticas.add(Box.createVerticalStrut(35));
        panelEstadisticas.add(crearFilaMetrica("Derrotas:", String.valueOf(derrotas)));
        panelEstadisticas.add(Box.createVerticalStrut(35));
        panelEstadisticas.add(crearFilaMetrica("Porcentaje V/D:", porcentajeVD));

        // Metemos ambas secciones en la cuadrícula fluida
        panelCuerpoCajas.add(panelIzquierdo);
        panelCuerpoCajas.add(panelEstadisticas);

        contenedorPrincipal.add(panelCuerpoCajas, BorderLayout.CENTER);

        // SECCIÓN INFERIOR: BOTÓN REGRESAR ESCALABLE
        JPanel panelInferiorBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        panelInferiorBoton.setOpaque(false);
        JButton btnRegresar = new JButton("Regresar al Casino");
        btnRegresar.setBackground(COLOR_FONDO_PANEL);
        btnRegresar.setForeground(COLOR_TEXTO);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegresar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnRegresar.addActionListener(e -> ventana.dispose());
        panelInferiorBoton.add(btnRegresar);

        contenedorPrincipal.add(panelInferiorBoton, BorderLayout.SOUTH);

        ventana.add(contenedorPrincipal);
        ventana.setVisible(true);
    }

    private static JPanel crearFilaMetrica(String titulo, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Escala horizontal infinita

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_VERDE_ACENTO);

        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("Arial", Font.BOLD, 15));
        lblValor.setForeground(COLOR_TEXTO);

        fila.add(lblTitulo, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.CENTER);
        return fila;
    }

    // Método Main para probar de forma directa el redimensionamiento y el icono de la barra de tareas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorLogros.getInstancia().cambiarEstadoLogro(0, true);
            GestorLogros.getInstancia().cambiarEstadoLogro(1, true);
            mostrarVentanaLogros();
        });
    }
}