package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;  // Import to handle image files
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomCostManagement {
    private JFrame frame;
    private JTextField txtSingleRoomCost, txtDoubleRoomCost, txtSuiteRoomCost;
    private JTextArea txtSingleRoomDetails, txtDoubleRoomDetails, txtSuiteRoomDetails;
    private JLabel lblSingleRoomImage, lblDoubleRoomImage, lblSuiteRoomImage;
    private String singleRoomImagePath, doubleRoomImagePath, suiteRoomImagePath;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/studentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Duvijaa18@mepco";

    public RoomCostManagement() {
        initialize();
        fetchRoomCosts();  // Fetch costs from DB
        loadImages(); // Load uploaded images if they exist
    }

    private void initialize() {
        frame = new JFrame("Room Cost Management");
        frame.setBounds(100, 100, 1000, 700); // Set the initial size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        // Create a scroll pane to hold the main panel
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(175, 195, 231)); // Pale light blue background for main panel
        frame.add(scrollPane);
        scrollPane.setViewportView(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Heading label
        JLabel lblHeading = new JLabel("Room Cost Management", SwingConstants.CENTER);
        lblHeading.setFont(new Font("Arial", Font.BOLD, 24));
        lblHeading.setForeground(Color.BLACK); // Set heading text to black for contrast
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainPanel.add(lblHeading, gbc);

        // Create the room sections
        createRoomSection(mainPanel, gbc, "Single Room", 1, "single");
        createRoomSection(mainPanel, gbc, "Double Room", 10, "double");
        createRoomSection(mainPanel, gbc, "Suite Room", 19, "suite");

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 30; // Move down for update button
        gbc.gridwidth = 1;
        mainPanel.add(btnUpdate, gbc);
        btnUpdate.addActionListener(e -> updateRoomCosts());

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 2;
        mainPanel.add(btnBack, gbc);
        btnBack.addActionListener(e -> {
        	frame.dispose();
            new AdminForm(); // Implement back action here
        });

        frame.setVisible(true);
    }

   
	private void createRoomSection(JPanel mainPanel, GridBagConstraints gbc, String roomType, int rowOffset, String roomDbType) {
        // Section for Room Type
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new GridBagLayout());
        roomPanel.setBorder(BorderFactory.createTitledBorder(roomType));
        roomPanel.setBackground(new Color(216, 230, 246)); // Slightly darker pale color for room sections
        gbc.gridx = 0;
        gbc.gridy = rowOffset;
        gbc.gridwidth = 3;
        mainPanel.add(roomPanel, gbc);

        GridBagConstraints roomGbc = new GridBagConstraints();
        roomGbc.insets = new Insets(5, 5, 5, 5);
        roomGbc.anchor = GridBagConstraints.WEST;
        roomGbc.fill = GridBagConstraints.HORIZONTAL;

        // Image Label (setting preferred size to ensure rectangular display)
        JLabel lblRoomImage = new JLabel();
        lblRoomImage.setPreferredSize(new Dimension(200, 150)); // Set to a rectangular size
        lblRoomImage.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Optional: Add border for clarity
        roomGbc.gridx = 0;
        roomGbc.gridy = 0;
        roomGbc.gridwidth = 1;
        roomPanel.add(lblRoomImage, roomGbc);        

        // Upload Image Button
        JButton btnUploadImage = new JButton("Upload Image");
        roomGbc.gridx = 0;
        roomGbc.gridy = 1;
        roomGbc.gridwidth = 1; // Reset to 1 for button
        roomPanel.add(btnUploadImage, roomGbc);

        // Image Upload Logic
        btnUploadImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // Store image path based on room type
                if (roomDbType.equals("single")) {
                    singleRoomImagePath = selectedFile.getPath();
                    lblSingleRoomImage = lblRoomImage; // Keep reference
                } else if (roomDbType.equals("double")) {
                    doubleRoomImagePath = selectedFile.getPath();
                    lblDoubleRoomImage = lblRoomImage;
                } else if (roomDbType.equals("suite")) {
                    suiteRoomImagePath = selectedFile.getPath();
                    lblSuiteRoomImage = lblRoomImage;
                }
                
                // Set the uploaded image, resized to fit the label
                lblRoomImage.setIcon(new ImageIcon(resizeImage(selectedFile.getPath(), 200, 150))); 
                saveImages(); // Save the image paths to a file
            }
        });

        // Cost Label
        JLabel lblRoomCost = new JLabel(roomType + " Cost:");
        lblRoomCost.setForeground(Color.BLACK); // Set the cost label text to black
        roomGbc.gridy = 2;
        roomGbc.gridx = 1;
        roomGbc.gridwidth = 1;
        roomPanel.add(lblRoomCost, roomGbc);

        JTextField txtRoomCost = new JTextField(10);
        txtRoomCost.setHorizontalAlignment(JTextField.CENTER);
        roomGbc.gridx = 2; // Next column for cost input field
        roomPanel.add(txtRoomCost, roomGbc);

        // Text area for Room Details
        JTextArea txtRoomDetails = new JTextArea(3, 15);
        txtRoomDetails.setLineWrap(true);
        txtRoomDetails.setWrapStyleWord(true);
        txtRoomDetails.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtRoomDetails);
        roomGbc.gridy = 3;
        roomGbc.gridx = 0;
        roomGbc.gridwidth = 3; // Span across all columns
        roomPanel.add(scrollPane, roomGbc);

        // Set references for later use
        switch (roomDbType) {
            case "single":
                txtSingleRoomCost = txtRoomCost;
                txtSingleRoomDetails = txtRoomDetails;
                lblSingleRoomImage = lblRoomImage;
                break;
            case "double":
                txtDoubleRoomCost = txtRoomCost;
                txtDoubleRoomDetails = txtRoomDetails;
                lblDoubleRoomImage = lblRoomImage;
                break;
            case "suite":
                txtSuiteRoomCost = txtRoomCost;
                txtSuiteRoomDetails = txtRoomDetails;
                lblSuiteRoomImage = lblRoomImage;
                break;
        }
    }

    // Method to resize the image to fit JLabel while maintaining aspect ratio
    private Image resizeImage(String imagePath, int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            // Scale image to fit the label size
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return resizedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchRoomCosts() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement("SELECT room_type, amount FROM room_types")) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String roomType = rs.getString("room_type");
                double amount = rs.getDouble("amount");

                switch (roomType) {
                    case "single":
                        txtSingleRoomCost.setText(String.valueOf(amount));
                        txtSingleRoomDetails.setText("Typically features one single or queen-size bed.\r\n"
                        		+ "Basic furnishings including a desk, chair, and closet.\r\n"
                        		+ "En-suite bathroom with essential toiletries.\r\n"
                        		+ "Television, mini-fridge, and sometimes a small safe.\r\n"
                        		+ "Wi-Fi access.");
                        break;
                    case "double":
                        txtDoubleRoomCost.setText(String.valueOf(amount));
                        txtDoubleRoomDetails.setText("Usually includes one king-size bed or two twin beds.\r\n"
                        		+ "Additional seating area with chairs or a small table.\r\n"
                        		+ "En-suite bathroom with a bathtub or shower.\r\n"
                        		+ "Complimentary amenities such as coffee/tea making facilities.\r\n"
                        		+ "Flat-screen TV, mini-fridge, and free Wi-Fi.");
                        break;
                    case "suite":
                        txtSuiteRoomCost.setText(String.valueOf(amount));
                        txtSuiteRoomDetails.setText("One or more bedrooms with king or queen beds, plus a separate living room with a sofa or chairs.\r\n"
                        		+ "Upgraded amenities including a kitchenette or full kitchen.\r\n"
                        		+ "Larger bathrooms which may include a soaking tub or shower facilities.\r\n"
                        		+ "Additional features such as a dining area and enhanced entertainment options (more TVs, sound systems).\r\n"
                        		+ "Complimentary services like room service, access to lounges, and possibly breakfast included.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching room costs: " + e.getMessage());
        }
    }

    private void saveImages() {
        try (PrintWriter writer = new PrintWriter(new File("image_paths.txt"))) {
            writer.println(singleRoomImagePath != null ? singleRoomImagePath : "");
            writer.println(doubleRoomImagePath != null ? doubleRoomImagePath : "");
            writer.println(suiteRoomImagePath != null ? suiteRoomImagePath : "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving image paths: " + e.getMessage());
        }
    }

    private void loadImages() {
        File imagePathFile = new File("image_paths.txt");
        if (imagePathFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(imagePathFile))) {
                singleRoomImagePath = reader.readLine();
                doubleRoomImagePath = reader.readLine();
                suiteRoomImagePath = reader.readLine();

                // Set image labels to show images if paths are not blank
                if (singleRoomImagePath != null && !singleRoomImagePath.isEmpty()) {
                    lblSingleRoomImage.setIcon(new ImageIcon(resizeImage(singleRoomImagePath, 200, 150)));
                }
                if (doubleRoomImagePath != null && !doubleRoomImagePath.isEmpty()) {
                    lblDoubleRoomImage.setIcon(new ImageIcon(resizeImage(doubleRoomImagePath, 200, 150)));
                }
                if (suiteRoomImagePath != null && !suiteRoomImagePath.isEmpty()) {
                    lblSuiteRoomImage.setIcon(new ImageIcon(resizeImage(suiteRoomImagePath, 200, 150)));
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error loading image paths: " + e.getMessage());
            }
        }
    }

    private void updateRoomCosts() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateSql = "UPDATE room_types SET amount = ? WHERE room_type = ?";

            // Update Single Room
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setDouble(1, Double.parseDouble(txtSingleRoomCost.getText()));
                ps.setString(2, "single");
                ps.executeUpdate();
            }

            // Update Double Room
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setDouble(1, Double.parseDouble(txtDoubleRoomCost.getText()));
                ps.setString(2, "double");
                ps.executeUpdate();
            }

            // Update Suite Room
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setDouble(1, Double.parseDouble(txtSuiteRoomCost.getText()));
                ps.setString(2, "suite");
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Room costs updated successfully.");
            fetchRoomCosts(); // Refresh the displayed values

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating room costs: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomCostManagement::new);
    }
}