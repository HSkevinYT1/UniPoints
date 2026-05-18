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
                background.setFocusable(true);
                background.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                                background.requestFocusInWindow();
                        }
                });

                // --- CONTENEDOR CENTRAL (Caja del Login) ---
                JPanel container = new JPanel(new GridLayout(1, 2));
                container.setPreferredSize(new Dimension(1150, 720));
                container.setBackground(new Color(5, 8, 15));
                container.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
                container.setFocusable(true);
                container.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                                container.requestFocusInWindow();
                        }
                });

                // --- PANEL IZQUIERDO (Diseño/Imagen) ---
                JPanel leftPanel = new JPanel(new BorderLayout());
                leftPanel.setBackground(new Color(4, 8, 12));
                leftPanel.setBorder(new EmptyBorder(40, 40, 100, 40));
                leftPanel.setFocusable(true);
                leftPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                                leftPanel.requestFocusInWindow();
                        }
                });

                // Contenedor central para Títulos
                JPanel centerContainer = new JPanel(new GridBagLayout());
                centerContainer.setOpaque(false);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Logo principal
                ImageIcon logoIcon = new ImageIcon("Icons/AppLogo.jpeg");
                Image scaledLogo = logoIcon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo), SwingConstants.CENTER);

                gbc.gridy = 0;
                centerContainer.add(logoLabel, gbc);

                // Subtítulo
                JLabel subTitle = new JLabel(
                                "<html><div style='text-align: center;'>Tu app de apuestas y<br>minijuegos favoritos</div></html>",
                                SwingConstants.CENTER);
                subTitle.setForeground(new Color(180, 180, 180));
                subTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
                gbc.gridy = 1;
                gbc.insets = new Insets(20, 0, 0, 0);
                centerContainer.add(subTitle, gbc);

                leftPanel.add(centerContainer, BorderLayout.CENTER);

                // Contenedor inferior para Iconos de características
                JPanel featuresPanel = new JPanel(new GridLayout(1, 4, 10, 0));
                featuresPanel.setOpaque(false);
                featuresPanel.add(new FeatureIcon("Segura", "Icons/shield.png"));
                featuresPanel.add(new FeatureIcon("Confiable", "Icons/trophy.png"));
                featuresPanel.add(new FeatureIcon("Rápida", "Icons/thunder.png"));
                featuresPanel.add(new FeatureIcon("Premios", "Icons/gift.png"));

                leftPanel.add(featuresPanel, BorderLayout.SOUTH);

                // --- PANEL DERECHO (Formulario) ---
                JPanel rightPanel = new JPanel(null);
                rightPanel.setBackground(new Color(9, 12, 16));
                rightPanel.setBorder(new EmptyBorder(120, 50, 60, 50));
                rightPanel.setFocusable(true);
                rightPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                                rightPanel.requestFocusInWindow();
                        }
                });

                // Título: de bienvenido
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
                Image scaledUser = userIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                RoundedTextField userField = new RoundedTextField("Ingresa tu correo o usuario",
                                new ImageIcon(scaledUser));
                userField.setBounds(70, 170, 430, 55);
                rightPanel.add(userField);

                // Etiqueta: Contraseña
                JLabel passLabel = new JLabel("Contraseña");
                passLabel.setForeground(new Color(180, 180, 180));
                passLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                passLabel.setBounds(70, 240, 300, 30);
                rightPanel.add(passLabel);

                // Campo: Contraseña (con icono)
                ImageIcon lockIcon = new ImageIcon("Icons/lock.png");
                Image scaledLock = lockIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                RoundedPasswordField passField = new RoundedPasswordField("Ingresa tu contraseña",
                                new ImageIcon(scaledLock));
                passField.setBounds(70, 280, 430, 55);
                rightPanel.add(passField);

                // Texto: Olvidaste contraseña
                JLabel forgotPassword = new JLabel("¿Olvidaste tu contraseña?");
                forgotPassword.setForeground(new Color(29, 200, 35));
                forgotPassword.setFont(new Font("SansSerif", Font.BOLD, 15));
                forgotPassword.setBounds(300, 340, 250, 30);
                rightPanel.add(forgotPassword);

                // BOTÓN: Iniciar sesión
                RoundedButton loginBtn = new RoundedButton("Iniciar sesión");
                loginBtn.setBounds(80, 400, 430, 55);
                
                // Acción: iniciar sesión
                loginBtn.addActionListener(e -> {
                        String identifier = userField.getText().trim();
                        String pass = new String(passField.getPassword());

                        if (identifier.isEmpty() || pass.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Por favor llena todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                        }

                        if (Usuario.iniciarSesion(identifier, pass)) {
                                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                // Abre el menú principal
                                new MainMenu();
                                dispose();
                        } else {
                                JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                });
                
                rightPanel.add(loginBtn);

                // SECCIÓN: Social Login
                SeparatorPanel separator = new SeparatorPanel("O continúa con");
                separator.setBounds(70, 485, 450, 30);
                rightPanel.add(separator);

                // Botón: Google
                SocialButton googleBtn = new SocialButton("Google", "Icons/google.png");
                googleBtn.setBounds(70, 540, 205, 55);
                rightPanel.add(googleBtn);

                // Botón: Facebook
                SocialButton facebookBtn = new SocialButton("Facebook", "Icons/facebook.png");
                facebookBtn.setBounds(295, 540, 205, 55);
                rightPanel.add(facebookBtn);

                // Texto: ¿No tienes cuenta? Regístrate
                JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
                registerLinkPanel.setOpaque(false);
                registerLinkPanel.setBounds(70, 620, 430, 25);

                JLabel registerText = new JLabel("¿No tienes una cuenta?");
                registerText.setForeground(new Color(180, 180, 180));
                registerText.setFont(new Font("SansSerif", Font.PLAIN, 15));

                JLabel registerLink = new JLabel("Regístrate");
                registerLink.setForeground(new Color(29, 200, 35));
                registerLink.setFont(new Font("SansSerif", Font.BOLD, 15));
                registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Acción: abrir Register y cerrar Login
                registerLink.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                new Register();
                                dispose();
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                                registerLink.setForeground(new Color(22, 163, 74));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                                registerLink.setForeground(new Color(29, 200, 35));
                        }
                });

                registerLinkPanel.add(registerText);
                registerLinkPanel.add(registerLink);
                rightPanel.add(registerLinkPanel);

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
// Icono de característica (Segura, Confiable, etc.)
// ─────────────────────────────────────────────────────────────────────────────
class FeatureIcon extends JPanel {
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

// ─────────────────────────────────────────────────────────────────────────────
// Separador con texto "O continúa con"
// ─────────────────────────────────────────────────────────────────────────────
class SeparatorPanel extends JPanel {
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

// ─────────────────────────────────────────────────────────────────────────────
// Botón social (Google, Facebook)
// ─────────────────────────────────────────────────────────────────────────────
class SocialButton extends JButton {
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
                        ImageIcon rawIcon = new ImageIcon(iconPath);
                        Image scaled = rawIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                        this.icon = new ImageIcon(scaled);
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
                                        rawIcon.getImage().getScaledInstance(EYE_ICON_SIZE, EYE_ICON_SIZE, Image.SCALE_SMOOTH));
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
                return x >= getWidth() - 45 && x <= getWidth() - 15 &&
                                y >= (getHeight() - EYE_ICON_SIZE) / 2
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