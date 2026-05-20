import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
                SwingUtilities.invokeLater(Login.this::dispose);
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
                try {
                    new Register();
                    SwingUtilities.invokeLater(Login.this::dispose);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error abriendo Register: " + ex.toString());
                }
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

        WindowPreserver.configurarVentana(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
