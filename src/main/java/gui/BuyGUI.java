package gui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import businessLogic.BLFacade;

import javax.swing.JLabel;

import domain.*;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.awt.Dimension;
import java.awt.event.ActionEvent;



public class BuyGUI extends JFrame {
	private JFrame thisFrame;
	private QuerySalesGUI querySalesGUI;
	private ShowSaleGUI showGUI;
	private Sale currentSale;
    private Seller currentSeller;
	
	public BuyGUI(Sale s, Seller curr, QuerySalesGUI querySalesGUI, ShowSaleGUI showGUI) {
		thisFrame = this;
		this.showGUI = showGUI;
		this.currentSale = s;
		this.currentSeller = curr;
		this.querySalesGUI = querySalesGUI;
		this.setVisible(true);
		this.setSize(new Dimension(604, 370));
		getContentPane().setLayout(null);
		
		JLabel lblProduktua = new JLabel("Produktua:");
		lblProduktua.setBounds(41, 35, 96, 25);
		lblProduktua.setText(ResourceBundle.getBundle("Etiquetas").getString("BuyGUI.Product"));
		getContentPane().add(lblProduktua);
		
		JLabel lblPrezioa = new JLabel("Prezioa:");
		lblPrezioa.setBounds(41, 89, 46, 14);
		lblPrezioa.setText(ResourceBundle.getBundle("Etiquetas").getString("BuyGUI.Price"));
		getContentPane().add(lblPrezioa);
		
		JLabel price = new JLabel(s.getTitle().toString());
		price.setBounds(207, 40, 191, 14);
		getContentPane().add(price);
		
		double pr= s.getPrice();
		String str1 = Double.toString(pr);
		JLabel lblNewLabel_1 = new JLabel(str1+"€");
		lblNewLabel_1.setBounds(207, 99, 191, 14);
		getContentPane().add(lblNewLabel_1);
		
		JButton btnErosi = new JButton("Erosi");
		btnErosi.setText(ResourceBundle.getBundle("Etiquetas").getString("BuyGUI.Buy"));
		btnErosi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
				currentSeller = facade.getSellerByEmail(currentSeller.getEmail());
				float p = currentSale.getPrice();
				if(currentSeller.getDirua() < p) {
					System.out.println("Ez duzu saldo nahikoa");
					return;
				}
				facade.buySale(currentSale, currentSeller);
				currentSeller.setDirua(-p);
				if (querySalesGUI != null) {
                    querySalesGUI.refresh();
                }
				thisFrame.setVisible(false);
				thisFrame.dispose();
				
				 if (showGUI != null) {
	                    showGUI.setVisible(false);
	                    showGUI.dispose();
	                }

				
			}
		});
		btnErosi.setBounds(48, 185, 89, 23);
		getContentPane().add(btnErosi);
		
		JButton btnAtzera = new JButton("Atzera");
		btnAtzera.setText(ResourceBundle.getBundle("Etiquetas").getString("Close"));
		btnAtzera.setBounds(226, 185, 89, 23);
		btnAtzera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);			}
		});
		getContentPane().add(btnAtzera);
	}
}
