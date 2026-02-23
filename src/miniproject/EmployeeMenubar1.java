package miniproject;


import javax.swing.*;

//import firstPage.minproject1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeMenubar1 {




   public static JMenuBar createMenuBar(JFrame currentFrame) {
       JMenuBar menuBar = new JMenuBar();

       // Create Help menu
       JMenu helpMenu = new JMenu("Help");
       JMenuItem helpItem = new JMenuItem("Help Contents");
       helpItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Help Contents here..."));
       helpMenu.add(helpItem);
       
       JMenuItem aboutItem = new JMenuItem("About");
       aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "About this application..."));
       helpMenu.add(aboutItem);
       
       menuBar.add(helpMenu);

       // Create Options menu
       JMenu optionsMenu = new JMenu("Options");
       JMenuItem optionsItem = new JMenuItem("change password");
       optionsItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               currentFrame.dispose(); // Close the current frame
               try {
				new SetUpNewPassword1();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}// Create and show the home page
           }
       });
       optionsMenu.add(optionsItem);
       //optionsItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Options menu here..."));
       JMenuItem addItem = new JMenuItem("Add Employee");
       addItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               currentFrame.dispose(); // Close the current frame
               SwingUtilities.invokeLater(() -> {
                   EmployeeDetailsApp app = new EmployeeDetailsApp();
                   app.createAndShowGUI();
               });

           }
       });
       optionsMenu.add(addItem);
       JMenuItem remItem = new JMenuItem("Remove Employee");
       remItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               currentFrame.dispose(); // Close the current frame
               SwingUtilities.invokeLater(() -> {
                   EmployeeDetailsApp1 app = new EmployeeDetailsApp1();
                   app.createAndShowGUI();
               });

           }
           
       });
       optionsMenu.add(remItem);
       JMenuItem upItem = new JMenuItem("Update Employee");
       upItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               currentFrame.dispose(); // Close the current frame
               SwingUtilities.invokeLater(() -> {
                   EmployeeDetailsApp2 app = new EmployeeDetailsApp2();
                   app.createAndShowGUI();
               });

           }
       });
       optionsMenu.add(upItem);
       
       
       //optionsItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Options menu here..."));
       
       menuBar.add(optionsMenu);

       // Create Back menu
       JMenu backMenu = new JMenu("Back");
       JMenuItem backItem = new JMenuItem("Go Back");
       backItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               currentFrame.dispose(); // Close the current frame
               new MainApplication().showEmployeeManagementPage(); // Create and show the home page
           }
       });
       backMenu.add(backItem);
       JMenuItem logItem = new JMenuItem("Logout");
      // logItem.addActionListener(e -> System.exit(0));
       backMenu.add(logItem);
       
       menuBar.add(backMenu);
       logItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
        	   System.exit(0);
                // Close the current frame
               // Create and show the home page
           }
       });
       
       menuBar.add(backMenu);
       return menuBar;
   }
}
