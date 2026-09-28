package gui;
import gui.*;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;

import domain.*;
import javax.swing.JLabel;
import javax.swing.JTextField;

import businessLogic.BLFacade;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class ProfileGUI extends JFrame {
	private Seller s;
	private JTextField textuser;
	private JTextField textemail;
	private JPasswordField textpass;
	private JPasswordField textrepeatpass;
	private JFrame thisFrame;
	
	public ProfileGUI(Seller s) {
		this.s = s;
		thisFrame=this;
		getContentPane().setLayout(null);
		this.setSize(new Dimension(604, 370));
		
		
		JLabel lbluser = new JLabel("New label");
		lbluser.setBounds(38, 49, 146, 28);
		lbluser.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.User"));
		getContentPane().add(lbluser);
		
		JLabel lblemail = new JLabel("New label");
		lblemail.setBounds(38, 103, 158, 19);
		lblemail.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Email"));
		getContentPane().add(lblemail);
		
		
		JLabel lblpass = new JLabel("New label");
		lblpass.setBounds(38, 141, 158, 28);
		lblpass.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Password"));
		getContentPane().add(lblpass);
		
		textuser = new JTextField();
		textuser.setBounds(232, 54, 173, 18);
		textuser.setText(s.getName());
		getContentPane().add(textuser);
		textuser.setColumns(10);
		
		textemail = new JTextField();
		textemail.setEditable(false);
		textemail.setBounds(232, 103, 173, 18);
		textemail.setText(s.getEmail());
		getContentPane().add(textemail);
		textemail.setColumns(10);
		
		textpass = new JPasswordField();
		textpass.setBounds(232, 146, 173, 18);
		textpass.setText(s.getPass());
		getContentPane().add(textpass);
		textpass.setColumns(10);
		
		JLabel lblrepeatpass = new JLabel("New label");
		lblrepeatpass.setBounds(38, 199, 158, 28);
		lblrepeatpass.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.ConfirmPassword"));
		getContentPane().add(lblrepeatpass);
		
		textrepeatpass = new JPasswordField();
		textrepeatpass.setBounds(232, 204, 173, 18);
		getContentPane().add(textrepeatpass);
		textrepeatpass.setText(s.getPass());
		textrepeatpass.setColumns(10);
		
		
		JLabel jLabelMsg = new JLabel();
		jLabelMsg.setBounds(new Rectangle(38, 238, 481, 29));
		jLabelMsg.setForeground(Color.red);
		this.getContentPane().add(jLabelMsg, null);
		
		
		JButton btnupdate = new JButton("New button");
		btnupdate.setText(ResourceBundle.getBundle("Etiquetas").getString("Accept"));
		btnupdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email = textemail.getText().trim();
		        String user = textuser.getText().trim();
		        char[] pass =  textpass.getPassword();
		        char[] repeatPass = textrepeatpass.getPassword();
		        String password = new String(pass);
		        String rpassword = new String(repeatPass);
		        
		       
		        if (email.isEmpty() || user.isEmpty() || password.isEmpty() || rpassword.isEmpty()) {
					jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.EmptyError"));
		            return;
		        }
		        
		        
		        if (!email.endsWith("@gmail.com")) {
					jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.MailError"));
		        	return;
		        }
		        
		        
		        if (!password.equals(rpassword)) {
					jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.PasswordError"));
		        	return;
		        }
		        
		        BLFacade facade = MainGUI.getBusinessLogic();
		        if(facade==null) {
		        	return;
		        }
		        boolean actualizado = facade.updateProfile(s.getEmail(), user, password);
		        System.out.println(actualizado);
		        thisFrame.setVisible(false);
		       
		        
			}
		});
		btnupdate.setBounds(244, 278, 84, 20);
		getContentPane().add(btnupdate);
		
		
		
		
	}
}
