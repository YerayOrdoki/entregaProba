package gui;

import businessLogic.BLFacade;
import domain.Seller;
import domain.Eskaera;
import domain.Sale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class QueryEskaerakGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JLabel jLabelEskaerak = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Eskaera"));

	private JButton jButtonSearch = new JButton(ResourceBundle.getBundle("Etiquetas").getString("QueryEskaerakGUI.Buscar"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Close"));

	private JScrollPane scrollPanelEskaerak = new JScrollPane();
	private JTable tableEskaerak = new JTable();

	private DefaultTableModel tableModelEskaerak;

	private JFrame thisFrame;

	private String[] columnNamesEskaerak = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Title"),
			ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Estado"),
			ResourceBundle.getBundle("Etiquetas").getString("QueryEskaerakGUI.Fecha")
	};
	private Seller currentSeller;
	
	private JTextField jTextFieldSearch;

	public QueryEskaerakGUI(String email, Seller curr) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		thisFrame = this;
		this.currentSeller = curr;
		tableEskaerak.setEnabled(false);
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("QueryEskaerakGUI.Ver"));

		jLabelEskaerak.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelEskaerak);

		jButtonClose.setBounds(new Rectangle(220, 379, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
			}
		});

		this.getContentPane().add(jButtonClose, null);

		scrollPanelEskaerak.setBounds(new Rectangle(52, 137, 550, 150));
		scrollPanelEskaerak.setViewportView(tableEskaerak);

		tableModelEskaerak = new DefaultTableModel(null, columnNamesEskaerak);
		tableEskaerak.setModel(tableModelEskaerak);

		tableModelEskaerak.setDataVector(null, columnNamesEskaerak);
		tableModelEskaerak.setColumnCount(4);

		tableEskaerak.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableEskaerak.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableEskaerak.getColumnModel().getColumn(2).setPreferredWidth(100);

		tableEskaerak.getColumnModel().removeColumn(tableEskaerak.getColumnModel().getColumn(3));

		
		tableEskaerak.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() ==2) {
					int selectedRow = tableEskaerak.rowAtPoint(e.getPoint());
					if(selectedRow == -1) return;
					Eskaera eskaera = (Eskaera) tableEskaerak.getModel().getValueAt(selectedRow, 3);
					ShowEskaeraGUI gui = new ShowEskaeraGUI(eskaera, currentSeller);
					gui.setVisible(true);
				}
			}
		});
		
		this.getContentPane().add(scrollPanelEskaerak, null);

		jTextFieldSearch = new JTextField();
		jTextFieldSearch.setBounds(52, 56, 357, 26);
		getContentPane().add(jTextFieldSearch);
		jTextFieldSearch.setColumns(10);

		jButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		jButtonSearch.setBounds(427, 56, 117, 29);
		getContentPane().add(jButtonSearch);

		refresh();
	}
	




	public void refresh() {
		try {
			tableModelEskaerak.setDataVector(null, columnNamesEskaerak);
			tableModelEskaerak.setColumnCount(4);

			BLFacade facade = MainGUI.getBusinessLogic();

			List<Eskaera> eskaerak = facade.getEskaerak(jTextFieldSearch.getText());

			if (eskaerak.isEmpty()) {
				jLabelEskaerak.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.EzDagoEskaera"));
			} else {
				jLabelEskaerak.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowEskaeraGUI.Eskaera"));
			}

			for (Eskaera eskaera : eskaerak) {
				if(eskaera.isBought()) continue;
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

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		tableEskaerak.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableEskaerak.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableEskaerak.getColumnModel().getColumn(2).setPreferredWidth(100);

		tableEskaerak.getColumnModel().removeColumn(tableEskaerak.getColumnModel().getColumn(3));
	}

	public QueryEskaerakGUI getThis() {
		return this;
	}
}