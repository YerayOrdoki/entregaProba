package businessLogic;


import java.io.File;
import java.util.Date;
import java.util.List;

import domain.Complaint;
import domain.Eskaera;
import domain.EskaeraOferta;
import domain.Review;
import domain.Sale;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.awt.Image;
import dataAccess.*;
import dataAccess.Emaitza;
import domain.Seller;

import gui.*;
/**
 * Interface that specifies the business logic.
 */
@WebService
public interface BLFacade  {
	  

	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Sale
	 */
   @WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException;
	
	
	/**
	 * This method retrieves the products that contain desc
	 * 
	 * @param desc the text to search
	 * @return collection of sales that contain desc 
	 */
	@WebMethod public List<Sale> getSales(String desc);
	
	/**
	 * 	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @param pubDate the date  of the publication date
	 * @return collection of sales that contain desc and published before pubDate
	 */
	@WebMethod public List<Sale> getPublishedSales(String desc, Date pubDate);

	
	/**
	 * This method calls the data access to initialize the database with some sellers and products.
	 * It is only invoked  when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	@WebMethod public void initializeBD();
	
		
	@WebMethod public Image downloadImage(String imageName);
	
	@WebMethod
	public List<Complaint> getUnmanagedComplaints();

	@WebMethod
	public void markComplaintAsManaged(Integer complaintId);

	   @WebMethod
		public Emaitza isLogged(String log, String password);
	   
	   @WebMethod
	   public Emaitza isAdminLogged(int ID, String pass);
	   
	   @WebMethod Emaitza isAdminRegistered(int ID, String pass);
			
	   @WebMethod 
	   public Emaitza isRegistered(String mail, String user, String pass);
	   
	   @WebMethod
	   public String getEmail(String name, String password);
	   
	   @WebMethod
		public void buySale(Sale s, Seller current);
	   @WebMethod
	   public boolean updateProfile(String email, String newName, String newPassword);
	   @WebMethod
	   public Seller getSellerByEmail(String email);
	   @WebMethod
	   public void updateMoney(String email, float amount);
	   @WebMethod 
	   void saveComplaint(String message, Seller complainant, Sale sale);
	   @WebMethod 
	   public void addProductToBasket(String email, int saleNumber);
	   @WebMethod
	   public void buyMultipleSales(List<Sale> sales, Seller buyer);
	   @WebMethod 
	   public List<Sale> getBasket(String email);
	   @WebMethod 
	   public void clearBasket(String email);
	   @WebMethod
	   public void createEskaera(String title, String status, String description, String sellerMail);
	   @WebMethod
	   public List<Eskaera> getEskaerak(String search);
	   @WebMethod
	   public Eskaera getEskaeraByID(Integer id);
	   @WebMethod
	   public boolean createEskaeraOferta(Integer eskID, String selEmail, float amount, String message);
	   @WebMethod
	   public List<Eskaera> getMyEskaerak(Seller s);
	   @WebMethod
	   public List<EskaeraOferta> getOffersByEskaera(Eskaera esk);
	   @WebMethod
	   public Sale createSaleFromEskaeraOfer(Integer id);
	   @WebMethod 
	   public void createReview(String authorEmail, int saleNumber, String comment, int stars);
	   @WebMethod
	   public List<Sale> getBoughtsBySeller(String email);
	   @WebMethod
	   public List<Sale> getSalesBySeller(String email);
	   @WebMethod
	   public List<Review> getReviewsBySeller(String email);
	   @WebMethod
	   public String getSellerEmailBySale(Integer saleId);
}
