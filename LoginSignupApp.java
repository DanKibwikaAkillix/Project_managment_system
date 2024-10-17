import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginSignupApp extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Project_management_systemV2";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public LoginSignupApp() {
        setTitle("Login & Signup App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 150, 136));
        JLabel headerLabel = new JLabel("Welcome to Cloud Base Project Management");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);

        // Login Form Panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(34, 45, 65));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Login Fields
        JTextField emailField = createCustomTextField();
        JPasswordField passwordField = createCustomPasswordField();
        JButton loginButton = createCustomButton("Login");
        JButton signupButton = createCustomButton("Signup");

        // Add Labels and Fields to the Login Panel
        addLabelAndField(formPanel, "Email:", emailField, gbc, 0);
        addLabelAndField(formPanel, "Password:", passwordField, gbc, 1);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        formPanel.add(signupButton, gbc);

        // Add Panels to Main Panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Button Actions
        loginButton.addActionListener(e -> loginUser(emailField.getText(), new String(passwordField.getPassword())));
        signupButton.addActionListener(e -> new SignupForm().setVisible(true));
    }

    // Helper Method for Adding Label and Field
    private void addLabelAndField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // Custom Text Field
    private JTextField createCustomTextField() {
        JTextField textField = new JTextField(20);
        textField.setBackground(new Color(245, 245, 245));
        textField.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 136), 2));
        return textField;
    }

    // Custom Password Field
    private JPasswordField createCustomPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(new Color(245, 245, 245));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 136), 2));
        return passwordField;
    }

    // Custom Button
    private JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    // Login User Method
    private void loginUser(String email, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            CallableStatement stmt = conn.prepareCall("{CALL getUserByEmail(?)}");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getString("password").equals(password)) {
                String firstName = rs.getString("firstName");
                String roleName = rs.getString("roleName");

                // Store email in UserSession
                UserSession.setEmail(email);
                
                JOptionPane.showMessageDialog(this, "Welcome " + firstName + "! Role: " + roleName);
                dispose(); // Close the login window
                new ProfessionalDashboard().setVisible(true); // Open the dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Signup Form Class
    private class SignupForm extends JFrame {
        public SignupForm() {
            setTitle("Signup Form");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
            panel.setBackground(new Color(34, 45, 65));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            JTextField userNameField = createCustomTextField();
            JTextField emailField = createCustomTextField();
            JPasswordField passwordField = createCustomPasswordField();
            JTextField firstNameField = createCustomTextField();
            JTextField lastNameField = createCustomTextField();
            JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Creator", "Participant"});
            JTextArea roleDescriptionArea = new JTextArea(3, 20);
            roleDescriptionArea.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 136), 2));
            JButton submitButton = createCustomButton("Signup");

            panel.add(createLabel("Username:"));
            panel.add(userNameField);
            panel.add(createLabel("Email:"));
            panel.add(emailField);
            panel.add(createLabel("Password:"));
            panel.add(passwordField);
            panel.add(createLabel("First Name:"));
            panel.add(firstNameField);
            panel.add(createLabel("Last Name:"));
            panel.add(lastNameField);
            panel.add(createLabel("Role:"));
            panel.add(roleComboBox);
            panel.add(createLabel("Role Description:"));
            panel.add(new JScrollPane(roleDescriptionArea));
            panel.add(new JLabel());
            panel.add(submitButton);

            add(panel);

            submitButton.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    CallableStatement stmt = conn.prepareCall("{CALL addUser(?, ?, ?, ?, ?, ?, ?)}");
                    stmt.setString(1, userNameField.getText());
                    stmt.setString(2, emailField.getText());
                    stmt.setString(3, new String(passwordField.getPassword()));
                    stmt.setString(4, (String) roleComboBox.getSelectedItem());
                    stmt.setString(5, roleDescriptionArea.getText());
                    stmt.setString(6, firstNameField.getText());
                    stmt.setString(7, lastNameField.getText());
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User signed up successfully.");
                    dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
        }

        private JLabel createLabel(String text) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginSignupApp().setVisible(true));
    }
}

// UserSession.java
class UserSession {
    private static String email;

    public static void setEmail(String email) {
        UserSession.email = email;
    }

    public static String getEmail() {
        return email;
    }
}
