import javax.swing.*;
import java.awt.*;

public class AssignedToMeApp extends JPanel {
    public AssignedToMeApp() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Tasks Assigned to Me", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);

        // Additional UI components can be added here
    }
}
