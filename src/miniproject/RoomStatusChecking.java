package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomStatusChecking extends JFrame {
    private static final int TOTAL_ROOMS = 300;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hi"; // Update your database URL
    private static final String USER = "root"; // Your database username
    private static final String PASS = "Duvijaa18@mepco"; // Your database password

    private JLabel lblAvailableRooms;
    private JButton btnBookNow; // Button to book selected rooms
    private int selectedRoomsCount; // Track the number of selected rooms
    private StringBuilder selectedRoomNumbers; // Store selected room numbers as a StringBuilder
    private String selectedRoomType; // Store selected room type

    public RoomStatusChecking() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize selected rooms list and room type string
        selectedRoomNumbers = new StringBuilder();
        selectedRoomType = "";

        // Create a top panel for labels
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        // Total Rooms Label
        JLabel lblTotalRooms = new JLabel("Total Rooms: " + TOTAL_ROOMS);
        lblTotalRooms.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblTotalRooms);

        // Available Rooms Label
        lblAvailableRooms = new JLabel("Available Rooms: " + TOTAL_ROOMS);
        lblAvailableRooms.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblAvailableRooms);

        // Add heading
        JLabel lblHeading = new JLabel("Room Status", SwingConstants.CENTER);
        lblHeading.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblHeading, BorderLayout.NORTH);
        add(topPanel, BorderLayout.SOUTH);

        // Create a panel to hold labels for colors (legend)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(5, 1));
        
        // Adding colored legend squares with descriptions
        leftPanel.add(createRoomLegend("Suite", new Color(255, 192, 203))); // Pink
        leftPanel.add(createRoomLegend("Double", new Color(230, 230, 250))); // Lavender
        leftPanel.add(createRoomLegend("Single", new Color(144, 238, 144))); // Pale Green
        leftPanel.add(createRoomLegend("Occupied", new Color(169, 169, 169))); // Light Gray
        leftPanel.add(createRoomLegend("Selected", Color.RED)); // Red

        // Create a panel for circles (room status)
        JPanel circlesPanel = new JPanel();
        circlesPanel.setLayout(new GridLayout(20, 15));

        selectedRoomsCount = 0; // Initialize selected rooms count

        // Create an array to hold the circle panels
        CirclePanel[] circlePanels = new CirclePanel[TOTAL_ROOMS];
        
        // Add circles to the grid
        for (int i = 1; i <= TOTAL_ROOMS; i++) {
            circlePanels[i - 1] = new CirclePanel(i);
            circlePanels[i - 1].addMouseListener(new RoomMouseListener(circlePanels[i - 1]));
            circlesPanel.add(circlePanels[i - 1]);
        }

        // Create a JScrollPane to enable scrolling through the circles
        JScrollPane scrollPane = new JScrollPane(circlesPanel);

        // Create a main panel to layout left panel and scroll pane together
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(leftPanel, BorderLayout.WEST); // Add legend on the left
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Add room status grid

        // Add the main panel into the frame
        add(mainPanel, BorderLayout.CENTER);

        // Create and initialize the Book Now button
        btnBookNow = new JButton("Book Now");
        btnBookNow.setVisible(false); // Initially hidden
        btnBookNow.addActionListener(e -> openRoomBookingForm()); // Open the booking dialog
        add(btnBookNow, BorderLayout.SOUTH);

        // Fetch room data, set circle colors, and update occupancy status
        List<Integer> occupiedRooms = fetchRoomData(circlePanels);
        for (Integer occupiedRoom : occupiedRooms) {
            circlePanels[occupiedRoom - 1].setOccupied(true); // Set the occupied status
        }

        // Update available rooms count after fetching the data
        updateAvailableRooms(occupiedRooms.size());

        // Make the frame visible
        setVisible(true);
    }

    // New method to create room legend with a color square
    private JPanel createRoomLegend(String text, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(100, 30));
        panel.setOpaque(false); // Allows the background to be transparent

        JLabel colorSquare = new JLabel();
        colorSquare.setPreferredSize(new Dimension(20, 20)); // Size of the square
        colorSquare.setBackground(color);
        colorSquare.setOpaque(true); // Fill the background with the color

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        panel.add(colorSquare);
        panel.add(label);
        return panel;
    }

    // Modify the fetchRoomData method to return occupied room numbers and update CirclePanel states
    private List<Integer> fetchRoomData(CirclePanel[] circlePanels) {
        List<Integer> occupiedRooms = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT room_number FROM rooms WHERE roomstatus = 'occupied'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    occupiedRooms.add(roomNumber);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching occupied rooms: " + ex.getMessage());
        }
        return occupiedRooms;
    }

    private void updateAvailableRooms(int occupiedCount) {
        int availableRooms = TOTAL_ROOMS - occupiedCount;
        lblAvailableRooms.setText("Available Rooms: " + availableRooms); // Update available rooms
    }

    private void openRoomBookingForm() {
        if (selectedRoomsCount > 0) {
            String selectedRoomNumbersStr = selectedRoomNumbers.toString(); // Convert selected room numbers to string
            dispose();
            new RoomBookingLoginForm(selectedRoomType, selectedRoomNumbersStr); // Pass selected type and numbers
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one room to book.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private class RoomMouseListener extends MouseAdapter {
        private CirclePanel circlePanel;

        public RoomMouseListener(CirclePanel circlePanel) {
            this.circlePanel = circlePanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!circlePanel.isOccupied()) { // Only allow selection for available rooms
                circlePanel.toggleSelection(); // Toggle room selection
                selectedRoomsCount += circlePanel.isSelected() ? 1 : -1; // Increase or decrease selected rooms count

                if (circlePanel.isSelected()) {
                    if (selectedRoomNumbers.length() > 0) {
                        selectedRoomNumbers.append(","); // Append a comma if there are already selected rooms
                    }
                    selectedRoomNumbers.append(circlePanel.roomNumber); // Add selected room number
                    selectedRoomType = getRoomType(circlePanel.roomNumber); // Automatically get room type based on room number
                } else {
                    // If a room is unselected, update the StringBuilder by removing the room number
                    removeRoomNumber(circlePanel.roomNumber);
                    if (selectedRoomNumbers.length() == 0) {
                        selectedRoomType = ""; // Clear room type if no rooms are selected
                    }
                }

                btnBookNow.setVisible(selectedRoomsCount > 0);
            }
        }
    }

    private void removeRoomNumber(int roomNumber) {
        // Convert the StringBuilder to a string, remove the room number, and update the StringBuilder
        String strNumbers = selectedRoomNumbers.toString();
        String[] numbersArray = strNumbers.split(","); // Split by comma
        StringBuilder newNumbers = new StringBuilder();

        // Rebuild the StringBuilder without the removed room number
        for (String num : numbersArray) {
            if (!num.trim().equals(String.valueOf(roomNumber))) {
                if (newNumbers.length() > 0) {
                    newNumbers.append(","); // Append a comma between numbers
                }
                newNumbers.append(num);
            }
        }

        selectedRoomNumbers = newNumbers; // Update selectedRoomNumbers
    }

    private String getRoomType(int roomNumber) {
        if (roomNumber <= 100) {
            return "Suite";
        } else if (roomNumber <= 200) {
            return "Double";
        } else {
            return "Single";
        }
    }

    private static class CirclePanel extends JPanel {
        private int roomNumber;
        private boolean isOccupied; // Track if the room is occupied
        private boolean isSelected; // Track selection state

        public CirclePanel(int roomNumber) {
            this.roomNumber = roomNumber;
            setPreferredSize(new Dimension(50, 50));
            this.isOccupied = false;
            this.isSelected = false;
        }

        public void setOccupied(boolean occupied) {
            this.isOccupied = occupied;
            repaint();
        }

        public boolean isOccupied() {
            return isOccupied;
        }

        public void toggleSelection() {
            this.isSelected = !this.isSelected; // Toggle selection state
            repaint();
        }

        public boolean isSelected() {
            return isSelected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(getRoomColor());
            g.fillOval(5, 5, 40, 40); // Draw a circle
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(roomNumber), 20, 25); // Center the room number
        }

        private Color getRoomColor() {
            // Define the colors for each type
            if (isOccupied) {
                return new Color(169, 169, 169); // Light Gray for occupied
            } else if (isSelected) {
                return Color.RED; // Red for selected
            } else if (roomNumber <= 100) {
                return new Color(255, 192, 203); // Pink for Suite
            } else if (roomNumber <= 200) {
                return new Color(230, 230, 250); // Lavender for Double
            } else {
                return new Color(144, 238, 144); // Pale Green for Single
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomStatusChecking::new);
    }
}