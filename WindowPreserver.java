import java.awt.Frame;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.io.File;

public class WindowPreserver {
    // Estado global compartido por todas las ventanas de la aplicación
    private static boolean maximized = false;
    private static Rectangle bounds = null;

    /**
     * Guarda el estado actual de una ventana (posición, tamaño y si está maximizada).
     */
    public static void registrarEstado(JFrame frame) {
        if (frame == null) return;
        
        // Verificamos si la ventana está maximizada
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            maximized = true;
        } else {
            maximized = false;
            // Solo guardamos los bounds si la ventana es visible y no está minimizada o maximizada
            if (frame.isVisible() && frame.getWidth() > 0 && frame.getHeight() > 0) {
                bounds = frame.getBounds();
            }
        }
    }

    /**
     * Configura la nueva ventana basándose en el estado de la ventana anterior.
     */
    public static void configurarVentana(JFrame nuevaVentana) {
        if (nuevaVentana == null) return;

        // Establecer el icono de la aplicación
        try {
            ImageIcon appIcon = ImageLoader.load("Icons/IconPlaceHolder.png");
            if (appIcon != null && appIcon.getImage() != null) {
                nuevaVentana.setIconImage(appIcon.getImage());
            }
        } catch (Exception e) {
            // ignorar fallas al cargar el icono
        }

        // Aplicamos el estado guardado
        if (maximized && nuevaVentana.isResizable()) {
            nuevaVentana.setExtendedState(Frame.MAXIMIZED_BOTH);
        } else if (bounds != null) {
            if (nuevaVentana.isResizable()) {
                // Conservamos la posición y dimensiones, respetando el tamaño mínimo de la nueva ventana
                int w = Math.max(nuevaVentana.getWidth(), bounds.width);
                int h = Math.max(nuevaVentana.getHeight(), bounds.height);
                nuevaVentana.setBounds(bounds.x, bounds.y, w, h);
            } else {
                // Si la ventana no es redimensionable (como Login y Register), preservamos su posición 
                // pero la centramos con respecto a las dimensiones de la ventana anterior para evitar que se deforme.
                int x = bounds.x + (bounds.width - nuevaVentana.getWidth()) / 2;
                int y = bounds.y + (bounds.height - nuevaVentana.getHeight()) / 2;
                nuevaVentana.setLocation(x, y);
            }
        } else {
            // Si es la primera ventana, la centramos en la pantalla
            nuevaVentana.setLocationRelativeTo(null);
        }

        // Agregamos escuchadores para actualizar el estado global en tiempo real
        nuevaVentana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                registrarEstado(nuevaVentana);
            }
            @Override
            public void windowDeactivated(java.awt.event.WindowEvent e) {
                registrarEstado(nuevaVentana);
            }
        });

        nuevaVentana.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                registrarEstado(nuevaVentana);
            }
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                registrarEstado(nuevaVentana);
            }
        });
    }
}
