package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDetailsApp2 {

    private static final String URL = "jdbc:mysql://localhost:3306/login"; // Update with your database name
    private static final String USER = "root"; // Update with your database username
    private static final String PASSWORD = "Duvijaa18@mepco"; // Update with your database password

    private JTextField empIdField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField dobField;
    private JTextField experienceField;
    private JTextField salaryField; // Salary field
    private JComboBox<String> jobTitleComboBox;
    private JTextField addressField;
    private JTextField districtField;
    private JTextField maritalStatusField;
    private JTextField qualificationField;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Employee Updation Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600); // Increased size for the new field
        frame.setLayout(new GridLayout(14, 2)); // Adjusted to include the new salary field
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.pink); // Set background color
        

        // Employee ID field
        empIdField = new JTextField();
        empIdField.addActionListener(this::fetchEmployeeDetails);

        // Other fields
        nameField = new JTextField();
        phoneField = new JTextField();
        dobField = new JTextField();
        experienceField = new JTextField();
        salaryField = new JTextField(); // Initialize Salary field
        jobTitleComboBox = new JComboBox<>(new String[]{"Manager", "Receptionist", "Room Service", "Cleaner"});
        jobTitleComboBox.addActionListener(e -> updateSalaryBasedOnJobTitle()); // Update action
        addressField = new JTextField();
        districtField = new JTextField();
        maritalStatusField = new JTextField();
        qualificationField = new JTextField();

        // Add fields to the frame
        frame.add(new JLabel("Employee ID:"));
        frame.add(empIdField);
        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Phone No:"));
        frame.add(phoneField);
        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        frame.add(dobField);
        frame.add(new JLabel("Experience:"));
        frame.add(experienceField);
        frame.add(new JLabel("Job Title:"));
        frame.add(jobTitleComboBox);
        frame.add(new JLabel("Salary:")); // New Salary label
        frame.add(salaryField); // Add Salary field to the frame
        frame.add(new JLabel("Address:"));
        frame.add(addressField);
        frame.add(new JLabel("District:"));
        frame.add(districtField);
        frame.add(new JLabel("Marital Status:"));
        frame.add(maritalStatusField);
        frame.add(new JLabel("Qualification:"));
        frame.add(qualificationField);

        // Buttons
        JButton updateButton = new JButton("Update");
        JButton backButton = new JButton("Back");

        updateButton.addActionListener(this::updateEmployeeDetails);
        backButton.addActionListener(e -> new MainApplication().showEmployeeManagementPage());

        frame.add(updateButton);
        frame.add(backButton);

        // Make text fields editable only if an employee is found
        setFieldsEditable(false);

        frame.setVisible(true);
    }

    private void fetchEmployeeDetails(ActionEvent e) {
        String employeeId = empIdField.getText();

        if (employeeId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM employ WHERE employee_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, employeeId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    nameField.setText(resultSet.getString("name"));
                    phoneField.setText(resultSet.getString("phone_number"));
                    dobField.setText(resultSet.getString("date_of_birth"));
                    experienceField.setText(String.valueOf(resultSet.getInt("experience")));
                    jobTitleComboBox.setSelectedItem(resultSet.getString("job_title"));
                    addressField.setText(resultSet.getString("address"));
                    districtField.setText(resultSet.getString("district"));
                    maritalStatusField.setText(resultSet.getString("marital_status"));
                    qualificationField.setText(resultSet.getString("qualification"));
                    salaryField.setText(String.valueOf(resultSet.getDouble("salary"))); // Get salary from DB

                    setFieldsEditable(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Employee not found.");
                    setFieldsEditable(false);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void updateSalaryBasedOnJobTitle() {
        String jobTitle = (String) jobTitleComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT salary FROM jobsalaries WHERE jobtitle = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, jobTitle);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    salaryField.setText(String.valueOf(resultSet.getDouble("salary"))); // Set salary based on job title
                } else {
                    salaryField.setText("0"); // No salary found for the job title
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void updateEmployeeDetails(ActionEvent e) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "UPDATE employ SET name = ?, phone_number = ?, date_of_birth = ?, experience = ?, job_title = ?, " +
                           "address = ?, district = ?, marital_status = ?, qualification = ?, salary = ? " +
                           "WHERE employee_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nameField.getText());
                statement.setString(2, phoneField.getText());
                statement.setString(3, dobField.getText());
                statement.setInt(4, Integer.parseInt(experienceField.getText()));
                statement.setString(5, (String) jobTitleComboBox.getSelectedItem());
                statement.setString(6, addressField.getText());
                statement.setString(7, districtField.getText());
                statement.setString(8, maritalStatusField.getText());
                statement.setString(9, qualificationField.getText());
                statement.setDouble(10, Double.parseDouble(salaryField.getText())); // Update salary directly from the field
                statement.setString(11, empIdField.getText());

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "Employee details updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Error updating employee details.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input: " + ex.getMessage());
        }
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        phoneField.setEditable(editable);
        dobField.setEditable(editable);
        experienceField.setEditable(editable);
        salaryField.setEditable(false); // Salary should not be edited directly
        jobTitleComboBox.setEnabled(editable);
        addressField.setEditable(editable);
        districtField.setEditable(editable);
        maritalStatusField.setEditable(editable);
        qualificationField.setEditable(editable);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeDetailsApp2 app = new EmployeeDetailsApp2();
            app.createAndShowGUI();
        });
    }
}