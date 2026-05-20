import javax.swing.*;
import java.awt.*;

public class SeparatorPanel extends JPanel {
    private String text;

    public SeparatorPanel(String text) {
        this.text = text;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.setColor(new Color(150, 150, 150));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = (getWidth() - textWidth) / 2;
        int textY = getHeight() / 2 + fm.getAscent() / 2 - 2;

        g2.drawString(text, textX, textY);

        g2.setColor(new Color(60, 60, 60));
        int lineY = getHeight() / 2;
        g2.drawLine(0, lineY, textX - 15, lineY);
        g2.drawLine(textX + textWidth + 15, lineY, getWidth(), lineY);

        g2.dispose();
    }
}
