package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import domain.*;
import businessLogic.BLFacade;

import java.awt.*;
import java.util.ResourceBundle;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserHistoryGUI extends JFrame {
    private JTable tableSales, tableBoughts;
    private DefaultTableModel modelSales, modelBoughts;
    private JButton btnReclamar = new JButton(ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Complaint"));
    private JButton btnValorar = new JButton(ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Review"));

    public UserHistoryGUI(Seller s) {
        BLFacade facade = MainGUI.getBusinessLogic();

        List<Sale> sales = facade.getSalesBySeller(s.getEmail());
        List<Sale> boughts = facade.getBoughtsBySeller(s.getEmail());

        setTitle("Historial: " + s.getName());
        setSize(700, 600);
        getContentPane().setLayout(new GridLayout(2, 1)); 

        JPanel panelSales = new JPanel(new BorderLayout());
        panelSales.setBorder(BorderFactory.createTitledBorder("Sales"));
        modelSales = new DefaultTableModel(new Object[]{
                ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Product"),
                ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Price"),
                ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Status")
        }, 0);

        for (Sale sale : sales) {
            modelSales.addRow(new Object[]{
                    sale.getTitle(),
                    sale.getPrice() + "€",
                    sale.getStatus() == 10
                            ? ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Sold")
                            : ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.OnSale")
            });
        }

        tableSales = new JTable(modelSales);
        panelSales.add(new JScrollPane(tableSales), BorderLayout.CENTER);

        JPanel panelBoughts = new JPanel(new BorderLayout());
        panelBoughts.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.MyBoughts")));
        modelBoughts = new DefaultTableModel(new Object[]{
                ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Product"),
                ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Price")
        }, 0);

        for (Sale bought : boughts) {
            modelBoughts.addRow(new Object[]{
                    bought.getTitle(),
                    bought.getPrice() + "€"
            });
        }

        tableBoughts = new JTable(modelBoughts);
        panelBoughts.add(new JScrollPane(tableBoughts), BorderLayout.CENTER);

        JPanel panelAccionesCompras = new JPanel(new FlowLayout());
        panelAccionesCompras.add(btnReclamar);
        panelAccionesCompras.add(btnValorar);
        panelBoughts.add(panelAccionesCompras, BorderLayout.SOUTH);

        btnReclamar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fila = tableBoughts.getSelectedRow();
                if (fila != -1) {
                    Sale ventaSeleccionada = boughts.get(fila);
                    JFrame recGUI = new ComplaintGUI(s, ventaSeleccionada);
                    recGUI.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.Select"));
                }
            }
        });

        btnValorar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fila = tableBoughts.getSelectedRow();
                if (fila != -1) {
                    Sale ventaSeleccionada = boughts.get(fila);
                    JFrame revGUI = new ReviewGUI(s, ventaSeleccionada);
                    revGUI.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("UserHistoryGUI.SelectASeller"));
                }
            }
        });

        getContentPane().add(panelSales);
        getContentPane().add(panelBoughts);
    }
}