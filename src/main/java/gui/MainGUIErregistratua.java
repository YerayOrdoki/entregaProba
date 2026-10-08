package gui;

/**
 * @author Software Engineering teachers
 */


import javax.swing.*;

import businessLogic.BLFacade;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import domain.Seller; 

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainGUIErregistratua extends JFrame {
	
	private Seller currentUser;
    private String sellerMail;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;
	private JButton btnProfile;
	private JButton btndirua;
	private JButton btnEskaeraEgin;
	private JButton btnCarrito;

    private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
	}
	public Seller getCurrentUser() {
		return this.currentUser;
	}
	 
	public static void setBussinessLogic (BLFacade facade){
		appFacadeInterface=facade;
	}
	protected JLabel jLabelSelectOption;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton_2;
	private JPanel panel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnhistory;

	
	/**
	 * This is the default constructor
	 */
	public MainGUIErregistratua(String mail, Seller curr) {
		super();
		this.currentUser = curr;

		this.sellerMail=mail;
		
		this.setSize(666, 494);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setBounds(0, 1, 650, 40);
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		btnProfile = new JButton();
		btnProfile.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.ChangeProfile"));
		
		btndirua = new JButton(); 
		btndirua.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Money"));
		
		rdbtnNewRadioButton = new JRadioButton("English");
		rdbtnNewRadioButton.setBounds(360, 7, 57, 21);
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton);
		
		rdbtnNewRadioButton_1 = new JRadioButton("Euskara");
		rdbtnNewRadioButton_1.setBounds(216, 7, 61, 21);
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		rdbtnNewRadioButton_2 = new JRadioButton("Castellano");
		rdbtnNewRadioButton_2.setBounds(282, 7, 73, 21);
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_2);
	
		panel = new JPanel();
		panel.setBounds(0, 340, 650, 113);
		panel.setLayout(null);
		panel.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton);
		
		
		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setBounds(0, 52, 650, 70);
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateSaleGUI(sellerMail);
				a.setVisible(true);
			}
		});
		selectCurrentLanguage();
		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setBounds(0, 119, 650, 70);
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI(sellerMail, curr);

				a.setVisible(true);
			}
		});
		
		jContentPane = new JPanel();
		jContentPane.setLayout(null);
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(panel);
		
		
		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
		        Seller m = facade.getSellerByEmail(curr.getEmail());
				JFrame a = new ProfileGUI(m);
				a.setVisible(true);
			}
		});
		btnProfile.setBounds(21, 33, 163, 20);
		panel.add(btnProfile);
		
		
		btndirua.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
				Seller m = facade.getSellerByEmail(curr.getEmail());
				JFrame a = new MoneyGUI(m);
				a.setVisible(true);
				
			}
		});
		btndirua.setBounds(444, 35, 174, 20);
		panel.add(btndirua);
		
		btnhistory = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUIHistory")); 
		btnhistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
		        Seller updatedSeller = facade.getSellerByEmail(curr.getEmail());

		        UserHistoryGUI historyWin = new UserHistoryGUI(updatedSeller);
		        historyWin.setVisible(true);
			}
		});
		btnhistory.setBounds(226, 35, 174, 20);
		panel.add(btnhistory);
		
		btnCarrito = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Carrito")); //$NON-NLS-1$ //$NON-NLS-2$
		btnCarrito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame basketWin = new BasketGUI(currentUser); 
		        basketWin.setVisible(true);
			}
		});
		btnCarrito.setBounds(282, 79, 89, 23);
		panel.add(btnCarrito);
		
		
		
		
		setContentPane(jContentPane);
		
		btnEskaeraEgin = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Eskaerak")); //$NON-NLS-1$ //$NON-NLS-2$
		btnEskaeraEgin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainEskaerakGUI eskaeraGUI = new MainEskaerakGUI(currentUser);
				eskaeraGUI.setVisible(true);
			}
			
		});
		btnEskaeraEgin.setBounds(0, 187, 650, 70);
		jContentPane.add(btnEskaeraEgin);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") +": "+sellerMail);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
	private void selectCurrentLanguage() {
		String lang = Locale.getDefault().getLanguage();

		if ("en".equals(lang)) {
			rdbtnNewRadioButton.setSelected(true);
		} else if ("es".equals(lang)) {
			rdbtnNewRadioButton_2.setSelected(true);
		} else if ("eus".equals(lang) || "eu".equals(lang)) {
			rdbtnNewRadioButton_1.setSelected(true);
		}
	}
	
	private void paintAgain() {
		btnProfile.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.ChangeProfile"));
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		btndirua.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Money"));
		btnhistory.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIHistory"));
		btnEskaeraEgin.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Eskaerak"));
		btnCarrito.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Carrito"));

		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle")+ ": "+sellerMail);
	}
} // @jve:decl-index=0:visual-constraint="0,0"

