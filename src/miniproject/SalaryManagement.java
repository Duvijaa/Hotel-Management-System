package miniproject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SalaryManagement extends JFrame {
    private JTextField managerSalary;
    private JTextField receptionistSalary;
    private JTextField roomServiceSalary;
    private JTextField cleanerSalary;
    JLabel head;

    private Connection connection;
    private BackgroundPanel backgroundPanel;

    public SalaryManagement() {
        // Setup the JFrame
        setTitle("Salary Management");
        setSize(700, 600); // Increase the form size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize background panel
        backgroundPanel = new BackgroundPanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new GridBagLayout());

        // Initialize fields
        initFields();
        head = new JLabel("Hotel Management System", SwingConstants.CENTER);
        head.setForeground(Color.DARK_GRAY); // Set a dark color for the header
        head.setFont(new Font("Arial", Font.BOLD, 40));
        
        // Create buttons
        JButton updateButton = new JButton("Update Costs");
        JButton backButton = new JButton("Back");

        // Increase font size
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        updateButton.setFont(buttonFont);
        backButton.setFont(buttonFont);
        
        updateButton.setForeground(Color.WHITE); // Change button text color
        backButton.setForeground(Color.WHITE); // Change button text color
        updateButton.setBackground(Color.DARK_GRAY); // Button background
        backButton.setBackground(Color.DARK_GRAY); // Button background

        updateButton.addActionListener(e -> updateData());
        //backButton.addActionListener(e -> new AdminForm()); // Close the application
        backButton.addActionListener(e -> {
        	dispose();
            new AdminForm(); // Implement back action here
        });


        // Create GridBagConstraints for positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between components
        JLabel headingLabel = new JLabel("Update Employee Salaries");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        headingLabel.setForeground(Color.DARK_GRAY); // Heading font color
        gbc.gridwidth = 1;
        gbc.gridx = 1; // Reset column span
        gbc.gridy = 0; // Place it at the top
        backgroundPanel.add(headingLabel, gbc);

        // Adding components to the frame using GridBagLayout
        gbc.gridx = 0; gbc.gridy = 1; backgroundPanel.add(new JLabel("Job Title"), gbc);
        gbc.gridx = 1; backgroundPanel.add(new JLabel("Salary"), gbc);

        gbc.gridx = 0; gbc.gridy = 2; backgroundPanel.add(new JLabel("Manager:"), gbc);
        gbc.gridx = 1; backgroundPanel.add(managerSalary, gbc);

        gbc.gridx = 0; gbc.gridy = 3; backgroundPanel.add(new JLabel("Receptionist:"), gbc);
        gbc.gridx = 1; backgroundPanel.add(receptionistSalary, gbc);

        gbc.gridx = 0; gbc.gridy = 4; backgroundPanel.add(new JLabel("Room Service:"), gbc);
        gbc.gridx = 1; backgroundPanel.add(roomServiceSalary, gbc);

        gbc.gridx = 0; gbc.gridy = 5; backgroundPanel.add(new JLabel("Cleaner:"), gbc);
        gbc.gridx = 1; backgroundPanel.add(cleanerSalary, gbc);

        gbc.gridx = 1; gbc.gridy = 6; backgroundPanel.add(updateButton, gbc);
        gbc.gridx = 2; backgroundPanel.add(backButton, gbc);

        // Initialize Database Connection
        if (initDatabase()) {
            loadJobSalaries(); // Fetch and load existing job salaries into the fields
        }

        // Set visibility
        setVisible(true);
    }

    private void initFields() {
        managerSalary = new JTextField();
        receptionistSalary = new JTextField();
        roomServiceSalary = new JTextField();
        cleanerSalary = new JTextField();

        // Increase font size for the text fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        managerSalary.setFont(fieldFont);
        receptionistSalary.setFont(fieldFont);
        roomServiceSalary.setFont(fieldFont);
        cleanerSalary.setFont(fieldFont);
    }

    private boolean initDatabase() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Replace with your database credentials
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "Duvijaa18@mepco");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadJobSalaries() {
        String[] jobTitles = {"Manager", "Receptionist", "Room Service", "Cleaner"};
        JTextField[] salaryFields = {managerSalary, receptionistSalary, roomServiceSalary, cleanerSalary};

        for (int i = 0; i < jobTitles.length; i++) {
            try {
                String jobTitle = jobTitles[i];
                PreparedStatement stmt = connection.prepareStatement("SELECT Salary FROM JobSalaries WHERE JobTitle = ?");
                stmt.setString(1, jobTitle);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    salaryFields[i].setText(rs.getBigDecimal("Salary").toString());
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateData() {
        try {
            String[] jobTitles = {"Manager", "Receptionist", "Room Service", "Cleaner"};
            JTextField[] salaryFields = {managerSalary, receptionistSalary, roomServiceSalary, cleanerSalary};

            for (int i = 0; i < jobTitles.length; i++) {
                String jobTitle = jobTitles[i];
                java.math.BigDecimal salary = new java.math.BigDecimal(salaryFields[i].getText());

                PreparedStatement stmt = connection.prepareStatement("UPDATE JobSalaries SET Salary = ? WHERE JobTitle = ?");
                stmt.setBigDecimal(1, salary);
                stmt.setString(2, jobTitle);

                int rowsUpdated = stmt.executeUpdate();
                stmt.close();
            }
            JOptionPane.showMessageDialog(this, "Salaries Updated Successfully!");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating salaries: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load your background image
                backgroundImage = ImageIO.read(new File("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\saa.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the image to fill the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalaryManagement::new);
    }
}