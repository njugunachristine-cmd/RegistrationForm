import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationForm extends JFrame {

    // Form fields
    JTextField txtName, txtMobile;
    JTextArea txtAddress;
    JRadioButton rbMale, rbFemale;
    JComboBox<String> cmbDay, cmbMonth, cmbYear;
    JCheckBox chkTerms;
    JButton btnSubmit, btnReset;
    JTable table;
    DefaultTableModel model;

    public RegistrationForm() {
        setTitle("Registration Form");
        setSize(950, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // LEFT PANEL - FORM
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "User Details", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtName, gbc);

        // Mobile
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mobile:"), gbc);
        txtMobile = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtMobile, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Gender:"), gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbMale); bg.add(rbFemale);
        genderPanel.add(rbMale); genderPanel.add(rbFemale);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(genderPanel, gbc);

        // DOB
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("DOB:"), gbc);

        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) days[i - 1] = String.valueOf(i);
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        int start = 1980, end = 2030;
        String[] years = new String[(end - start) + 1];
        for(int i=start,k=0;i<=end;i++,k++) years[k]=String.valueOf(i);
        cmbDay = new JComboBox<>(days);
        cmbMonth = new JComboBox<>(months);
        cmbYear = new JComboBox<>(years);
        dobPanel.add(cmbDay); dobPanel.add(cmbMonth); dobPanel.add(cmbYear);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(dobPanel, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane spAddress = new JScrollPane(txtAddress);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(spAddress, gbc);

        // Terms
        gbc.gridx = 1; gbc.gridy = 5;
        chkTerms = new JCheckBox("Accept Terms & Conditions");
        formPanel.add(chkTerms, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel();
        btnSubmit = new JButton("Submit");
        btnReset = new JButton("Reset");
        btnPanel.add(btnSubmit); btnPanel.add(btnReset);
        formPanel.add(btnPanel, gbc);

        // RIGHT PANEL - TABLE
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID","Name","Mobile","Gender","DOB","Address"});
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(22);
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Registered Users", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16)));

        // Add panels to main
        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(tablePane, BorderLayout.CENTER);

        // Load table data
        loadTableData();

        // Action Listeners
        btnSubmit.addActionListener(e -> submitForm());
        btnReset.addActionListener(e -> resetForm());
    }

    private void submitForm() {
        if (!chkTerms.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please accept the terms!");
            return;
        }

        String name = txtName.getText();
        String mobile = txtMobile.getText();
        String gender = rbMale.isSelected() ? "Male" : rbFemale.isSelected() ? "Female" : "";
        String dob = cmbYear.getSelectedItem() + "-" +
                (cmbMonth.getSelectedIndex()+1) + "-" +
                cmbDay.getSelectedItem();
        String address = txtAddress.getText();

        if(name.isEmpty() || mobile.isEmpty() || gender.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO users (name, mobile, gender, dob, address) VALUES (?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, mobile);
            pst.setString(3, gender);
            pst.setString(4, dob);
            pst.setString(5, address);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Inserted Successfully!");
            loadTableData();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadTableData() {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.prepareStatement("SELECT * FROM users").executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("mobile"),
                        rs.getString("gender"),
                        rs.getString("dob"),
                        rs.getString("address")
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        txtName.setText("");
        txtMobile.setText("");
        txtAddress.setText("");
        rbMale.setSelected(false);
        rbFemale.setSelected(false);
        chkTerms.setSelected(false);
        cmbDay.setSelectedIndex(0);
        cmbMonth.setSelectedIndex(0);
        cmbYear.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}
