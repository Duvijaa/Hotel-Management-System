package miniproject;

import javax.swing.*;
import java.awt.*;

public class MainApplication {

    // Method to create the main GUI
    public void createAndShowGUI() {
        // Create the main frame
        JFrame mainFrame = new JFrame("Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 600);
        mainFrame.setLocationRelativeTo(null);

        // Create a panel with a background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\man.jpg"); // Change this to the path of your background image
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Create constraints for button placement
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding around buttons
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons fill horizontal space
        gbc.anchor = GridBagConstraints.CENTER;   // Center the buttons in the GridBag

        // Create a heading label
        JLabel headingLabel = new JLabel("Manager Dashboard");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        headingLabel.setForeground(Color.white);
        gbc.gridwidth = 1; // Reset column span
        gbc.gridy = 0; // Place it at the top
        backgroundPanel.add(headingLabel, gbc);

        // Create buttons
        JButton employeesButton = new JButton("Employees");
        JButton changePasswordButton = new JButton("Change Password");
        JButton roomManagementButton = new JButton("Customer");

        // Set button color to light gray
        employeesButton.setBackground(Color.lightGray);
        changePasswordButton.setBackground(Color.lightGray);
        roomManagementButton.setBackground(Color.lightGray);

        // Add action listeners
        //employeesButton.addActionListener(e -> showEmployeeManagementPage());
        employeesButton.addActionListener(e -> {
        	mainFrame.dispose();
        	showEmployeeManagementPage();
        });
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");

        logoutItem.addActionListener(e -> mainFrame.dispose());  // Properly close the application

        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        
        mainFrame.setJMenuBar(menuBar); // Set the menu bar

        changePasswordButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                	mainFrame.dispose();
                    new SetUpNewPassword1(); // Ensure SetUpNewPassword1 has a proper createAndShow method
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            });
        });

        roomManagementButton.addActionListener(e ->  new HotelCustomer());
        

        // Add buttons to the panel
        gbc.gridwidth = 1; // Reset column span
        gbc.gridy = 4; 
        backgroundPanel.add(employeesButton, gbc);
        gbc.gridy = 5; 
        backgroundPanel.add(changePasswordButton, gbc);
        gbc.gridy = 6; 
        backgroundPanel.add(roomManagementButton, gbc);

        // Add the panel to the frame
        mainFrame.add(backgroundPanel);
        mainFrame.setVisible(true);
    }

    // Method to create the Employee Management page
    void showEmployeeManagementPage() {
        // Create the employee management frame
        JFrame employeeFrame = new JFrame("Employee Management");
        employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        employeeFrame.setSize(700, 600);
        employeeFrame.setLocationRelativeTo(null);

        // Create a panel with a background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\e.jpg"); // Change this to the path of your employee background image
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding around buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Create a heading label for employee management
        JLabel employeeHeadingLabel = new JLabel("Employee Management");
        employeeHeadingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        employeeHeadingLabel.setForeground(Color.white);
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        backgroundPanel.add(employeeHeadingLabel, gbc);

        // Create buttons for Employee Management
        JButton addEmployeeButton = new JButton("Add Employee");
        JButton removeEmployeeButton = new JButton("Remove Employee");
        JButton editEmployeeButton = new JButton("Update Employee");
        JButton attendanceButton = new JButton("Leave Updation");
        // JButton salaryButton = new JButton("Salary Management");
        JButton salaryCalculationButton = new JButton("Salary Calculation");
        JButton employeeInfoButton = new JButton("Employee Info");
        JButton backButton = new JButton("Logout");
        

        // Set button colors
        addEmployeeButton.setBackground(Color.lightGray);
        removeEmployeeButton.setBackground(Color.lightGray);
        editEmployeeButton.setBackground(Color.lightGray);
        attendanceButton.setBackground(Color.lightGray);
        // salaryButton.setBackground(Color.lightGray);
        salaryCalculationButton.setBackground(Color.lightGray);
        employeeInfoButton.setBackground(Color.lightGray);
        backButton.setBackground(Color.lightGray);
        

        // Add action listeners for employee management buttons
        addEmployeeButton.addActionListener(e -> {
        	employeeFrame.dispose();
            EmployeeDetailsApp app = new EmployeeDetailsApp();
            app.createAndShowGUI();
        });
        removeEmployeeButton.addActionListener(e -> {
        	employeeFrame.dispose();
            EmployeeDetailsApp1 app = new EmployeeDetailsApp1();
            app.createAndShowGUI();
        });
        editEmployeeButton.addActionListener(e -> {
        	employeeFrame.dispose();
            EmployeeDetailsApp2 app = new EmployeeDetailsApp2();
            app.createAndShowGUI();
        });
        attendanceButton.addActionListener(e -> {
        	employeeFrame.dispose();
            LeaveUpdateApp form = new LeaveUpdateApp();
            form.setVisible(true);
        });
        //salaryButton.addActionListener(e -> new SalaryManagement());
        salaryCalculationButton.addActionListener(e -> {
        	employeeFrame.dispose();
            SalaryCalculationApp app = new SalaryCalculationApp();
            app.createAndShowGUI();
        });
        employeeInfoButton.addActionListener(e -> {
        	employeeFrame.dispose();
            EmployeeDetails app = new EmployeeDetails();
            //app.createAndShowGUI();
        });
        backButton.addActionListener(e -> {
        	employeeFrame.dispose();
            //EmployeeDetails app = new EmployeeDetails();
            //app.createAndShowGUI();
        });

        // Add buttons to the panel
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        backgroundPanel.add(addEmployeeButton, gbc);

        gbc.gridy = 2;
        backgroundPanel.add(removeEmployeeButton, gbc);

        gbc.gridy = 3;
        backgroundPanel.add(editEmployeeButton, gbc);

        gbc.gridy = 4;
        backgroundPanel.add(attendanceButton, gbc);

        gbc.gridy = 5;
        // panel.add(salaryButton, gbc);

        gbc.gridy = 6;
        backgroundPanel.add(salaryCalculationButton, gbc);

        gbc.gridy = 7;
        backgroundPanel.add(employeeInfoButton, gbc);
        gbc.gridy = 8;
        backgroundPanel.add(backButton, gbc);
        

        // Add the panel to the frame
        employeeFrame.add(backgroundPanel);
        employeeFrame.setVisible(true);
        employeeFrame.setLocationRelativeTo(null);
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.createAndShowGUI();
        });
    }
}
