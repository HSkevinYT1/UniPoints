
import java.awt.Frame;
import javax.swing.JFrame;

//este es para mantener todas las pestañas en pantalla completa
public class WindowPreserver {

    public static void configurarVentana(JFrame nuevaVentana) {
        for (Frame frame : Frame.getFrames()) {
            if (frame != nuevaVentana && frame.isVisible() && frame instanceof JFrame) {
                if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    nuevaVentana.setExtendedState(Frame.MAXIMIZED_BOTH);
                } else {
                    // Mantener el tamaño si la anterior es más grande, 
                    // pero no encoger la nueva por debajo de sus dimensiones de diseño
                    java.awt.Rectangle bounds = frame.getBounds();
                    int w = Math.max(nuevaVentana.getWidth(), bounds.width);
                    int h = Math.max(nuevaVentana.getHeight(), bounds.height);
                    nuevaVentana.setBounds(bounds.x, bounds.y, w, h);
                }
                return;
            }
        }
    }
}

