import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class LoginSignupApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PROJECT_MANAGEMENT_SYSTEM"; // Update with your DB name
    private static final String USER = "root"; // Your MySQL username
    private static final String PASSWORD = ""; // Your MySQL password

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginSignupApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Login & Signup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new CardLayout());
        frame.setResizable(false); // Disable resizing

        // Create the card layout
        JPanel cardPanel = new JPanel(new CardLayout());

        // Create the login panel
        JPanel loginPanel = createLoginPanel(cardPanel);

        // Create the signup panel
        JPanel signupPanel = createSignupPanel(cardPanel);

        // Add panels to the card layout
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(signupPanel, "Signup");

        // Add the card panel to the frame
        frame.add(cardPanel);
        frame.setVisible(true);
    }

    private static JPanel createLoginPanel(JPanel cardPanel) {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create login components
        JLabel titleLabel = new JLabel("Welcome to PM System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");
        JLabel messageLabel = new JLabel("");

        // Style buttons
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        signupButton.setBackground(new Color(0, 123, 255));
        signupButton.setForeground(Color.WHITE);

        // Add components to the login panel
        gbc.gridx = 0; gbc.gridy = 0; loginPanel.add(titleLabel, gbc);
        gbc.gridy = 1; loginPanel.add(emailLabel, gbc);
        gbc.gridx = 1; loginPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; loginPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; loginPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; loginPanel.add(messageLabel, gbc);
        gbc.gridx = 1; loginPanel.add(loginButton, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; // Make the Signup button span across columns
        loginPanel.add(signupButton, gbc);

        // Action listener for login button
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String userPassword = new String(passwordField.getPassword());
            authenticateUser(email, userPassword, messageLabel);
        });

        // Action listener for signup button
        signupButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) (cardPanel.getLayout());
            cl.show(cardPanel, "Signup"); // Show the signup panel
        });

        return loginPanel;
    }

    private static JPanel createSignupPanel(JPanel cardPanel) {
        JPanel signupPanel = new JPanel();
        signupPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create signup components
        JLabel signupTitleLabel = new JLabel("Create Account");
        signupTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        JLabel userNameLabel = new JLabel("Username:");
        JTextField userNameField = new JTextField(20);
        JLabel signupEmailLabel = new JLabel("Email:");
        JTextField signupEmailField = new JTextField(20);
        JLabel signupPasswordLabel = new JLabel("Password:");
        JPasswordField signupPasswordField = new JPasswordField(20);
        JButton signupSubmitButton = new JButton("Signup");
        JLabel signupMessageLabel = new JLabel("");

        // Style buttons
        signupSubmitButton.setBackground(new Color(0, 123, 255));
        signupSubmitButton.setForeground(Color.WHITE);

        // Add components to the signup panel
        gbc.gridx = 0; gbc.gridy = 0; signupPanel.add(signupTitleLabel, gbc);
        gbc.gridy = 1; signupPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1; signupPanel.add(firstNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; signupPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1; signupPanel.add(lastNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; signupPanel.add(userNameLabel, gbc);
        gbc.gridx = 1; signupPanel.add(userNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; signupPanel.add(signupEmailLabel, gbc);
        gbc.gridx = 1; signupPanel.add(signupEmailField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; signupPanel.add(signupPasswordLabel, gbc);
        gbc.gridx = 1; signupPanel.add(signupPasswordField, gbc);
        gbc.gridx = 0; gbc.gridy = 6; signupPanel.add(signupMessageLabel, gbc);
        gbc.gridx = 1; signupPanel.add(signupSubmitButton, gbc);

        // Action listener for signup submit button
        signupSubmitButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String userName = userNameField.getText().trim();
            String signupEmail = signupEmailField.getText().trim();
            String password = new String(signupPasswordField.getPassword());

            registerUser(firstName, lastName, userName, signupEmail, password, signupMessageLabel);
        });

        return signupPanel;
    }

    private static void authenticateUser(String email, String userPassword, JLabel messageLabel) {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String query = "CALL GetUserByEmail(?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String accountStatus = resultSet.getString("accountStatus");
                String dbPassword = resultSet.getString("password");

                if (!"active".equals(accountStatus)) {
                    messageLabel.setText("Account is inactive.");
                } else if (PasswordUtils.verifyPassword(userPassword, dbPassword)) {
                    messageLabel.setText("Login successful!");
                    // Redirect to the dashboard or perform further actions
                } else {
                    messageLabel.setText("Invalid password.");
                }
            } else {
                messageLabel.setText("User not found.");
            }

            connection.close(); // Close the connection
        } catch (ClassNotFoundException e) {
            messageLabel.setText("MySQL Driver not found.");
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    private static void registerUser(String firstName, String lastName, String userName, String email, String password, JLabel messageLabel) {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Check if the email is already registered
            String checkEmailQuery = "SELECT * FROM Users WHERE emailAddress = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkEmailQuery);
            checkStatement.setString(1, email);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                messageLabel.setText("Email already registered.");
            } else {
                // Call the stored procedure to register a new user
                String procedureCall = "{CALL RegisterUser(?, ?, ?, ?, ?, ?)}"; // Call the RegisterUser procedure
                CallableStatement callableStatement = connection.prepareCall(procedureCall);
                callableStatement.setString(1, userName);
                callableStatement.setString(2, email);
                callableStatement.setString(3, PasswordUtils.hashPassword(password)); // Hash the password
                callableStatement.setInt(4, 1); // Assuming roleID is 1 (change as needed)
                callableStatement.setString(5, firstName);
                callableStatement.setString(6, lastName);

                boolean executed = callableStatement.execute();
                if (!executed) {
                    messageLabel.setText("Registration successful! Please log in.");
                } else {
                    messageLabel.setText("Registration failed.");
                }
            }

            connection.close(); // Close the connection
        } catch (ClassNotFoundException e) {
            messageLabel.setText("MySQL Driver not found.");
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}

class PasswordUtils {
    // Method to hash a password
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] byteData = md.digest();
            // Convert byte array to base64 string
            return Base64.getEncoder().encodeToString(byteData);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }

    // Method to verify a password
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInputPassword = hashPassword(plainPassword);
        return hashedInputPassword.equals(hashedPassword);
    }
}
