package businessLogic;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import dataAccess.*;
import dataAccess.DataAccess;
import domain.Complaint;
import domain.Eskaera;
import domain.EskaeraOferta;
import domain.Review;
import domain.Sale;
import dataAccess.Emaitza;
import domain.Seller;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;
import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;


/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	 private static final int baseSize = 160;

		private static final String basePath="src/main/resources/images/";
	DataAccess dbManager;

	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		dbManager=new DataAccess();		
	}
	
    public BLFacadeImplementation(DataAccess da)  {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dbManager=da;		
	}
    

	/**
	 * {@inheritDoc}
	 */
   @WebMethod
	public Sale createSale(String title, String description,int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		dbManager.open();
		Sale product=dbManager.createSale(title, description, status, price, pubDate, sellerEmail, file);		
		dbManager.close();
		return product;
   };
	
   /**
    * {@inheritDoc}
    */
	@WebMethod 
	public List<Sale> getSales(String desc){
		dbManager.open();
		List<Sale>  rides=dbManager.getSales(desc);
		dbManager.close();
		return rides;
	}
	
	/**
	    * {@inheritDoc}
	    */
		@WebMethod 
		public List<Sale> getPublishedSales(String desc, Date pubDate) {
			dbManager.open();
			List<Sale>  rides=dbManager.getPublishedSales(desc,pubDate);
			dbManager.close();
			return rides;
		}
	/**
	    * {@inheritDoc}
	    */
	@WebMethod public BufferedImage getFile(String fileName) {
		return dbManager.getFile(fileName);
	}

    
	public void close() {
		DataAccess dB4oManager=new DataAccess();
		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
    @WebMethod	
	 public void initializeBD(){
    	dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}
    /**
	 * {@inheritDoc}
	 */
    @WebMethod public Image downloadImage(String imageName) {
        File image = new File(basePath+imageName);
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @WebMethod public Emaitza isLogged(String log, String pass) {
    	dbManager.open();
    	Emaitza b = dbManager.isLogged(log, pass);
    	dbManager.close();
    	return b;
    }
    @WebMethod public Emaitza isAdminLogged(int ID, String pass) {
    	dbManager.open();
    	Emaitza b = dbManager.isAdminLogged(ID, pass);
    	dbManager.close();
    	return b;
    }
    @WebMethod public Emaitza isAdminRegistered(int ID, String pass) {
    	dbManager.open();
    	Emaitza b = dbManager.isAdminRegistered(ID, pass);
    	dbManager.close();
    	return b;
    }
    @WebMethod public Emaitza isRegistered(String mail, String user, String password) {
    	dbManager.open();
    	Emaitza b = dbManager.isRegistered(mail, user, password);
    	dbManager.close();
    	return b;
    }
    
    @WebMethod public String getEmail(String name, String password) {
    	dbManager.open();
    	return dbManager.getEmail(name, password); 
    	
    }
    @WebMethod 	public void buySale(Sale s, Seller current) {
    	dbManager.open();
    	dbManager.buySale(s, current);

    }
    @WebMethod
    public boolean updateProfile(String email, String newName, String newPassword) {
        dbManager.open();
        boolean result = dbManager.updateProfile(email, newName, newPassword);
        dbManager.close();
        return result;
    }
    @WebMethod
    public Seller getSellerByEmail(String email) {
    	dbManager.open();
    	Seller s = dbManager.getSellerByEmail(email);
    	dbManager.close();
    	return s;
    }
    
    @WebMethod 
    public void updateMoney(String email, float amount) {
        dbManager.open();
        dbManager.updateMoney(email, amount);
        dbManager.close();
    }
    
    @WebMethod
    public List<Complaint> getUnmanagedComplaints() {
        dbManager.open();
        List<Complaint> complaints = dbManager.getUnmanagedComplaints();
        dbManager.close();
        return complaints;
    }
    
    @WebMethod
    public void markComplaintAsManaged(Integer complaintId) {
        dbManager.open();
        dbManager.markComplaintAsManaged(complaintId);
        dbManager.close();
    }
    
    @WebMethod
    public void saveComplaint(String message, Seller complainant, Sale sale) {
        dbManager.open();
        dbManager.createComplaint(message, complainant, sale);
        dbManager.close();
    }
    
    @WebMethod 
    public void addProductToBasket(String email, int saleNumber) {
        dbManager.open();
        dbManager.addProductToBasket(email, saleNumber);
        dbManager.close();
    }

   
    
    @WebMethod
    public void buyMultipleSales(List<Sale> sales, Seller buyer) {
        dbManager.open();
        try {
            for (Sale s : sales) {
                dbManager.buySale(s, buyer);
            }
        } finally {
            dbManager.close();
        }
        
    }
    
    @WebMethod 
    public List<Sale> getBasket(String email) {
        dbManager.open(); 
        List<Sale> basket = dbManager.getBasket(email); 
        dbManager.close(); 
        return basket;
    }
    
    @WebMethod 
    public void clearBasket(String email) {
        dbManager.open();
        dbManager.clearBasket(email);
        dbManager.close();
    }

    @WebMethod
    public void createEskaera(String title, String status, String description, String sellerMail) {
    	dbManager.open();
    	dbManager.createEskaera(title, status, description, sellerMail);
    	dbManager.close();
    }
    @WebMethod
    public List<Eskaera> getEskaerak(String desc) {
    	dbManager.open();
    	List<Eskaera> list1 = dbManager.getEskaerak(desc);
    	dbManager.close();
    	return list1;
    }
    @WebMethod
    public Eskaera getEskaeraByID(Integer id) {
    	dbManager.open();
    	Eskaera e = dbManager.getEskaeraByID(id);
    	dbManager.close();
    	return e;
    }
    
    @WebMethod
    public boolean createEskaeraOferta(Integer eskID, String selEmail, float amount, String message) {
    	dbManager.open();
    	boolean a = dbManager.createEskaeraOferta(eskID, selEmail, amount, message);
    	dbManager.close();
    	return a;
    }
    
    @WebMethod
	public List<Eskaera> getMyEskaerak(Seller s){
    	dbManager.open();
    	List<Eskaera> lista = dbManager.getMyEskaerak(s);
    	dbManager.close();
    	return lista;
    }
    
    @WebMethod
	   public List<EskaeraOferta> getOffersByEskaera(Eskaera esk){
    	dbManager.open();
    	List<EskaeraOferta> lista = dbManager.getOffersByEskaera(esk);
    	dbManager.close();
    	return lista;
    }
    
    @WebMethod
	   public Sale createSaleFromEskaeraOfer(Integer id) {
    	dbManager.open();
    	Sale s = dbManager.createSaleFromEskaeraOfer(id);
    	dbManager.close();
    	return s;
    }
    
    @WebMethod
    public void createReview(String authorEmail, int saleNumber, String comment, int stars) {
        dbManager.open();
        dbManager.createReview(authorEmail, saleNumber, comment, stars);
        dbManager.close();
    }
    @WebMethod
    public List<Sale> getBoughtsBySeller(String email) {
    	dbManager.open();
        List<Sale> a =dbManager.getBoughtsBySeller(email);
        dbManager.close();
        return a;
    }

    @WebMethod
    public List<Sale> getSalesBySeller(String email) {
    	dbManager.open();
    	List<Sale> a =dbManager.getSalesBySeller(email);
    	dbManager.close();
    	return a;
    }
    @Override
    public List<Review> getReviewsBySeller(String email) {
    	dbManager.open();
        List<Review> a=dbManager.getReviewsBySeller(email);
        dbManager.close();
        return a;
    }
    @WebMethod
    public String getSellerEmailBySale(Integer saleId) {
    	dbManager.open();
    	String s =dbManager.getSellerEmailBySale(saleId);
    	dbManager.close();
    	return s;
    }

}
