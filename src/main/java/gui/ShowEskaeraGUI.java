package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import domain.Eskaera;
import domain.Seller;

import javax.swing.JLabel;

import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ShowEskaeraGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private Eskaera eskaera;
	private Seller currentSeller;

	public ShowEskaeraGUI(Eskaera eskaera, Seller currentSeller) {
		this.eskaera = eskaera;
		this.currentSeller = currentSeller;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Eskaera"));

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Title"));
		lblTitle.setBounds(40, 30, 80, 20);
		contentPane.add(lblTitle);

		JLabel lblTitleValue = new JLabel(eskaera.getTitle());
		lblTitleValue.setBounds(130, 30, 300, 20);
		contentPane.add(lblTitleValue);

		JLabel lblStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Estado"));
		lblStatus.setBounds(40, 65, 80, 20);
		contentPane.add(lblStatus);

		JLabel lblStatusValue = new JLabel(eskaera.getStatus());
		lblStatusValue.setBounds(130, 65, 300, 20);
		contentPane.add(lblStatusValue);

		JLabel lblDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Desk"));
		lblDescription.setBounds(40, 100, 100, 20);
		contentPane.add(lblDescription);

		JTextArea textAreaDescription = new JTextArea();
		textAreaDescription.setEditable(false);
		textAreaDescription.setLineWrap(true);
		textAreaDescription.setWrapStyleWord(true);

		if (eskaera.getDescription() != null) {
			textAreaDescription.setText(eskaera.getDescription());
		}

		JScrollPane scrollPane = new JScrollPane(textAreaDescription);
		scrollPane.setBounds(130, 100, 300, 100);
		contentPane.add(scrollPane);

		JButton btnOffer = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.MakeOffer"));
		btnOffer.setBounds(110, 240, 130, 30);
		contentPane.add(btnOffer);

		JButton btnClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Close"));
		btnClose.setBounds(260, 240, 100, 30);
		contentPane.add(btnClose);

		btnOffer.addActionListener(e -> {
			OfertaEginGUI gui = new OfertaEginGUI(eskaera, currentSeller);
			gui.setVisible(true);
		});

		btnClose.addActionListener(e -> dispose());
	}
}