
package miniproject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeDetails extends JFrame {
    private Connection connection;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public EmployeeDetails() {
        // Set up the database connection
        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "Duvijaa18@mepco");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit constructor early if connection fails
        }

        // Set up the frame
        setTitle("Employee Details");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(EmployeeMenubar1.createMenuBar(this)); // Moved after the connection is established
        
        // Create an outer panel with a pink background and a border
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.setBackground(Color.PINK);
        outerPanel.setBorder(new LineBorder(Color.BLACK, 2));

        // Create the heading
        JLabel headerLabel = new JLabel("Employee Details", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLACK);
        outerPanel.add(headerLabel, BorderLayout.NORTH);

        // Create the panel for the search field
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(Color.PINK);

        JLabel phoneLabel = new JLabel("Employee ID:");
        JTextField phoneField = new JTextField(15);

        JButton searchButton = new JButton("Search");
        JButton backButton = new JButton("Back");
        searchButton.addActionListener(e -> searchCustomer(phoneField.getText()));

        searchPanel.add(phoneLabel);
        searchPanel.add(phoneField);
        searchPanel.add(searchButton);
        searchPanel.add(backButton);
        //backButton.addActionListener(e -> new MainApplication().showEmployeeManagementPage());
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainApplication().showEmployeeManagementPage();
            }
        });

        outerPanel.add(searchPanel, BorderLayout.CENTER);

        // Create table for displaying employee details
        String[] columns = {"Employee ID", "Name", "Phone", "Email", "Address", "Qualification", "Job", "Salary"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        customerTable.setFillsViewportHeight(true);
        customerTable.setBorder(new LineBorder(Color.BLACK, 1));

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
        outerPanel.add(scrollPane, BorderLayout.SOUTH);

        // Load all employees into the table on initial display
        loadAllCustomers();

        add(outerPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadAllCustomers() {
        tableModel.setRowCount(0); // Clear the current table data
        String sql = "SELECT * FROM employ";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Add row to table model
                tableModel.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("phone_number"),
                    rs.getString("email_id"),
                    rs.getString("address"),
                    rs.getString("qualification"),
                    rs.getString("job_title"),
                    rs.getDouble("salary"),
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loading employee detail failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCustomer(String employeeId) {
        tableModel.setRowCount(0); // Clear table and prepare to show search results
        String sql = "SELECT * FROM employ WHERE employee_id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Add row to table model
                tableModel.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("phone_number"),
                    rs.getString("email_id"),
                    rs.getString("address"),
                    rs.getString("qualification"),
                    rs.getString("job_title"),
                    rs.getDouble("salary"),
                });
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with the provided employee ID.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeDetails::new);
    }
}