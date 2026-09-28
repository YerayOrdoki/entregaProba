package gui;

import java.awt.Color;
import java.awt.EventQueue;
import dataAccess.Emaitza;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import domain.Seller;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import businessLogic.*;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;

public class RegisterGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel jLabelMsg = new JLabel();
	private JTextField registerUser;
	private JPasswordField registerPassword;
	private JPasswordField registerRepeatPassword; 
	private JTextField registerEmail;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterGUI frame = new RegisterGUI();
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
	public RegisterGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel registerUserText = new JLabel("User:");
		registerUserText.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registerUserText.setBounds(20, 52, 187, 49);
		registerUserText.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.User"));
		contentPane.add(registerUserText);
		
		JLabel registerPasswordText = new JLabel("Password:");
		registerPasswordText.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registerPasswordText.setBounds(25, 112, 157, 14);
		registerPasswordText.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Pass"));
		contentPane.add(registerPasswordText);
		
		registerUser = new JTextField();
		registerUser.setBounds(217, 68, 146, 20);
		contentPane.add(registerUser);
		registerUser.setColumns(10);
		
		registerPassword = new JPasswordField();
		registerPassword.setBounds(217, 111, 146, 20);
		contentPane.add(registerPassword);
		registerPassword.setColumns(10);
		
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				
				String email = registerEmail.getText().trim();
		        String user = registerUser.getText().trim();
		        char[] pass = registerPassword.getPassword();
		        String passStr = new String(pass);
		        char[] repeatPass = registerRepeatPassword.getPassword();
		        String passRepStr = new String(repeatPass);
		        
		       
		        if (email.isEmpty() || user.isEmpty() || passStr.isEmpty() || passRepStr.isEmpty()) {
					jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.EmptyError"));
		            return;
		        }
		        
		        
		        if (!email.endsWith("@gmail.com")) {
					jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.MailError"));
		        	return;
		        }
		        
		        
		        if (!passStr.equals(passRepStr)) {
					jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.PasswordError"));
		        	return;
		        }
				
		        //TODO String hash = BCrypt.hashpw(passStr, BCrypt.gensalt());
				BLFacade facade = MainGUI.getBusinessLogic();
				Emaitza b = facade.isRegistered(registerEmail.getText(), registerUser.getText(), passStr);
				if(b.getLog()) new MainGUIErregistratua(registerEmail.getText(), b.getSeller()).setVisible(true);
				System.out.println();
			}
		});
		registerButton.setBounds(162, 227, 89, 23);
		contentPane.add(registerButton);
		
		JLabel registerPasswordRepeatText = new JLabel("Repeat Password:");
		registerPasswordRepeatText.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registerPasswordRepeatText.setBounds(25, 154, 157, 14);
		registerPasswordRepeatText.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.RepeatPass"));
		contentPane.add(registerPasswordRepeatText);
		
		registerRepeatPassword = new JPasswordField();
		registerRepeatPassword.setBounds(217, 153, 146, 20);
		contentPane.add(registerRepeatPassword);
		registerRepeatPassword.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Email:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(20, 27, 162, 14);
		lblNewLabel.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Email"));
		contentPane.add(lblNewLabel);
		
		registerEmail = new JTextField();
		registerEmail.setBounds(217, 26, 146, 20);
		contentPane.add(registerEmail);
		registerEmail.setColumns(10);
		
		jLabelMsg.setBounds(new Rectangle(20, 184, 377, 20));
		jLabelMsg.setForeground(Color.red);
		this.getContentPane().add(jLabelMsg, null);

		


	}
	public boolean correctPass(String p1, String p2) {
		return p1.equals(p2);
	}
}
