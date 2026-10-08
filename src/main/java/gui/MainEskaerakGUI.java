package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import domain.Seller;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;

public class MainEskaerakGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public MainEskaerakGUI(Seller s) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton btnNewButton = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainEskaerakGUI.NireEskaerak"));
		btnNewButton.setBounds(0, 11, 434, 55);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NireEskaerakGUI gui = new NireEskaerakGUI(s);
				gui.setVisible(true);
			}
		});
		contentPane.setLayout(null);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainEskaerakGUI.EskaeraEgin"));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				EskaeraEginGUI eskaeraGUI = new EskaeraEginGUI(s.getEmail());
				eskaeraGUI.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(0, 86, 434, 55);
		contentPane.add(btnNewButton_1);
		
		
		JButton btnNewButton_2 = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainEskaerakGUI.Eskaerak"));
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				QueryEskaerakGUI query = new QueryEskaerakGUI(s.getEmail(), s);
				query.setVisible(true);
			}
		});
		btnNewButton_2.setBounds(0, 166, 434, 55);
		contentPane.add(btnNewButton_2);

	}

}
