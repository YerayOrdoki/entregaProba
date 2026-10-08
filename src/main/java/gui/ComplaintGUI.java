package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import domain.*;
import businessLogic.BLFacade;

public class ComplaintGUI extends JFrame {
    private JTextArea textArea = new JTextArea();
    private JButton btnSend = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.Send"));

    public ComplaintGUI(Seller user, Sale sale) {
        setTitle("Complaint: " + sale.getTitle());
        setSize(400, 300);
        setLayout(new BorderLayout());
        
        textArea.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.Write")));
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        btnSend.addActionListener(e -> {
            String msg = textArea.getText().trim();
            if(!msg.isEmpty()) {
                BLFacade facade = MainGUI.getBusinessLogic();
                facade.saveComplaint(msg, user, sale);
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.Sent"));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.NotEmpty"));
            }
        });
        add(btnSend, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }
}
