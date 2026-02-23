package miniproject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel; 
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HotelCustomer extends JFrame {
    private Connection connection;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public HotelCustomer() {
        //setJMenuBar(EmployeeMenubar.createMenuBar(this));
         
        // Set up the database connection
        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hi", "root", "Duvijaa18@mepco");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set up the frame
        setTitle("Hotel Customer Details");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an outer panel with a light blue background and a border
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.setBackground(new Color(173, 216, 230)); // Light Blue
        outerPanel.setBorder(new LineBorder(Color.BLACK, 2)); // Black border with 2 pixels thickness

        // Create the heading
        JLabel headerLabel = new JLabel("Customer Booking Details", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLACK);
        outerPanel.add(headerLabel, BorderLayout.NORTH);

        // Create the panel for the search field
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center the components
        searchPanel.setBackground(new Color(173, 216, 230)); // Light Blue

        // Input field for Phone Number
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(15); // Set width for the text field

        // Search button
        JButton searchButton = new JButton("Search Customer");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch and display customer details based on phone number
                searchCustomer(phoneField.getText());
            }
        });

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
            	MainApplication app = new MainApplication();
                app.createAndShowGUI();
            }
        });

        // Add components to the search panel
        searchPanel.add(phoneLabel);
        searchPanel.add(phoneField);
        searchPanel.add(searchButton);
        searchPanel.add(backButton);

        // Add the search panel below the heading
        outerPanel.add(searchPanel, BorderLayout.CENTER);

        // Create table for displaying customer details
        String[] columns = {"Customer ID", "Name", "Phone", "Email", "Address", "Aadhar No", "Room Type", "Room Number", "Check-in Date", "Check-out Date"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(new Color(224, 255, 255)); // Pale Blue for rows
                return c;
            }
        };

        customerTable.setFillsViewportHeight(true);
        customerTable.setBorder(new LineBorder(Color.BLACK, 1)); // Border for table
        customerTable.getTableHeader().setBackground(new Color(135, 206, 250)); // Lighter Blue for header
        customerTable.getTableHeader().setForeground(Color.WHITE); // White text for header

        // Add table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1)); // Border around scroll pane
        outerPanel.add(scrollPane, BorderLayout.SOUTH); // Add to the bottom

        // Load all customers into the table on initial display
        loadAllCustomers();

        // Add the outer panel to the frame
        add(outerPanel);

        // Center window on the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadAllCustomers() {
        tableModel.setRowCount(0); // Clear the current table data
        String sql = "SELECT * FROM bookings"; // Adjust this SQL as required

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Collect data and check room status
                String roomNumber = rs.getString("roomnumber");
                String checkOutDate = rs.getString("checkoutdate"); // Fetch check-out date
                String checkInDate = rs.getString("checkindate"); // Fetch check-in date

                // Add row to table model
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phonenumber"),
                    rs.getString("emailid"),
                    rs.getString("address"),
                    rs.getString("aadharnumber"),
                    rs.getString("roomtype"),
                    roomNumber,
                    checkInDate, // Add check-in date
                    checkOutDate // Add check-out date
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loading customers failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCustomer(String phone) {
        // Clear table and prepare to show search results
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM bookings WHERE phonenumber=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Collect data and check room status
                String roomNumber = rs.getString("roomnumber");
                String checkOutDate = rs.getString("checkoutdate"); // Fetch check-out date
                String checkInDate = rs.getString("checkindate"); // Fetch check-in date

                if (roomNumber == null || roomNumber.isEmpty() || (checkOutDate != null && !checkOutDate.isEmpty())) {
                    roomNumber = "Vacated"; // Set status to Vacated if room number is null or checked out
                }

                // Add row to table model
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phonenumber"),
                    rs.getString("emailid"),
                    rs.getString("address"),
                    rs.getString("aadharnumber"),
                    rs.getString("roomtype"),
                    roomNumber,
                    checkInDate, // Add check-in date
                    checkOutDate // Add check-out date
                });
            } else {
                JOptionPane.showMessageDialog(this, "No customer found with the provided phone number.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new HotelCustomer();
    }
}
