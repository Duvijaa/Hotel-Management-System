package miniproject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;

public class FoodCostManager extends JFrame {
    private Connection connection;

    // Declaring scrollablePanel
    private JPanel scrollablePanel;

    // Cost fields
    private JTextField txtBreakfastCost, txtLunchCost, txtDinnerCost, txtSnacksCost, txtTeaCost;

    // Description areas
    private JTextArea txtBreakfastDetails, txtLunchDetails, txtDinnerDetails, txtSnacksDetails, txtTeaDetails;

    // Image labels
    private JLabel lblBreakfastImage, lblLunchImage, lblDinnerImage, lblSnacksImage, lblTeaImage;

    // Image paths
    private String breakfastImagePath, lunchImagePath, dinnerImagePath, snacksImagePath, teaImagePath;
    private final String IMAGE_PATHS_FILE = "food_image_paths.txt";

    public FoodCostManager() {
        // Setup the frame
        setTitle("Food Cost Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background color (Light green)
        getContentPane().setBackground(new Color(144, 238, 144)); // Light green background

        // Initialize the scrollable panel
        scrollablePanel = new JPanel();
        scrollablePanel.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(scrollablePanel);
        add(scrollPane, BorderLayout.CENTER);

        // Set up the database connection
        if (!setupDatabaseConnection()) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit if connection fails
        }

        // Create food entries
        createFoodEntry("Breakfast", 0);
        createFoodEntry("Lunch", 1);
        createFoodEntry("Dinner", 2);
        createFoodEntry("Snacks", 3);
        createFoodEntry("Tea", 4);

        // Fetch and display costs from the database
        fetchCosts();

        // Load images if they exist
        loadImages();

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateCosts());
        buttonPanel.add(updateButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new AdminForm(); // Navigate back to the admin form
            dispose();
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createFoodEntry(String mealType, int row) {
        GridBagConstraints constraints = createConstraints(0, row);
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.BOTH;

        // Create panel for each food item
        JPanel foodPanel = new JPanel();
        foodPanel.setLayout(new GridBagLayout());
        foodPanel.setBorder(BorderFactory.createTitledBorder(mealType));

        // Constraints for components within the food panel
        GridBagConstraints foodConstraints = createConstraints(0, 0);

        // Meal label
        foodPanel.add(new JLabel(mealType + ":"), foodConstraints);

        // Cost field
        foodConstraints.gridx = 1;
        JTextField costField = new JTextField(10);
        foodPanel.add(costField, foodConstraints);

        // Upload image button
        foodConstraints.gridx = 2;
        JButton uploadButton = new JButton("Upload Image");
        JLabel imageLabel = new JLabel();
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(FoodCostManager.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                updateImageLabel(imageLabel, selectedFile);
                // Store image path
                switch (mealType) {
                    case "Breakfast":
                        breakfastImagePath = selectedFile.getAbsolutePath();
                        break;
                    case "Lunch":
                        lunchImagePath = selectedFile.getAbsolutePath();
                        break;
                    case "Dinner":
                        dinnerImagePath = selectedFile.getAbsolutePath();
                        break;
                    case "Snacks":
                        snacksImagePath = selectedFile.getAbsolutePath();
                        break;
                    case "Tea":
                        teaImagePath = selectedFile.getAbsolutePath();
                        break;
                }
                saveImages(); // Save the file path
            }
        });
        foodPanel.add(uploadButton, foodConstraints);

        // Image label
        foodConstraints.gridx = 3;
        imageLabel.setPreferredSize(new Dimension(120, 120)); // Slightly increased size
        foodPanel.add(imageLabel, foodConstraints);

        // Description area
        foodConstraints.gridx = 0;
        foodConstraints.gridy++;
        foodConstraints.gridwidth = 4;
        foodConstraints.weighty = 1.0;
        JTextArea descriptionArea = new JTextArea(2, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        foodPanel.add(descriptionScrollPane, foodConstraints);

        // Add foodPanel to the main scrollable panel
        constraints.gridy = row; // Set the row for the main panel
        scrollablePanel.add(foodPanel, constraints);

        // Store references based on meal type
        switch (mealType) {
            case "Breakfast":
                txtBreakfastCost = costField;
                lblBreakfastImage = imageLabel;
                txtBreakfastDetails = descriptionArea;
                break;
            case "Lunch":
                txtLunchCost = costField;
                lblLunchImage = imageLabel;
                txtLunchDetails = descriptionArea;
                break;
            case "Dinner":
                txtDinnerCost = costField;
                lblDinnerImage = imageLabel;
                txtDinnerDetails = descriptionArea;
                break;
            case "Snacks":
                txtSnacksCost = costField;
                lblSnacksImage = imageLabel;
                txtSnacksDetails = descriptionArea;
                break;
            case "Tea":
                txtTeaCost = costField;
                lblTeaImage = imageLabel;
                txtTeaDetails = descriptionArea;
                break;
        }
    }

