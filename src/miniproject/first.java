package miniproject;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class first extends JFrame implements MouseListener, KeyListener {
    JLabel head, tail;

    public first() {
        setSize(700, 600);
        setTitle("Hotel Management Demo");
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.black);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\hotel4.jpg");
        JLabel imgLabel = new JLabel(img);
        imgLabel.addMouseListener(this);
        imgLabel.setFocusable(true);
        imgLabel.addKeyListener(this);
        add(imgLabel, BorderLayout.CENTER);

        setResizable(false);

        head = new JLabel("", SwingConstants.CENTER);
        head.setForeground(Color.PINK);
        head.setFont(new Font("Times New Roman", Font.BOLD, 40));
        add(head, BorderLayout.NORTH);

        tail = new JLabel("", SwingConstants.CENTER);
        tail.setForeground(Color.pink);
        tail.setFont(new Font("Arial", Font.PLAIN, 20));
        add(tail, BorderLayout.SOUTH);

        setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        displayTitle("StarLit Haven", 300, () -> displayPrompt("Press Enter To Continue", 150));
    }

    private void displayTitle(String title, int delay, Runnable onComplete) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (char c : title.toCharArray()) {
                    publish(String.valueOf(c));
                    Thread.sleep(delay);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String c : chunks) {
                    head.setText(head.getText() + c);
                }
            }

            @Override
            protected void done() {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        };

        worker.execute();
    }

    private void displayPrompt(String prompt, int delay) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (char c : prompt.toCharArray()) {
                    publish(String.valueOf(c));
                    Thread.sleep(delay);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String c : chunks) {
                    tail.setText(tail.getText() + c);
                }
            }
        };

        worker.execute();
    }

    private void openUserRoleSelection() {
        new LoginForm();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            this.dispose();
            openUserRoleSelection();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        first frame = new first();
        frame.requestFocusInWindow();
    }
}

class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("C:\\Users\\Duvijaa\\OneDrive\\Pictures\\im.jpg");
        backgroundPanel.setLayout(new GridBagLayout());

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernameLabel.setForeground(Color.black);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordLabel.setForeground(Color.black);
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        backgroundPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        backgroundPanel.add(passwordField, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        backgroundPanel.add(loginButton, gbc);

        add(backgroundPanel);
        setVisible(true);
    }

    protected void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Database connection variables
        String url = "jdbc:mysql://localhost:3306/loginpage"; // Change to your database URL
        String dbUsername = "root"; // Database username
        String dbPassword = "Duvijaa18@mepco"; // Database password

        String query = "SELECT password1 FROM logintable WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password1");

                if (storedPassword.equals(password)) {
                    if (username.equals("adm11")) {
                    	dispose();
                        new AdminForm();
                    } else if (username.equals("rec11")) {
                    	dispose();
                        new ReceptionistDashboard();
                    } else if (username.equals("man11")) {
                    	dispose();
                    	MainApplication app = new MainApplication();
                        app.createAndShowGUI();
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

