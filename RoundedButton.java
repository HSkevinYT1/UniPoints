
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

//no tocar este archivo porque se daña el register JAJJAJ, estuve 2 horas con esto
public class RoundedButton extends JButton {

    private static final Color COLOR_NORMAL = new Color(29, 200, 35); // verde principal
    private static final Color COLOR_HOVER = new Color(22, 163, 74); // verde más oscuro al pasar el mouse
    private static final Color COLOR_PRESS = new Color(15, 130, 55); // verde más oscuro al hacer clic
    private static final int ARC = 14; // radio de esquinas

    private Color currentColor = COLOR_NORMAL;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 20));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover y presión
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = COLOR_HOVER;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = COLOR_NORMAL;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = COLOR_PRESS;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = COLOR_HOVER;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo redondeado del botón
        g2.setColor(currentColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC * 2, ARC * 2));

        // Texto centrado
        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
