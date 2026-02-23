package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EmployeeDetailsApp {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/login"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "Duvijaa18@mepco"; 

    // GUI components
    private JTextField employeeIdField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField dobField;
    private JTextField experienceField;
    private JTextField addressField;
    private JTextField districtField;
    private JComboBox<String> maritalStatusField;
    private JTextField emailField;
    private JTextField qualificationField;
    private JTextField salaryField;
    private JComboBox<String> jobField;

    // Job titles and marital status options
    private static final String[] JOB_TITLES = {"Manager", "Receptionist", "Room Service", "Cleaner"};
    private static final String[] MARITAL_STATUS_OPTIONS = {"Select", "Single", "Married", "Divorced", "Widowed"};

    // Background image
    private Image backgroundImage;

    // Method to create and display the GUI
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Employee Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600); // Adjusted size for better visibility

        // Load background image
        backgroundImage = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\emp.jpg");

        // Create Help menu
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("Help Contents");
        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Help Contents here..."));
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        // Set background color
        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Heading
        JLabel heading = new JLabel("Onboarding Employee");
        heading.setFont(new Font("Arial", Font.BOLD, 24)); // Set font style and size
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns for the heading
        gbc.anchor = GridBagConstraints.CENTER; // Center the heading
        panel.add(heading, gbc);
        
        // Reset gridwidth after heading
        gbc.gridwidth = 1;

        // Initialize input fields
        employeeIdField = new JTextField(15);
        employeeIdField.setEditable(false);  // Employee ID should be non-editable
        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        
        // Date of Birth Field
        dobField = new JTextField(15);
        dobField.setText("YYYY-MM-DD"); // Set placeholder text
        
        // Add focus listener
        dobField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (dobField.getText().equals("YYYY-MM-DD")) {
                    dobField.setText(""); // Clear placeholder
                }
            }

            public void focusLost(FocusEvent e) {
                if (dobField.getText().isEmpty()) {
                    dobField.setText("YYYY-MM-DD"); // Restore placeholder
                }
            }
        });

        experienceField = new JTextField(15);
        addressField = new JTextField(15);
        districtField = new JTextField(15);
        maritalStatusField = new JComboBox<>(MARITAL_STATUS_OPTIONS); // JComboBox for marital status
        emailField = new JTextField(15);
        qualificationField = new JTextField(15);
        salaryField = new JTextField(15);
        salaryField.setEditable(false); // Salary field should be non-editable

        // Create job title dropdown
        jobField = new JComboBox<>(JOB_TITLES);
        jobField.addActionListener(e -> fetchSalaryFromDatabase());

        // Create labels and add to panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(employeeIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        panel.add(dobField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Experience (years):"), gbc);
        gbc.gridx = 1;
        panel.add(experienceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Job Title:"), gbc);
        gbc.gridx = 1;
        panel.add(jobField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        panel.add(salaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("District:"), gbc);
        gbc.gridx = 1;
        panel.add(districtField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(new JLabel("Marital Status:"), gbc);
        gbc.gridx = 1;
        panel.add(maritalStatusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(new JLabel("Email ID:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        panel.add(new JLabel("Qualification:"), gbc);
        gbc.gridx = 1;
        panel.add(qualificationField, gbc);

        // Create a submit button
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2; // Make the button span two columns
        JButton submitButton = new JButton("Submit");
        panel.add(submitButton, gbc);

        // Create a back button
        JButton backButton = new JButton("Back");
        gbc.gridy = 14;
        panel.add(backButton, gbc);

        submitButton.addActionListener(e -> insertEmployeeDetails());
        //backButton.addActionListener(e -> new MainApplication().showEmployeeManagementPage()); // Close the application or go back to the previous screen
        backButton.addActionListener(e -> {
        	frame.dispose();
        	new MainApplication().showEmployeeManagementPage();
        });
        
        // Fetch next Employee ID to display
        fetchNextEmployeeId();

        // Add the panel to the frame
        frame.add(panel);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center on screen
    }

    // Method to fetch the next Employee ID based on the current highest ID in the database
    private void fetchNextEmployeeId() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT MAX(employee_id) FROM employ WHERE employee_id LIKE 'HMID%'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String maxId = resultSet.getString(1);
                    int newIdNumber = maxId != null ? Integer.parseInt(maxId.replaceAll("\\D", "")) + 1 : 1;  // Increment
                    String newId = String.format("HMID%03d", newIdNumber); // Generate new ID in format HMID001
                    employeeIdField.setText(newId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching the Employee ID: " + e.getMessage());
        }
    }

    // Method to fetch salary from the database based on the selected job title
    private void fetchSalaryFromDatabase() {
        String jobTitle = (String) jobField.getSelectedItem();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT salary FROM jobsalaries WHERE jobtitle = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, jobTitle);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int salary = resultSet.getInt("salary"); // Get the corresponding salary from the database
                    salaryField.setText(String.valueOf(salary));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching salary: " + e.getMessage());
        }
    }

    // Method to validate input fields
    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty.");
            return false;
        }
        if (!nameField.getText().trim().matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(null, "Name can contain only alphabets.");
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Phone Number cannot be empty.");
            return false;
        }
        if (!phoneField.getText().trim().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "Phone Number must be exactly 10 digits.");
            return false;
        }

        if (dobField.getText().trim().isEmpty() || dobField.getText().equals("YYYY-MM-DD")) {
            JOptionPane.showMessageDialog(null, "Date of Birth cannot be empty.");
            return false;
        }

        // Validate age based on the date of birth
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dobField.getText().trim(), formatter);
            int age = today.getYear() - dateOfBirth.getYear();
            if (dateOfBirth.getDayOfYear() > today.getDayOfYear()) {
                age--; // Adjust for if birthday hasn't occurred yet this year
            }
            if (age < 18) {
                JOptionPane.showMessageDialog(null, "Age must be 18 years or older.");
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Date of Birth must be in the format YYYY-MM-DD.");
            return false;
        }

        if (experienceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Experience cannot be empty.");
            return false;
        }
        if (!experienceField.getText().trim().matches("\\d+")) { // Experience should be digits
            JOptionPane.showMessageDialog(null, "Experience must be in digits.");
            return false;
        }

        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address cannot be empty.");
            return false;
        }
        if (districtField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "District cannot be empty.");
            return false;
        }

        // Check if marital status is selected
        String maritalStatus = (String) maritalStatusField.getSelectedItem();
        if (maritalStatus.equals("Select")) {
            JOptionPane.showMessageDialog(null, "Marital Status must be selected.");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email ID cannot be empty.");
            return false;
        }
        String email = emailField.getText().trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(null, "Email ID is not valid.");
            return false;
        }

        if (qualificationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Qualification cannot be empty.");
            return false;
        }

        if (salaryField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Salary cannot be empty.");
            return false;
        }
        return true;
    }

    // Method to insert employee details into the database
    private void insertEmployeeDetails() {
        // Validate input values
        if (!validateFields()) {
            return; // Stop execution if validation fails
        }

        // Retrieve input values
        String employeeId = employeeIdField.getText();
        String name = nameField.getText();
        String phoneNumber = phoneField.getText();
        String dateOfBirth = dobField.getText();
        int experience = Integer.parseInt(experienceField.getText());
        String jobTitle = (String) jobField.getSelectedItem();
        int salary = Integer.parseInt(salaryField.getText());
        String address = addressField.getText();
        String district = districtField.getText();
        String maritalStatus = (String) maritalStatusField.getSelectedItem(); // Use JComboBox selection
        String emailId = emailField.getText();
        String qualification = qualificationField.getText();

        // JDBC code
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "INSERT INTO employ (employee_id, name, phone_number, date_of_birth, experience, " +
                           "job_title, salary, address, district, marital_status, email_id, qualification) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, employeeId);
                statement.setString(2, name);
                statement.setString(3, phoneNumber);
                statement.setString(4, dateOfBirth);
                statement.setInt(5, experience);
                statement.setString(6, jobTitle);
                statement.setInt(7, salary);
                statement.setString(8, address);
                statement.setString(9, district);
                statement.setString(10, maritalStatus);
                statement.setString(11, emailId);
                statement.setString(12, qualification);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Employee details inserted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Error inserting employee details.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeDetailsApp app = new EmployeeDetailsApp();
            app.createAndShowGUI();
        });
    }

    // Inner class for the background panel
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}