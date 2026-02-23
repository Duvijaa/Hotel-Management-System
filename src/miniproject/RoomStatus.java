package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RoomStatus extends JFrame {
    private static final int TOTAL_ROOMS = 300;
    protected static final String DB_URL = "jdbc:mysql://localhost:3306/hi"; // Database URL
    protected static final String USER = "root"; // Database username
    protected static final String PASS = "Duvijaa18@mepco"; // Database password

    private JLabel lblTotalRooms;
    private JLabel lblAvailableRooms;
    private List<Integer> occupiedRooms;
    private JPanel circlesPanel;

    public RoomStatus() {
        occupiedRooms = fetchOccupiedRoomsFromDatabase();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        lblTotalRooms = new JLabel("Total Rooms: " + TOTAL_ROOMS);
        lblTotalRooms.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblTotalRooms);

        lblAvailableRooms = new JLabel("Available Rooms: " + (TOTAL_ROOMS - occupiedRooms.size()));
        lblAvailableRooms.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblAvailableRooms);

        JLabel lblHeading = new JLabel("Room Status", SwingConstants.CENTER);
        lblHeading.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblHeading, BorderLayout.NORTH);
        add(topPanel, BorderLayout.SOUTH);

        JPanel colorLegendPanel = createColorIndicator();
        add(colorLegendPanel, BorderLayout.NORTH);

        circlesPanel = new JPanel();
        circlesPanel.setLayout(new GridLayout(20, 15));

        for (int i = 1; i <= TOTAL_ROOMS; i++) {
            CirclePanel circlePanel = new CirclePanel(this, i, occupiedRooms.contains(i)); // Pass instance reference
            circlesPanel.add(circlePanel);
        }

        JScrollPane scrollPane = new JScrollPane(circlesPanel);
        scrollPane.setPreferredSize(new Dimension(780, 400));
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private List<Integer> fetchOccupiedRoomsFromDatabase() {
        List<Integer> occupiedRooms = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT room_number FROM rooms WHERE roomstatus = 'occupied'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    occupiedRooms.add(rs.getInt("room_number"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching occupied rooms: " + ex.getMessage());
        }
        return occupiedRooms;
    }

    private JPanel createColorIndicator() {
        JPanel colorIndicatorPanel = new JPanel();
        colorIndicatorPanel.setBorder(BorderFactory.createTitledBorder("Room Color Legend"));
        colorIndicatorPanel.setLayout(new GridLayout(4, 1));

        addColorLegendItem(colorIndicatorPanel, Color.PINK, "Suite Room (Available)");
        addColorLegendItem(colorIndicatorPanel, Color.WHITE, "Double Cot Room (Available)");
        addColorLegendItem(colorIndicatorPanel, Color.GREEN, "Single Cot Room (Available)");
        addColorLegendItem(colorIndicatorPanel, Color.BLACK, "Occupied Room");

        return colorIndicatorPanel;
    }

    private void addColorLegendItem(JPanel panel, Color color, String description) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new FlowLayout());

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        itemPanel.add(colorBox);

        JLabel label = new JLabel(description);
        itemPanel.add(label);

        panel.add(itemPanel);
    }

    public class CirclePanel extends JPanel { // Now a non-static inner class
        private int roomNumber;
        private boolean isOccupied;

        public CirclePanel(RoomStatus roomStatus, int roomNumber, boolean isOccupied) {
            this.roomNumber = roomNumber;
            this.isOccupied = isOccupied;
            setPreferredSize(new Dimension(50, 50));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isOccupied) {
                        openBookingForm(roomNumber);
                    } else {
                        openCheckoutForm(roomNumber);
                    }
                }
            });
        }

        private void openBookingForm(int roomNumber) {
            new BookingForm(roomNumber, this);
        }

        private void openCheckoutForm(int roomNumber) {
            new CheckoutForm(RoomStatus.this, roomNumber);
        }

        public void setOccupied(boolean occupied) {
            this.isOccupied = occupied;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(getRoomColor(roomNumber));
            g.fillOval(5, 5, 40, 40);

            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(roomNumber), 20, 25);
        }

        private Color getRoomColor(int roomNumber) {
            if (roomNumber <= 100) {
                return isOccupied ? Color.BLACK : Color.PINK;
            } else if (roomNumber <= 200) {
                return isOccupied ? Color.BLACK : Color.WHITE;
            } else {
                return isOccupied ? Color.BLACK : Color.GREEN;
            }
        }
    }

    private static class BookingForm extends JDialog {
        private JTextField txtName, txtAadhar, txtPhone, txtEmail, txtAddress, txtPersons;
        private JCheckBox chkBreakfast, chkLunch, chkDinner, chkTea, chkSnacks;
        private JButton btnBook;
        private int roomNumber;
        private CirclePanel circlePanel;

        public BookingForm(int roomNumber, CirclePanel circlePanel) {
            this.roomNumber = roomNumber;
            this.circlePanel = circlePanel;

            setTitle("Booking Form for Room " + roomNumber);
            setSize(400, 500);
            setLocationRelativeTo(null);
            setModal(true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel lblHeader = new JLabel("Booking Form", SwingConstants.CENTER);
            lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
            add(lblHeader, gbc);

            // Add text fields and labels
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(new JLabel("Name: "), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            txtName = new JTextField(15);
            add(txtName, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(new JLabel("Aadhar Number: "), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            txtAadhar = new JTextField(15);
            add(txtAadhar, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(new JLabel("Phone Number: "), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            txtPhone = new JTextField(15);
            add(txtPhone, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(new JLabel("Email ID: "), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            txtEmail = new JTextField(15);
            add(txtEmail, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(new JLabel("Address: "), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            txtAddress = new JTextField(15);
            add(txtAddress, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(new JLabel("Number of Persons: "), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            txtPersons = new JTextField(15);
            add(txtPersons, gbc);

            // Food Items panel
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(new JLabel("Food Items (optional): "), gbc);

            JPanel foodPanel = new JPanel(new GridBagLayout());
            GridBagConstraints foodGbc = new GridBagConstraints();
            foodGbc.insets = new Insets(5, 5, 5, 5);
            foodGbc.anchor = GridBagConstraints.LINE_START;

            chkBreakfast = new JCheckBox("Breakfast");
            foodPanel.add(chkBreakfast, foodGbc);

            chkLunch = new JCheckBox("Lunch");
            foodPanel.add(chkLunch, foodGbc);

            chkDinner = new JCheckBox("Dinner");
            foodPanel.add(chkDinner, foodGbc);

            chkTea = new JCheckBox("Tea");
            foodPanel.add(chkTea, foodGbc);

            chkSnacks = new JCheckBox("Snacks");
            foodPanel.add(chkSnacks, foodGbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            add(foodPanel, gbc);

            // Book now button
            btnBook = new JButton("Book Now");
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(btnBook, gbc);
            btnBook.addActionListener(e -> bookRoom());

            setVisible(true);
        }

        private void bookRoom() {
            String name = txtName.getText().trim();
            String aadhar = txtAadhar.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();
            String persons = txtPersons.getText().trim();
            String foodItems = "";

            // Validation checks
            if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid name (only alphabets and spaces).");
                return;
            }

            if (aadhar.length() != 12 || !aadhar.matches("[0-9]+")) {
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

            if (persons.isEmpty() || !persons.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number of persons.");
                return;
            }

            // Collect selected food items
            if (chkBreakfast.isSelected()) foodItems += "Breakfast ";
            if (chkLunch.isSelected()) foodItems += "Lunch ";
            if (chkDinner.isSelected()) foodItems += "Dinner ";
            if (chkTea.isSelected()) foodItems += "Tea ";
            if (chkSnacks.isSelected()) foodItems += "Snacks ";

            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            try (Connection conn = DriverManager.getConnection(RoomStatus.DB_URL, RoomStatus.USER, RoomStatus.PASS)) {
                String sqlInsert = "INSERT INTO bookings(roomnumber, name, aadharnumber, phonenumber, emailid, address, numberofpersons, food, roomtype, checkindate, checkintime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setInt(1, roomNumber);
                    pstmt.setString(2, name);
                    pstmt.setString(3, aadhar);
                    pstmt.setString(4, phone);
                    pstmt.setString(5, email);
                    pstmt.setString(6, address);
                    pstmt.setInt(7, Integer.parseInt(persons));
                    pstmt.setString(8, foodItems.trim());
                    pstmt.setString(9, getRoomType(roomNumber));
                    pstmt.setDate(10, java.sql.Date.valueOf(currentDate));
                    pstmt.setTime(11, java.sql.Time.valueOf(currentTime));

                    pstmt.executeUpdate();

                    String sqlUpdate = "UPDATE rooms SET roomstatus = ? WHERE room_number = ?";
                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                        pstmtUpdate.setString(1, "occupied");
                        pstmtUpdate.setInt(2, roomNumber);
                        pstmtUpdate.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Room booked successfully!");
                    circlePanel.setOccupied(true);
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error during booking: " + ex.getMessage());
            }
        }

        private boolean isValidEmail(String email) {
            String emailRegex = "^[a-zA-Z0-9_\\.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
            return email.matches(emailRegex);
        }

        private String getRoomType(int roomNumber) {
            if (roomNumber <= 100) {
                return "Suite Room";
            } else if (roomNumber <= 200) {
                return "Double Cot";
            } else {
                return "Single Cot";
            }
        }
    }

    private static class CheckoutForm extends JDialog {
        private JTextField txtPhone;
        private JButton btnCheckout, btnBill;
        private JTextArea txtAreaDetails;
        private int roomNumber;
        private RoomStatus roomStatus;

        public CheckoutForm(RoomStatus roomStatus, int roomNumber) {
            this.roomStatus = roomStatus;
            this.roomNumber = roomNumber;
            setTitle("Checkout for Room " + roomNumber);
            setSize(400, 300);
            setLocationRelativeTo(null);
            setModal(true);
            setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel(new FlowLayout());
            inputPanel.add(new JLabel("Phone Number: "));
            txtPhone = new JTextField(15);
            inputPanel.add(txtPhone);

            btnCheckout = new JButton("Checkout");
            inputPanel.add(btnCheckout);

            txtAreaDetails = new JTextArea(10, 30);
            txtAreaDetails.setEditable(false);
            txtAreaDetails.setLineWrap(true);
            txtAreaDetails.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(txtAreaDetails);

            btnBill = new JButton("Generate Bill");
            btnBill.setEnabled(false);
            btnBill.addActionListener(e -> openBillingForm());

            add(inputPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(btnBill, BorderLayout.SOUTH);

            btnCheckout.addActionListener(e -> checkOut());

            setVisible(true);
        }

        private void checkOut() {
            String phone = txtPhone.getText().trim();
            BookingDetails details = fetchBookingDetails(phone);

            if (details != null) {
                String message = "Booking Details:\n" +
                        "Name: " + details.name + "\n" +
                        "Aadhar: " + details.aadhar + "\n" +
                        "Room No: " + roomNumber + "\n" +
                        "Persons: " + details.persons + "\n" +
                        "Food: " + details.food + "\n" +
                        "Check-in Date: " + details.checkinDate + "\n" +
                        "Check-in Time: " + details.checkinTime + "\n";
                txtAreaDetails.setText(message);
                btnBill.setEnabled(true);
                processCheckout();
            } else {
                JOptionPane.showMessageDialog(this, "No booking found for this phone number.");
            }
        }

        private void processCheckout() {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            try (Connection conn = DriverManager.getConnection(RoomStatus.DB_URL, RoomStatus.USER, RoomStatus.PASS)) {
                String updateBookingSql = "UPDATE bookings SET checkoutdate = ?, checkouttime = ? WHERE roomnumber = ? AND phonenumber = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBookingSql)) {
                    pstmt.setDate(1, java.sql.Date.valueOf(currentDate));
                    pstmt.setTime(2, java.sql.Time.valueOf(currentTime));
                    pstmt.setInt(3, roomNumber);
                    pstmt.setString(4, txtPhone.getText().trim());
                    pstmt.executeUpdate();
                }

                String sqlUpdate = "UPDATE rooms SET roomstatus = ? WHERE room_number = ?";
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setString(1, "available");
                    pstmtUpdate.setInt(2, roomNumber);
                    pstmtUpdate.executeUpdate();
                }

                CirclePanel circlePanel = roomStatus.getCirclePanel(roomNumber);
                if (circlePanel != null) {
                    circlePanel.setOccupied(false);
                }

                JOptionPane.showMessageDialog(this, "Checkout successful!");
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error during checkout: " + ex.getMessage());
            }
        }

        private BookingDetails fetchBookingDetails(String phone) {
            BookingDetails details = null;

            try (Connection conn = DriverManager.getConnection(RoomStatus.DB_URL, RoomStatus.USER, RoomStatus.PASS)) {
                String sql = "SELECT * FROM bookings WHERE phonenumber = ? AND roomnumber = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, phone);
                    pstmt.setInt(2, roomNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            details = new BookingDetails();
                            details.name = rs.getString("name");
                            details.aadhar = rs.getString("aadharnumber");
                            details.persons = rs.getInt("numberofpersons");
                            details.food = rs.getString("food");
                            details.checkinDate = rs.getDate("checkindate");
                            details.checkinTime = rs.getTime("checkintime");
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error fetching booking details: " + ex.getMessage());
            }

            return details;
        }

        private void openBillingForm() {
            new BillingForm(roomNumber, txtPhone.getText().trim());
        }

        private static class BookingDetails {
            String name;
            String aadhar;
            int persons;
            String food;
            java.sql.Date checkinDate;
            java.sql.Time checkinTime;
        }
    }

    public CirclePanel getCirclePanel(int roomNumber) {
        for (Component component : circlesPanel.getComponents()) {
            if (component instanceof CirclePanel) {
                CirclePanel circlePanel = (CirclePanel) component;
                if (circlePanel.roomNumber == roomNumber) {
                    return circlePanel;
                }
            }
        }
        return null;
    }

    private static class BillingForm extends JDialog {
        public BillingForm(int roomNumber, String phoneNumber) {
            setTitle("Billing Details for Room " + roomNumber);
            setSize(400, 300);
            setLocationRelativeTo(null);

            JTextArea txtAreaBill = new JTextArea("This will display billing details for room " + roomNumber);
            txtAreaBill.setEditable(false);
            add(new JScrollPane(txtAreaBill));

            setModal(true);
            setVisible(true);
        }

        // Add billing calculation methods here when ready.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomStatus::new);
    }
}