
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Register extends JFrame {

    public Register() {
        // --- CONFIGURACIÓN DE LA VENTANA ---
        setTitle("Unab Points - Crear cuenta");
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

        // --- CONTENEDOR CENTRAL ---
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setPreferredSize(new Dimension(1150, 720));
        container.setBackground(new Color(5, 8, 15));
        container.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        // --- PANEL IZQUIERDO (igual que Login) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(4, 8, 12));
        leftPanel.setBorder(new EmptyBorder(40, 40, 100, 40));

        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo principal
        ImageIcon logoIcon = ImageLoader.load("Icons/AppLogo.jpeg");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(ImageLoader.load(scaledLogo), SwingConstants.CENTER);
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

        // Iconos de características
        JPanel featuresPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        featuresPanel.setOpaque(false);
        featuresPanel.add(new FeatureIcon("Segura", "Icons/shield.png"));
        featuresPanel.add(new FeatureIcon("Confiable", "Icons/trophy.png"));
        featuresPanel.add(new FeatureIcon("Rápida", "Icons/thunder.png"));
        featuresPanel.add(new FeatureIcon("Premios", "Icons/gift.png"));
        leftPanel.add(featuresPanel, BorderLayout.SOUTH);

        // --- PANEL DERECHO (Formulario de Registro) ---
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(new Color(9, 12, 16));
        rightPanel.setFocusable(true);
        rightPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                rightPanel.requestFocusInWindow();
            }
        });

        // Título
        JLabel title = new JLabel("¡Crea tu cuenta!", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        title.setBounds(75, 20, 430, 45);
        rightPanel.add(title);

        // Subtítulo
        JLabel subtitle = new JLabel("Empieza a jugar y ganar ahora", SwingConstants.CENTER);
        subtitle.setForeground(new Color(180, 180, 180));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setBounds(75, 65, 430, 25);
        rightPanel.add(subtitle);

        // Campo: Nombre completo
        ImageIcon userIcon = ImageLoader.load("Icons/user.png");
        Image scaledUser = userIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        RoundedTextField nameField = new RoundedTextField("Ingresa tu nombre completo", ImageLoader.load(scaledUser));
        nameField.setBounds(70, 105, 430, 55);
        rightPanel.add(nameField);

        // Campo: Correo
        ImageIcon mailIcon = ImageLoader.load("Icons/email.png");
        Image scaledMail = mailIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        RoundedTextField emailField = new RoundedTextField("Ingresa tu correo", ImageLoader.load(scaledMail));
        emailField.setBounds(70, 175, 430, 55);
        rightPanel.add(emailField);

        // Campo: Nombre de usuario
        Image scaledUser2 = userIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        RoundedTextField usernameField = new RoundedTextField("Crea tu nombre de usuario", ImageLoader.load(scaledUser2));
        usernameField.setBounds(70, 245, 430, 55);
        rightPanel.add(usernameField);

        // Campo: Contraseña
        ImageIcon lockIcon = ImageLoader.load("Icons/lock.png");
        Image scaledLock = lockIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        RoundedPasswordField passField = new RoundedPasswordField("Ingresa tu contraseña", ImageLoader.load(scaledLock));
        passField.setBounds(70, 315, 430, 55);
        rightPanel.add(passField);

        // Campo: Repetir contraseña
        Image scaledLock2 = lockIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        RoundedPasswordField confirmPassField = new RoundedPasswordField("Repite tu contraseña",
                ImageLoader.load(scaledLock2));
        confirmPassField.setBounds(70, 385, 430, 55);
        rightPanel.add(confirmPassField);

        // Checkbox: Términos y condiciones
        CheckboxTerms checkboxPanel = new CheckboxTerms();
        checkboxPanel.setBounds(70, 455, 430, 30);
        rightPanel.add(checkboxPanel);

        // BOTÓN: Crear cuenta
        RoundedButton registerBtn = new RoundedButton("Crear cuenta");
        registerBtn.setBounds(70, 500, 430, 55);

        // Acción: registrar usuario
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor llena todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!checkboxPanel.isChecked()) {
                JOptionPane.showMessageDialog(this, "Debes aceptar los términos y condiciones", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario nuevoUsuario = new Usuario(name, email, username, pass);
            if (Usuario.registrarUsuario(nuevoUsuario)) {
                JOptionPane.showMessageDialog(this, "Cuenta creada exitosamente. ¡Bienvenido!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                new Login();
                SwingUtilities.invokeLater(Register.this::dispose);
            } else {
                JOptionPane.showMessageDialog(this, "El correo o nombre de usuario ya está registrado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        rightPanel.add(registerBtn);

        // SECCIÓN: Social Login
        SeparatorPanel separator = new SeparatorPanel("O regístrate con");
        separator.setBounds(70, 570, 430, 30);
        rightPanel.add(separator);

        // Botón: Google
        SocialButton googleBtn = new SocialButton("Google", "Icons/google.png");
        googleBtn.setBounds(70, 615, 205, 55);
        rightPanel.add(googleBtn);

        // Botón: Facebook
        SocialButton facebookBtn = new SocialButton("Facebook", "Icons/facebook.png");
        facebookBtn.setBounds(295, 615, 205, 55);
        rightPanel.add(facebookBtn);

        // Texto: ¿Ya tienes cuenta? Inicia sesión
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        loginLinkPanel.setOpaque(false);
        loginLinkPanel.setBounds(70, 685, 430, 25);

        JLabel loginText = new JLabel("¿Ya tienes una cuenta?");
        loginText.setForeground(new Color(180, 180, 180));
        loginText.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel loginLink = new JLabel("Inicia sesión");
        loginLink.setForeground(new Color(29, 200, 35));
        loginLink.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción: abrir Login y cerrar Register
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login();
                SwingUtilities.invokeLater(Register.this::dispose);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setForeground(new Color(22, 163, 74));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setForeground(new Color(29, 200, 35));
            }
        });

        loginLinkPanel.add(loginText);
        loginLinkPanel.add(loginLink);
        rightPanel.add(loginLinkPanel);

        // Ensamblado final
        container.add(leftPanel);
        container.add(rightPanel);
        background.add(container);
        add(background);

        WindowPreserver.configurarVentana(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Register();
            } catch (Throwable e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.toString());
            }
        });
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Checkbox personalizado para términos y condiciones
// ─────────────────────────────────────────────────────────────────────────────
class CheckboxTerms extends JPanel {

