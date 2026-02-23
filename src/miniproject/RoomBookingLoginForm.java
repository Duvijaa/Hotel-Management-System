package miniproject;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class RoomBookingLoginForm extends JFrame {
    private JTextField nameField, emailField, phoneField, addressField, aadharField, roomTypeField;
    private JTextField roomNumberField; // Using JTextField for display
    private JSpinner numberOfPersonsSpinner;
    private JCheckBox breakfastCheckBox, lunchCheckBox, dinnerCheckBox, snacksCheckBox, teaCheckBox;

    // Database details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hi"; // Change to your database URL
    private static final String USER = "root"; // Your database username
    private static final String PASS = "Duvijaa18@mepco"; // Your database password

    public RoomBookingLoginForm(String roomType, String roomNumbers) {
        setTitle("Room Booking Form");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(formPanel);

        addFormComponents(formPanel, roomType, roomNumbers);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addFormComponents(JPanel formPanel, String roomType, String roomNumbers) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Existing form components
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email ID:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        phoneField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Aadhar Number:"), gbc);
        aadharField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(aadharField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Room Type:"), gbc);
        roomTypeField = new JTextField(20);
        roomTypeField.setEditable(false); // Make it non-editable if it's auto-set
        gbc.gridx = 1; formPanel.add(roomTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Number of Persons:"), gbc);
        numberOfPersonsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1; formPanel.add(numberOfPersonsSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Food Options:"), gbc);

        // Initialize checkboxes
        gbc.gridx = 0; gbc.gridy = 8;
        breakfastCheckBox = new JCheckBox("Breakfast");
        formPanel.add(breakfastCheckBox, gbc);

        gbc.gridx = 1;
        lunchCheckBox = new JCheckBox("Lunch");
        formPanel.add(lunchCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        dinnerCheckBox = new JCheckBox("Dinner");
        formPanel.add(dinnerCheckBox, gbc);

        gbc.gridx = 1;
        snacksCheckBox = new JCheckBox("Snacks");
        formPanel.add(snacksCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        teaCheckBox = new JCheckBox("Tea");
        formPanel.add(teaCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 11;
        formPanel.add(new JLabel("Room Numbers:"), gbc);
        roomNumberField = new JTextField();
        roomNumberField.setEditable(false);
        roomNumberField.setText(roomNumbers); // Set room numbers from previous selection
        gbc.gridx = 1; formPanel.add(roomNumberField, gbc);

        JButton submitButton = new JButton("Submit");
        gbc.gridx = 0; gbc.gridy = 12;
        formPanel.add(submitButton, gbc);
        submitButton.addActionListener(e -> submitBooking());

        JButton backButton = new JButton("Back");
        gbc.gridx = 1; formPanel.add(backButton, gbc);
        backButton.addActionListener(e -> {
        	dispose();
            new ReceptionistDashboard();
        });

        // Fetch and display roomType and roomNumbers if provided
        roomTypeField.setText(roomType); // Set room type if it has been selected before
    }

    private void submitBooking() {
        // Collect data from the form
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String aadharNumber = aadharField.getText();
        String roomType = roomTypeField.getText();
        int numberOfPersons = (Integer) numberOfPersonsSpinner.getValue();
        String roomNumbers = roomNumberField.getText(); // Room numbers are retrieved here
        String foodOptions = getSelectedFoodOptions();

        // Validate the fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || aadharNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid name (only alphabets and spaces).");
            return;
        }

        if (aadharNumber.length() != 12 || !aadharNumber.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "Aadhar number must be 12 digits long and numeric.");
            return;
        }

        if (phone.length() != 10 || !phone.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits long and numeric.");
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        // Confirm submission
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit the booking?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) {
            return; // User opted not to submit
        }
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Insert data into the database
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Insert booking data
            String sqlBooking = "INSERT INTO bookings (name, emailid, phonenumber, address, aadharnumber, roomtype, numberofpersons, roomnumber, food, checkindate, checkintime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtBooking = conn.prepareStatement(sqlBooking)) {
                pstmtBooking.setString(1, name);
                pstmtBooking.setString(2, email);
                pstmtBooking.setString(3, phone);
                pstmtBooking.setString(4, address);
                pstmtBooking.setString(5, aadharNumber);
                pstmtBooking.setString(6, roomType);
                pstmtBooking.setInt(7, numberOfPersons);
                pstmtBooking.setString(8, roomNumbers); // Store room numbers as comma-separated string
                pstmtBooking.setString(9, foodOptions);
                pstmtBooking.setDate(10, java.sql.Date.valueOf(currentDate));
                pstmtBooking.setTime(11, java.sql.Time.valueOf(currentTime));

                pstmtBooking.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            // Update each room status to "occupied"
          //  String[] roomNumbersArray = roomNumbers.split(","); // Split the room numbers
         // Update each room status to "occupied" after confirming its current status
            String[] roomNumbersArray = roomNumbers.split(","); // Split the room numbers
            String sqlCheckStatus = "SELECT roomstatus FROM rooms WHERE room_number = ?";
            String sqlUpdate = "UPDATE rooms SET roomstatus = ? WHERE room_number = ?";

            try {
                for (String roomNumber : roomNumbersArray) {
                    int roomNum = Integer.parseInt(roomNumber.trim()); // Parse and trim room number
                    
                    // Check the current status of the room
                    try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckStatus)) {
                        pstmtCheck.setInt(1, roomNum);
                        ResultSet rs = pstmtCheck.executeQuery();
                        if (rs.next()) {
                            String currentStatus = rs.getString("roomstatus");
                            
                            // If the room is not occupied, then update its status
                            if (!"occupied".equalsIgnoreCase(currentStatus)) {
                                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                                    pstmtUpdate.setString(1, "occupied");
                                    pstmtUpdate.setInt(2, roomNum);
                                    pstmtUpdate.executeUpdate();
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_\\.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        return email.matches(emailRegex);
    }

    private String getSelectedFoodOptions() {
        StringBuilder options = new StringBuilder();
        if (breakfastCheckBox.isSelected()) options.append("Breakfast, ");
        if (lunchCheckBox.isSelected()) options.append("Lunch, ");
        if (dinnerCheckBox.isSelected()) options.append("Dinner, ");
        if (snacksCheckBox.isSelected()) options.append("Snacks, ");
        if (teaCheckBox.isSelected()) options.append("Tea, ");
        if (options.length() > 0) {
            options.setLength(options.length() - 2); // Remove trailing comma and space
        }
        return options.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoomBookingLoginForm("Single", "101, 102, 103")); // Sample inputs for demonstration
    }
}