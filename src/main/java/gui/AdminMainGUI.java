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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class AdminMainGUI extends JFrame {
	
    private String sellerMail;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonRegister = null;
	private JButton jButtonLogin = null;

    private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
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
	
	/**
	 * This is the default constructor
	 */
	public AdminMainGUI(String mail) {
		super();

		this.sellerMail=mail;
		
		this.setSize(495, 290);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setBounds(0, 0, 479, 50);
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		rdbtnNewRadioButton = new JRadioButton("English");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton);
		
		rdbtnNewRadioButton_1 = new JRadioButton("Euskara");
		rdbtnNewRadioButton_1.setSelected(true);
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		rdbtnNewRadioButton_2 = new JRadioButton("Castellano");
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_2);
	
		panel = new JPanel();
		panel.setBounds(0, 176, 479, 33);
		panel.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton);
		
	
		
		
		jButtonLogin = new JButton();
		jButtonLogin.setBounds(0, 100, 479, 50);
		jButtonLogin.setText(ResourceBundle.getBundle("Etiquetas").getString("AdminMainGUI.Login"));
		jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new AdminLoginGUI();

				a.setVisible(true);
			}
		});
		
		jButtonRegister = new JButton();
		jButtonRegister.setBounds(0, 50, 479, 50);
		jButtonRegister.setText(ResourceBundle.getBundle("Etiquetas").getString("AdminMainGUI.Register"));
		jButtonRegister.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new AdminRegisterGUI();

				a.setVisible(true);
			}
		});
		
		jContentPane = new JPanel();
		jContentPane.setLayout(null);
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonRegister);
		jContentPane.add(jButtonLogin);
		jContentPane.add(panel);
		
		
		setContentPane(jContentPane);
		
		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
	
	public AdminMainGUI() {
		// TODO Auto-generated constructor stub
	}

	private void paintAgain() {
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonLogin.setText(ResourceBundle.getBundle("Etiquetas").getString("AdminMainGUI.Login"));
		jButtonRegister.setText(ResourceBundle.getBundle("Etiquetas").getString("AdminMainGUI.Register"));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle")+ ": "+sellerMail);
	}
} // @jve:decl-index=0:visual-constraint="0,0"