    private boolean checked = false;
    private final int BOX_SIZE = 18;

    public CheckboxTerms() {
        setOpaque(false);
        setLayout(null);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Etiqueta "Acepto los"
        JLabel textLabel = new JLabel("Acepto los");
        textLabel.setForeground(new Color(180, 180, 180));
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        textLabel.setBounds(10 + BOX_SIZE + 10, 0, 90, BOX_SIZE + 4);
        add(textLabel);

        // Etiqueta "términos y condiciones" en verde
        JLabel termsLabel = new JLabel("términos y condiciones");
        termsLabel.setForeground(new Color(29, 200, 35));
        termsLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        termsLabel.setBounds(10 + BOX_SIZE + 90, 0, 200, BOX_SIZE + 4);
        add(termsLabel);

        // Click en todo el panel para marcar/desmarcar
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checked = !checked;
                repaint();
            }
        });
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = (getHeight() - BOX_SIZE) / 2;
        int x = 10; // Margen izquierdo para alinear con los campos

        // Fondo del checkbox
        g2.setColor(checked ? new Color(29, 200, 35) : new Color(15, 18, 25));
        g2.fillRoundRect(x, y, BOX_SIZE, BOX_SIZE, 6, 6);

        // Borde
        g2.setColor(checked ? new Color(29, 200, 35) : new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, BOX_SIZE - 1, BOX_SIZE - 1, 6, 6);

        // Checkmark
        if (checked) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int[] xPoints = {x + 3, x + 7, x + BOX_SIZE - 3};
            int[] yPoints = {y + BOX_SIZE / 2, y + BOX_SIZE - 4, y + 4};
            g2.drawPolyline(xPoints, yPoints, 3);
        }

        g2.dispose();
    }
}
