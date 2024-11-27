import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BillingDetailsForm extends JFrame {
    
    // Swing components
    private JLabel lblName, lblAddress, lblAmount, lblDate;
    private JTextField txtName, txtAddress, txtAmount;
    private JDateChooser txtDate; // JDateChooser for selecting date (requires JCalendar library)
    private JButton btnSubmit;
    
    // JDBC components
    private Connection connection;
    private PreparedStatement pst;
    
    // Constructor to setup the form
    public BillingDetailsForm() {
        // JFrame settings
        setTitle("Billing Details");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        // Initialize components
        lblName = new JLabel("Customer Name:");
        lblAddress = new JLabel("Address:");
        lblAmount = new JLabel("Amount:");
        lblDate = new JLabel("Date:");
        
        txtName = new JTextField(20);
        txtAddress = new JTextField(20);
        txtAmount = new JTextField(20);
        txtDate = new JDateChooser(); // Date picker component
        
        btnSubmit = new JButton("Submit");

        // Add components to the frame
        add(lblName);
        add(txtName);
        add(lblAddress);
        add(txtAddress);
        add(lblAmount);
        add(txtAmount);
        add(lblDate);
        add(txtDate);
        add(btnSubmit);
        
        // Event handling for the button
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBillingDetails();
            }
        });
        
        // Initialize database connection
        connectToDatabase();
    }

    // Method to connect to the database
    private void connectToDatabase() {
        try {
            // Database URL, username, password (modify according to your setup)
            String url = "jdbc:mysql://localhost:3306/billing_db";
            String username = "root";
            String password = "password";
            
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to save billing details to the database
    private void saveBillingDetails() {
        String customerName = txtName.getText();
        String address = txtAddress.getText();
        String amountText = txtAmount.getText();
        java.util.Date date = txtDate.getDate();
        
        // Validate inputs
        if (customerName.isEmpty() || address.isEmpty() || amountText.isEmpty() || date == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            // SQL query to insert the billing details into the database
            String query = "INSERT INTO billing_details (customer_name, customer_address, amount, date) VALUES (?, ?, ?, ?)";
            pst = connection.prepareStatement(query);

            pst.setString(1, customerName);
            pst.setString(2, address);
            pst.setDouble(3, amount);
            pst.setDate(4, new java.sql.Date(date.getTime()));

            // Execute the query
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Billing details saved successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save billing details.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Please try again.");
        }
    }

    public static void main(String[] args) {
        // Create and display the form
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BillingDetailsForm form = new BillingDetailsForm();
                form.setVisible(true);
            }
        });
    }
}
