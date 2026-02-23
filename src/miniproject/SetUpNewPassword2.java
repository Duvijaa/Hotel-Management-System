package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetUpNewPassword2 extends JFrame {
    private Connection connection;

    public SetUpNewPassword2() throws ClassNotFoundException {
        // Set up the frame
        setTitle("Setup New Password");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        //setJMenuBar(EmployeeMenubar.createMenuBar(this));

        // Use Custom BackgroundPanel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Create the heading
        JLabel headerLabel = new JLabel("Setup a New Password", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.white); // Set color to improve visibility on image
        headerLabel.setBounds(0, 20, 700, 30);
        backgroundPanel.add(headerLabel);

        // Create the panel for the form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(100, 80, 500, 350);
        formPanel.setOpaque(false);  // Make the form panel transparent
        formPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Close");
        JMenuItem logoutItem = new JMenuItem("Back");
        //JMenu clMenu = new JMenu("Options");
        //JMenuItem chItem = new JMenuItem("Change Password");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
                new AdminForm(); // Close the AdminForm
            }
        });
       

        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);


        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.white);
        
        usernameLabel.setBounds(10, 20, 150, 25);
        JTextField usernameField = new JTextField();
        usernameField.setBounds(160, 20, 300, 25);

        // Old Password field
        JLabel oldPasswordLabel = new JLabel("Old Password:");
        oldPasswordLabel.setForeground(Color.white);
        oldPasswordLabel.setBounds(10, 60, 150, 25);
        JPasswordField oldPasswordField = new JPasswordField();
        oldPasswordField.setBounds(160, 60, 300, 25);

        // New Password field
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setForeground(Color.white);
        newPasswordLabel.setBounds(10, 100, 150, 25);
        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setBounds(160, 100, 300, 25);

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setForeground(Color.white);
        confirmPasswordLabel.setBounds(10, 140, 150, 25);
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(160, 140, 300, 25);

        // Password criteria label with HTML for multiple lines
        JLabel criteriaLabel = new JLabel("<html>Password must contain at least 1 uppercase letter, " +
                "1 lowercase letter,<br>1 digit, and 1 special character.</html>");
        criteriaLabel.setBounds(10, 180, 475, 75);
        criteriaLabel.setForeground(Color.red);  // Change the color for visibility
        formPanel.add(criteriaLabel);

        // Confirm button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(160, 260, 150, 30);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String oldPassword = new String(oldPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Simple validation example
                if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(SetUpNewPassword2.this,
                            "All fields must be filled.");
                } else if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(SetUpNewPassword2.this,
                            "New password and confirm password do not match.");
                    usernameField.setText("");
                    oldPasswordField.setText("");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } else if (!isValidPassword(newPassword)) {
                    JOptionPane.showMessageDialog(SetUpNewPassword2.this,
                            "New password does not meet the criteria.");
                    usernameField.setText("");
                    oldPasswordField.setText("");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } else {
                    // Call method to update password in the database
                    if (updatePassword(username, oldPassword, newPassword)) {
                        JOptionPane.showMessageDialog(SetUpNewPassword2.this,
                                "Password changed successfully!");
                        usernameField.setText("");
                        oldPasswordField.setText("");
                        newPasswordField.setText("");
                        confirmPasswordField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(SetUpNewPassword2.this,
                                "Error changing password. Please check your username and old password.");
                        usernameField.setText("");
                        oldPasswordField.setText("");
                        newPasswordField.setText("");
                        confirmPasswordField.setText("");
                    }
                }
            }
        });

        // Add components to the form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(oldPasswordLabel);
        formPanel.add(oldPasswordField);
        formPanel.add(newPasswordLabel);
        formPanel.add(newPasswordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        formPanel.add(confirmButton);

        // Add the form panel to the center of the background panel
        backgroundPanel.add(formPanel);

        // Set frame visibility
        setVisible(true);
        
        // Initialize database connection
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() throws ClassNotFoundException {
        try {
            // Change URL, username, and password according to your database setup
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/loginpage";
            String user = "root";
            String password = "Duvijaa18@mepco";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
        }
   }

    private boolean updatePassword(String username, String oldPassword, String newPassword) {
        String sql = "UPDATE logintable SET password1 = ? WHERE username = ? AND password1 = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            statement.setString(3, oldPassword);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0; // Return true if password was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to validate password based on criteria
    private boolean isValidPassword(String password) {
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // Custom JPanel with Background Image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("C://Users//Duvijaa//OneDrive//Pictures//h.jpg").getImage();
            if (backgroundImage == null) {
                System.out.println("Background image not found!");
            }
            setPreferredSize(new Dimension(700, 600));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.out.println("Background image not loaded!");
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(700, 600);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SetUpNewPassword2();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}