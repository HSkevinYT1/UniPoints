import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Chat extends JFrame {
    // COLORES
    static final Color BG_DARK = new Color(0x0D, 0x0D, 0x0D);
    static final Color BG_PANEL = new Color(0x12, 0x12, 0x12);
    static final Color BG_CARD = new Color(0x1A, 0x1A, 0x1A);
    static final Color BG_SELECTED = new Color(0x1E, 0x2A, 0x1E);
    static final Color BG_MSG_IN = new Color(0x1E, 0x1E, 0x1E);
    static final Color BG_MSG_OUT = new Color(0x1A, 0x2B, 0x1A);
    static final Color BG_INPUT = new Color(0x1C, 0x1C, 0x1C);
    static final Color BG_DIALOG = new Color(0x16, 0x16, 0x16);
    static final Color GREEN_MAIN = new Color(0x00, 0xE5, 0x00);
    static final Color GREEN_DARK = new Color(0x00, 0xAA, 0x00);
    static final Color RED_DANGER = new Color(0xFF, 0x44, 0x44);
    static final Color TEXT_PRIMARY = new Color(0xF0, 0xF0, 0xF0);
    static final Color TEXT_SECONDARY = new Color(0x88, 0x88, 0x88);
    static final Color TEXT_ONLINE = new Color(0x00, 0xE5, 0x00);
    static final Color DIVIDER = new Color(0x22, 0x22, 0x22);
    static final Color BORDER_CARD = new Color(0x2A, 0x2A, 0x2A);
    static final Color BORDER_FOCUS = new Color(0x00, 0xAA, 0x00, 120);
    // FUENTES
    static final Font FONT_NAME = new Font("Segoe UI", Font.BOLD, 14);
    static final Font FONT_PREVIEW = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font FONT_TIME = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font FONT_MSG = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    static final Font FONT_SECTION = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    // ESTADO
    private List<Contact> contacts = new ArrayList<>();
    private Contact selectedContact = null;
    private Map<String, List<Message>> conversations = new HashMap<>();

    private JPanel chatMessagesPanel;
    private JScrollPane chatScrollPane;
    private JTextField inputField;
    private JLabel headerNameLabel;
    private JLabel headerStatusLabel;
    private JPanel contactListPanel;
    private JTextField searchField;
    private boolean[] searchFocused = { false };
    private JPanel inputBarPanel;
    private JPanel chatHeaderPanel;

    private static final Image ICON_ADD = new ImageIcon("Icons/AddUser.png").getImage();
    private static final Image ICON_INFO = new ImageIcon("Icons/Info.png").getImage();
    private static final Image ICON_SEND = new ImageIcon("Icons/SendMessage.png").getImage();
    private static final Image ICON_USER = new ImageIcon("Icons/UserDefaultpfp.png").getImage();
    // CONSTRUCTOR
    public Chat() {
        initData();
        buildUI();
        setTitle("Mensajes");
        setSize(1100, 700);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // dejar de seleccionar contacto al dar click fuera
        Toolkit.getDefaultToolkit().addAWTEventListener(evt -> {
            if (evt instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) evt;
                if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                    Component src = SwingUtilities.getDeepestComponentAt(
                            me.getComponent(), me.getX(), me.getY());
                    if (src != searchField && searchFocused[0]) {
                        getRootPane().requestFocusInWindow();
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);

        setVisible(true);
    }
    // DATOS
    private void initData() {
        contacts.add(new Contact("Kevin Bayona", "Ey!", true, 1, "11:42"));
        contacts.add(new Contact("Juan Nuñez", "Buena partida!", false, 0, "10:30"));
        contacts.add(new Contact("Julian Malagon", "Buena partida!", false, 0, "09:15"));
        contacts.add(new Contact("Alejandro Prada", "Buena partida!", false, 0, "Ayer"));
        contacts.add(new Contact("Gustavo Rueda", "Buena partida!", false, 0, "Lunes"));

        List<Message> m1 = new ArrayList<>();
        m1.add(new Message("Ey!", "text", true, "11:42"));
        conversations.put("Kevin Bayona", m1);

        List<Message> m2 = new ArrayList<>();
        m2.add(new Message("Buena partida!", "text", true, "10:30"));
        conversations.put("Juan Nu\u00f1ez", m2);

        List<Message> m3 = new ArrayList<>();
        m3.add(new Message("Buena partida!", "text", true, "09:15"));
        conversations.put("Julian Malagon", m3);

        List<Message> m4 = new ArrayList<>();
        m4.add(new Message("Buena partida!", "text", true, "Ayer"));
        conversations.put("Alejandro Pradilla", m4);

        List<Message> m5 = new ArrayList<>();
        m5.add(new Message("Buena partida!", "text", true, "Lunes"));
        conversations.put("Gustavo Rueda", m5);
    }
    // RAIZ UI
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }
    // PANEL IZQUIERDO
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(new MatteBorder(0, 0, 0, 1, DIVIDER));

        // Cabecera
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(18, 20, 14, 16));

        JLabel title = new JLabel("Conversaciones");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);

        // Botón + solo para añadir contacto
        JPanel addBtn = makeIconBtn(this::showAddContactDialog, g2 -> {
            g2.drawImage(ICON_ADD, 4, 4, 26, 26, null);
        });

        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);

        // Buscador
        JPanel searchArea = buildSearchBar();

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_PANEL);
        top.add(header, BorderLayout.NORTH);
        top.add(searchArea, BorderLayout.SOUTH);

        // Lista contactos
        contactListPanel = new JPanel();
        contactListPanel.setLayout(new BoxLayout(contactListPanel, BoxLayout.Y_AXIS));
        contactListPanel.setBackground(BG_PANEL);
        for (Contact c : contacts)
            contactListPanel.add(buildContactRow(c));
        contactListPanel.add(Box.createVerticalGlue());

        JScrollPane listScroll = styledScroll(contactListPanel);

        panel.add(top, BorderLayout.NORTH);
        panel.add(listScroll, BorderLayout.CENTER);
        return panel;
    }
    private JPanel buildSearchBar() {
        boolean[] foc = searchFocused;

        JPanel wrap = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                if (foc[0]) {
                    g2.setColor(BORDER_FOCUS);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 22, 22);
                }
            }
        };
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(0, 38));

        JPanel icon = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(36, 38);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(TEXT_SECONDARY);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(12, 11, 13, 13);
                g2.drawLine(21, 20, 27, 26);
            }
        };
        icon.setOpaque(false);

        searchField = new JTextField();
        searchField.setOpaque(false);
        searchField.setBorder(null);
        searchField.setFont(FONT_INPUT);
        searchField.setText("Buscar usuarios...");
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setCaretColor(GREEN_MAIN);

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                foc[0] = true;
                wrap.repaint();
                if (searchField.getText().equals("Buscar usuarios...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }

            public void focusLost(FocusEvent e) {
                foc[0] = false;
                wrap.repaint();
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Buscar usuarios...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
                filterContacts(""); // restaurar lista al perder foco
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                doFilter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                doFilter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }

            private void doFilter() {
                String q = searchField.getText();
                if (!q.equals("Buscar usuarios..."))
                    filterContacts(q);
            }
        });

        wrap.add(icon, BorderLayout.WEST);
        wrap.add(searchField, BorderLayout.CENTER);

        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(new EmptyBorder(0, 14, 10, 14));
        pad.add(wrap);
        return pad;
    }

    private void filterContacts(String query) {
        contactListPanel.removeAll();
        String q = query.toLowerCase().trim();
        for (Contact c : contacts) {
            if (q.isEmpty() || c.name.toLowerCase().contains(q))
                contactListPanel.add(buildContactRow(c));
        }
        contactListPanel.add(Box.createVerticalGlue());
        contactListPanel.revalidate();
        contactListPanel.repaint();
    }
    private JPanel buildContactRow(Contact contact) {
        boolean[] hov = { false };
        JPanel row = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (contact == selectedContact) {
                    g2.setColor(BG_SELECTED);
                    g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 14, 14);
                    g2.setColor(GREEN_MAIN);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(4, 2, getWidth() - 9, getHeight() - 5, 14, 14);
                } else if (hov[0]) {
                    g2.setColor(BG_CARD);
                    g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 14, 14);
                }
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 12, 8, 12));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        row.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hov[0] = true;
                row.repaint();
            }

            public void mouseExited(MouseEvent e) {
                hov[0] = false;
                row.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                getRootPane().requestFocusInWindow(); // quitar foco de buscador
                selectContact(contact);
            }
        });

        row.add(buildAvatar(contact), BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel nameLbl = new JLabel(contact.name);
        nameLbl.setFont(FONT_NAME);
        nameLbl.setForeground(TEXT_PRIMARY);
        JLabel prevLbl = new JLabel(contact.lastMessage);
        prevLbl.setFont(FONT_PREVIEW);
        prevLbl.setForeground(TEXT_SECONDARY);
        info.add(nameLbl);
        info.add(prevLbl);
        row.add(info, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        JLabel timeLbl = new JLabel(contact.time != null ? contact.time : "");
        timeLbl.setFont(FONT_TIME);
        timeLbl.setForeground(contact.unread > 0 ? GREEN_MAIN : TEXT_SECONDARY);
        timeLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(timeLbl);
        right.add(Box.createVerticalStrut(4));
        if (contact.unread > 0)
            right.add(buildBadge(contact.unread));
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private JPanel buildAvatar(Contact contact) {
        return new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(46, 46);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.drawImage(ICON_USER, 4, 5, 34, 34, null);
                if (contact.online) {
                    g2.setColor(BG_PANEL);
                    g2.fillOval(27, 30, 12, 12);
                    g2.setColor(GREEN_MAIN);
                    g2.fillOval(28, 31, 10, 10);
                }
            }
        };
    }

    private JLabel buildBadge(int count) {
        JLabel b = new JLabel(String.valueOf(count), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                aa(g).setColor(GREEN_MAIN);
                ((Graphics2D) g).fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(BG_DARK);
        Dimension d = new Dimension(22, 22);
        b.setPreferredSize(d);
        b.setMinimumSize(d);
        b.setMaximumSize(d);
        b.setAlignmentX(Component.RIGHT_ALIGNMENT);
        b.setOpaque(false);
        return b;
    }
    // PANEL DERECHO
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.add(buildChatHeader(), BorderLayout.NORTH);
        panel.add(buildMessagesArea(), BorderLayout.CENTER);
        panel.add(buildInputBar(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildChatHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG_PANEL);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(DIVIDER);
                g.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel left = new JPanel(new BorderLayout(14, 0));
        left.setOpaque(false);

        JPanel bigAvatar = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(44, 44);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.drawImage(ICON_USER, 4, 4, 36, 36, null);
            }
        };
        bigAvatar.setOpaque(false);

        JPanel nameStatus = new JPanel(new GridLayout(2, 1, 0, 1));
        nameStatus.setOpaque(false);
        headerNameLabel = new JLabel("Selecciona un chat");
        headerNameLabel.setFont(FONT_TITLE);
        headerNameLabel.setForeground(TEXT_PRIMARY);
        headerStatusLabel = new JLabel("\u2014");
        headerStatusLabel.setFont(FONT_PREVIEW);
        headerStatusLabel.setForeground(TEXT_SECONDARY);
        nameStatus.add(headerNameLabel);
        nameStatus.add(headerStatusLabel);

        left.add(bigAvatar, BorderLayout.WEST);
        left.add(nameStatus, BorderLayout.CENTER);

        // Botón info (i)
        boolean[] ihov = { false };
        JPanel infoBtn = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(34, 34);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (ihov[0]) {
                    g2.setColor(new Color(0x28, 0x28, 0x28));
                    g2.fillOval(1, 1, 32, 32);
                }
                g2.drawImage(ICON_INFO, 4, 4, 26, 26, null);
            }
        };
        infoBtn.setOpaque(false);
        infoBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        infoBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ihov[0] = true;
                infoBtn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                ihov[0] = false;
                infoBtn.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                showInfoMenu(infoBtn);
            }
        });

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(infoBtn);

        header.add(left, BorderLayout.WEST);
        header.add(rightWrap, BorderLayout.EAST);
        chatHeaderPanel = header;
        chatHeaderPanel.setVisible(false);
        return header;
    }

    private JScrollPane buildMessagesArea() {
        chatMessagesPanel = new JPanel();
        chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        chatMessagesPanel.setBackground(BG_DARK);
        chatMessagesPanel.setBorder(new EmptyBorder(16, 24, 16, 24));
        chatScrollPane = styledScroll(chatMessagesPanel);
        return chatScrollPane;
    }
    // BARRA DE ENTRADA
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(DIVIDER);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Campo de texto con borde vivo
        boolean[] inputFoc = { false };
        JPanel inputContainer = new JPanel(new BorderLayout(6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(inputFoc[0] ? BORDER_FOCUS : BORDER_CARD);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
            }
        };
        inputContainer.setOpaque(false);
        inputContainer.setPreferredSize(new Dimension(0, 50));
        inputContainer.setBorder(new EmptyBorder(0, 16, 0, 4));

        inputField = new JTextField();
        inputField.setOpaque(false);
        inputField.setBorder(null);
        inputField.setForeground(TEXT_SECONDARY);
        inputField.setCaretColor(GREEN_MAIN);
        inputField.setFont(FONT_INPUT);
        inputField.setText("Escribe un mensaje...");

        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                inputFoc[0] = true;
                inputContainer.repaint();
                if (inputField.getText().equals("Escribe un mensaje...")) {
                    inputField.setText("");
                    inputField.setForeground(TEXT_PRIMARY);
                }
            }

            public void focusLost(FocusEvent e) {
                inputFoc[0] = false;
                inputContainer.repaint();
                if (inputField.getText().isEmpty()) {
                    inputField.setText("Escribe un mensaje...");
                    inputField.setForeground(TEXT_SECONDARY);
                }
            }
        });
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendMessage();
            }
        });

        inputContainer.add(inputField, BorderLayout.CENTER);

        // Botón enviar
        boolean[] shov = { false };
        JPanel sendBtn = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(46, 46);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (shov[0]) {
                    g2.setColor(new Color(0xFF, 0xFF, 0xFF, 14));
                    g2.fillOval(3, 3, 40, 40);
                }
                g2.drawImage(ICON_SEND, 8, 8, 30, 30, null);
            }
        };
        sendBtn.setOpaque(false);
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                shov[0] = true;
                sendBtn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                shov[0] = false;
                sendBtn.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                sendMessage();
            }
        });

        bar.add(inputContainer, BorderLayout.CENTER);
        bar.add(sendBtn, BorderLayout.EAST);
        inputBarPanel = bar;
        inputBarPanel.setVisible(false);
        return bar;
    }
    // EMOJI PICKER
    private JPopupMenu emojiPopup = null;

    private void showEmojiPicker() {
        if (selectedContact == null)
            return;
        if (emojiPopup != null && emojiPopup.isVisible()) {
            emojiPopup.setVisible(false);
            return;
        }

        String[][] categories = {
                { "\uD83D\uDE00", "\uD83D\uDE02", "\uD83D\uDE0D", "\uD83D\uDE0E", "\uD83E\uDD73", "\uD83D\uDE22",
                        "\uD83D\uDE21", "\uD83E\uDD14", "\uD83D\uDE34", "\uD83E\uDD29" },
                { "\uD83D\uDC4D", "\uD83D\uDC4E", "\uD83D\uDC4F", "\uD83D\uDE4C", "\uD83E\uDD1D", "\uD83D\uDCAA",
                        "\uD83C\uDF89", "\u2764\uFE0F", "\uD83D\uDD25", "\u2B50" },
                { "\uD83D\uDE08", "\uD83E\uDD23", "\uD83D\uDE05", "\uD83D\uDE07", "\uD83E\uDD7A", "\uD83E\uDD2F",
                        "\uD83D\uDE24", "\uD83E\uDD17", "\uD83D\uDE0F", "\uD83D\uDE43" },
                { "\uD83C\uDF55", "\uD83C\uDFAE", "\u26BD", "\uD83C\uDFC6", "\uD83C\uDFAF", "\uD83D\uDC80",
                        "\uD83D\uDC7B", "\uD83E\uDD16", "\uD83D\uDC36", "\uD83D\uDC31" }
        };

        JPanel picker = new JPanel(new BorderLayout(0, 6));
        picker.setBackground(BG_CARD);
        picker.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CARD, 1),
                new EmptyBorder(10, 10, 10, 10)));

        JPanel grid = new JPanel(new GridLayout(4, 10, 3, 3));
        grid.setBackground(BG_CARD);

        for (String[] row : categories) {
            for (String em : row) {
                JLabel lbl = new JLabel(em, SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                lbl.setPreferredSize(new Dimension(36, 36));
                lbl.setOpaque(true);
                lbl.setBackground(BG_CARD);
                lbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                lbl.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        lbl.setBackground(BG_SELECTED);
                    }

                    public void mouseExited(MouseEvent e) {
                        lbl.setBackground(BG_CARD);
                    }

                    public void mouseClicked(MouseEvent e) {
                        String cur = inputField.getText();
                        if (cur.equals("Escribe un mensaje..."))
                            cur = "";
                        inputField.setText(cur + em);
                        inputField.setForeground(TEXT_PRIMARY);
                        inputField.requestFocusInWindow();
                        if (emojiPopup != null)
                            emojiPopup.setVisible(false);
                    }
                });
                grid.add(lbl);
            }
        }

        picker.add(grid, BorderLayout.CENTER);

        emojiPopup = new JPopupMenu();
        emojiPopup.setLayout(new BorderLayout());
        emojiPopup.setBorder(null);
        emojiPopup.add(picker);

        // Mostrar encima del campo de texto
        emojiPopup.show(inputField, 0, -grid.getPreferredSize().height - 28);
    }
    // MULTIMEDIA
    private void openFilePicker() {
        if (selectedContact == null)
            return;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Enviar archivo");
        fc.setAcceptAllFileFilterUsed(true);
        fc.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Im\u00e1genes", "png", "jpg", "jpeg", "gif", "bmp", "webp"));
        fc.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Videos", "mp4", "mov", "avi", "mkv"));
        fc.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Documentos", "pdf", "docx", "txt", "xlsx", "zip"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            boolean isImg = isImageFile(f);
            Message msg = new Message(f.getName(), isImg ? "image" : "file", false, time);
            msg.filePath = f.getAbsolutePath();
            conversations.computeIfAbsent(selectedContact.name, k -> new ArrayList<>()).add(msg);
            selectedContact.lastMessage = "\uD83D\uDCCE " + f.getName();
            refreshMessages();
            refreshContactList();
        }
    }

    private boolean isImageFile(File f) {
        String n = f.getName().toLowerCase();
        return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg")
                || n.endsWith(".gif") || n.endsWith(".bmp") || n.endsWith(".webp");
    }
    // MENÚ INFO (botón i)
    private void showInfoMenu(Component anchor) {
        if (selectedContact == null)
            return;

        JPopupMenu menu = darkPopup();
        addMenuItem(menu, "Ver perfil completo", TEXT_PRIMARY, () -> JOptionPane.showMessageDialog(this,
                "Usuario: " + selectedContact.name + "\nEstado: " +
                        (selectedContact.online ? "En l\u00ednea" : "Desconectado"),
                "Perfil", JOptionPane.INFORMATION_MESSAGE));

        addMenuItem(menu, "Silenciar notificaciones", TEXT_PRIMARY,
                () -> showToast("Silenciado: " + selectedContact.name));

        menu.addSeparator();

        addMenuItem(menu, "Bloquear usuario", RED_DANGER, () -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "¿Bloquear a " + selectedContact.name + "?\nEsta persona dejará de aparecer en tu lista.",
                    "Bloquear usuario", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION)
                blockContact(selectedContact);
        });

        addMenuItem(menu, "Reportar usuario", RED_DANGER, () -> JOptionPane.showMessageDialog(this,
                "Reporte enviado. Revisaremos el caso en 24h.",
                "Reportar", JOptionPane.INFORMATION_MESSAGE));

        menu.show(anchor, 0, anchor.getHeight() + 6);
    }

    private void blockContact(Contact c) {
        String name = c.name;
        contacts.remove(c);
        conversations.remove(c.name);
        selectedContact = null;
        headerNameLabel.setText("Selecciona un chat");
        if (chatHeaderPanel != null)
            chatHeaderPanel.setVisible(false);
        if (inputBarPanel != null)
            inputBarPanel.setVisible(false);
        headerStatusLabel.setText("\u2014");
        headerStatusLabel.setForeground(TEXT_SECONDARY);
        chatMessagesPanel.removeAll();
        chatMessagesPanel.revalidate();
        chatMessagesPanel.repaint();
        refreshContactList();
        showToast("Has bloqueado a " + name);
    }
    // DIÁLOGO AÑADIR CONTACTO
    private void showAddContactDialog() {
        JDialog dlg = new JDialog(this, "A\u00f1adir contacto", true);
        dlg.setSize(380, 240);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(BG_DIALOG);
        content.setBorder(new EmptyBorder(26, 30, 22, 30));

        JLabel titleLbl = new JLabel("Nuevo contacto");
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(TEXT_PRIMARY);

        JPanel fields = new JPanel(new GridLayout(1, 1, 0, 10));
        fields.setBackground(BG_DIALOG);

        JTextField nameField = styledTextField("Nombre del usuario");
        fields.add(nameField);

        JPanel btns = new JPanel(new GridLayout(1, 2, 12, 0));
        btns.setBackground(BG_DIALOG);

        JButton cancel = styledButton("Cancelar", BORDER_CARD, TEXT_SECONDARY);
        JButton add = styledButton("A\u00f1adir", GREEN_DARK, TEXT_PRIMARY);

        cancel.addActionListener(e -> dlg.dispose());
        add.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty() || name.equals("Nombre del usuario")) {
                nameField.setBorder(new LineBorder(RED_DANGER, 1, true));
                return;
            }
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Contact nc = new Contact(name, "Nuevo contacto", true, 0, time);
            contacts.add(0, nc);
            refreshContactList();
            dlg.dispose();
            showToast("Contacto \"" + name + "\" a\u00f1adido");
        });

        btns.add(cancel);
        btns.add(add);

        content.add(titleLbl, BorderLayout.NORTH);
        content.add(fields, BorderLayout.CENTER);
        content.add(btns, BorderLayout.SOUTH);
        dlg.setContentPane(content);
        dlg.setVisible(true);
    }
    // TOAST
    private void showToast(String msg) {
        JWindow toast = new JWindow(this);
        JLabel lbl = new JLabel("  " + msg + "  ", SwingConstants.CENTER);
        lbl.setFont(FONT_PREVIEW);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(0x28, 0x28, 0x28));
        lbl.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CARD, 1),
                new EmptyBorder(10, 16, 10, 16)));
        toast.setContentPane(lbl);
        toast.pack();
        int tx = getX() + (getWidth() - toast.getWidth()) / 2;
        int ty = getY() + getHeight() - toast.getHeight() - 48;
        toast.setLocation(tx, ty);
        toast.setVisible(true);
        javax.swing.Timer t = new javax.swing.Timer(2400, e -> toast.dispose());
        t.setRepeats(false);
        t.start();
    }
    // LÓGICA CHAT
    private void selectContact(Contact contact) {
        selectedContact = contact;
        contact.unread = 0;
        if (inputBarPanel != null)
            inputBarPanel.setVisible(true);
        if (chatHeaderPanel != null)
            chatHeaderPanel.setVisible(true);
        headerNameLabel.setText(contact.name);
        headerStatusLabel.setText(contact.online ? "En l\u00ednea" : "Desconectado");
        headerStatusLabel.setForeground(contact.online ? TEXT_ONLINE : TEXT_SECONDARY);
        refreshMessages();
        refreshContactList();
    }

    private void refreshMessages() {
        chatMessagesPanel.removeAll();
        if (selectedContact == null) {
            chatMessagesPanel.revalidate();
            chatMessagesPanel.repaint();
            return;
        }

        List<Message> msgs = conversations.getOrDefault(selectedContact.name, new ArrayList<>());
        String dateLabel = (selectedContact.time != null && !selectedContact.time.contains(":")) 
            ? selectedContact.time : "Hoy";
        chatMessagesPanel.add(buildDateSeparator(dateLabel));
        chatMessagesPanel.add(Box.createVerticalStrut(10));
        for (Message m : msgs) {
            chatMessagesPanel.add(buildMessageBubble(m));
            chatMessagesPanel.add(Box.createVerticalStrut(6));
        }
        chatMessagesPanel.add(Box.createVerticalGlue());
        chatMessagesPanel.revalidate();
        chatMessagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar sb = chatScrollPane.getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }

    private void refreshContactList() {
        String q = searchField.getText();
        if (q.equals("Buscar usuarios..."))
            q = "";
        filterContacts(q);
    }

    private void sendMessage() {
        if (selectedContact == null)
            return;
        String text = inputField.getText().trim();
        if (text.isEmpty() || text.equals("Escribe un mensaje..."))
            return;

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        conversations.computeIfAbsent(selectedContact.name, k -> new ArrayList<>())
                .add(new Message(text, "text", false, time));
        selectedContact.lastMessage = text;
        selectedContact.time = time;
        selectedContact.timestamp = System.currentTimeMillis();
        contacts.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        inputField.setText("");
        inputField.setForeground(TEXT_PRIMARY);
        refreshMessages();
        refreshContactList();

        // Respuesta automática aleatoria
        javax.swing.Timer t = new javax.swing.Timer(900, e -> {
            if (selectedContact == null)
                return;
            String[] rs = { "Claro que si", "Jaja ok", "Cuando jugamos?", "Gg!",
                    "Oye cuentame mas", "Excelente",
                    "Exacto!", "Jajaja", "Ok ok", "Buenas noches!" };
            String r = rs[new Random().nextInt(rs.length)];
            String rt = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            conversations.get(selectedContact.name).add(new Message(r, "text", true, rt));
            selectedContact.lastMessage = r;
            selectedContact.time = rt;
            selectedContact.timestamp = System.currentTimeMillis();
            contacts.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
            refreshMessages();
            refreshContactList();
        });
        t.setRepeats(false);
        t.start();
    }
    // CONSTRUCTORES DE BURBUJA Y SEPARADOR
    private JPanel buildDateSeparator(String label) {
        JPanel sep = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getComponentCount() == 0)
                    return;
                Graphics2D g2 = aa(g);
                g2.setColor(new Color(0x3A, 0x3A, 0x3A));
                g2.setStroke(new BasicStroke(1f));
                Component c = getComponent(0);
                int cy = getHeight() / 2;
                g2.drawLine(16, cy, c.getX() - 8, cy);
                g2.drawLine(c.getX() + c.getWidth() + 8, cy, getWidth() - 16, cy);
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(TEXT_SECONDARY);
        sep.add(lbl);
        return sep;
    }

    private JPanel buildMessageBubble(Message msg) {
        JPanel wrapper = new JPanel(new FlowLayout(
                msg.incoming ? FlowLayout.LEFT : FlowLayout.RIGHT, 0, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        wrapper.setOpaque(false);

        JPanel bubble = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(msg.incoming ? BG_MSG_IN : BG_MSG_OUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                if (!msg.incoming) {
                    g2.setColor(new Color(0, 160, 0, 45));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                }
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 14, 8, 14));
        bubble.setMaximumSize(new Dimension(440, Integer.MAX_VALUE));

        if ("image".equals(msg.type) || "file".equals(msg.type)) {
            JPanel card = new JPanel(new BorderLayout(10, 0));
            card.setOpaque(false);
            String ico = "image".equals(msg.type) ? "\uD83D\uDDBC\uFE0F" : "\uD83D\uDCCE";
            JLabel iconLbl = new JLabel(ico);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            String kind = "image".equals(msg.type) ? "Imagen" : "Archivo";
            JLabel nameLbl = new JLabel("<html><span style='color:#f0f0f0'><b>" +
                    msg.text + "</b></span><br><span style='color:#888;font-size:10'>" + kind + "</span></html>");
            nameLbl.setFont(FONT_PREVIEW);
            card.add(iconLbl, BorderLayout.WEST);
            card.add(nameLbl, BorderLayout.CENTER);
            bubble.add(card, BorderLayout.CENTER);
        } else {
            FontMetrics fm = chatMessagesPanel.getFontMetrics(FONT_MSG);
            boolean isLong = fm.stringWidth(msg.text) > 300;
            String textContent = isLong
                    ? "<html><body style='width:300px; margin:0; padding:0;'>" + msg.text + "</body></html>"
                    : msg.text;
            JLabel text = new JLabel(textContent);
            text.setFont(FONT_MSG);
            text.setForeground(TEXT_PRIMARY);
            bubble.add(text, BorderLayout.CENTER);
        }

        JLabel time = new JLabel(msg.time);
        time.setFont(FONT_TIME);
        time.setForeground(TEXT_SECONDARY);
        time.setHorizontalAlignment(msg.incoming ? SwingConstants.LEFT : SwingConstants.RIGHT);
        bubble.add(time, BorderLayout.SOUTH);

        wrapper.add(bubble);
        return wrapper;
    }
    // HELPERS
    interface Painter {
        void paint(Graphics2D g2);
    }

    private JPanel makeIconBtn(Runnable action, Painter painter) {
        boolean[] hov = { false };
        JPanel btn = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(34, 34);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                if (hov[0]) {
                    g2.setColor(new Color(0xFF, 0xFF, 0xFF, 14));
                    g2.fillRoundRect(2, 2, 30, 30, 8, 8);
                }
                painter.paint(g2);
            }
        };
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hov[0] = true;
                btn.repaint();
            }

            public void mouseExited(MouseEvent e) {
                hov[0] = false;
                btn.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        return btn;
    }

    private JPopupMenu darkPopup() {
        JPopupMenu m = new JPopupMenu();
        m.setBackground(BG_CARD);
        m.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CARD, 1),
                new EmptyBorder(4, 0, 4, 0)));
        return m;
    }

    private void addMenuItem(JPopupMenu menu, String label, Color fg, Runnable action) {
        JMenuItem item = new JMenuItem(label);
        item.setBackground(BG_CARD);
        item.setForeground(fg);
        item.setFont(FONT_PREVIEW);
        item.setBorder(new EmptyBorder(9, 16, 9, 32));
        item.addActionListener(e -> action.run());
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(BG_SELECTED);
            }

            public void mouseExited(MouseEvent e) {
                item.setBackground(BG_CARD);
            }
        });
        menu.add(item);
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                aa(g).setColor(BG_CARD);
                ((Graphics2D) g).fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CARD, 1),
                new EmptyBorder(8, 12, 8, 12)));
        f.setForeground(TEXT_SECONDARY);
        f.setCaretColor(GREEN_MAIN);
        f.setFont(FONT_INPUT);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT_PRIMARY);
                }
            }

            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(TEXT_SECONDARY);
                }
            }
        });
        return f;
    }

    private JButton styledButton(String label, Color borderColor, Color fgColor) {
        JButton b = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setColor(getModel().isRollover() ? new Color(
                        borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 60) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(fgColor);
        b.setFont(FONT_PREVIEW);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 0, 10, 0));
        return b;
    }

    private JScrollPane styledScroll(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        sp.getVerticalScrollBar().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    private static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
    // CLASES DE DATOS
    static class Contact {
        String name, lastMessage, time;
        boolean online;
        int unread;
        long timestamp;
        private static long counter = 0;

        Contact(String n, String lm, boolean on, int u, String t) {
            name = n;
            lastMessage = lm;
            online = on;
            unread = u;
            time = t;
            timestamp = System.currentTimeMillis() - (counter++ * 1000000L);
        }
    }

    static class Message {
        String text, type, time, filePath;
        boolean incoming;

        Message(String text, String type, boolean incoming, String time) {
            this.text = text;
            this.type = type;
            this.incoming = incoming;
            this.time = time;
        }
    }
    // SCROLLBAR SLIM
    static class SlimScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0x3A, 0x3A, 0x3A);
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int o) {
            return zero();
        }

        @Override
        protected JButton createIncreaseButton(int o) {
            return zero();
        }

        private JButton zero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 3, r.y, r.width - 6, r.height, 6, 6);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        }
    }
    // MAIN
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(Chat::new);
    }
}