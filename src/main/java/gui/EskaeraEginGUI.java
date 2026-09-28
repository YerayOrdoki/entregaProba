package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import businessLogic.BLFacade;
import domain.Eskaera;

public class EskaeraEginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;


	public EskaeraEginGUI(String email) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 560, 390);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnEskaeraEgin = new JButton(ResourceBundle.getBundle("Etiquetas").getString("EskaeraEginGUI.EskaeraEgin"));
		btnEskaeraEgin.setBounds(212, 308, 108, 32);
		contentPane.add(btnEskaeraEgin);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(121, 140, 413, 110);
		contentPane.add(textArea);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(121, 89, 413, 40);
		contentPane.add(comboBox);
		
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("EskaeraEginGUI.New"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("EskaeraEginGUI.Used"));
		comboBox.addItem(ResourceBundle.getBundle("Etiquetas").getString("EskaeraEginGUI.Good"));
		
		textField = new JTextField();
		textField.setBounds(121, 35, 413, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Title"));
		lblNewLabel.setBounds(22, 38, 89, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Estado"));
		lblStatus.setBounds(22, 102, 89, 14);
		contentPane.add(lblStatus);
		
		JLabel lblDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Desk"));
		lblDescription.setBounds(22, 145, 89, 14);
		contentPane.add(lblDescription);

		btnEskaeraEgin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String title = textField.getText();
				String status = (String) comboBox.getSelectedItem();
				String description = textArea.getText();

				if (title.length() == 0 || description.length() == 0 || status == null) {
					JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.AllFieldsRequired"));
					return;
				}

				try {
					

					BLFacade facade = MainGUI.getBusinessLogic();
					facade.createEskaera(title, status, description, email);

					JOptionPane.showMessageDialog(null,  ResourceBundle.getBundle("Etiquetas").getString("EskaeraEginGUI.EskaeraEginda"));

					textField.setText("");
					textArea.setText("");
					comboBox.setSelectedIndex(0);

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
			}
		});
	}
}