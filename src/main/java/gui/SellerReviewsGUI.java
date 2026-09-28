package gui;

import domain.Review;
import domain.Seller;
import businessLogic.BLFacade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

public class SellerReviewsGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTable tableReviews;
	private DefaultTableModel tableModelReviews;

	private JLabel lblTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.SellerReviews"));
	private JLabel lblAverage = new JLabel();
	private JButton btnClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Close"));

	private String sellerEmail;
	private List<Review> reviews;

	private String[] columnNamesReviews = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.Stars"),
			ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.Comentario")
	};

	public SellerReviewsGUI(String sellerEmail) {
		this.sellerEmail = sellerEmail;

		BLFacade facade = MainGUI.getBusinessLogic();

		if (sellerEmail != null) {
			reviews = facade.getReviewsBySeller(sellerEmail);
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 550, 350);
		setTitle("Reviews");

		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblTitle.setBounds(40, 20, 300, 20);
		contentPane.add(lblTitle);

		if (sellerEmail != null) {
			lblAverage.setText(
					ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.Seller")
					+ " " + sellerEmail
					+ " | "
					+ ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.Avg")
					+ " " + calcularMedia(reviews)
			);
		} else {
			lblAverage.setText(ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.SellerNotFound"));
		}

		lblAverage.setBounds(40, 45, 450, 20);
		contentPane.add(lblAverage);

		tableModelReviews = new DefaultTableModel(null, columnNamesReviews) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableReviews = new JTable(tableModelReviews);

		JScrollPane scrollPane = new JScrollPane(tableReviews);
		scrollPane.setBounds(40, 80, 450, 170);
		contentPane.add(scrollPane);

		btnClose.setBounds(220, 270, 100, 25);
		contentPane.add(btnClose);

		btnClose.addActionListener(e -> dispose());

		refresh();
	}

	private void refresh() {
		tableModelReviews.setRowCount(0);

		if (reviews == null || reviews.isEmpty()) {
			lblTitle.setText(ResourceBundle.getBundle("Etiquetas").getString("SellerReviewsGUI.NoReviews"));
			return;
		}

		for (Review review : reviews) {
			Vector<Object> row = new Vector<Object>();

			row.add(review.getStars());
			row.add(review.getComment());

			tableModelReviews.addRow(row);
		}

		tableReviews.getColumnModel().getColumn(0).setPreferredWidth(80);
		tableReviews.getColumnModel().getColumn(1).setPreferredWidth(350);
	}

	private float calcularMedia(List<Review> reviews) {
		if (reviews == null || reviews.isEmpty()) {
			return 0;
		}

		float sum = 0;

		for (Review r : reviews) {
			sum += r.getStars();
		}

		return sum / reviews.size();
	}
}