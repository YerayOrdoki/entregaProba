package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import businessLogic.BLFacade;
import domain.Eskaera;
import domain.Seller;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class OfertaEginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	private Eskaera eskaera;
	private Seller currentSeller;

	/**
	 * Create the frame.
	 */
	public OfertaEginGUI(Eskaera eskaera, Seller s) {
		this.eskaera = eskaera;
		this.currentSeller = s;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Cantidad:");
		lblNewLabel.setBounds(189, 62, 83, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Mensaje:");
		lblNewLabel_1.setBounds(38, 112, 154, 14);
		contentPane.add(lblNewLabel_1);

		textField = new JTextField();
		textField.setBounds(282, 59, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(184, 109, 191, 71);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JButton btnNewButton = new JButton("Enviar");
		btnNewButton.setBounds(173, 227, 89, 23);
		contentPane.add(btnNewButton);

		JLabel lblNewLabel_2 = new JLabel("Oferta para: " + eskaera.getTitle());
		lblNewLabel_2.setBounds(38, 21, 330, 14);
		contentPane.add(lblNewLabel_2);

		btnNewButton.addActionListener(e -> eginOferta());
	}

	private void eginOferta() {
		try {
			if (eskaera == null) {
				JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna eskaera");
				return;
			}

			if (currentSeller == null) {
				JOptionPane.showMessageDialog(this, "No hay seller logueado");
				return;
			}

			String amountText = textField.getText().trim();
			String message = textField_1.getText().trim();

			if (amountText.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Introduce una cantidad");
				return;
			}

			if (message.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Introduce un mensaje");
				return;
			}

			float amount = Float.parseFloat(amountText.replace(",", "."));

			if (amount <= 0) {
				JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que 0");
				return;
			}

			BLFacade facade = MainGUI.getBusinessLogic();

			boolean result = facade.createEskaeraOferta(
					eskaera.getId(),
					currentSeller.getEmail(),
					amount,
					message
			);

			if (result) {
				JOptionPane.showMessageDialog(this, "Oferta enviada correctamente");
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "No se ha podido enviar la oferta");
			}

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Cantidad no válida");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al enviar la oferta");
		}
	}
}