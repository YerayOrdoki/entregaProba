package gui;

import java.util.*;
import domain.Seller;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.awt.image.BufferedImage;

import businessLogic.BLFacade;
import domain.Sale;


public class ShowSaleGUI extends JFrame {
	
    File targetFile;
    BufferedImage targetImg;
    public JPanel panel_1;
    private static final int baseSize = 160;
	private static final String basePath="src/main/resources/images/";
	
	private static final long serialVersionUID = 1L;

	private JTextField fieldTitle=new JTextField();
	private JTextField fieldDescription=new JTextField();
	
	JLabel labelStatus = new JLabel(); 

	private JLabel jLabelTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Title"));
	private JLabel jLabelDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description")); 
	private JLabel jLabelProductStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
	private JLabel jLabelPrice = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"));
	private JTextField fieldPrice = new JTextField();
	private File selectedFile;
    private String irudia;

	private JScrollPane scrollPaneEvents = new JScrollPane();
	DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JLabel jLabelMsg = new JLabel();
	private JLabel jLabelError = new JLabel();
	private JLabel statusField=new JLabel();
	private JFrame thisFrame;
	private QuerySalesGUI querySalesGUI;
	private Sale currentSale;
	private Seller currentSeller;
	
	public ShowSaleGUI(Sale sale, Seller curr, QuerySalesGUI querySalesGUI) { 
		thisFrame=this; 
		this.currentSale = sale;
		this.currentSeller = curr;
		this.querySalesGUI = querySalesGUI;
		this.setVisible(true);
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(552, 406));
		//this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("CreateProductGUI.CreateProduct"));

		fieldTitle.setText(sale.getTitle());
		fieldDescription.setText(sale.getDescription());

		fieldPrice.setText(Float.toString(sale.getPrice()));		
		
		labelStatus.setText(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPublicationDate()));
		
		jLabelTitle.setBounds(new Rectangle(6, 56, 92, 20));
		
		jLabelPrice.setBounds(new Rectangle(6, 166, 101, 20));
		fieldPrice.setEditable(false);
		fieldPrice.setBounds(new Rectangle(137, 166, 60, 20));

		
		scrollPaneEvents.setBounds(new Rectangle(25, 44, 346, 116));
		jButtonClose.setBounds(new Rectangle(16, 268, 114, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);			}
		});

		jLabelMsg.setBounds(new Rectangle(275, 214, 305, 20));
		jLabelMsg.setForeground(Color.red);

		jLabelError.setBounds(new Rectangle(6, 231, 320, 20));
		jLabelError.setForeground(Color.red);
		

		this.getContentPane().add(jLabelMsg, null);
		this.getContentPane().add(jLabelError, null);

		this.getContentPane().add(jButtonClose, null);
		

		this.getContentPane().add(jLabelTitle, null);
		
		
		this.getContentPane().add(jLabelPrice, null);
		this.getContentPane().add(fieldPrice, null);
		
		jLabelProductStatus.setBounds(new Rectangle(40, 15, 140, 25));
		jLabelProductStatus.setBounds(6, 187, 140, 25);
		getContentPane().add(jLabelProductStatus);
		
		jLabelDescription.setBounds(6, 81, 109, 16);
		getContentPane().add(jLabelDescription);
		fieldTitle.setEditable(false);
		
		
		fieldTitle.setBounds(128, 53, 370, 26);
		getContentPane().add(fieldTitle);
		fieldTitle.setColumns(10);
		fieldDescription.setEditable(false);
		
		JButton jButtonBuy = new JButton(ResourceBundle.getBundle("Etiquetas").getString("BuyGUI.Buy"));
		this.getContentPane().add(jButtonBuy, null);
		jButtonBuy.setBounds(new Rectangle(16, 268, 114, 30));
		jButtonBuy.setBounds(173, 268, 114, 30);
		getContentPane().add(jButtonBuy);
		setVisible(true);

		jButtonBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame a = new BuyGUI(sale, curr, querySalesGUI,ShowSaleGUI.this);
				a.setVisible(true);
			}
		});
		
		
		fieldDescription.setBounds(127, 81, 371, 73);
		getContentPane().add(fieldDescription);
		fieldDescription.setColumns(10);
		
		panel_1 = new JPanel();
		panel_1.setBounds(318, 166, 180, 160);
		getContentPane().add(panel_1);
		
		labelStatus.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelStatus.setBounds(37, 231, 289, 16);
		getContentPane().add(labelStatus);
		
		
		BLFacade facade = MainGUI.getBusinessLogic();
		String file=sale.getFile();
		if (file!=null) {
			Image img=facade.downloadImage(file);
			targetImg = rescale((BufferedImage)img);
			panel_1.setLayout(new BorderLayout(0, 0));
			panel_1.add(new JLabel(new ImageIcon(targetImg))); 
		}
		System.out.println("status: "+sale.getStatus());
		statusField = new JLabel(Utils.getStatus(sale.getStatus())); 
		statusField.setBounds(137, 191, 92, 16);
		getContentPane().add(statusField);
		
		JButton btnAddToBasket = new JButton("Añadir al carrito"); 
		btnAddToBasket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
		            BLFacade facade = MainGUI.getBusinessLogic();
		            facade.addProductToBasket(curr.getEmail(), sale.getSaleNumber());
		            JOptionPane.showMessageDialog(null, "Producto añadido a la cesta");
		        } catch (Exception e) {
		            
		            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		        }
			}
		});
		btnAddToBasket.setBounds(16, 320, 114, 23);
		getContentPane().add(btnAddToBasket);
		
		JButton btnNewButton = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Review")); //$NON-NLS-1$ //$NON-NLS-2$
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String sellerEmail = MainGUI.getBusinessLogic().getSellerEmailBySale(sale.getSaleNumber());
				
				new SellerReviewsGUI(sellerEmail).setVisible(true);
			}
		});
		btnNewButton.setBounds(173, 320, 114, 23);
		getContentPane().add(btnNewButton);
		
		

	}	 
	public BufferedImage rescale(BufferedImage originalImage)
    {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
}

