package gui;

import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.toedter.calendar.JCalendar;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.Seller;


public class CreateSaleGUI extends JFrame {
	
    File targetFile;
    BufferedImage targetImg;
    String encodedfile = null;

    public JPanel panel_1;
    private static final int baseSize = 128;
	private static final String basePath="src/main/resources/images/";

	
	private static final long serialVersionUID = 1L;

	private String sellerMail;
	private JTextField fieldTitle=new JTextField();
	private JTextField fieldDescription=new JTextField();
	
	private JLabel jLabelTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"));
	private JLabel jLabelDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description")); 
	private JLabel jLabelProductStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
	private JLabel jLabelPrice = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"));
	private JTextField jTextFieldPrice = new JTextField();

	private JCalendar jCalendar = new JCalendar();
	private Calendar calendarAct = null;
	private Calendar calendarAnt = null;

	private JScrollPane scrollPaneEvents = new JScrollPane();
	
	JComboBox<String> jComboBoxStatus = new JComboBox<String>();
	DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
	List<String> status;


	private JButton jButtonCreate = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.CreateProduct"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JLabel jLabelMsg = new JLabel();
	private JLabel jLabelError = new JLabel();
	private JFrame thisFrame;
	private final JButton btnNewButton_2 = new JButton("grabar Imagen"); //$NON-NLS-1$ //$NON-NLS-2$

	public CreateSaleGUI(String mail) {

		thisFrame=this;
		this.sellerMail=mail;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(604, 370));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.CreateProduct"));

		jLabelTitle.setBounds(new Rectangle(6, 24, 92, 20));
		
		jLabelPrice.setBounds(new Rectangle(6, 141, 101, 20));
		jTextFieldPrice.setBounds(new Rectangle(97, 141, 60, 20));

		
		scrollPaneEvents.setBounds(new Rectangle(25, 44, 346, 116));
		jButtonCreate.setFont(new Font("Lucida Grande", Font.BOLD, 15));

		jButtonCreate.setBounds(new Rectangle(100, 222, 216, 41));

		jButtonCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jLabelMsg.setText("");
				String error=check_fields_Errors();
				if (error!=null) 
					jLabelMsg.setText(error);
				else
					try {
						BLFacade facade = MainGUI.getBusinessLogic();
						float price = Float.parseFloat(jTextFieldPrice.getText());
						String s=(String)jComboBoxStatus.getSelectedItem();
						int numStatus=status.indexOf(s);
						facade.createSale(fieldTitle.getText(), fieldDescription.getText(), numStatus, price,  UtilDate.trim(jCalendar.getDate()), sellerMail, targetFile);
						jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ProductCreated"));
					
					} catch (Exception e1) {

						// TODO Auto-generated catch block
						jLabelMsg.setText(e1.getMessage());
					}
			}
		});
		jButtonClose.setBounds(new Rectangle(328, 228, 101, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);			}
		});

		jLabelMsg.setBounds(new Rectangle(26, 275, 377, 20));
		jLabelMsg.setForeground(Color.red);

		jLabelError.setBounds(new Rectangle(16, 275, 384, 20));
		jLabelError.setForeground(Color.red);
		
	    status=Utils.getStatus();
		for(String s:status) statusOptions.addElement(s);

		this.getContentPane().add(jLabelMsg, null);
		this.getContentPane().add(jLabelError, null);

		this.getContentPane().add(jButtonClose, null);
		this.getContentPane().add(jButtonCreate, null);
		this.getContentPane().add(jLabelTitle, null);
		
		
		this.getContentPane().add(jLabelPrice, null);
		this.getContentPane().add(jTextFieldPrice, null);
		
		jLabelProductStatus.setBounds(new Rectangle(40, 15, 140, 25));
		jLabelProductStatus.setBounds(6, 185, 140, 25);
		getContentPane().add(jLabelProductStatus);
		
		jLabelDescription.setBounds(6, 56, 109, 16);
		getContentPane().add(jLabelDescription);
		
		
		fieldTitle.setBounds(98, 21, 250, 26);
		getContentPane().add(fieldTitle);
		fieldTitle.setColumns(10);
		
		
		fieldDescription.setBounds(98, 56, 250, 73);
		getContentPane().add(fieldDescription);
		fieldDescription.setColumns(10);
		
		jComboBoxStatus.setModel(statusOptions);
		jComboBoxStatus.setBounds(90, 183, 114, 27);
		getContentPane().add(jComboBoxStatus);
		
		JButton btnNewButton = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.LoadPicture")); //$NON-NLS-1$ //$NON-NLS-2$
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF", "jpg", "gif");
				fileChooser.setFileFilter(filter);
		        int result = fileChooser.showOpenDialog(null);  

		        fileChooser.setBounds(30, 148, 320, 80);

		        if (result == JFileChooser.APPROVE_OPTION) {
		            targetFile = fileChooser.getSelectedFile();
		            panel_1.removeAll();
		            panel_1.repaint();

		            try {
		                targetImg = rescale(ImageIO.read(targetFile));
		                encodeFileToBase64Binary(targetFile);
		            } catch (IOException ex) {
		                //Logger.getLogger(MainAppFrame.class.getName()).log(Level.SEVERE, null, ex);
		            }
		            
		            panel_1.setLayout(new BorderLayout(0, 0));
		            panel_1.add(new JLabel(new ImageIcon(targetImg))); 
		            setVisible(true);

		            }
			}
		});
		btnNewButton.setBounds(186, 138, 162, 29);
		getContentPane().add(btnNewButton);
		
		panel_1 = new JPanel();
		panel_1.setBounds(461, 209, 124, 86);
		getContentPane().add(panel_1);
		
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				try {
					BufferedImage img = ImageIO.read(targetFile);
					
				    File outputfile = new File(basePath+targetFile.getName());

				   ImageIO.write(img, "png", outputfile);  // ignore returned boolean
				   System.out.println("file stored "+img);
				} catch(IOException ex) {
				 //System.out.println("Write error for " + outputfile.getPath()  ": " + ex.getMessage());
				  }
				
			}
		});
		btnNewButton_2.setBounds(137, 350, 117, 29);
		
		getContentPane().add(btnNewButton_2);
		
		jCalendar.setBounds(new Rectangle(360, 50, 225, 150));
		this.getContentPane().add(jCalendar, null);
		
		JLabel jLabelPublicationDate = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"));
		jLabelPublicationDate.setBounds(new Rectangle(6, 24, 92, 20));
		jLabelPublicationDate.setBounds(360, 26, 197, 20);
		getContentPane().add(jLabelPublicationDate);

		this.jCalendar.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertychangeevent) {
//			
				if (propertychangeevent.getPropertyName().equals("locale")) {
					jCalendar.setLocale((Locale) propertychangeevent.getNewValue());
				} else if (propertychangeevent.getPropertyName().equals("calendar")) {
					calendarAnt = (Calendar) propertychangeevent.getOldValue();
					calendarAct = (Calendar) propertychangeevent.getNewValue();
					
					int monthAnt = calendarAnt.get(Calendar.MONTH);
					int monthAct = calendarAct.get(Calendar.MONTH);
					if (monthAct!=monthAnt) {
						if (monthAct==monthAnt+2) { 
							// Si en JCalendar está 30 de enero y se avanza al mes siguiente, devolverá 2 de marzo (se toma como equivalente a 30 de febrero)
							// Con este código se dejará como 1 de febrero en el JCalendar
							calendarAct.set(Calendar.MONTH, monthAnt+1);
							calendarAct.set(Calendar.DAY_OF_MONTH, 1);
						}
						
						jCalendar.setCalendar(calendarAct);						
	
					}
					jCalendar.setCalendar(calendarAct);
					int offset = jCalendar.getCalendar().get(Calendar.DAY_OF_WEEK);
					
						if (Locale.getDefault().equals(new Locale("es")))
							offset += 4;
						else
							offset += 5;
				Component o = (Component) jCalendar.getDayChooser().getDayPanel().getComponent(jCalendar.getCalendar().get(Calendar.DAY_OF_MONTH) + offset);
				}}});
		
	}	 

	public BufferedImage rescale(BufferedImage originalImage)
    {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	private String check_fields_Errors() {
		
		try {
			if ((fieldTitle.getText().length()==0) || (fieldDescription.getText().length()==0)  || (jTextFieldPrice.getText().length()==0))
				return ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ErrorQuery");
			else {

				// trigger an exception if the introduced string is not a number
					float price = Float.parseFloat(jTextFieldPrice.getText());
					if (price <= 0) 
						return ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PriceMustBeGreaterThan0");
					
					else 
						return null;
			}
		} catch (java.lang.NumberFormatException e1) {

			return  ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ErrorNumber");		
		}catch (Exception e1) {
			e1.printStackTrace();
			return null;

		}
	}
	
	
public  String encodeFileToBase64Binary(File file){
        try {
            @SuppressWarnings("resource")
			FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile=new String(Base64.getEncoder().encode(bytes));

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }
}
