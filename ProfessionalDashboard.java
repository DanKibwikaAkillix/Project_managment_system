import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class ProfessionalDashboard extends JFrame {
    private JPanel mainContent; // Panel to hold the different apps
    private JButton[] sidebarButtons;

    public ProfessionalDashboard() {
        
        setTitle("Professional Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1)); // Use GridLayout for better spacing
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(60, 63, 65)); // Dark sidebar color

        // Display the email at the top of the sidebar
        String userEmail = UserSession.getEmail(); // Retrieve the email
        JLabel emailLabel = new JLabel("Welcome: " + userEmail);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sidebar.add(emailLabel); // Add the email label to the sidebar


        // Store email in UserSession
        UserSession.setEmail(userEmail);

        // Define sidebar items with icons
        String[] items = {"View Project", "Files", "Assigned to Me", "Logout"};
        String[] iconPaths = {
                "Icons/view_project.jpg", // Ensure correct paths
                "Icons/file.png",
                "Icons/assign_to_me.jpeg",
                "Icons/log_out.jpg"
        };

        sidebarButtons = new JButton[items.length];
        for (int i = 0; i < items.length; i++) {
            sidebarButtons[i] = createSidebarButton(items[i], iconPaths[i]);
            sidebar.add(sidebarButtons[i]);
        }

        // Add sidebar to frame
        add(sidebar, BorderLayout.WEST);

        // Create main content area
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(240, 240, 240)); // Light background for main content
        add(new JScrollPane(mainContent), BorderLayout.CENTER);

        // Set frame properties
        setVisible(true);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    // Method to create sidebar buttons with icons
    private JButton createSidebarButton(String title, String iconPath) {
        // Create button with icon
        ImageIcon icon = new ImageIcon(iconPath);
        // Scale the icon to fit the button
        Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton button = new JButton(title, new ImageIcon(scaledImage));

        button.setHorizontalTextPosition(SwingConstants.LEFT); // Align text to the left
        button.setVerticalTextPosition(SwingConstants.CENTER); // Center the text vertically
        button.setForeground(Color.WHITE); // Text color
        button.setBackground(new Color(52, 152, 219)); // Button color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Adjust button borders
        button.setPreferredSize(new Dimension(200, 40)); // Reduced button size
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Font style

        // Button hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185)); // Darker on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(Color.YELLOW)) { // If not highlighted, reset color
                    button.setBackground(new Color(52, 152, 219)); // Reset color
                }
            }
        });

        // Button action listener
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSidebarButtonClick(title);
            }
        });

        return button;
    }

    // Handle button click events
    private void handleSidebarButtonClick(String title) {
        // Clear the main content area
        mainContent.removeAll();

        // Highlight the selected button and reset others
        for (int i = 0; i < sidebarButtons.length; i++) {
            if (sidebarButtons[i].getText().equals(title)) {
                sidebarButtons[i].setBackground(Color.YELLOW); // Highlight active button
            } else {
                sidebarButtons[i].setBackground(new Color(52, 152, 219)); // Reset other buttons
            }
        }

        // Update the main content area based on the selected item
        switch (title) {
            case "View Project":
                mainContent.add(new ViewProjectApp(), BorderLayout.CENTER);
                break;
            case "Files":
                mainContent.add(new FilesApp(), BorderLayout.CENTER);
                break;
            case "Assigned to Me":
                mainContent.add(new AssignedToMeApp(), BorderLayout.CENTER);
                break;
            case "Logout":
                int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", 
                                                                "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    updateAccountStatusToInactive(); // Update account status before logging out
                    mainContent.add(new JLabel("Logging out...", SwingConstants.CENTER), BorderLayout.CENTER);
                    
                    // Delay to show logout message
                    Timer timer = new Timer(2000, e1 -> {
                        // Redirect to LoginSignupApp after a delay
                        this.dispose(); // Close current window
                        new LoginSignupApp(); // Open LoginSignupApp
                    });
                    timer.setRepeats(false); // Only execute once
                    timer.start();
                } else {
                    // If the user cancels the logout
                    mainContent.add(new JLabel("Logout canceled.", SwingConstants.CENTER), BorderLayout.CENTER);
                }
                break;
            default:
                break;
        }

        mainContent.revalidate(); // Refresh the main content area
        mainContent.repaint(); // Repaint to update the UI
    }

    private void updateAccountStatusToInactive() {
        final String DB_URL = "jdbc:mysql://localhost:3306/Project_management_systemV2";
    final String DB_USER = "root";
    String DB_PASSWORD = "";

        String query = "{CALL UpdateAccountStatus(?)}"; // Call the stored procedure

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement statement = conn.prepareCall(query)) {
            statement.setString(1, DB_USER);
            statement.executeUpdate();
            System.out.println("Logged out");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfessionalDashboard());
    }
}
