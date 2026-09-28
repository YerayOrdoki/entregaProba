package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import businessLogic.BLFacade;
import dataAccess.Emaitza;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;

public class AdminLoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldID;
	private JPasswordField textFieldPass;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminLoginGUI frame = new AdminLoginGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AdminLoginGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Login");
		lblNewLabel.setBounds(64, 39, 151, 14);
		lblNewLabel.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.User"));
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(64, 139, 151, 14);
		lblNewLabel_1.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Pass"));
		contentPane.add(lblNewLabel_1);
		
		textFieldID = new JTextField();
		textFieldID.setBounds(251, 37, 86, 20);
		contentPane.add(textFieldID);
		textFieldID.setColumns(10);
		
		textFieldPass = new JPasswordField();
		textFieldPass.setBounds(251, 137, 86, 20);
		contentPane.add(textFieldPass);
		textFieldPass.setColumns(10);
		
		JButton btnNewButton = new JButton("Login egin");
		btnNewButton.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Login"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
				char[] pass = textFieldPass.getPassword();
				String passStr = new String(pass);
				Emaitza b = facade.isAdminLogged(Integer.parseInt(textFieldID.getText()), passStr);
				if(b.getLog()) {
					new AdminComplaintsGUI().setVisible(true);
				}
			}
		});
		btnNewButton.setBounds(168, 211, 89, 23);
		contentPane.add(btnNewButton);

	}
}
