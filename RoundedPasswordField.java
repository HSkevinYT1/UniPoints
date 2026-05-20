
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//no tocar este archivo porque se daña el register JAJJAJ, estuve 2 horas con esto
public class RoundedPasswordField extends JPasswordField {

    private String placeholder;
    private Icon icon;
    private Icon eyeIcon;
    private boolean isPasswordVisible = false;
    private final int EYE_ICON_SIZE = 22;

    public RoundedPasswordField(String placeholder) {
        this(placeholder, null);
    }

    public RoundedPasswordField(String placeholder, Icon icon) {
        this.placeholder = placeholder;
        this.icon = icon;

        try {
            ImageIcon rawIcon = new ImageIcon("Icons/password_hide.png");
            eyeIcon = new ImageIcon(
                    rawIcon.getImage().getScaledInstance(EYE_ICON_SIZE, EYE_ICON_SIZE, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono Icons/password_hide.png");
        }

        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0)); // Evita el fondo blanco por defecto en Windows Look & Feel
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setFont(new Font("SansSerif", Font.PLAIN, 18));

        int leftPadding = (icon != null) ? 50 : 20;
        int rightPadding = 50;
        setBorder(BorderFactory.createEmptyBorder(10, leftPadding, 10, rightPadding));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isOverEye(e.getX(), e.getY())) {
                    togglePasswordVisibility();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isOverEye(e.getX(), e.getY())) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.TEXT_CURSOR));
                }
            }
        });
    }

    private boolean isOverEye(int x, int y) {
        return x >= getWidth() - 45 && x <= getWidth() - 15
                && y >= (getHeight() - EYE_ICON_SIZE) / 2
                && y <= (getHeight() + EYE_ICON_SIZE) / 2;
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        setEchoChar(isPasswordVisible ? (char) 0 : '•');
        // esto fue para que el texto no se seleccionara todo cuando quitabas lo de ver
        // la contraseña
        SwingUtilities.invokeLater(() -> {
            int len = getPassword().length;
            setSelectionStart(len);
            setSelectionEnd(len);
        });
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(15, 18, 25));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        if (icon != null) {
            int x = 15;
            int y = (getHeight() - 22) / 2;
            icon.paintIcon(this, g2, x, y);
        }

        if (eyeIcon != null) {
            int x = getWidth() - 40;
            int y = (getHeight() - eyeIcon.getIconHeight()) / 2;
            if (isPasswordVisible) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
            eyeIcon.paintIcon(this, g2, x, y);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        super.paintComponent(g);

        if (getPassword().length == 0) {
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
