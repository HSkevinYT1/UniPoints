
import java.awt.*;
import javax.swing.*;

//no tocar este archivo porque se daña el register JAJJAJ, estuve 2 horas con esto
public class RoundedTextField extends JTextField {

    private String placeholder;
    private Icon icon;

    public RoundedTextField(String placeholder) {
        this(placeholder, null);
    }

    public RoundedTextField(String placeholder, Icon icon) {
        this.placeholder = placeholder;
        this.icon = icon;
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0)); // Evita el fondo blanco por defecto en Windows Look & Feel
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setFont(new Font("SansSerif", Font.PLAIN, 18));

        int leftPadding = (icon != null) ? 50 : 20;
        setBorder(BorderFactory.createEmptyBorder(10, leftPadding, 10, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(15, 18, 25));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        if (icon != null) {
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            int x = 15 + (22 - iconWidth) / 2;
            int y = (getHeight() - iconHeight) / 2;
            icon.paintIcon(this, g2, x, y);
        }

        super.paintComponent(g);

        if (getText().isEmpty()) {
            g2.setColor(new Color(120, 120, 120));
            g2.setFont(getFont());
            Insets in = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(placeholder, in.left, getHeight() / 2 + fm.getAscent() / 2 - 4);
        }
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(60, 60, 60));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
        g2.dispose();
    }
}
