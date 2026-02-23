package miniproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class FetchUserStatus {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hi";
    private static final String USER = "root"; // Your DB username
    private static final String PASSWORD = "Duvijaa18@mepco"; // Your DB password

    // Method to create a database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    // Method to fetch room details based on phone number
    private ResultSet fetchRoomDetails(String phone) {
        ResultSet rs = null;
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM bookings WHERE phonenumber = ?");
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    // Method to handle checkout and store checkout date/time
    private String checkoutRoom(String phone) {
        String checkoutDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String checkoutTime = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String name = "";
        String roomNumbers = ""; // This should be a string to hold multiple room numbers

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE bookings SET checkoutdate = ?, checkouttime = ? WHERE phonenumber = ?")) {
                
            pstmt.setString(1, checkoutDate);
            pstmt.setString(2, checkoutTime);
            pstmt.setString(3, phone);
            int rowsAffected = pstmt.executeUpdate();

            // If checkout was successful, get the user's name and room numbers
            if (rowsAffected > 0) {
                // Select the user's name and room numbers for the given phone number
                PreparedStatement nameStmt = conn.prepareStatement("SELECT name, roomnumber FROM bookings WHERE phonenumber = ?");
                nameStmt.setString(1, phone);
                ResultSet nameRs = nameStmt.executeQuery();

                if (nameRs.next()) {
                    name = nameRs.getString("name");
                    roomNumbers = nameRs.getString("roomnumber"); // Get the room numbers for the status update
                }

                // Split the room numbers string by commas and update each room's status
                if (roomNumbers != null && !roomNumbers.isEmpty()) {
                    String[] roomNumberArray = roomNumbers.split(","); // Split by comma

                    for (String roomNumber : roomNumberArray) {
                        roomNumber = roomNumber.trim(); // Clean up any extra spaces
                        try (PreparedStatement roomStatusStmt = conn.prepareStatement("UPDATE rooms SET roomstatus = 'available' WHERE room_number = ?")) {
                            roomStatusStmt.setString(1, roomNumber);
                            roomStatusStmt.executeUpdate(); // Execute the update for each room number
                        }
                    }
                }
            }
            return name; // Return the name if checkout was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return null; 
        }
    }

    // GUI initialization
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Room Checkout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);  
        frame.setLocationRelativeTo(null);

        // Use custom panel for background image
        frame.setContentPane(new BackgroundPanel());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255, 0)); // Make sure it's transparent

        JLabel header = new JLabel("Room Checkout", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(Color.BLACK);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 0)); // Transparent background for form panel
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(phoneLabel, gbc);

        JTextField phoneTextField = new JTextField(10);
        phoneTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(phoneTextField, gbc);

        JTextArea resultArea = new JTextArea(8, 25);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255, 0)); // Transparent background for buttons

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(checkoutButton);

        JButton billButton = new JButton("Bill"); 
        billButton.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(billButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Initially hide the result area
        resultArea.setVisible(false);

        // ActionListener to fetch room details
        phoneTextField.addActionListener(e -> {
            String phoneNumber = phoneTextField.getText().trim();
            resultArea.setText("");  // Clear previous result
            resultArea.setVisible(false);  // Hide the result area before fetching new data

            // Only fetch data if the phone number is not empty
            if (!phoneNumber.isEmpty()) {
                // Fetch room details
                ResultSet rs = fetchRoomDetails(phoneNumber);

                if (rs != null) {
                    try {
                        if (rs.next()) {
                            StringBuilder details = new StringBuilder();
                            details.append("Room Details:\n")
                                   .append("Name: ").append(rs.getString("name1")).append("\n")
                                   .append("Email: ").append(rs.getString("email")).append("\n")
                                   .append("Phone: ").append(rs.getString("phone")).append("\n")
                                   .append("Address: ").append(rs.getString("address")).append("\n")
                                   .append("Aadhar Number: ").append(rs.getString("aadhar")).append("\n")
                                   .append("Room Type: ").append(rs.getString("room_type")).append("\n")
                                   .append("Check-in Date: ").append(rs.getString("checkin")).append("\n")
                                   .append("Check-in Time: ").append(rs.getString("checkin_time")).append("\n")
                                   .append("Room Status: ").append(rs.getString("roomstatus")).append("\n");

                            resultArea.setText(details.toString());
                            resultArea.setVisible(true);  // Show the result area when content is ready
                        } else {
                            resultArea.setText("No record found for that phone number.");
                            resultArea.setVisible(true);  // Show the result area when no data is found
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        resultArea.setText("Error retrieving room details.");
                        resultArea.setVisible(true);  // Show the result area in case of error
                    }
                }
            }
        });

        // ActionListener for checkout button
        checkoutButton.addActionListener(e -> {
            String phoneNumber = phoneTextField.getText().trim();
            if (!phoneNumber.isEmpty()) {
                String name = checkoutRoom(phoneNumber); // Get the user's name upon checkout
                if (name != null) {
                    resultArea.setText("Checked out successfully. Thank you, " + name + "!");
                    resultArea.setVisible(true);  // Show the result area 
                } else {
                    resultArea.setText("Room is already vacated or no record found.");
                    resultArea.setVisible(true);  // Show the result area 
                }
            }
        });

        // ActionListener for back button
        backButton.addActionListener(e -> {
            frame.dispose();
            new HotelCheckInOut(); // Assuming you have this class appropriately implemented
        });

        // ActionListener for bill button
        billButton.addActionListener(e -> {
            frame.dispose();
            new BillingForm(); // Assuming you have this class appropriately implemented
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FetchUserStatus form = new FetchUserStatus();
            form.createAndShowGUI();
        });
    }

    // Custom JPanel class to paint the background image
    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load the background image (ensure the path is correct)
                backgroundImage = new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\cch.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}