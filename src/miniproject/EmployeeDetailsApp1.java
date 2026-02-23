package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDetailsApp1 {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/login"; // Update with your database name
    private static final String USER = "root"; // Update with your database username
    private static final String PASSWORD = "Duvijaa18@mepco"; // Update with your database password

    // GUI components
    private JTextField empIdField;
    private JTextArea detailsArea; // For displaying employee details
    private JButton removeButton;
    private JButton backButton;

    // Custom Panel for Background Image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the image scaled to the panel size
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Method to create and display the GUI
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Dismissal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400); // Increase frame width and height
        frame.setLocationRelativeTo(null);
        
        // Use custom background panel
        BackgroundPanel panel = new BackgroundPanel("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\hotel.jpg"); // Use the path to your image file
        panel.setLayout(new BorderLayout()); // Set layout

        // Heading label
        JLabel headerLabel = new JLabel("Employee Layoff Form", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLACK); // Set text color for visibility
        panel.add(headerLabel, BorderLayout.NORTH);

        // Input field with label
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout()); // Horizontal layout for input fields
        JLabel empIdLabel = new JLabel("Employee ID:"); // Add label for Employee ID
        empIdLabel.setForeground(Color.BLACK); // Set text color for visibility
        empIdField = new JTextField(20); // Increase the width of the text field
        empIdField.setBackground(Color.WHITE); // Set background color of the text field

        inputPanel.add(empIdLabel); // Add the label to the input panel
        inputPanel.add(empIdField); // Add the text field to the input panel
        panel.add(inputPanel, BorderLayout.CENTER); // Add the input panel to the main panel

        // Text area for displaying employee details
        detailsArea = new JTextArea(15, 50); // Increase height as desired
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(new Color(255, 255, 255, 220)); // Slightly transparent white background for details area
        detailsArea.setForeground(Color.BLACK); // Set text color for visibility

        // Wrap the text area in a JScrollPane for scrolling functionality
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add some padding
        panel.add(detailsScrollPane, BorderLayout.SOUTH); // Change to SOUTH for better layout

        // Create action buttons
        removeButton = new JButton("Remove");
        backButton = new JButton("Back");

        JPanel btnPanel = new JPanel();
        btnPanel.add(removeButton);
        btnPanel.add(backButton);
        panel.add(btnPanel, BorderLayout.EAST); // Adjust button panel position

        // Button actions
        empIdField.addActionListener(e -> fetchEmployeeDetails()); // Fetch details when Enter is pressed
        removeButton.addActionListener(e -> removeEmployee());
        backButton.addActionListener(e -> {
            frame.dispose();
            new MainApplication().showEmployeeManagementPage(); // Replace with your actual management page method
        });

        // Add the panel to the frame
        frame.add(panel);
        frame.setVisible(true);
    }

    // Method to fetch employee details from the database
    private void fetchEmployeeDetails() {
        String empId = empIdField.getText();

        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID.");
            return;
        }

        // JDBC Code
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT name, phone_number, date_of_birth, experience, job_title, salary, address, district, marital_status, email_id, qualification "
                         + "FROM employ WHERE employee_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, empId);

                // Execute the query
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        StringBuilder details = new StringBuilder();
                        details.append("Name: ").append(resultSet.getString("name")).append("\n");
                        details.append("Phone Number: ").append(resultSet.getString("phone_number")).append("\n");
                        details.append("Date of Birth: ").append(resultSet.getString("date_of_birth")).append("\n");
                        details.append("Experience: ").append(resultSet.getString("experience")).append("\n");
                        details.append("Job Title: ").append(resultSet.getString("job_title")).append("\n");
                        details.append("Salary: ").append(resultSet.getString("salary")).append("\n");
                        details.append("Address: ").append(resultSet.getString("address")).append("\n");
                        details.append("District: ").append(resultSet.getString("district")).append("\n");
                        details.append("Marital Status: ").append(resultSet.getString("marital_status")).append("\n");
                        details.append("Email ID: ").append(resultSet.getString("email_id")).append("\n");
                        details.append("Qualification: ").append(resultSet.getString("qualification")).append("\n");
                        detailsArea.setText(details.toString());
                    } else {
                        JOptionPane.showMessageDialog(null, "Employee not found.");
                        clearFields();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }

    // Method to remove employee details from the database
    private void removeEmployee() {
        String empId = empIdField.getText();

        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this employee?", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return; // Cancel operation if not confirmed

        // JDBC Code
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "DELETE FROM employ WHERE employee_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, empId);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Thank you for working in the company!"); // Show thank you message
                    clearFields(); // Clear fields after successful deletion
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Employee not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }

    // Method to clear input fields
    private void clearFields() {
        empIdField.setText("");
        detailsArea.setText(""); // Clear details area after removal
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeDetailsApp1 app = new EmployeeDetailsApp1();
            app.createAndShowGUI();
        });
    }
}