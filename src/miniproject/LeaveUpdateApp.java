package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class LeaveUpdateApp extends JFrame {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_management"; // Update with your database
    private static final String DB_USER = "root"; // Update with your DB username
    private static final String DB_PASSWORD = "Duvijaa18@mepco"; // Update with your DB password

    // Form components
    private JTextField empIdField;
    private JTextField numLeavesField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> leaveTypeComboBox;
    private JTextField reasonField;
    private JButton submitButton;
    private JButton backButton;

    // Fonts
    private Font inputFont;
    private Font headingFont;

    // Background image
    private Image backgroundImage;

    public LeaveUpdateApp() {
        // Load background image
        backgroundImage = Toolkit.getDefaultToolkit().getImage("C:\\\\Users\\\\Duvijaa\\\\OneDrive\\\\Pictures\\\\hotel.jpg"); // Update image path

        // Frame properties
        setTitle("Leave Application Form");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setJMenuBar(EmployeeMenubar1.createMenuBar(this)); // Assuming this method exists

        // Use a GridBagLayout for flexible alignment and spacing
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Initialize fonts
        inputFont = new Font("Arial", Font.PLAIN, 18);
        headingFont = new Font("Arial", Font.BOLD, 24); // Font for the heading

        // Initialize components
        JLabel headingLabel = new JLabel("Leave Form");
        headingLabel.setFont(headingFont); // Set heading font
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the heading

        empIdField = new JTextField();
        empIdField.setFont(inputFont);
        empIdField.setPreferredSize(new Dimension(300, 30));

        numLeavesField = new JTextField();
        numLeavesField.setFont(inputFont);
        numLeavesField.setPreferredSize(new Dimension(300, 30));

        startDateField = new JTextField();
        startDateField.setFont(inputFont);
        startDateField.setPreferredSize(new Dimension(300, 30));
        startDateField.setText(LocalDate.now().plusDays(1).toString()); // Auto-fill to tomorrow's date

        endDateField = new JTextField();
        endDateField.setFont(inputFont);
        endDateField.setPreferredSize(new Dimension(300, 30));

        String[] leaveTypes = {"Sick Leave", "Casual Leave", "Annual Leave", "Maternity Leave"};
        leaveTypeComboBox = new JComboBox<>(leaveTypes);

        reasonField = new JTextField();
        reasonField.setFont(inputFont);
        reasonField.setPreferredSize(new Dimension(300, 30));

        submitButton = new JButton("Submit");
        submitButton.setFont(inputFont);

        backButton = new JButton("Back");
        backButton.setFont(inputFont); // Set font for the back button

        // Action listeners for the buttons
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack(); // Implement this method to handle going back
            }
        });

        // Adding components using GridBagLayout
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Make the heading span two columns
        add(headingLabel, gbc);
        
        gbc.gridwidth = 1; // Reset to single column

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Employee ID:"), gbc);

        gbc.gridx = 1;
        add(empIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Number of Leaves:"), gbc);

        gbc.gridx = 1;
        add(numLeavesField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("End Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Leave Type:"), gbc);

        gbc.gridx = 1;
        add(leaveTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Reason:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(reasonField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7; // Next row for the buttons
        gbc.gridwidth = 1; 
        add(submitButton, gbc);

        gbc.gridx = 1; // Positioning for back button
        add(backButton, gbc);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw background image
    }

    private void submitForm() {
        // Get values from fields
        String employeeId = empIdField.getText().trim();
        String numLeaves = numLeavesField.getText().trim();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        String leaveType = (String) leaveTypeComboBox.getSelectedItem();

        // Validation
        if (employeeId.isEmpty() || numLeaves.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || leaveType == null || leaveType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert string dates and number of leaves
        LocalDate start;
        LocalDate end;
        int leaveCount;

        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
            leaveCount = Integer.parseInt(numLeaves);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input format. Please check your dates and number of leaves.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate that the end date is after the start date or they are the same for one day leave
        if (leaveCount == 1) {
            if (!start.isEqual(end)) {
                JOptionPane.showMessageDialog(this, "For one day leave, start date and end date must be the same.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (!end.isAfter(start)) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Validate that the number of leaves corresponds to the duration of the leave
        if (leaveCount != (int) (end.toEpochDay() - start.toEpochDay()) + 1) {
            JOptionPane.showMessageDialog(this, "Number of leaves must match the duration between start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert data into the database
        try {
            insertLeaveApplication(employeeId, numLeaves, startDate, endDate, leaveType);
            JOptionPane.showMessageDialog(this, "Leave Application Submitted Successfully!", "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
            // Clear fields after submission
            empIdField.setText("");
            numLeavesField.setText("");
            startDateField.setText(LocalDate.now().plusDays(1).toString()); // Reset to tomorrow's date
            endDateField.setText("");
            leaveTypeComboBox.setSelectedIndex(0);
            reasonField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error inserting data into database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertLeaveApplication(String employeeId, String numLeaves, String startDate, String endDate, String leaveType) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO leave_request (employee_id, number_of_leaves, leave_start, leave_end, leave_type) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, employeeId);
                statement.setInt(2, Integer.parseInt(numLeaves));
                statement.setDate(3, java.sql.Date.valueOf(startDate));
                statement.setDate(4, java.sql.Date.valueOf(endDate));
                statement.setString(5, leaveType);
                statement.executeUpdate();
            }
        }
    }
    
    private void goBack() {
        // Implement the logic for the back button (for example, load the previous screen or exit)
        //JOptionPane.showMessageDialog(this, "Going back to the previous screen.", "Back", JOptionPane.INFORMATION_MESSAGE);
        // Here you can define the behavior when the button is pressed, for example:
        // this.dispose(); // If you want to close this window.
        // Or navigate to another JFrame if necessary.
    	dispose();
    	new MainApplication().showEmployeeManagementPage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LeaveUpdateApp form = new LeaveUpdateApp();
            form.setVisible(true);
        });
    }
}