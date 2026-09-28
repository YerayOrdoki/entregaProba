package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Complaint;
import domain.Sale;
import domain.Seller;

public class AdminComplaintsGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tabla;
    private JTextArea complaintText;
    private JButton btnMarkManaged;

    private List<Complaint> complaints = new ArrayList<Complaint>();

    public AdminComplaintsGUI() {
        setTitle("Complaints pendientes");
        setSize(800, 500);
        setLocationRelativeTo(null);

        tabla = new DefaultTableModel(
            new Object[] { "Producto", "Vendedor", "Comprador", "Fecha", "Gestionada" }, 
            0
        );

        table = new JTable(tabla);
        complaintText = new JTextArea();
        complaintText.setEditable(false);
        complaintText.setLineWrap(true);
        complaintText.setWrapStyleWord(true);

        btnMarkManaged = new JButton("Marcar como gestionada");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Queja"));
        bottomPanel.add(new JScrollPane(complaintText), BorderLayout.CENTER);
        bottomPanel.add(btnMarkManaged, BorderLayout.SOUTH);

        setLayout(new GridLayout(2, 1));
        add(new JScrollPane(table));
        add(bottomPanel);

        loadComplaints();

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();

            if (row >= 0 && row < complaints.size()) {
                Complaint c = complaints.get(row);
                complaintText.setText(c.getMessage());
            }
        });

        btnMarkManaged.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                return;
            }

            Complaint c = complaints.get(row);

            BLFacade facade = MainGUI.getBusinessLogic();
            facade.markComplaintAsManaged(c.getID());

            JOptionPane.showMessageDialog(this, "Queja marcada como gestionada");

            loadComplaints();
            complaintText.setText("");
        });
    }

    private void loadComplaints() {
        BLFacade facade = MainGUI.getBusinessLogic();
        complaints = facade.getUnmanagedComplaints();
        tabla.setRowCount(0);
        for (Complaint c : complaints) {
            Sale sale = c.getSale();
            Seller complainant = c.getComplainant();
            String productTitle = "";
            String sellerEmail = "";
            String complainantEmail = "";
            String managedText = "";
            if (sale != null) {
                productTitle = sale.getTitle();
                if (sale.getSeller() != null) {
                    sellerEmail = sale.getSeller().getEmail();
                }
            }
            if (complainant != null) {
                complainantEmail = complainant.getEmail();
            }
            if (c.isManaged()) {
                managedText = "Sí";
            } else {
                managedText = "No";
            }
            tabla.addRow(new Object[] {
                productTitle,
                sellerEmail,
                complainantEmail,
                c.getDate(),
                managedText
            });
        }
    }
}