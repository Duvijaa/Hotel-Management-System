package miniproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillingForm extends JFrame {

    // Database configurations
    private static final String ROOM_DB_URL = "jdbc:mysql://localhost:3306/studentdb"; // Replace with your database URL
    private static final String FOOD_COST_DB_URL = "jdbc:mysql://localhost:3306/food_cost_db"; // Replace with your food cost DB URL
    private static final String BOOKING_DB_URL = "jdbc:mysql://localhost:3306/hi"; // Replace with your booking DB URL
    private static final String USER = "root"; // Replace with your database username
    private static final String PASSWORD = "Duvijaa18@mepco"; // Replace with your database password

    // UI Components
    private JTextField phoneField, nameField, checkInField, checkOutField, checkInTimeField, checkOutTimeField;
    private JTextField roomNumbersField, foodItemsField, totalRoomCostField, totalFoodCostField, finalAmountField;
    private JButton backButton, generateBillButton;
    long days;

    public BillingForm() {
        setTitle("Bill Generator");
        setSize(450, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));
        setLayout(new GridBagLayout());

        // Create the form elements
        createForm();

        setVisible(true);
    }

    private void createForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Phone Number Input
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        phoneField = createTextField();
        add(phoneField, gbc);

        // Labels and Read-Only Text Fields for Bill Details
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        nameField = createReadOnlyField();
        add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Check-In Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        checkInField = createReadOnlyField();
        add(checkInField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Check-Out Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        checkOutField = createReadOnlyField();
        add(checkOutField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Check-In Time:"), gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        checkInTimeField = createReadOnlyField();
        add(checkInTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Check-Out Time:"), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        checkOutTimeField = createReadOnlyField();
        add(checkOutTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Room Numbers:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        roomNumbersField = createReadOnlyField();
        add(roomNumbersField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("Selected Food Items:"), gbc);

        gbc.gridx = 1; gbc.gridy = 7;
        foodItemsField = createReadOnlyField();
        add(foodItemsField, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        add(new JLabel("Total Room Cost:"), gbc);

        gbc.gridx = 1; gbc.gridy = 8;
        totalRoomCostField = createReadOnlyField();
        add(totalRoomCostField, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        add(new JLabel("Total Food Cost:"), gbc);

        gbc.gridx = 1; gbc.gridy = 9;
        totalFoodCostField = createReadOnlyField();
        add(totalFoodCostField, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        add(new JLabel("Final Amount Due:"), gbc);

        gbc.gridx = 1; gbc.gridy = 10;
        finalAmountField = createReadOnlyField();
        add(finalAmountField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 11;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> goBack());
        add(backButton, gbc);

        gbc.gridx = 1; gbc.gridy = 11;
        generateBillButton = new JButton("Generate Bill");
        generateBillButton.addActionListener(e -> generateBill());
        add(generateBillButton, gbc);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(30); 
        textField.setPreferredSize(new Dimension(300, 40));
        return textField;
    }

    private JTextField createReadOnlyField() {
        JTextField textField = createTextField(); 
        textField.setEditable(false);
        textField.setBackground(Color.LIGHT_GRAY);
        return textField;
    }

    private void generateBill() {
        String phoneNumber = phoneField.getText().trim();
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch booking details
        BookingDetails bookingDetails = fetchBookingDetails(phoneNumber);
        if (bookingDetails == null) {
            JOptionPane.showMessageDialog(this, "No booking found for this phone number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate costs and populate fields
        System.out.println(bookingDetails.roomNumbers);
        float roomCostPerPerson = getRoomCost(phoneNumber);
        float totalRoomCost = calculateRoomCost(bookingDetails.checkInDate, bookingDetails.checkOutDate,
                bookingDetails.checkInTime, bookingDetails.checkOutTime, roomCostPerPerson, bookingDetails.extraBedCount);
        float totalFoodCost = calculateFoodCost(bookingDetails.selectedFoodItems);
        float finalAmount = totalRoomCost + totalFoodCost;

        // Update fields with billing information
        updateBillDetails(bookingDetails, totalRoomCost, totalFoodCost, finalAmount);
    }

    private void goBack() {
        // TODO: Implement the back button functionality
        //JOptionPane.showMessageDialog(this, "Back button clicked."); // Placeholder action
    	dispose();
    	new ReceptionistDashboard();
    }

    private void updateBillDetails(BookingDetails bookingDetails, float totalRoomCost, float totalFoodCost, float finalAmount) {
        nameField.setText(bookingDetails.name);
        checkInField.setText(bookingDetails.checkInDate);
        checkOutField.setText(bookingDetails.checkOutDate);
        checkInTimeField.setText(bookingDetails.checkInTime);
        checkOutTimeField.setText(bookingDetails.checkOutTime);
        roomNumbersField.setText(bookingDetails.roomNumbers);
        foodItemsField.setText(bookingDetails.selectedFoodItems);
        totalRoomCostField.setText(String.valueOf(totalRoomCost));
        totalFoodCostField.setText(String.valueOf(totalFoodCost));
        finalAmountField.setText(String.valueOf(finalAmount));
    }

    private BookingDetails fetchBookingDetails(String phoneNumber) {
        String query = "SELECT name, checkindate, checkoutdate, checkintime, checkouttime, roomnumber, bed, food FROM bookings WHERE phonenumber = ?";
        try (Connection connection = DriverManager.getConnection(BOOKING_DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String checkinDate = resultSet.getString("checkindate");
                String checkoutDate = resultSet.getString("checkoutdate");
                String checkinTime = resultSet.getString("checkintime");
                String checkoutTime = resultSet.getString("checkouttime");
                String roomNumbers = resultSet.getString("roomnumber");
                int extraBeds = resultSet.getInt("bed");
                String foodOptions = resultSet.getString("food");

                return new BookingDetails(name, checkinDate, checkoutDate, checkinTime, checkoutTime, roomNumbers, extraBeds, foodOptions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private float getRoomCost(String roomNumbers) {
        float totalCost = 0;
        String[] rooms = roomNumbers.split(",");
        for (String room : rooms) {
            room = room.trim();
            String roomType = getRoomType(room);
            System.out.println(getRoomType(room));
            if (roomType != null) {
                float roomCost = fetchRoomCost(roomType);
                totalCost += roomCost;
            }
        }
        return totalCost;
    }

    private String getRoomType(String phoneNumber) {
        String roomType = null;
        String query = "SELECT roomtype FROM bookings WHERE phonenumber = ?";
        try (Connection connection = DriverManager.getConnection(BOOKING_DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                roomType = resultSet.getString("roomtype");
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomType;
    }

    private float fetchRoomCost(String roomType) {
        float roomCost = 0;
        String query = "SELECT amount FROM room_types WHERE room_type = ?";
        try (Connection connection = DriverManager.getConnection(ROOM_DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            

            preparedStatement.setString(1, roomType);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet);
            if (resultSet.next()) {
                roomCost = resultSet.getFloat("amount");
                System.out.println(roomCost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomCost;
    }

    private int calculateRoomCost(String checkInDateStr, String checkOutDateStr,
                                   String checkInTimeStr, String checkOutTimeStr,
                                   float roomCostPerPerson, int extraBeds) {
        try {
            // Parse check-in and check-out date strings into java.util.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date checkInDateTime = dateFormat.parse(checkInDateStr + " " + checkInTimeStr);
            Date checkOutDateTime = dateFormat.parse(checkOutDateStr + " " + checkOutTimeStr);

            // Calculate duration in milliseconds
            long duration = checkOutDateTime.getTime() - checkInDateTime.getTime();
            
            // Convert duration to days and determine if there are additional hours
            days = duration / (1000 * 60 * 60 * 24);
            System.out.println(days);
            long remainingHours = (duration / (1000 * 60 * 60)) % 24;
            System.out.println(remainingHours);

            // Calculate total room cost
            int totalCost = (int) (roomCostPerPerson * (days + (remainingHours >= 12 ? 1 : 0)));
            if(remainingHours<12 && remainingHours>=1)
            	totalCost+=roomCostPerPerson/2;
            System.out.println(totalCost);

            // Extra beds cost
            return totalCost + (extraBeds * 500); // Assuming 500 is the cost per extra bed
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int calculateFoodCost(String foodItems) {
        int totalFoodCost = 0;
        String[] items = foodItems.split(",");
        for (String item : items) {
            // Fetch cost for each food item
            String foodItem = item.trim();
            totalFoodCost += fetchFoodCost(foodItem);
        }
       
        return totalFoodCost;
    }


	private int fetchFoodCost(String foodItem) {
        int foodCost = 0;
        String query = "SELECT cost FROM food_costs WHERE meal_type = ?";
        try (Connection connection = DriverManager.getConnection(FOOD_COST_DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, foodItem);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                foodCost = resultSet.getInt("cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foodCost;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingForm::new);
    }
}

// BookingDetails class to store booking information
class BookingDetails {
    String name, checkInDate, checkOutDate, checkInTime, checkOutTime, roomNumbers, selectedFoodItems;
    int extraBedCount;

    public BookingDetails(String name, String checkInDate, String checkOutDate, String checkInTime, 
                          String checkOutTime, String roomNumbers, int extraBedCount, String selectedFoodItems) {
        this.name = name;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.roomNumbers = roomNumbers;
        this.extraBedCount = extraBedCount;
        this.selectedFoodItems = selectedFoodItems;
    }
}
