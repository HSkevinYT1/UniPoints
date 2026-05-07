import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class Login extends JFrame {

        public Login() {
                // --- CONFIGURACIÓN DE LA VENTANA ---
                setTitle("Unab Points");
                setSize(1300, 850);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setResizable(false);

                // --- PANEL PRINCIPAL (Fondo oscuro total) ---
                JPanel background = new JPanel(new GridBagLayout());
                background.setBackground(new Color(10, 10, 10));

                // --- CONTENEDOR CENTRAL (Caja del Login) ---
                JPanel container = new JPanel(new GridLayout(1, 2));
                container.setPreferredSize(new Dimension(1150, 720));
                container.setBackground(new Color(5, 8, 15));
                container.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

                // --- PANEL IZQUIERDO (Diseño/Imagen) ---
                JPanel leftPanel = new JPanel();
                leftPanel.setBackground(new Color(4, 8, 12));
                leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

                // --- PANEL DERECHO (Formulario) ---
                JPanel rightPanel = new JPanel(null);
                rightPanel.setBackground(new Color(9, 12, 16));
                rightPanel.setBorder(new EmptyBorder(120, 50, 60, 50));

                // Título: ¡Bienvenido!
                JLabel title = new JLabel("¡Bienvenido!", SwingConstants.CENTER);
                title.setForeground(Color.WHITE);
                title.setFont(new Font("SansSerif", Font.BOLD, 38));
                title.setBounds(80, 50, 400, 50);
                rightPanel.add(title);

                // Etiqueta: Usuario
                JLabel userLabel = new JLabel("Correo electrónico o usuario");
                userLabel.setForeground(new Color(180, 180, 180));
                userLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                userLabel.setBounds(70, 130, 300, 30);
                rightPanel.add(userLabel);

                // Campo: Usuario (con icono)
                ImageIcon userIcon = new ImageIcon("Icons/user.png");
                Image scaledUser = userIcon.getImage().getScaledInstance(22, -1, Image.SCALE_SMOOTH);
                RoundedTextField userField = new RoundedTextField("Ingresa tu correo o usuario",
                                new ImageIcon(scaledUser));
                userField.setBounds(70, 170, 430, 55);
                rightPanel.add(userField);

                // Etiqueta: Contraseña
                JLabel passLabel = new JLabel("Contraseña");
                passLabel.setForeground(new Color(180, 180, 180));
                passLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                passLabel.setBounds(70, 260, 300, 30);
                rightPanel.add(passLabel);

                // Campo: Contraseña (con icono)
                ImageIcon lockIcon = new ImageIcon("Icons/lock.png");
                Image scaledLock = lockIcon.getImage().getScaledInstance(22, -1, Image.SCALE_SMOOTH);
                RoundedPasswordField passField = new RoundedPasswordField("Ingresa tu contraseña",
                                new ImageIcon(scaledLock));
                passField.setBounds(70, 300, 430, 55);
                rightPanel.add(passField);

                // Texto: Olvidaste contraseña
                JLabel forgotPassword = new JLabel("¿Olvidaste tu contraseña?");
                forgotPassword.setForeground(new Color(0, 255, 100));
                forgotPassword.setFont(new Font("SansSerif", Font.BOLD, 15));
                forgotPassword.setBounds(250, 365, 250, 30);
                rightPanel.add(forgotPassword);

                // --- BOTÓN: Iniciar sesión ---
                RoundedButton loginBtn = new RoundedButton("Iniciar sesión");
                loginBtn.setBounds(80, 415, 430, 55);
                rightPanel.add(loginBtn);

                // Ensamblado final
                container.add(leftPanel);
                container.add(rightPanel);
                background.add(container);
                add(background);

                setVisible(true);
        }

        public static void main(String[] args) {
                SwingUtilities.invokeLater(Login::new);
        }
}

// Botón redondeado verde de inicio de sesion

class RoundedButton extends JButton {

        private static final Color COLOR_NORMAL = new Color(34, 197, 94); // verde principal
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

// ─────────────────────────────────────────────────────────────────────────────
// TextField personalizado con bordes redondeados, icono y placeholder
// ─────────────────────────────────────────────────────────────────────────────
class RoundedTextField extends JTextField {
        private String placeholder;
        private Icon icon;

        public RoundedTextField(String placeholder) {
                this(placeholder, null);
        }

        public RoundedTextField(String placeholder, Icon icon) {
                this.placeholder = placeholder;
                this.icon = icon;
                setOpaque(false);
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

// ─────────────────────────────────────────────────────────────────────────────
// PasswordField personalizado con bordes redondeados, icono y toggle
// visibilidad
// ─────────────────────────────────────────────────────────────────────────────
class RoundedPasswordField extends JPasswordField {
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
                                        rawIcon.getImage().getScaledInstance(EYE_ICON_SIZE, -1, Image.SCALE_SMOOTH));
                } catch (Exception e) {
                        System.err.println("No se pudo cargar el icono Icons/password_hide.png");
                }

                setOpaque(false);
                setForeground(Color.WHITE);
                setCaretColor(Color.WHITE);
                setFont(new Font("SansSerif", Font.PLAIN, 18));

                int leftPadding = (icon != null) ? 50 : 20;
                int rightPadding = 50;
                setBorder(BorderFactory.createEmptyBorder(10, leftPadding, 10, rightPadding));

                addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                                int x = e.getX();
                                int y = e.getY();
                                if (x >= getWidth() - 45 && x <= getWidth() - 15 &&
                                                y >= (getHeight() - EYE_ICON_SIZE) / 2
                                                && y <= (getHeight() + EYE_ICON_SIZE) / 2) {
                                        togglePasswordVisibility();
                                }
                        }
                });
                setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }

        private void togglePasswordVisibility() {
                isPasswordVisible = !isPasswordVisible;
                setEchoChar(isPasswordVisible ? (char) 0 : '•');
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