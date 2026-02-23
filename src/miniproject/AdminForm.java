package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// Custom JPanel to set the background image
class ImagePanel extends JPanel {
    private BufferedImage backgroundImage;

    public ImagePanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath)); // Load the image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the image
        }
    }
}

// AdminForm class
class AdminForm extends JFrame {
    public AdminForm() {
        // Create the JFrame
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create the ImagePanel with the path to your background image
        ImagePanel backgroundPanel = new ImagePanel("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\das.jpg"); // Set your image path here
        backgroundPanel.setLayout(new BorderLayout()); // Set layout to BorderLayout

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Close");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenu clMenu = new JMenu("Options");
        JMenuItem chItem = new JMenuItem("Change Password");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the AdminForm
            }
        });
        chItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    new SetUpNewPassword2();
                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } // Close the AdminForm
            }
        });

        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        clMenu.add(chItem);
        menuBar.add(clMenu);
        setJMenuBar(menuBar);

        // Create a heading for the admin form
        JLabel headLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headLabel.setFont(new Font("Arial", Font.BOLD, 40));
        headLabel.setForeground(Color.WHITE);
        backgroundPanel.add(headLabel, BorderLayout.NORTH); // Add heading to background panel

        // Create a panel for buttons with GridBagLayout for centering
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        buttonPanel.setOpaque(false); // Make transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons fill in the horizontal space
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around buttons
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Start from the first row

        // Create buttons with larger size
        JButton roomCostButton = new JButton("Room Cost");
        roomCostButton.setBackground(Color.LIGHT_GRAY);
        //roomCostButton.setForeground(Color.white);
        roomCostButton.setPreferredSize(new Dimension(200, 60)); // Increase button size
        roomCostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RoomCostManagement();
            }
        });

        JButton employeeSalaryButton = new JButton("Employee Salary");
        employeeSalaryButton.setBackground(Color.LIGHT_GRAY);
        employeeSalaryButton.setPreferredSize(new Dimension(200, 60)); // Increase button size
        employeeSalaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
               
                new SalaryManagement();
            }
        });

        JButton foodCostButton = new JButton("Food Cost");
        foodCostButton.setPreferredSize(new Dimension(200, 60)); // Increase button size
        foodCostButton.setBackground(Color.LIGHT_GRAY);
        foodCostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                FoodCostManager manager = new FoodCostManager();
                manager.setVisible(true);
                
            }
        });

        JButton employeeDetailsButton = new JButton("Employee Details");
        employeeDetailsButton.setPreferredSize(new Dimension(200, 60)); // Increase button size
        employeeDetailsButton.setBackground(Color.LIGHT_GRAY);
        employeeDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new EmployeeDetailsApp3();
            }
        });

        // Add buttons to the panel using GridBagConstraints
        buttonPanel.add(roomCostButton, gbc);
        gbc.gridy++; // Move to next row
        buttonPanel.add(employeeSalaryButton, gbc);
        gbc.gridy++; // Move to next row
        buttonPanel.add(foodCostButton, gbc);
        gbc.gridy++; // Move to next row
        buttonPanel.add(employeeDetailsButton, gbc);

        // Add button panel to the Center
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add the background panel to the frame
        add(backgroundPanel);

        // Make the form visible
        setVisible(true);
    }
}
