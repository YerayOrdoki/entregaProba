package gui;

import businessLogic.BLFacade;
import domain.Eskaera;
import domain.EskaeraOferta;
import domain.Sale;
import domain.Seller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

public class EskaeraOffersGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTable tableOffers;
	private DefaultTableModel tableModelOffers;

	private JLabel jLabelTitle = new JLabel("Ofertas recibidas");
	private JLabel jLabelEskaera = new JLabel();

	private JButton jButtonAccept = new JButton("Aceptar oferta");
	private JButton jButtonClose = new JButton("Cerrar");

	private Eskaera eskaera;
	private Seller currentSeller;

	private String[] columnNamesOffers = new String[] {
			"Seller",
			"Cantidad",
			"Mensaje"
	};

	public EskaeraOffersGUI(Eskaera eskaera, Seller currentSeller) {
		this.eskaera = eskaera;
		this.currentSeller = currentSeller;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 420);
		setTitle("Ofertas de eskaera");

		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		jLabelTitle.setBounds(40, 20, 300, 20);
		contentPane.add(jLabelTitle);

		jLabelEskaera.setBounds(40, 45, 500, 20);
		jLabelEskaera.setText("Eskaera: " + eskaera.getTitle());
		contentPane.add(jLabelEskaera);

		tableModelOffers = new DefaultTableModel(null, columnNamesOffers) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableOffers = new JTable(tableModelOffers);

		JScrollPane scrollPane = new JScrollPane(tableOffers);
		scrollPane.setBounds(40, 80, 600, 220);
		contentPane.add(scrollPane);
		jButtonAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = tableOffers.getSelectedRow();
				if (row == -1) return;
				EskaeraOferta oferta = (EskaeraOferta) tableOffers.getModel().getValueAt(row, 3);
				try {
					
					BLFacade facade = MainGUI.getBusinessLogic();
					Sale sale = facade.createSaleFromEskaeraOfer(oferta.getId());
					if(sale == null) return;
					facade.buySale(sale, currentSeller);
					dispose();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});

		jButtonAccept.setBounds(190, 330, 140, 25);
		contentPane.add(jButtonAccept);

		jButtonClose.setBounds(360, 330, 100, 25);
		contentPane.add(jButtonClose);

		jButtonClose.addActionListener(e -> dispose());

		

	

		refresh();
	}

	private void refresh() {
		try {
			tableModelOffers.setDataVector(null, columnNamesOffers);
			tableModelOffers.setColumnCount(4);

			BLFacade facade = MainGUI.getBusinessLogic();

			List<EskaeraOferta> offers = facade.getOffersByEskaera(eskaera);

			for (EskaeraOferta offer : offers) {
				Vector<Object> row = new Vector<Object>();

				if (offer.getSeller() != null) {
					row.add(offer.getSeller().getEmail());
				} else {
					row.add("");
				}

				row.add(offer.getAmount());
				row.add(offer.getMessage());
				row.add(offer);

				tableModelOffers.addRow(row);
			}

			tableOffers.getColumnModel().getColumn(0).setPreferredWidth(160);
			tableOffers.getColumnModel().getColumn(1).setPreferredWidth(80);
			tableOffers.getColumnModel().getColumn(2).setPreferredWidth(300);

			if (tableOffers.getColumnModel().getColumnCount() > 3) {
				tableOffers.getColumnModel().removeColumn(tableOffers.getColumnModel().getColumn(3));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}