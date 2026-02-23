package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceptionistLoginForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public ReceptionistLoginForm() {
        // Set up the frame
        setTitle("Receptionist Login Form");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Initialize the background panel and add it to the frame
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // Create the heading
        JLabel headerLabel = new JLabel("Receptionist Login Form", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.black);
        headerLabel.setBounds(0, 20, 700, 30);
        backgroundPanel.add(headerLabel);

        // Create the panel for the form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(200, 80, 300, 180);
        formPanel.setOpaque(false); // Make formPanel transparent to see background
        formPanel.setBorder(BorderFactory.createLineBorder(Color.white, 1));

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 20, 100, 25);
        usernameField = new JTextField();
        usernameField.setBounds(120, 20, 150, 25);
        usernameLabel.setForeground(Color.black);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 100, 25);
        passwordField = new JPasswordField();
        passwordField.setBounds(120, 60, 150, 25);
        passwordLabel.setForeground(Color.black);

        

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(120, 100, 150, 30);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate the credentials against the database
                if (validateLogin(username, password)) {
                    new ReceptionistDashboard(); // Open the dashboard
                    dispose(); // Close the login form
                } else {
                    JOptionPane.showMessageDialog(ReceptionistLoginForm.this,
                            "Invalid credentials, please try again.");
                }
            }
        });

        // Add components to the form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);

        // Add the form panel to the center of the background panel
        backgroundPanel.add(formPanel);

        // Set frame visibility
        setVisible(true);

        // Initialize the database connection
        initializeDatabase();
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // Load the background image
            backgroundImage = new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\rec.jpg.png").getImage(); // Change this path accordingly
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image, scaling it to the panel's dimensions
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initializeDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/loginpage";
            String user = "root";
            String password = "Duvijaa18@mepco";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM logintable WHERE username = ? AND password1 = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Return true if a record is found
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // No matching record found
    }

    public static void main(String[] args) {
        new ReceptionistLoginForm();
    }
}