package gui;

import businessLogic.BLFacade;
import domain.Eskaera;
import domain.Seller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

public class NireEskaerakGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTable tableEskaerak;
	private DefaultTableModel tableModelEskaerak;

	private JLabel jLabelTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainEskaerakGUI.NireEskaerak"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Close"));

	private Seller currentSeller;

	private String[] columnNamesEskaerak = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Title"),
			ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Estado"),
			ResourceBundle.getBundle("Etiquetas").getString("QueryEskaerakGUI.Fecha")
	};

	public NireEskaerakGUI(Seller currentSeller) {
		this.currentSeller = currentSeller;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainEskaerakGUI.NireEskaerak"));

		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		jLabelTitle.setBounds(40, 25, 300, 20);
		contentPane.add(jLabelTitle);

		tableModelEskaerak = new DefaultTableModel(null, columnNamesEskaerak) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableEskaerak = new JTable(tableModelEskaerak);

		JScrollPane scrollPane = new JScrollPane(tableEskaerak);
		scrollPane.setBounds(40, 60, 550, 220);
		contentPane.add(scrollPane);

		jButtonClose.setBounds(260, 310, 100, 25);
		contentPane.add(jButtonClose);

		jButtonClose.addActionListener(e -> dispose());

		tableEskaerak.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = tableEskaerak.rowAtPoint(e.getPoint());

					if (row == -1) {
						return;
					}

					Eskaera eskaera = (Eskaera) tableEskaerak.getModel().getValueAt(row, 3);

					EskaeraOffersGUI gui = new EskaeraOffersGUI(eskaera, currentSeller);
					gui.setVisible(true);
				}
			}
		});

		refresh();
	}

	private void refresh() {
		try {
			tableModelEskaerak.setDataVector(null, columnNamesEskaerak);
			tableModelEskaerak.setColumnCount(4);

			BLFacade facade = MainGUI.getBusinessLogic();

			List<Eskaera> eskaerak = facade.getMyEskaerak(currentSeller);

			for (Eskaera eskaera : eskaerak) {
				Vector<Object> row = new Vector<Object>();

				row.add(eskaera.getTitle());
				row.add(eskaera.getStatus());

				if (eskaera.getCreationDate() != null) {
					row.add(new SimpleDateFormat("dd-MM-yyyy").format(eskaera.getCreationDate()));
				} else {
					row.add("");
				}

				row.add(eskaera);

				tableModelEskaerak.addRow(row);
			}

			tableEskaerak.getColumnModel().getColumn(0).setPreferredWidth(200);
			tableEskaerak.getColumnModel().getColumn(1).setPreferredWidth(100);
			tableEskaerak.getColumnModel().getColumn(2).setPreferredWidth(100);

			if (tableEskaerak.getColumnModel().getColumnCount() > 3) {
				tableEskaerak.getColumnModel().removeColumn(tableEskaerak.getColumnModel().getColumn(3));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}