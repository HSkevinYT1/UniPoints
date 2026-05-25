
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//no tocar este archivo porque se daña el register JAJJAJ, estuve 2 horas con esto
public class SocialButton extends JButton {

    private Icon icon;
    private static final int ARC = 15;
    private Color hoverColor = new Color(25, 28, 35);
    private Color normalColor = new Color(15, 18, 25);
    private Color currentColor = normalColor;

    public SocialButton(String text, String iconPath) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon rawIcon = ImageLoader.load(iconPath);
            Image scaled = rawIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            this.icon = ImageLoader.load(scaled);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + iconPath);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC * 2, ARC * 2);

        // Borde
        g2.setColor(new Color(60, 60, 60));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC * 2, ARC * 2);

        // Contenido (Icono + Texto)
        FontMetrics fm = g2.getFontMetrics();
        int iconWidth = (icon != null) ? icon.getIconWidth() : 0;
        int spacing = 12;
        int totalWidth = iconWidth + spacing + fm.stringWidth(getText());
        int startX = (getWidth() - totalWidth) / 2;

        if (icon != null) {
            icon.paintIcon(this, g2, startX, (getHeight() - icon.getIconHeight()) / 2);
        }

        g2.setColor(getForeground());
        g2.drawString(getText(), startX + iconWidth + spacing,
                (getHeight() - fm.getHeight()) / 2 + fm.getAscent());

        g2.dispose();
    }
}
