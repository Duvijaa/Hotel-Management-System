package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;

public class SalaryCalculationApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/login"; // Update with your database URL
    private static final String DB_USER = "root"; // Update with your database username
    private static final String DB_PASSWORD = "Duvijaa18@mepco"; // Update with your database password

    private JTextField empIdField;
    private JLabel nameLabel;
    private JLabel jobTitleLabel;
    private JLabel salaryLabel;
    private JLabel leaveDaysTakenLabel;
    private JLabel leaveDaysRemainingLabel;
    private JLabel dpLabel;
    private JLabel hpLabel;
    private JLabel allowanceLabel;
    private JLabel netSalaryLabel;

    private JComboBox<String> monthSelector;
    private JComboBox<Integer> yearSelector;

    // Default values for DP (Dearness Allowance), HP (House Rent Allowance), and Allowance
    private static final double DEFAULT_DP = 1000.00;
    private static final double DEFAULT_HP = 1500.00;
    private static final double DEFAULT_ALLOWANCE = 500.00;

    // Default leave days (e.g., 30 days per year)
    private static final int DEFAULT_LEAVE_DAYS = 5;

    // Font size constant
    private static final int FONT_SIZE = 18;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Salary Calculation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600); // Set frame size to 700x600
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Set background image
        setBackgroundImage(frame);

        // Employee ID field
        empIdField = new JTextField(20);
        empIdField.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));

        // Labels for employee details
        nameLabel = new JLabel("Name: ");
        jobTitleLabel = new JLabel("Job Title: ");
        salaryLabel = new JLabel("Salary: ");
        leaveDaysTakenLabel = new JLabel("Leave Days Taken: ");
        leaveDaysRemainingLabel = new JLabel("Leave Days Remaining: ");
        dpLabel = new JLabel("Dearness Allowance (DP): ");
        hpLabel = new JLabel("House Rent Allowance (HP): ");
        allowanceLabel = new JLabel("Other Allowance: ");
        netSalaryLabel = new JLabel("Net Salary: ");

        // Set font for labels
        Font labelFont = new Font("Arial", Font.PLAIN, FONT_SIZE);
        nameLabel.setFont(labelFont);
        jobTitleLabel.setFont(labelFont);
        salaryLabel.setFont(labelFont);
        leaveDaysTakenLabel.setFont(labelFont);
        leaveDaysRemainingLabel.setFont(labelFont);
        dpLabel.setFont(labelFont);
        hpLabel.setFont(labelFont);
        allowanceLabel.setFont(labelFont);
        netSalaryLabel.setFont(labelFont);

        // Month Selector
        monthSelector = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthSelector.addItem(String.format("%02d", i)); // Format as two digits
        }
        
        // Year Selector
        yearSelector = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 10; i <= currentYear; i++) {
            yearSelector.addItem(i);
        }

        // Button to calculate salary
        JButton calculateButton = new JButton("Calculate Salary");
        calculateButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        calculateButton.addActionListener(this::calculateSalary);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        //backButton.addActionListener(e -> resetFields());
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                MainApplication app = new MainApplication();
                app.createAndShowGUI();
            }
        });

        // Set layout and add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        frame.add(empIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(new JLabel("Select Month: "), gbc);
        gbc.gridx = 1;
        frame.add(monthSelector, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(new JLabel("Select Year: "), gbc);
        gbc.gridx = 1;
        frame.add(yearSelector, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel headingLabel = new JLabel("Employee Details");
        headingLabel.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        frame.add(headingLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(nameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(jobTitleLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(salaryLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(leaveDaysTakenLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(leaveDaysRemainingLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(dpLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(hpLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(allowanceLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(netSalaryLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // Span both columns for the button
        frame.add(calculateButton, gbc);

        gbc.gridy++;
        frame.add(backButton, gbc);

        frame.setVisible(true);
    }

    private void setBackgroundImage(JFrame frame) {
        JLabel backgroundLabel = new JLabel(new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\saa.png")); // Provide your image path
        backgroundLabel.setLayout(new BorderLayout());
        
        JLayeredPane layeredPane = frame.getLayeredPane();
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void calculateSalary(ActionEvent e) {
        String employeeId = empIdField.getText().trim();
        if (employeeId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID.");
            return;
        }

        String selectedMonth = (String) monthSelector.getSelectedItem();
        int selectedYear = (Integer) yearSelector.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Fetch Employee Details
            String employeeQuery = "SELECT name, job_title, salary FROM employ WHERE employee_id = ?";
            try (PreparedStatement empStatement = connection.prepareStatement(employeeQuery)) {
                empStatement.setString(1, employeeId);
                ResultSet empResultSet = empStatement.executeQuery();

                if (empResultSet.next()) {
                    String name = empResultSet.getString("name");
                    String jobTitle = empResultSet.getString("job_title");
                    double salary = empResultSet.getDouble("salary");

                    double dp = DEFAULT_DP;
                    double hp = DEFAULT_HP;
                    double allowance = DEFAULT_ALLOWANCE;

                    nameLabel.setText("Name: " + name);
                    jobTitleLabel.setText("Job Title: " + jobTitle);
                    salaryLabel.setText("Salary: $" + salary);
                    dpLabel.setText("Dearness Allowance (DP): $" + dp);
                    hpLabel.setText("House Rent Allowance (HP): $" + hp);
                    allowanceLabel.setText("Other Allowance: $" + allowance);

                    // Fetch Leave Details based on selected month and year
                    String leaveQuery = "SELECT leave_start, leave_end FROM leave_request WHERE employee_id = ? AND MONTH(leave_start) = ? AND YEAR(leave_start) = ?";
                    try (PreparedStatement leaveStatement = connection.prepareStatement(leaveQuery)) {
                        leaveStatement.setString(1, employeeId);
                        leaveStatement.setInt(2, Integer.parseInt(selectedMonth));
                        leaveStatement.setInt(3, selectedYear);
                        ResultSet leaveResultSet = leaveStatement.executeQuery();

                        int leaveDaysTaken = 0;
                        while (leaveResultSet.next()) {
                            Date startDate = leaveResultSet.getDate("leave_start");
                            Date endDate = leaveResultSet.getDate("leave_end");
                            if (startDate != null && endDate != null) {
                                long daysBetween = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
                                leaveDaysTaken += daysBetween;
                            }
                        }

                        leaveDaysTakenLabel.setText("Leave Days Taken: " + leaveDaysTaken);

                        // Calculate remaining leave days
                        int leaveDaysRemaining = DEFAULT_LEAVE_DAYS - leaveDaysTaken;
                        leaveDaysRemainingLabel.setText("Leave Days Remaining: " + leaveDaysRemaining);

                        double dailyRate = salary / 30; // Assuming 30 days in a month
                        double leaveDeduction = dailyRate * leaveDaysTaken;

                        // Calculate Net Salary
                        double grossSalary = salary + dp + hp + allowance;
                        double netSalary = grossSalary - leaveDeduction;

                        netSalaryLabel.setText("Net Salary: $" + netSalary);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Employee ID not found.");
                    resetFields();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void resetFields() {
        empIdField.setText("");
        monthSelector.setSelectedIndex(0);
        yearSelector.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR));
        
        nameLabel.setText("Name: ");
        jobTitleLabel.setText("Job Title: ");
        salaryLabel.setText("Salary: ");
        leaveDaysTakenLabel.setText("Leave Days Taken: ");
        leaveDaysRemainingLabel.setText("Leave Days Remaining: ");
        dpLabel.setText("Dearness Allowance (DP): ");
        hpLabel.setText("House Rent Allowance (HP): ");
        allowanceLabel.setText("Other Allowance: ");
        netSalaryLabel.setText("Net Salary: ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SalaryCalculationApp app = new SalaryCalculationApp();
            app.createAndShowGUI();
        });
    }
}