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
import javax.swing.JTextField;
import java.awt.Color;
public class MoneyGUI extends JFrame {
	
	    private JFrame thisFrame;
		private QuerySalesGUI querySalesGUI;
		private ShowSaleGUI showGUI;
		private Sale currentSale;
	    private Seller currentSeller;
	    private JTextField textaddMoney;
	    private JTextField textdeleteMoney;
	    private JLabel errorText = new JLabel();
		private Seller s;
		public MoneyGUI(Seller s) {
			this.s = s;
			this.setSize(new Dimension(604, 370));
			getContentPane().setLayout(null);
			
			errorText.setForeground(Color.RED);
			
			JLabel lblSaldo = new JLabel();
			lblSaldo.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.Saldo"));
			lblSaldo.setBounds(29, 41, 104, 20);
			getContentPane().add(lblSaldo);
			
			JLabel lbladdMoney = new JLabel();
			lbladdMoney.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.AddMoney"));
			lbladdMoney.setBounds(29, 114, 104, 20);
			getContentPane().add(lbladdMoney);
			
			JLabel lbldeleteMoney = new JLabel();
			lbldeleteMoney.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.DeleteMoney"));
			lbldeleteMoney.setBounds(285, 110, 104, 29);
			getContentPane().add(lbldeleteMoney);
			
			JLabel lblNewSal = new JLabel();
			lblNewSal.setText(Float.toString(s.getDirua()));
			lblNewSal.setBounds(203, 43, 145, 18);
			getContentPane().add(lblNewSal);
			
			textaddMoney = new JTextField();
			textaddMoney.setBounds(21, 157, 145, 29);
			getContentPane().add(textaddMoney);
			textaddMoney.setColumns(10);
			
			textdeleteMoney = new JTextField();
			textdeleteMoney.setBounds(268, 157, 145, 29);
			getContentPane().add(textdeleteMoney);
			textdeleteMoney.setColumns(10);
			
			JButton btnaddMoney = new JButton();
			btnaddMoney.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.AddMoney"));
			btnaddMoney.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
					errorText.setText("");
					int d = Integer.parseInt(textaddMoney.getText());
		            BLFacade facade = MainGUI.getBusinessLogic();
		            if (d <= 0) {
		                
		                errorText.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.Positive"));
		            }else {
		            	s.setDirua(d); 
		            	facade.updateMoney(s.getEmail(), d);
		            	lblNewSal.setText(Float.toString(s.getDirua()));
		            	textaddMoney.setText("");
		            }
		            
				
					}catch (NumberFormatException ex) {
						errorText.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.Baliozko"));
			        }
				}
			});
			btnaddMoney.setBounds(21, 196, 145, 46);
			getContentPane().add(btnaddMoney);
			
			JButton btndeleteMoney = new JButton();
			btndeleteMoney.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.DeleteMoney"));
			btndeleteMoney.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						errorText.setText("");

			            int c = Integer.parseInt(textdeleteMoney.getText());
			            if (c <= 0) {
			                errorText.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.Positive"));
			                return;
			            }
			            if (s.getDirua() >= c) {
			                BLFacade facade = MainGUI.getBusinessLogic();
			                facade.updateMoney(s.getEmail(), -c);
			                s.setDirua(-c);
			                lblNewSal.setText( Float.toString(s.getDirua()));
			                textdeleteMoney.setText("");
			            } else {
			            	errorText.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.NotEnoughFunds"));
			            }
			            
			        } catch (NumberFormatException ex) {
			        	errorText.setText(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.Baliozko"));
			           
			        }
					
				}
			});
			btndeleteMoney.setBounds(268, 196, 145, 46);
			getContentPane().add(btndeleteMoney);
			
			
			errorText.setBounds(21, 265, 392, 29);
			getContentPane().add(errorText);
			
			
		
		
		}
}
