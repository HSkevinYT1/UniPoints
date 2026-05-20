
import java.awt.*;
import javax.swing.*;

////no tocar este archivo porque se daña el register JAJJAJ, estuve 2 horas con esto

public class FeatureIcon extends JPanel {

    public FeatureIcon(String text, String iconPath) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        try {
            ImageIcon rawIcon = new ImageIcon(iconPath);
            // El escudo suele verse más grande, así que le damos un tamaño menor que a los otros
            int size = iconPath.contains("shield") ? 40 : 48;
            Image scaled = rawIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaled));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(iconLabel);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + iconPath);
        }

        add(Box.createVerticalStrut(10));

        JLabel label = new JLabel(text);
        label.setForeground(new Color(180, 180, 180));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);
    }
}
