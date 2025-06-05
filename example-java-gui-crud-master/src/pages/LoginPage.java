package pages;

import dal.admins.AdminDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPage extends JFrame {
    private final AdminDAO adminDao = new AdminDAO();
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPage() {
        setTitle("Admin Login");
        setSize(420, 320); // Increased height slightly to fit author label
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridBagLayout());

        // === Color Palette ===
        Color backgroundColor = new Color(225, 240, 255);
        Color panelColor = Color.WHITE;
        Color buttonColor = new Color(135, 206, 250);
        Color buttonHoverColor = new Color(100, 180, 240);
        Color fieldColor = new Color(240, 248, 255);
        Color borderColor = new Color(200, 220, 240);
        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        // === Rounded Panel ===
        RoundedPanel panel = new RoundedPanel(25);
        panel.setBackground(panelColor);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // === Username Label ===
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(font);
        panel.add(usernameLabel, gbc);

        // === Username Field ===
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField = new JTextField(15);
        usernameField.setFont(font);
        usernameField.setBackground(fieldColor);
        usernameField.setBorder(BorderFactory.createLineBorder(borderColor));
        panel.add(usernameField, gbc);

        // === Password Label ===
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        panel.add(passwordLabel, gbc);

        // === Password Field ===
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField(15);
        passwordField.setFont(font);
        passwordField.setBackground(fieldColor);
        passwordField.setBorder(BorderFactory.createLineBorder(borderColor));
        panel.add(passwordField, gbc);

        // === Sign In Button ===
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton signInButton = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? buttonHoverColor : buttonColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        signInButton.setFont(font);
        signInButton.setForeground(Color.WHITE);
        signInButton.setContentAreaFilled(false);
        signInButton.setFocusPainted(false);
        signInButton.setBorderPainted(false);
        signInButton.setPreferredSize(new Dimension(120, 35));
        panel.add(signInButton, gbc);

        signInButton.addActionListener(_ -> handleLogin());

        // === Footer / Author Label ===
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel authorLabel = new JLabel("© Mark Clarenz D. Alpajora");
        authorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        authorLabel.setForeground(new Color(120, 120, 120));
        panel.add(authorLabel, gbc);

        // === Final Layout ===
        getContentPane().setBackground(backgroundColor);
        add(panel);
        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        boolean valid = adminDao.checkIfAdminExists(username, password);
        if (valid) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            new StudentPage(); // Redirect to student dashboard
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    // === Custom Rounded Panel ===
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }
}