    private GridBagConstraints createConstraints(int gridx, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }

    private void updateImageLabel(JLabel label, File imageFile) {
        try {
            Image img = ImageIO.read(imageFile);
            img = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // Slightly increased size
            label.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean setupDatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/food_cost_db", "root", "Duvijaa18@mepco");
            return true; // Connection successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Connection failed
        }
    }

    private void fetchCosts() {
        try {
            String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snacks", "Tea"};
            JTextField[] fields = {txtBreakfastCost, txtLunchCost, txtDinnerCost, txtSnacksCost, txtTeaCost};
            JTextArea[] descriptionAreas = {txtBreakfastDetails, txtLunchDetails, txtDinnerDetails, txtSnacksDetails, txtTeaDetails};

            // Default description setup
            String[] defaultDescriptions = {
            		"Start your day with a plate full of sunshine and energy.",
                "Nourish your body with a delightful midday feast.",
                "Gather around for a heartwarming meal that warms both the body and soul.",
                "A little something to keep the hunger at bay!",
                "Sip your worries away with a cup of calm and comfort."
            };

            for (int i = 0; i < mealTypes.length; i++) {
                PreparedStatement stmt = connection.prepareStatement("SELECT cost FROM food_costs WHERE meal_type = ?");
                stmt.setString(1, mealTypes[i]);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double cost = rs.getDouble("cost");
                    fields[i].setText(String.valueOf(cost));
                    descriptionAreas[i].setText(defaultDescriptions[i]); // Set the default description
                } else {
                    fields[i].setText(""); // Clear the field if no value is found
                    descriptionAreas[i].setText(defaultDescriptions[i]); // Set default description
                }

                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching costs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveImages() {
        try (PrintWriter writer = new PrintWriter(new File(IMAGE_PATHS_FILE))) {
            writer.println(breakfastImagePath != null ? breakfastImagePath : "");
            writer.println(lunchImagePath != null ? lunchImagePath : "");
            writer.println(dinnerImagePath != null ? dinnerImagePath : "");
            writer.println(snacksImagePath != null ? snacksImagePath : "");
            writer.println(teaImagePath != null ? teaImagePath : "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving image paths: " + e.getMessage());
        }
    }

    private void loadImages() {
        File imagePathFile = new File(IMAGE_PATHS_FILE);
        if (imagePathFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(imagePathFile))) {
                breakfastImagePath = reader.readLine();
                lunchImagePath = reader.readLine();
                dinnerImagePath = reader.readLine();
                snacksImagePath = reader.readLine();
                teaImagePath = reader.readLine();

                // Set image labels to show images if paths are not blank
                setImageLabel(lblBreakfastImage, breakfastImagePath);
                setImageLabel(lblLunchImage, lunchImagePath);
                setImageLabel(lblDinnerImage, dinnerImagePath);
                setImageLabel(lblSnacksImage, snacksImagePath);
                setImageLabel(lblTeaImage, teaImagePath);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image paths: " + e.getMessage());
            }
        }
    }

    private void setImageLabel(JLabel label, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            updateImageLabel(label, new File(imagePath));
        } else {
            label.setIcon(null); // Clear the icon if path is empty
        }
    }

    private void updateCosts() {
        try {
            // Update costs for each meal type without altering image path or description
            updateCost("Breakfast", txtBreakfastCost.getText());
            updateCost("Lunch", txtLunchCost.getText());
            updateCost("Dinner", txtDinnerCost.getText());
            updateCost("Snacks", txtSnacksCost.getText());
            updateCost("Tea", txtTeaCost.getText());

            JOptionPane.showMessageDialog(this, "Costs updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating costs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCost(String mealType, String costInput) throws SQLException {
        if (!costInput.trim().isEmpty()) {
            double cost = Double.parseDouble(costInput.trim());

            // Update only the cost; keep image path and description unchanged
            PreparedStatement pstmt = connection.prepareStatement("UPDATE food_costs SET cost = ? WHERE meal_type = ?");
            pstmt.setDouble(1, cost);
            pstmt.setString(2, mealType);
            pstmt.executeUpdate();
            pstmt.close();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FoodCostManager manager = new FoodCostManager();
            manager.setVisible(true);
        });
    }
}