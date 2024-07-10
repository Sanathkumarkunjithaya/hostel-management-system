/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.sql.*;
import Project.ConnectionProvider;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.util.Calendar;
/**
 *
 * @author kunji
 */
public class StudentFee extends javax.swing.JFrame {
    
private void printChallan() {
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(new Printable() {
        @Override
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
            if (page > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());

            // Drawing the title
            Font titleFont = new Font("Arial", Font.BOLD, 20);
            g2d.setFont(titleFont);
            g2d.drawString("Sahyadri Educational Institution", 100, 100);

Calendar calendar = Calendar.getInstance();
int day = calendar.get(Calendar.DAY_OF_MONTH);
int month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so adding 1 to get the correct month
int year = calendar.get(Calendar.YEAR);

// Drawing the date within boxes dynamically
g2d.drawRect(100, 120, 40, 30); // Day box (slightly wider)
g2d.drawString(String.format("%02d", day), 110, 140);
g2d.drawRect(150, 120, 40, 30); // Month box (moved right and slightly wider)
g2d.drawString(String.format("%02d", month), 160, 140);
g2d.drawRect(200, 120, 60, 30); // Year box (moved right and wider)
g2d.drawString(String.valueOf(year), 210, 140);

            // Drawing the serial number from the backend table
            String serialNumber = ""; // Initialize serial number
            try {
                Connection con = ConnectionProvider.getCon();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT MAX(serial_number) FROM fees"); // Assuming serial number is stored in the 'fees' table
                if (rs.next()) {
                    serialNumber = rs.getString(1); // Assuming serial number is stored in the first column of the result set
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Font serialFont = new Font("Arial", Font.PLAIN, 12);
            g2d.setFont(serialFont);
            g2d.drawString("Serial Number: " + serialNumber, 20, 20); // Adjust the position as needed

            // Drawing additional details
            g2d.drawString("Student Name: " + jTextField2.getText(), 100, 180);
            g2d.drawString("USN: " + jTextField1.getText(), 100, 200);
            g2d.drawString("Year: " + jTextField4.getText(), 100, 220);

            // Drawing the amount
            double amount = Double.parseDouble(jTextField7.getText());
            String amountInWords = convertAmountToWords(amount);
            Font amountFont = new Font("Arial", Font.PLAIN, 14);
            g2d.setFont(amountFont);
            g2d.drawString("Amount: " + amount, 100, 240);
            g2d.drawString("Amount in Words: " + amountInWords, 100, 260);

            // Space for administrator's signature
            Font signatureFont = new Font("Arial", Font.PLAIN, 12);
            g2d.setFont(signatureFont);
            g2d.drawString("Administrator's Signature:", 100, 300);

            return PAGE_EXISTS;
        }
    });

    boolean ok = job.printDialog();
    if (ok) {
        try {
            job.print();
        } catch (PrinterException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error printing: " + ex.getMessage());
        }
    }
}
private String convertAmountToWords(double amount) {
    // Array containing words for numbers from 0 to 19
    String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", 
                      "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", 
                      "Eighteen", "Nineteen"};
    // Array containing words for tens multiples
    String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    
    // If the amount is zero, return "Zero Rupees"
    if (amount == 0) {
        return "Zero Rupees";
    }
    
    // Convert the amount to words
    StringBuilder words = new StringBuilder();
    
    // Convert crore (if applicable)
    int crore = (int)(amount / 10000000);
    if (crore > 0) {
        words.append(convert(crore)).append(" Crore ");
        amount %= 10000000;
    }
    
    // Convert lakh (if applicable)
    int lakh = (int)(amount / 100000);
    if (lakh > 0) {
        words.append(convert(lakh)).append(" Lakh ");
        amount %= 100000;
    }
    
    // Convert thousand (if applicable)
    int thousand = (int)(amount / 1000);
    if (thousand > 0) {
        words.append(convert(thousand)).append(" Thousand ");
        amount %= 1000;
    }
    
    // Convert hundreds (if applicable)
    int hundred = (int)(amount / 100);
    if (hundred > 0) {
        words.append(convert(hundred)).append(" Hundred ");
        amount %= 100;
    }
    
    // Convert tens and units
    if (amount > 0) {
        if (words.length() != 0) {
            words.append("and ");
        }
        if (amount < 20) {
            words.append(units[(int) amount]);
        } else {
            words.append(tens[(int) (amount / 10)]);
            if ((amount % 10) > 0) {
                words.append(" ").append(units[(int) (amount % 10)]);
            }
        }
    }
    
    words.append(" Rupees Only");
    
    return words.toString();
}

// Method to convert numbers less than 1000 to words
private String convert(int number) {
    String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    
    StringBuilder words = new StringBuilder();
    
    if (number > 99) {
        words.append(units[number / 100]).append(" Hundred ");
        number %= 100;
    }
    if (number > 19) {
        words.append(tens[number / 10]).append(" ");
        number %= 10;
    }
    if (number > 0) {
        words.append(units[number]);
    }
    
    return words.toString();
}


private boolean isPaymentMade(String USN) {
    try {
        Connection con = ConnectionProvider.getCon();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM fees WHERE USN='" + USN + "'");

        // If there is a result, it means payment is already made for this student
        return rs.next();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error checking payment status: " + e.getMessage());
        return false; 
    }
}
      private boolean paymentMade = false;
            public void clear(){
                jTextField1.setEditable(true);
                jTextField1.setText("");
                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                jTextField5.setText("");
                jTextField6.setText("");
                jTextField7.setText("");
                DefaultTableModel dtm=(DefaultTableModel) jTable1.getModel();
                dtm.setRowCount(0);

}
public void tableDetails() {
    DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
    dtm.setRowCount(0);

    try {
        Connection con = ConnectionProvider.getCon();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Database connection is null");
            return;
        }

        String query = "SELECT * FROM fees ORDER BY month";
        try (PreparedStatement st = con.prepareStatement(query)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                dtm.addRow(new Object[]{
                        rs.getInt("serial_number"),
                        rs.getString("USN"),
                        rs.getString("month"),
                        rs.getDouble("amount_paid")
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching data from fees table: " + e.getMessage());
        e.printStackTrace();
    }
}

                private void insertDataToBackendTable(String usn, String month, double amount) {
        try {
            Connection con = ConnectionProvider.getCon();
            Statement st = con.createStatement();
            String query = "INSERT INTO fees (USN, month, amount_paid) VALUES ('" + usn + "', '" + month + "', " + amount + ")";
            st.executeUpdate(query);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Creates new form StudentFee
     */
    public StudentFee() {
        initComponents();
        jTextField2.setEditable(false);
        jTextField4.setEditable(false);
        jTextField5.setEditable(false);
        jTextField6.setEditable(false);
        jTextField3.setEditable(false);
            jTextField1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jButton1.doClick(); // Simulate a click on the "Search" button
        }
    });
        getContentPane().setBackground(new java.awt.Color(204,204,255));
        tableDetails();  // Call tableDetails() when the frame is created
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                // Initial data if needed
            },
            new String [] {
                "Serial Number", "USN", "Date", "Amount"
            }
        ));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(480, 150));
        setUndecorated(true);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel1.setText("USN");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel2.setText("Name");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel4.setText("Year");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel5.setText("Mobile Number");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel6.setText("Room Number");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel7.setText("Amount");

        jTextField1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jTextField5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jTextField6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jTextField7.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(102, 204, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jButton2.setText("Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 102, 102));
        jButton3.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jButton3.setText("Clear");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 102, 51));
        jButton4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jButton4.setText("<- Back");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Serial Number", "USN", "Date", "Amount"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setHeaderValue("Serial Number");
            jTable1.getColumnModel().getColumn(1).setHeaderValue("USN");
            jTable1.getColumnModel().getColumn(2).setHeaderValue("Date");
            jTable1.getColumnModel().getColumn(3).setHeaderValue("Amount");
        }

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel8.setText("Date");

        jTextField3.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addGap(49, 49, 49)))
                        .addGap(59, 59, 59)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                            .addComponent(jTextField2)
                            .addComponent(jTextField6)
                            .addComponent(jTextField4)
                            .addComponent(jTextField5)
                            .addComponent(jTextField7)
                            .addComponent(jTextField3))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 97, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2)
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed
private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {
    tableDetails();
}
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
    paymentMade = false; 
    if (!paymentMade) {
        String USN = jTextField1.getText();
  
SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
String month = dFormat.format(new Date());

        try {
            Connection con = ConnectionProvider.getCon();
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM student WHERE USN='" + USN + "'");
            if (rs.first()) {
                jTextField1.setEditable(false);
                jTextField2.setText(rs.getString("name"));
                jTextField4.setText(rs.getString("year"));
                jTextField5.setText(rs.getString("number"));
                String roomNumber = rs.getString("rno");
                jTextField6.setText(roomNumber);
                jTextField3.setText(month);

                ResultSet roomRs = st.executeQuery("SELECT * FROM room WHERE number='" + roomNumber + "'");
                if (roomRs.first()) {
                    double fees = roomRs.getDouble("fees");
                    jTextField7.setText(String.valueOf(fees));
                } else {
                    JOptionPane.showMessageDialog(null, "Room details not found");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Student Does Not exist");
                clear();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Payment already made.");
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    this.setVisible(false);
    // Release resources associated with the frame
    this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
     // TODO add your handling code here:
    if (paymentMade) {
        JOptionPane.showMessageDialog(null, "Payment already made.");
        return;  
    }

    String USN = jTextField1.getText();
    String month = jTextField3.getText();
    
    try {
        double amount = Double.parseDouble(jTextField7.getText());
        if (isPaymentMade(USN)) {
            JOptionPane.showMessageDialog(null, "Payment already made.");
            return;  
        }

        insertDataToBackendTable(USN, month, amount);
        paymentMade = true;
        
        JOptionPane.showMessageDialog(null, "Data saved successfully!");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid amount format. Please enter a valid number.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
    }
    printChallan();
    clear();
    tableDetails();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
            clear();
    tableDetails();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StudentFee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StudentFee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StudentFee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StudentFee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StudentFee().setVisible(true);
                StudentFee studentFee = new StudentFee();
                studentFee.setVisible(true);
                studentFee.tableDetails();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables

    private int getLatestSerialNumber() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
