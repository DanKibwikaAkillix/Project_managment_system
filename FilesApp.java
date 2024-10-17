import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class FilesApp extends JPanel {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Project_management_systemV2";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private JTextField actionField;
    private JTable filesTable;
    private DefaultTableModel tableModel;
    private String selectedFilePath; // Store the path of the selected file

    public FilesApp() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Managing Files", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        loadFiles();
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        actionField = new JTextField(15);

        inputPanel.add(new JLabel("Action:"));
        inputPanel.add(actionField);
        inputPanel.add(createButton("Upload File", e -> uploadFile())); 
        inputPanel.add(createButton("Delete Selected File", e -> deleteFile()));

        return inputPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"File ID", "File Name", "Action", "File Type", "File Path"};
        tableModel = new DefaultTableModel(columns, 0);
        filesTable = new JTable(tableModel);
        filesTable.setFillsViewportHeight(true);
        filesTable.setRowHeight(25);
        filesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        filesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Ensure single selection

        filesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openFile();
                }
            }
        });

        return new JScrollPane(filesTable);
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove focus border
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(actionListener);
        return button;
    }

    private void loadFiles() {
        tableModel.setRowCount(0);
        String query = "CALL getAllFiles()";
        executeQuery(query, stmt -> {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("fileID"),
                    rs.getString("fileName"),
                    rs.getString("action"),
                    rs.getString("fileType"),
                    rs.getString("filePath")
                };
                tableModel.addRow(row);
            }
        });
    }

    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedFilePath = selectedFile.getAbsolutePath();
            String fileName = selectedFile.getName();
            String action = actionField.getText().trim();
            String fileType = getFileExtension(selectedFile);

            String query = "CALL addFile(?, ?, ?, ?)";
            executeUpdate(query, pstmt -> {
                pstmt.setString(1, fileName);
                pstmt.setString(2, action);
                pstmt.setString(3, fileType);
                pstmt.setString(4, selectedFilePath);
            });
            JOptionPane.showMessageDialog(this, "File uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        return lastIndexOfDot == -1 ? "" : name.substring(lastIndexOfDot + 1);
    }

    private void deleteFile() {
        int selectedRow = filesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a file to delete.");
            return;
        }

        int fileID = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "CALL deleteFile(?)";
        executeUpdate(query, pstmt -> pstmt.setInt(1, fileID));

        // Remove the deleted file from the table
        tableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "File deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFile() {
        int selectedRow = filesTable.getSelectedRow();
        if (selectedRow != -1) {
            String filePath = (String) tableModel.getValueAt(selectedRow, 4);
            try {
                Desktop.getDesktop().open(new File(filePath));
            } catch (IOException ex) {
                showError("Error opening file", ex);
            }
        }
    }

    private void executeQuery(String query, SqlConsumer<PreparedStatement> consumer) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            consumer.accept(pstmt);
        } catch (SQLException ex) {
            showError("Database Error", ex);
        }
    }

    private void executeUpdate(String query, SqlConsumer<PreparedStatement> consumer) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            consumer.accept(pstmt);
            pstmt.execute();
            loadFiles(); // Refresh file list
        } catch (SQLException ex) {
            showError("Database Error", ex);
        }
    }

    private void showError(String message, Exception ex) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(message).append(": ").append(ex.getMessage()).append("\n\n");

        for (StackTraceElement element : ex.getStackTrace()) {
            errorMessage.append(" at ").append(element.getClassName())
                    .append(".").append(element.getMethodName())
                    .append("(").append(element.getFileName())
                    .append(":").append(element.getLineNumber()).append(")\n");
        }

        JOptionPane.showMessageDialog(this, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        void accept(T t) throws SQLException;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("File Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setContentPane(new FilesApp());
            frame.setVisible(true);
        });
    }
}
