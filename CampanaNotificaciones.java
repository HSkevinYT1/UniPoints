
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CampanaNotificaciones {

    // Colores del tema global
    private static final Color BG_CARD = new Color(6, 12, 15);
    private static final Color BORDER_COL = new Color(25, 35, 30);
    private static final Color GREEN = new Color(44, 243, 53);
    private static final Color RED_BADGE = new Color(220, 50, 50);

    /**
     * Crea y devuelve un JPanel con la campana + badge dinámico. Al hacer clic
     * muestra el popup de notificaciones.
     *
     * @param owner La ventana padre (JFrame) que contiene la campana.
     */
    public static JPanel crear(JFrame owner) {
        GestorNotificaciones gestor = GestorNotificaciones.getInstancia();

        // Panel que pinta la campana
        JPanel bell = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Círculo de fondo
                g2.setColor(BG_CARD);
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(0, 0, 41, 41);

                // Icono campana (emoji de mientras)
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                g2.setColor(new Color(200, 205, 195));
                g2.drawString("\uD83D\uDD14", 8, 28);

                // Badge rojo dinámico
                int noLeidas = gestor.contarNoLeidas();
                if (noLeidas > 0) {
                    g2.setColor(RED_BADGE);
                    g2.fillOval(26, 2, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                    FontMetrics fm = g2.getFontMetrics();
                    String s = noLeidas > 9 ? "9+" : String.valueOf(noLeidas);
                    g2.drawString(s, 34 - fm.stringWidth(s) / 2, 14);
                }
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }
        };
        bell.setOpaque(false);
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover: ligero brillo
        bell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bell.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bell.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarPopup(bell, gestor);
            }
        });

        return bell;
    }

    // popup notificaciones
    private static void mostrarPopup(JPanel bell, GestorNotificaciones gestor) {
        List<GestorNotificaciones.Notificacion> notifs = gestor.getLista();

        // Panel contenedor del popup
        JPanel notifPanel = new JPanel();
        notifPanel.setLayout(new BoxLayout(notifPanel, BoxLayout.Y_AXIS));
        notifPanel.setBackground(new Color(6, 12, 15));
        notifPanel.setBorder(new EmptyBorder(4, 0, 4, 0));

        // Header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(10, 16, 12, 16));
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titulo = new JLabel("Notificaciones");
        titulo.setForeground(new Color(240, 240, 240));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel marcarTodo = new JLabel("Marcar como le\u00EDdo");
        marcarTodo.setForeground(GREEN);
        marcarTodo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        marcarTodo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerRow.add(titulo, BorderLayout.WEST);
        headerRow.add(marcarTodo, BorderLayout.EAST);
        notifPanel.add(headerRow);

        // Separador superior
        notifPanel.add(makeSeparator(new Color(40, 55, 35)));

        // Items
        for (int i = 0; i < notifs.size(); i++) {
            final int idx = i;
            GestorNotificaciones.Notificacion n = notifs.get(i);

            JPanel item = new JPanel(new BorderLayout(12, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            item.setOpaque(true);
            item.setBackground(n.leida ? new Color(6, 12, 15) : new Color(10, 20, 13));
            item.setBorder(new EmptyBorder(11, 16, 11, 16));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Emoji
            JLabel emoji = new JLabel(n.emoji, SwingConstants.CENTER);
            emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            emoji.setPreferredSize(new Dimension(36, 36));

            // Textos
            JPanel texts = new JPanel();
            texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
            texts.setOpaque(false);

            JLabel tl = new JLabel(n.titulo);
            tl.setForeground(n.leida ? new Color(140, 145, 135) : new Color(235, 235, 235));
            tl.setFont(new Font("SansSerif", Font.BOLD, 13));

            JLabel dl = new JLabel(n.descripcion);
            dl.setForeground(new Color(110, 115, 108));
            dl.setFont(new Font("SansSerif", Font.PLAIN, 11));

            JLabel timel = new JLabel(n.tiempo);
            timel.setForeground(new Color(44, 160, 50));
            timel.setFont(new Font("SansSerif", Font.PLAIN, 10));

            texts.add(tl);
            texts.add(Box.createVerticalStrut(2));
            texts.add(dl);
            texts.add(Box.createVerticalStrut(2));
            texts.add(timel);

            // Punto verde = no leída
            JPanel dotArea = new JPanel(new GridBagLayout());
            dotArea.setOpaque(false);
            dotArea.setPreferredSize(new Dimension(14, 36));
            if (!n.leida) {
                JPanel dot = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(GREEN);
                        g2.fillOval(0, 0, 8, 8);
                        g2.dispose();
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(8, 8);
                    }
                };
                dot.setOpaque(false);
                dotArea.add(dot);
            }

            item.add(emoji, BorderLayout.WEST);
            item.add(texts, BorderLayout.CENTER);
            item.add(dotArea, BorderLayout.EAST);

            Color bgNormal = n.leida ? new Color(6, 12, 15) : new Color(10, 20, 13);
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    item.setBackground(new Color(18, 30, 20));
                    item.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    item.setBackground(bgNormal);
                    item.repaint();
                }
            });

            notifPanel.add(item);

            if (i < notifs.size() - 1) {
                notifPanel.add(makeSeparator(new Color(22, 32, 20)));
            }
        }

        // popup
        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BorderLayout());
        popup.setBackground(new Color(6, 12, 15));
        popup.setBorder(BorderFactory.createLineBorder(new Color(40, 55, 35), 1));
        popup.add(notifPanel, BorderLayout.CENTER);

        // Acción marcar todo como leído
        marcarTodo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gestor.marcarTodasLeidas();
                bell.repaint();
                popup.setVisible(false);
            }
        });

        int popW = 340;
        int px = bell.getWidth() - popW;
        popup.show(bell, px, bell.getHeight() + 4);
    }

    // linea separadora
    private static JPanel makeSeparator(Color color) {
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(color);
                g.fillRect(0, 0, getWidth(), 1);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 1);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 1);
            }
        };
        sep.setOpaque(false);
        return sep;
    }
}
