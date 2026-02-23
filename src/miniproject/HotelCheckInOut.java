package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HotelCheckInOut extends JFrame {

    public HotelCheckInOut() {
        // Set up the frame
        setTitle("Hotel Check In/Out System");
        setSize(700, 500); // Set frame size to 700x500
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Use GridBagLayout for flexible layout

        // Set the custom JPanel with background image
        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout()); // Reset layout after setting the content pane

        // Create the heading
        JLabel headerLabel = new JLabel("Booking", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.black);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0); // Add vertical spacing around the header
        add(headerLabel, gbc);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Close");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenu clMenu = new JMenu("Options");
        JMenuItem chItem = new JMenuItem("Back");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the AdminForm
            }
        });
        chItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
				new ReceptionistDashboard();
            }
        });

        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        clMenu.add(chItem);
        menuBar.add(clMenu);
        setJMenuBar(menuBar);


        // Create Check In Button
        JButton checkInButton = new JButton("Check In");
        checkInButton.setBackground(Color.lightGray);
        checkInButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 1;
        add(checkInButton, gbc); // Add Check In button

        // Create Check Out Button
        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.setBackground(Color.lightGray);
        checkOutButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2;
        add(checkOutButton, gbc); // Add Check Out button below Check In button

        // Button action listeners
        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
                new RoomStatusChecking(); // Open Room Booking Login Form
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                FetchUserStatus form = new FetchUserStatus();
                form.createAndShowGUI(); // Open Fetch User Status Form
            }
        });

        // Set frame visibility
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    public static void main(String[] args) {
        new HotelCheckInOut(); // Start the application
    }

    // Custom JPanel class to paint the background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load the background image (ensure the path is correct)
                backgroundImage = new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\r.png").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}