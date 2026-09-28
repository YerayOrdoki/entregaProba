package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import businessLogic.BLFacade;
import domain.Sale;
import domain.Seller;

public class BasketGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private List<Sale> basketItems;
    private Seller currentUser;

    public BasketGUI(Seller user) { 
        this.currentUser = user;
        BLFacade facade = MainGUI.getBusinessLogic();
        
       
        this.basketItems = facade.getBasket(user.getEmail()); 
        
        setTitle(ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.MyBasket"));
        setSize(500, 400);
        setLayout(new BorderLayout());

        
        model = new DefaultTableModel(new Object[]{ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.Product"), ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.Price")}, 0);
        
       
        for(Sale s : basketItems) {
            model.addRow(new Object[]{s.getTitle(), s.getPrice()});
        }
        
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnBuy = new JButton(ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.Checkout"));
        btnBuy.addActionListener(e -> {
            if (basketItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.EmptyBasket"));
                return;
            }
            try {
                BLFacade facade1 = MainGUI.getBusinessLogic();
                
                facade1.buyMultipleSales(basketItems, currentUser);
                
            
                facade1.clearBasket(currentUser.getEmail());
                
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.SuccessBuy"));
                basketItems.clear();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Etiquetas").getString("BasketGUI.Error") + ex.getMessage());
            }
        });
        add(btnBuy, BorderLayout.SOUTH);
    }
}
