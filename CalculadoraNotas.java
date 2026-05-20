import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraNotas extends JFrame {

    // Colores principales de la interfaz.
    private static final Color BG_DARK = new Color(1, 3, 7);
    private static final Color BG_CARD = new Color(6, 12, 15);
    private static final Color BG_INPUT = new Color(12, 18, 24);
    private static final Color GREEN = new Color(44, 243, 53);
    private static final Color GREEN_DIM = new Color(30, 180, 38);
    private static final Color TEXT_WHITE = new Color(240, 240, 240);
    private static final Color TEXT_GRAY = new Color(150, 155, 145);
    private static final Color GOLD = new Color(245, 166, 35);
    private static final Color BORDER = new Color(25, 35, 30);
    private static final Color RED = new Color(240, 80, 80);

    // Componentes y datos que se actualizan cuando el usuario edita notas.
    private final List<NotaRow> rows = new ArrayList<>();
    private final JPanel rowsPanel = new JPanel();
    private final JLabel promedioLbl = new JLabel("0.00");
    private final JLabel porcentajeLbl = new JLabel("0%");
    private final JLabel faltanteLbl = new JLabel("100%");
    private final JLabel necesariaLbl = new JLabel("-");
    private final JLabel estadoLbl = new JLabel("Agrega tus notas para empezar");
    private final JTextField objetivoField = new JTextField("3.0");

    // Configura la ventana principal de la calculadora.
    public CalculadoraNotas() {
        setTitle("Unab Points - Calculadora de notas");
        setSize(1300, 850);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        preserveMaximizedState();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);
        addDefaultRows();
        calcular();
        setVisible(true);
    }

    // Mantiene la ventana maximizada si el usuario venia trabajando asi.
    private void preserveMaximizedState() {
        for (Frame frame : Frame.getFrames()) {
            if (frame.isVisible() && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                return;
            }
        }
    }

    // Crea la barra superior con boton de volver, saldo, notificaciones y avatar.
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 22, 14, 22));

        JButton backBtn = makeIconButton("<", 32);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        backBtn.addActionListener(e -> {
            new MainMenu();
            dispose();
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(backBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(makePointsChip());
        right.add(CampanaNotificaciones.crear(this));
        right.add(makeAvatarPanel());


        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // Construye el contenido central: tabla de notas y resumen de resultados.
    private JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(28, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(34, 56, 42, 56));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Calculadora de notas");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Calcula tu promedio ponderado y la nota que necesitas para pasar.");
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setBorder(new EmptyBorder(8, 0, 22, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(subtitle);
        left.add(buildTableCard());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(18, 0, 0, 0));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addBtn = makePrimaryButton("+ Agregar nota");
        addBtn.addActionListener(e -> addRow("Nota " + (rows.size() + 1), "", ""));

        JButton clearBtn = makeSecondaryButton("Limpiar");
        clearBtn.addActionListener(e -> {
            rows.clear();
            rowsPanel.removeAll();
            addDefaultRows();
            calcular();
        });

        actions.add(addBtn);
        actions.add(clearBtn);
        left.add(actions);

        content.add(left, BorderLayout.CENTER);
        content.add(buildSummaryCard(), BorderLayout.EAST);

        return content;
    }

    // Crea la tarjeta donde se escriben las evaluaciones, notas y porcentajes.
    private JPanel buildTableCard() {
        JPanel card = new RoundedPanel(22);
        card.setLayout(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new GridLayout(1, 4, 12, 0));
        header.setOpaque(false);
        header.add(makeHeaderLabel("Evaluacion"));
        header.add(makeHeaderLabel("Nota (0.0 - 5.0)"));
        header.add(makeHeaderLabel("Porcentaje"));
        header.add(makeHeaderLabel(""));

        rowsPanel.setOpaque(false);
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(rowsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setPreferredSize(new Dimension(700, 420));

        JPanel tableBody = new JPanel(new BorderLayout());
        tableBody.setOpaque(false);
        tableBody.setBorder(new EmptyBorder(12, 0, 0, 0));
        tableBody.add(scroll, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(tableBody, BorderLayout.CENTER);
        return card;
    }

    // Crea la tarjeta lateral que muestra el promedio y la nota necesaria.
    private JPanel buildSummaryCard() {
        JPanel card = new RoundedPanel(22);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(330, 0));

        JLabel title = new JLabel("Resumen");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel objetivoPanel = new JPanel(new BorderLayout(10, 0));
        objetivoPanel.setOpaque(false);
        objetivoPanel.setBorder(new EmptyBorder(18, 0, 18, 0));
        JLabel objetivoLbl = new JLabel("Objetivo");
        objetivoLbl.setForeground(TEXT_GRAY);
        objetivoLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        objetivoField.setHorizontalAlignment(SwingConstants.CENTER);
        objetivoField.setFont(new Font("SansSerif", Font.BOLD, 16));
        styleField(objetivoField);
        objetivoField.getDocument().addDocumentListener(changeListener());
        objetivoPanel.add(objetivoLbl, BorderLayout.WEST);
        objetivoPanel.add(objetivoField, BorderLayout.CENTER);

        card.add(title);
        card.add(objetivoPanel);
        card.add(makeMetric("Promedio actual", promedioLbl, GREEN));
        card.add(makeMetric("Porcentaje registrado", porcentajeLbl, TEXT_WHITE));
        card.add(makeMetric("Porcentaje faltante", faltanteLbl, TEXT_WHITE));
        card.add(makeMetric("Nota necesaria", necesariaLbl, GOLD));
        card.add(Box.createVerticalStrut(18));

        estadoLbl.setForeground(TEXT_GRAY);
        estadoLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        estadoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(estadoLbl);

        return card;
    }

    // Crea una fila visual del resumen.
    private JPanel makeMetric(String title, JLabel value, Color valueColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 12, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JLabel label = new JLabel(title);
        label.setForeground(TEXT_GRAY);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));

        value.setForeground(valueColor);
        value.setFont(new Font("SansSerif", Font.BOLD, 28));

        panel.add(label, BorderLayout.NORTH);
        panel.add(value, BorderLayout.SOUTH);
        return panel;
    }

    // Crea los titulos de las columnas de la tabla.
    private JLabel makeHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_GRAY);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        return label;
    }

    // Agrega una estructura inicial de tres cortes comunes.
    private void addDefaultRows() {
        addRow("Primer corte", "0", "30");
        addRow("Segundo corte", "0", "30");
        addRow("Tercer corte", "", "40");
    }

    // Agrega una nueva fila editable a la lista de notas.
    private void addRow(String nombre, String nota, String porcentaje) {
        NotaRow row = new NotaRow(nombre, nota, porcentaje);
        rows.add(row);
        refreshRowsPanel();
        calcular();
    }

    // Redibuja la tabla despues de agregar o eliminar filas.
    private void refreshRowsPanel() {
        rowsPanel.removeAll();
        for (NotaRow row : rows) {
            rowsPanel.add(row.panel);
            rowsPanel.add(Box.createVerticalStrut(10));
        }
        rowsPanel.revalidate();
        rowsPanel.repaint();
    }

    // Calcula promedio, porcentaje acumulado, porcentaje pendiente y nota necesaria.
    private void calcular() {
        double acumulado = 0;
        double porcentajeUsado = 0;

        for (NotaRow row : rows) {
            Double nota = parseNumber(row.notaField.getText());
            Double porcentaje = parseNumber(row.porcentajeField.getText());
            if (nota == null || porcentaje == null || porcentaje <= 0) {
                continue;
            }
            porcentajeUsado += porcentaje;
            acumulado += nota * porcentaje / 100.0;
        }

        double faltante = Math.max(0, 100 - porcentajeUsado);
        double objetivo = parseNumberOrDefault(objetivoField.getText(), 3.0);
        Double necesaria = faltante > 0 ? (objetivo - acumulado) * 100.0 / faltante : null;

        promedioLbl.setText(String.format("%.2f", acumulado));
        porcentajeLbl.setText(String.format("%.0f%%", porcentajeUsado));
        faltanteLbl.setText(String.format("%.0f%%", faltante));

        if (necesaria == null) {
            necesariaLbl.setText("-");
            estadoLbl.setForeground(acumulado >= objetivo ? GREEN : RED);
            estadoLbl.setText(acumulado >= objetivo ? "Vas cumpliendo el objetivo." : "No alcanzas el objetivo con estas notas.");
        } else {
            necesariaLbl.setText(String.format("%.2f", necesaria));
            if (necesaria <= 0) {
                estadoLbl.setForeground(GREEN);
                estadoLbl.setText("Ya alcanzas el objetivo con lo acumulado.");
            } else if (necesaria <= 5.0) {
                estadoLbl.setForeground(GOLD);
                estadoLbl.setText("Necesitas esa nota en lo que falta.");
            } else {
                estadoLbl.setForeground(RED);
                estadoLbl.setText("Con ese objetivo, necesitas mas de 5.0.");
            }
        }

        if (porcentajeUsado > 100) {
            estadoLbl.setForeground(RED);
            estadoLbl.setText("Los porcentajes superan el 100%.");
        }
    }

    // Convierte texto a numero aceptando punto o coma decimal.
    private Double parseNumber(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // Convierte texto a numero y usa un valor por defecto si no es valido.
    private double parseNumberOrDefault(String text, double fallback) {
        Double value = parseNumber(text);
        return value != null ? value : fallback;
    }

    // Escucha cambios en los campos para recalcular automaticamente.
    private DocumentListener changeListener() {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcular(); }
            public void removeUpdate(DocumentEvent e) { calcular(); }
            public void changedUpdate(DocumentEvent e) { calcular(); }
        };
    }

    // Crea un campo de texto con el estilo de la app.
    private JTextField makeTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        styleField(field);
        field.getDocument().addDocumentListener(changeListener());
        return field;
    }

    // Aplica colores, borde y cursor a los campos de texto.
    private void styleField(JTextField field) {
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(GREEN);
        field.setBackground(BG_INPUT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(9, 10, 9, 10)
        ));
    }

    // Crea el boton principal usado para agregar notas.
    private JButton makePrimaryButton(String text) {
        JButton button = makeButton(text, GREEN, new Color(8, 18, 10), TEXT_WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    // Crea botones secundarios como limpiar o eliminar.
    private JButton makeSecondaryButton(String text) {
        return makeButton(text, BORDER, BG_CARD, TEXT_WHITE);
    }

    // Centraliza el estilo base de los botones.
    private JButton makeButton(String text, Color border, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setForeground(fg);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Crea botones pequenos del header, como el boton de volver.
    private JButton makeIconButton(String text, int size) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(size + 14, size + 4));
        btn.setForeground(TEXT_WHITE);
        btn.setBackground(BG_CARD);
        btn.setBorder(new LineBorder(BORDER, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Crea el indicador de puntos UP del usuario.
    private JPanel makePointsChip() {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 25, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(6, 12, 6, 14));

        double saldo = 500;
        Usuario actual = Usuario.getUsuarioActual();
        if (actual != null) {
            saldo = actual.getSaldo();
        }

        JLabel coinIcon = new JLabel(makeCoinIcon(22));
        JLabel points = new JLabel(String.format("%,.0f ", saldo));
        points.setFont(new Font("SansSerif", Font.BOLD, 16));
        points.setForeground(TEXT_WHITE);
        JLabel up = new JLabel("UP");
        up.setFont(new Font("SansSerif", Font.BOLD, 16));
        up.setForeground(GREEN);

        chip.add(coinIcon);
        chip.add(points);
        chip.add(up);
        return chip;
    }

    // Dibuja la campana de notificaciones con su contador.
    private JPanel makeBellWithBadge(int count) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(BORDER);
                g2.drawOval(0, 0, 41, 41);
                g2.setColor(TEXT_WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(10, 8, 22, 20, 0, 180);
                g2.drawLine(10, 18, 10, 27);
                g2.drawLine(32, 18, 32, 27);
                g2.drawLine(10, 27, 32, 27);
                g2.drawLine(18, 31, 24, 31);
                g2.setColor(GREEN);
                g2.fillOval(26, 2, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                String value = String.valueOf(count);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(value, 34 - fm.stringWidth(value) / 2, 14);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    // Dibuja el avatar y permite cerrar sesion desde el menu emergente.
    private JPanel makeAvatarPanel() {
        JPanel avatar = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    String fotoPath = (Usuario.getUsuarioActual() != null)
                            ? Usuario.getUsuarioActual().getFotoPerfil()
                            : "Icons/UserDefaultpfp.png";
                    ImageIcon icon = new ImageIcon(fotoPath);
                    Image img = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
                    Shape clip = new Ellipse2D.Float(2, 2, 38, 38);
                    g2.setClip(clip);
                    g2.drawImage(img, 2, 2, 38, 38, null);
                    g2.setClip(null);
                    g2.setColor(GREEN);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(2, 2, 37, 37);
                } catch (Exception e) {
                    g2.setColor(new Color(40, 50, 38));
                    g2.fillOval(2, 2, 38, 38);
                }
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem logoutItem = new JMenuItem("Cerrar sesion");
                logoutItem.addActionListener(ae -> {
                    Usuario.cerrarSesion();
                    new Login();
                    dispose();
                });
                popup.add(logoutItem);
                popup.show(avatar, avatar.getWidth() - popup.getPreferredSize().width, avatar.getHeight() + 4);
            }
        });
        return avatar;
    }

    // Genera el icono circular de moneda UP.
    private ImageIcon makeCoinIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(GOLD);
        g2.fillOval(0, 0, size - 1, size - 1);
        g2.setColor(new Color(0xB8860B));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(0, 0, size - 1, size - 1);
        g2.setColor(new Color(0x7B5800));
        g2.setFont(new Font("SansSerif", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("UP", (size - fm.stringWidth("UP")) / 2, (size + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
        return new ImageIcon(img);
    }

    // Representa una fila editable de evaluacion, nota, porcentaje y eliminar.
    private class NotaRow {
        private final JPanel panel;
        private final JTextField nombreField;
        private final JTextField notaField;
        private final JTextField porcentajeField;

        NotaRow(String nombre, String nota, String porcentaje) {
            panel = new JPanel(new GridLayout(1, 4, 12, 0));
            panel.setOpaque(false);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            nombreField = makeTextField(nombre);
            notaField = makeTextField(nota);
            porcentajeField = makeTextField(porcentaje);

            JButton deleteBtn = makeSecondaryButton("Eliminar");
            deleteBtn.addActionListener(e -> {
                rows.remove(this);
                refreshRowsPanel();
                calcular();
            });

            panel.add(nombreField);
            panel.add(notaField);
            panel.add(porcentajeField);
            panel.add(deleteBtn);
        }
    }

    // Panel reutilizable con bordes redondeados para tarjetas.
    private static class RoundedPanel extends JPanel {
        private final int arc;

        RoundedPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculadoraNotas::new);
    }
}
