import javax.swing.*;
import java.awt.*;

public class JuegoCarrera extends JFrame{
    public JuegoCarrera() {
        setTitle("Carreras - Unab Points");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Contenido de tu juego...
        add(new JLabel("¡Bienvenido al juego de Carreras!", SwingConstants.CENTER));
    }
}
