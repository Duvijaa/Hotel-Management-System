package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ReceptionistDashboard extends JFrame {
    
    // Background panel class
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load the background image
                backgroundImage = ImageIO.read(new File("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\hio.jpg")); // Change this to the path of your image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public ReceptionistDashboard() {
        // Set the title of the window
        setTitle("Receptionist Dashboard");
        //setJMenuBar(EmployeeMenubar.createMenuBar(this));

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        // Set the layout for the frame
        setLayout(new BorderLayout());

        // Create a label for the heading
        JLabel heading = new JLabel("Receptionist Dashboard", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Create a background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Add heading to the top of the background panel
        backgroundPanel.add(heading, BorderLayout.NORTH);

        // Create a JPanel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);

        // Create buttons
        JButton changePasswordButton = new JButton("Change Password");
        JButton roomBookingButton = new JButton("Room Booking");
        JButton customerDetailsButton = new JButton("Customer Details");
        JButton roomStatusButton = new JButton("Logout");

        // Set button bounds
        changePasswordButton.setBounds(270, 50, 150, 50);
        roomBookingButton.setBounds(270, 150, 150, 50);
        customerDetailsButton.setBounds(270, 250, 150, 50);
        roomStatusButton.setBounds(270, 350, 150, 50);

        // Set button background color
        changePasswordButton.setBackground(Color.lightGray);
        roomBookingButton.setBackground(Color.lightGray);
        customerDetailsButton.setBackground(Color.lightGray);
        roomStatusButton.setBackground(Color.lightGray);

        // Add action listeners
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new SetupNewPasswordForm();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        roomBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
             new HotelCheckInOut();
            }
        });

        customerDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HotelCustomerDetails();
            }
        });

        roomStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                
            }
        });
        // Add buttons to the button panel
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(roomBookingButton);
        buttonPanel.add(customerDetailsButton);
        buttonPanel.add(roomStatusButton);

        // Set background color of the button panel
        buttonPanel.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent color

        // Set preferred size for the button panel
        buttonPanel.setPreferredSize(new Dimension(450, 400));

        // Add button panel to the center of the background panel
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add background panel to the frame
        add(backgroundPanel, BorderLayout.CENTER);

        // Set the size of the window
        setSize(700, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceptionistDashboard());
    }
}