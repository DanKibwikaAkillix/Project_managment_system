import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewProjectApp extends JPanel {
    private JTextField projectNameField;
    private JTextArea projectDescriptionArea;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JComboBox<String> projectStatusCombo;
    private JTextField userEmailField; // New field for user email
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private int selectedProjectID = -1;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Project_management_systemV2";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public ViewProjectApp() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize UI components
        add(createTitle(), BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);

        loadProjects(); // Load data from the database on startup
    }

    private JLabel createTitle() {
        JLabel titleLabel = new JLabel("Project Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 128, 128));
        return titleLabel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.LIGHT_GRAY);

        // Initialize input components
        projectNameField = new JTextField(15);
        projectDescriptionArea = new JTextArea(5, 15);
        startDateSpinner = createDateSpinner();
        endDateSpinner = createDateSpinner();
        projectStatusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        userEmailField = new JTextField(15); // Initialize user email field

        // Add input fields and buttons to the panel
        inputPanel.add(createLabeledComponent("Project Name:", projectNameField));
        inputPanel.add(createLabeledComponent("Description:", new JScrollPane(projectDescriptionArea)));
        inputPanel.add(createLabeledComponent("Start Date:", startDateSpinner));
        inputPanel.add(createLabeledComponent("End Date:", endDateSpinner));
        inputPanel.add(createLabeledComponent("Status:", projectStatusCombo));
        inputPanel.add(createLabeledComponent("User Email:", userEmailField)); // Add user email field

        // Add control buttons
        inputPanel.add(createButton("Add", new AddProjectAction()));
        inputPanel.add(createButton("Update", new UpdateProjectAction()));
        inputPanel.add(createButton("Delete", new DeleteProjectAction()));

        return inputPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Name", "Description", "Start Date", "End Date", "Status", "User Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the cells non-editable if desired
            }
        };
        projectTable = new JTable(tableModel);
        projectTable.setFillsViewportHeight(true);
        projectTable.setRowHeight(25);
        projectTable.setFont(new Font("Arial", Font.PLAIN, 14));
        projectTable.getSelectionModel().addListSelectionListener(e -> updateSelectedProject());
    
        // Set preferred sizes for columns
        projectTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        projectTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        projectTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        projectTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        projectTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        projectTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        projectTable.getColumnModel().getColumn(6).setPreferredWidth(150);
    
        return new JScrollPane(projectTable);
    }
    
    private JPanel createLabeledComponent(String label, Component component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        return button;
    }

    private void updateSelectedProject() {
        int row = projectTable.getSelectedRow();
        if (row != -1) {
            selectedProjectID = (int) tableModel.getValueAt(row, 0);
            projectNameField.setText((String) tableModel.getValueAt(row, 1));
            projectDescriptionArea.setText((String) tableModel.getValueAt(row, 2));
            startDateSpinner.setValue(tableModel.getValueAt(row, 3));
            endDateSpinner.setValue(tableModel.getValueAt(row, 4));
            projectStatusCombo.setSelectedItem(tableModel.getValueAt(row, 5));
            userEmailField.setText((String) tableModel.getValueAt(row, 6)); // Get user email
        }
    }

    private void loadProjects() {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM projects")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("projectID"),
                    rs.getString("projectName"),
                    rs.getString("projectDescription"),
                    rs.getDate("startDate"),
                    rs.getDate("endDate"),
                    rs.getString("projectStatus"),
                    rs.getString("userMail") // Fetch user email from database
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            showError("Error loading projects", ex);
        }
    }

    private void showError(String message, SQLException ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearFields() {
        projectNameField.setText("");
        projectDescriptionArea.setText("");
        startDateSpinner.setValue(new java.util.Date());
        endDateSpinner.setValue(new java.util.Date());
        projectStatusCombo.setSelectedIndex(0);
        userEmailField.setText(""); // Clear user email field
        selectedProjectID = -1;
    }

    private class AddProjectAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = projectNameField.getText().trim();
            String description = projectDescriptionArea.getText().trim();
            String startDate = new SimpleDateFormat("yyyy-MM-dd").format(startDateSpinner.getValue());
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(endDateSpinner.getValue());
            String status = (String) projectStatusCombo.getSelectedItem();
            String userEmail = userEmailField.getText().trim(); // Get user email

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("CALL addProject(?, ?, ?, ?, ?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, startDate);
                pstmt.setString(4, endDate);
                pstmt.setString(5, status);
                pstmt.setString(6, userEmail); // Pass user email to the stored procedure
                pstmt.execute();
                clearFields();
                loadProjects();
            } catch (SQLException ex) {
                showError("Error adding project", ex);
            }
        }
    }

    private class UpdateProjectAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (selectedProjectID == -1) {
                JOptionPane.showMessageDialog(ViewProjectApp.this, "Please select a project to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = projectNameField.getText().trim();
            String description = projectDescriptionArea.getText().trim();
            String startDate = new SimpleDateFormat("yyyy-MM-dd").format(startDateSpinner.getValue());
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(endDateSpinner.getValue());
            String status = (String) projectStatusCombo.getSelectedItem();
            String userEmail = userEmailField.getText().trim(); // Get user email

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("CALL updateProject(?, ?, ?, ?, ?, ?)")) {
                pstmt.setInt(1, selectedProjectID);
                pstmt.setString(2, name);
                pstmt.setString(3, description);
                pstmt.setString(4, startDate);
                pstmt.setString(5, endDate);
                pstmt.setString(6, status);
                pstmt.setString(7, userEmail); // Pass user email to the stored procedure
                pstmt.execute();
                clearFields();
                loadProjects();
            } catch (SQLException ex) {
                showError("Error updating project", ex);
            }
        }
    }

    private class DeleteProjectAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (selectedProjectID == -1) {
                JOptionPane.showMessageDialog(ViewProjectApp.this, "Please select a project to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(ViewProjectApp.this, "Are you sure you want to delete this project?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement("CALL deleteProject(?)")) {
                    pstmt.setInt(1, selectedProjectID);
                    pstmt.execute();
                    clearFields();
                    loadProjects();
                } catch (SQLException ex) {
                    showError("Error deleting project", ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Project Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ViewProjectApp());
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
