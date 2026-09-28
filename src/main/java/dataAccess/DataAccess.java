package dataAccess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.jws.WebMethod;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Seller;
import domain.Sale;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;
import gui.MainGUIErregistratua;
import domain.Admin;
import domain.Complaint;
import domain.Eskaera;
import domain.EskaeraOferta;
import domain.Review;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccess  {
	private  EntityManager  db;
	private  EntityManagerFactory emf;
    private static final int baseSize = 160;

	private static final String basePath="src/main/resources/images/";



	ConfigXML c=ConfigXML.getInstance();

     public DataAccess()  {
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();
				System.out.println("File deleted");
			 } else {
				 System.out.println("Operation failed");
				}
		}
		open();
		if  (c.isDatabaseInitialized()) 
			initializeDB();
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();

	}
     
    public DataAccess(EntityManager db) {
    	this.db=db;
    }

	
	
	/**
	 * This method  initializes the database with some products and sellers.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try { 
	       
		    //Create sellers 
			Seller seller1=new Seller("seller1@gmail.com","Aitor Fernandez", "aurrera");
			Seller seller2=new Seller("seller22@gmail.com","Ane Gaztañaga", "aurrera");
			Seller seller3=new Seller("seller3@gmail.com","Test Seller", "aurrera");

			
			//Create products
			Date today = UtilDate.trim(new Date());
		
			
			seller1.addSale("futbol baloia", "oso polita, gutxi erabilita", 0, 2,  today, null);
			seller1.addSale("salomon mendiko botak", "44 zenbakia, 3 ateraldi",20,  2,  today, null);
			seller1.addSale("samsung 42\" telebista", "berria, erabili gabe", 175, 1,  today, null);


			seller2.addSale("imac 27", "7 urte, dena ondo dabil", 1, 200,today, null);
			seller2.addSale("iphone 17", "oso gutxi erabilita", 2, 400, today, null);
			seller2.addSale("orbea mendiko bizikleta", "29\" 10 urte, mantenua behar du", 3,225, today, null);
			seller2.addSale("polar kilor erlojua", "Vantage M, ondo dago", 3, 30, today, null);

			seller3.addSale("sukaldeko mahaia", "1.8*0.8, 4 aulkiekin. Prezio finkoa", 3,45, today, null);

			
			db.persist(seller1);
			db.persist(seller2);
			db.persist(seller3);

	
			db.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Product
 	 * @throws SaleAlreadyExistException if the same product already exists for the seller
	 */
	public Sale createSale(String title, String description, int status, float price,  Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		

		System.out.println(">> DataAccess: createProduct=> title= "+title+" seller="+sellerEmail);
		try {
		

			if(pubDate.before(UtilDate.trim(new Date()))) {
				throw new MustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
			}
			if (file==null)
				throw new FileNotUploadedException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorFileNotUploadedException"));

			db.getTransaction().begin();
			
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller.doesSaleExist(title)) {
				db.getTransaction().commit();
				throw new SaleAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.SaleAlreadyExist"));
			}

			Sale sale = seller.addSale(title, description, status, price, pubDate, file);
			
			db.persist(sale);
			db.merge(seller); 
			db.getTransaction().commit();
			 System.out.println("sale stored "+sale+ " "+seller);

			

			   System.out.println("hasta aqui");

			return sale;
		} catch (NullPointerException e) {
			   e.printStackTrace();
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}
		
		
	}
	
	/**
	 * This method retrieves all the products that contain a desc text in a title
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getSales(String desc) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
	 		 if(sale.getStatus()!=10) {
		   res.add(sale);
	 		 }
		  }
	 	return res;
	}
	
	/**
	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.pubDate <=?2",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		query.setParameter(2,pubDate);
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   if(sale.getStatus()!=10) res.add(sale);
		  }
	 	return res;
	}

public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  db = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());

		
	}

	public BufferedImage getFile(String fileName) {
		File file=new File(basePath+fileName);
		BufferedImage targetImg=null;
		try {
             targetImg = rescale(ImageIO.read(file));
        } catch (IOException ex) {
            //Logger.getLogger(MainAppFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
		return targetImg;

	}
	
	public List<Seller> getUser(String log, String pass) {
		TypedQuery<Seller> query = db.createQuery("SELECT s FROM Seller s",Seller.class);   
		return query.getResultList();
	}
	
	public BufferedImage rescale(BufferedImage originalImage)
    {
		System.out.println("rescale "+originalImage);
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	
	
	public Emaitza isLogged(String log, String pass) {
		if(log == null || pass == null) return new Emaitza(false, "", null, null);
		Emaitza ema;
		TypedQuery<Seller> query = db.createQuery("SELECT s FROM Seller s WHERE s.name=?1 AND s.pass=?2",Seller.class);   
		query.setParameter(1, log);
		query.setParameter(2, pass);
		if(!query.getResultList().isEmpty()) {
			ema = new Emaitza(true, query.getResultList().get(0).getEmail(), query.getResultList().get(0), null);
		}else {
			ema = new Emaitza(false, "", null, null);
		}
		return ema;
	}
	
	public Emaitza isAdminLogged(int ID, String pass) {
	if((Integer) ID == null || pass == null) return new Emaitza(false, "", null, null);
	Emaitza ema;
	TypedQuery<Admin> query = db.createQuery("SELECT a FROM Admin a WHERE a.ID=?1 AND a.pass=?2", Admin.class);
	query.setParameter(1, ID);
	query.setParameter(2, pass);
	if(!query.getResultList().isEmpty()) {
		ema = new Emaitza(true,"", null, query.getResultList().get(0));
	}else {
		ema = new Emaitza(false, "", null, null);
	}
	return ema;
	}
	
	public Emaitza isRegistered(String mail, String user, String password) {
		if(mail ==null || user == null || password == null) return new Emaitza(false, "", null, null);
		Seller s = db.find(Seller.class, mail);
		Emaitza ema = new Emaitza(false, "", null, null);
		if(s != null) return ema;
		TypedQuery<Seller> query = db.createQuery("SELECT s FROM Seller s WHERE s.name=?1", Seller.class);
		query.setParameter(1,  user);
		if(!query.getResultList().isEmpty()) {
			return ema;
		}
		db.getTransaction().begin();
		Seller s1 = new Seller(mail, user, password);
		ema = new Emaitza(true, mail, s1, null);
		db.persist(s1);
		db.getTransaction().commit();
		return ema;
		}
	
	public Emaitza isAdminRegistered(int ID, String pass) {
		if((Integer) ID == null) return new Emaitza(false, "", null, null);
		Admin s = db.find(Admin.class, ID);
		Emaitza ema = new Emaitza(false, "", null, null);
		if(s != null) return ema;
		TypedQuery<Seller> query = db.createQuery("SELECT a FROM Admin a WHERE a.ID=?1", Seller.class);
		query.setParameter(1,  ID);
		if(!query.getResultList().isEmpty()) {
			return ema;
		}
		db.getTransaction().begin();
		Admin s1 = new Admin(ID, pass);
		ema = new Emaitza(true, "", null, s1);
		db.persist(s1);
		db.getTransaction().commit();
		return ema;
		}
	
	public String getEmail(String name, String pass) {
		String str = "";
		if(name == null || pass ==null) return str;
		TypedQuery<Seller> query = db.createQuery("SELECT s FROM Seller s WHERE s.name=?1 AND s.pass=?2", Seller.class);
		query.setParameter(1, name);
		query.setParameter(2, pass);
		if(!query.getResultList().isEmpty()) {
			return query.getResultList().get(0).getEmail();
		}
		return str;
	}
	
	public void buySale(Sale s, Seller current) {
		if(s == null || current == null || s.getSaleNumber() == null || current.getEmail() == null) return;
		try {
	        db.getTransaction().begin();

	        Sale foundS = db.find(Sale.class, s.getSaleNumber());
	        Seller foundCurrent = db.find(Seller.class, current.getEmail());

	        if (foundS == null || foundCurrent == null) {
	            throw new RuntimeException("Sale or buyer not found");
	        }
	        foundS.setStatus(10);
	        
	        float p = s.getPrice();
	        foundCurrent.setDirua(-p);
	        foundCurrent.addBought(foundS);
	        Seller seller = foundS.getSeller();
	        seller.setDirua(p);
		
	        db.merge(foundS);
	        db.merge(foundCurrent);
	        db.merge(seller);
	        db.getTransaction().commit();
		} catch(Exception e) {
			if(db.getTransaction().isActive()) {
				db.getTransaction().commit();
			}
			throw e;
		}
		

	}
	public boolean updateProfile(String email, String newName, String newPassword) {
		if (email == null || newName == null || newPassword == null) {
			return false;
		}
		try {
	        db.getTransaction().begin();
	        
	        Seller seller = db.find(Seller.class, email);
	        
	        if (seller != null) {
	            seller.setName(newName);
	            seller.setPass(newPassword);
	            
	            
	            db.merge(seller);
	            db.getTransaction().commit();
	            return true;
	        }
	        
	        db.getTransaction().commit();
	        return false;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        db.getTransaction().rollback();
	        return false;
	    }
	}
	
	public Seller getSellerByEmail(String email) {
		if(email == null) return null;
		db.getTransaction().begin();
		Seller s = db.find(Seller.class, email);
		db.getTransaction().commit();
		return s;
	}
	
	public void updateMoney(String email, float amount) {
		if(email == null) return;
	    db.getTransaction().begin();
	    Seller seller = db.find(Seller.class, email);
	    if (seller != null) {
	        seller.setDirua(amount);
	        db.merge(seller);
	    }
	    db.getTransaction().commit();
	}
	
	public void createComplaint(String message, Seller complainant, Sale sale) {
		if (message == null || complainant == null || sale == null || complainant.getEmail() == null || sale.getSaleNumber() == null) return;
		    db.getTransaction().begin();

		    Seller c = db.find(Seller.class, complainant.getEmail());
		    Sale s = db.find(Sale.class, sale.getSaleNumber());

		    Complaint complaint = new Complaint(message, c, s);
		    db.persist(complaint);

		    db.getTransaction().commit();
		}
		/*
		 * 		db.getTransaction().begin();
	    
	    Seller c = db.find(Seller.class, complainant.getEmail());
	    Sale s = db.find(Sale.class, sale.getSaleNumber());
	    s.createComplaint(message, c, s);
	    
	    // no hay que crear objetos desde DataAccess para minimizar las depedencias
	    // habría que hacerlo mediante s.createComplaint(message, c ...
		 */

	public List<Complaint> getUnmanagedComplaints() {
	    TypedQuery<Complaint> query = db.createQuery("SELECT c FROM Complaint c WHERE c.managed = false ORDER BY c.date DESC",Complaint.class);
	    return query.getResultList();
	}
	
	
	public void markComplaintAsManaged(Integer complaintId) {
	    if (complaintId == null) return;

	    db.getTransaction().begin();

	    Complaint complaint = db.find(Complaint.class, complaintId);

	    if (complaint != null) {
	        complaint.setManaged(true);
	        db.merge(complaint);
	    }

	    db.getTransaction().commit();
	}
	
	public void buyBasket(String email) {
	    db.getTransaction().begin();
	    Seller buyer = db.find(Seller.class, email);
	    List<Sale> basket = buyer.getBasket();
	    
	    for (Sale s : new ArrayList<>(basket)) { 
	        Sale sale = db.find(Sale.class, s.getSaleNumber());
	        Seller sellerOfProduct = sale.getSeller();
	        
	        
	        buyer.setDirua(-sale.getPrice());
	        sellerOfProduct.setDirua(sale.getPrice());
	        
	       
	        sale.setStatus(10); 
	        buyer.addBought(sale);
	    }
	    buyer.clearBasket(); 
	    db.getTransaction().commit();
	}

	public void addProductToBasket(String email, int saleNumber) {
		db.getTransaction().begin(); 
	    try {
	        Seller buyer = db.find(Seller.class, email);
	        Sale productToAdd = db.find(Sale.class, saleNumber);
	        
	        if (buyer.getBasket() == null) {
	            buyer.setBasket(new ArrayList<Sale>());
	        }
	        
	        List<Sale> currentBasket = buyer.getBasket();
	        
	        
	        if (!currentBasket.isEmpty()) {
	            Seller firstSeller = currentBasket.get(0).getSeller();
	            if (!firstSeller.getEmail().equals(productToAdd.getSeller().getEmail())) {
	                db.getTransaction().rollback(); 
	                throw new RuntimeException("Solo puedes añadir productos del mismo vendedor.");
	            }
	        }
	        
	        buyer.addToBasket(productToAdd);
	        db.getTransaction().commit();
	        System.out.println("Producto guardado en BD. Tamaño cesta: " + buyer.getBasket().size());
	        
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) db.getTransaction().rollback();
	        throw e;
	    }
	}
	
	public void clearBasket(String email) {
		db.getTransaction().begin();
	    Seller buyer = db.find(Seller.class, email);
	    if (buyer != null && buyer.getBasket() != null) {
	        buyer.getBasket().clear();
	        db.getTransaction().commit();
	    } else {
	        db.getTransaction().rollback();
	    }
	}
	
	public void buyMultipleSales(List<Sale> sales, Seller buyer) {
	    db.getTransaction().begin();
	    Seller b = db.find(Seller.class, buyer.getEmail());
	    
	    for (Sale s : sales) {
	        Sale saleInDb = db.find(Sale.class, s.getSaleNumber());
	        Seller vendor = saleInDb.getSeller();
	        
	        float price = saleInDb.getPrice();
	        
	        b.setDirua(-price);
	        
	        vendor.setDirua(price);
	        
	        saleInDb.setStatus(10); 
	       
	        b.addBought(saleInDb);
	    }
	    db.getTransaction().commit();
	}
	
	public List<Sale> getBasket(String email) {
		Seller seller = db.find(Seller.class, email);
	    if (seller != null && seller.getBasket() != null) {
	      
	        seller.getBasket().size(); 
	        return new ArrayList<Sale>(seller.getBasket());
	    }
	    return new ArrayList<Sale>();
	}
	
	
	public void close(){
		db.close();
		System.out.println("DataAcess closed");
	}
	public Eskaera createEskaera(String title, String description, String status, String sellerEmail) {		
		try {
			db.getTransaction().begin();
			
			Seller seller = db.find(Seller.class, sellerEmail);
			
			if (seller == null) {
				db.getTransaction().commit();
				return null;
			}
			
			Eskaera eskaera = seller.addEskaera(title, description, status);
			
			db.persist(eskaera);
			db.merge(seller);
			db.getTransaction().commit();
			
			
			return eskaera;
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();}
			return null;
		}
	}
	public List<Eskaera> getEskaerak(String search) {

		if (search == null || search.trim().isEmpty()) {
			return db.createQuery("SELECT e FROM Eskaera e", Eskaera.class).getResultList();
		}

		return db.createQuery("SELECT e FROM Eskaera e WHERE LOWER(e.title) LIKE LOWER(:search)",Eskaera.class).setParameter("search", "%" + search + "%").getResultList();
	}
	
	public Eskaera getEskaeraByID(Integer id) {
		if(id == null) return null;
		return db.find(Eskaera.class, id);
	}
	
	public boolean createEskaeraOferta(Integer eskID, String selEmail, float amount, String message) {
		if(eskID ==  null || selEmail == null || message.trim().isEmpty()) {
			return false;
		}
		try {
			db.getTransaction().begin();
			Eskaera eskaera = db.find(Eskaera.class, eskID);
			Seller seller = db.find(Seller.class, selEmail);
			if(eskaera == null || seller == null) {
				db.getTransaction().rollback();
				
			}
			EskaeraOferta oferta = eskaera.addOferta(amount, message, seller);
			db.persist(oferta);
			db.merge(eskaera);
			db.getTransaction().commit();
			return true;

			
			
			
		}catch(Exception e){
			e.printStackTrace();
			if(db.getTransaction().isActive()) {
				db.getTransaction().rollback();
				return false;
			}
		}
		return true;
	}
	
	public List<Eskaera> getMyEskaerak(Seller s) {
		TypedQuery<Eskaera> query = db.createQuery("SELECT e FROM Eskaera e WHERE e.seller=?1", Eskaera.class);
		query.setParameter(1, s);
		return query.getResultList();
	}
	
	
	public List<EskaeraOferta> getOffersByEskaera(Eskaera esk){
		TypedQuery<EskaeraOferta> query = db.createQuery("SELECT eo FROM EskaeraOferta eo WHERE eo.eskaera=?1" ,EskaeraOferta.class);
		query.setParameter(1, esk);
		return query.getResultList();
	}
	
	public Sale createSaleFromEskaeraOfer(Integer ofertaID){
		try {
			db.getTransaction().begin();
			EskaeraOferta esk = db.find(EskaeraOferta.class, ofertaID);
			if(esk == null) {
				db.getTransaction().rollback();
				return null;
			}
			Eskaera eskaera = esk.getEskaera();
			Seller seller = esk.getSeller();
			if(eskaera == null || seller == null) {
				db.getTransaction().rollback();
				return null;
			}
			Sale sale = seller.addSale(eskaera.getTitle(), esk.getMessage(), 0, esk.getAmount(), new Date(), null);
			eskaera.setBought(true);
			db.persist(sale);
			db.merge(seller);
			db.merge(eskaera);
			db.getTransaction().commit();
			return sale;
			
		}catch(Exception e) {
			e.printStackTrace();
			if(db.getTransaction().isActive()) db.getTransaction().rollback();
		}
		return null;
	}
	
	public void createReview(String authorEmail, int saleNumber, String comment, int stars) {
	    if (stars < 1 || stars > 5) throw new RuntimeException("Las estrellas deben estar entre 1 y 5.");
	    
	    db.getTransaction().begin();
	    try {
	        Sale s = db.find(Sale.class, saleNumber);
	        Seller author = db.find(Seller.class, authorEmail);
	        Seller target = s.getSeller();

	        if (s.getStatus() != 10) {
	            throw new RuntimeException("Solo puedes valorar productos ya comprados.");
	        }

	        Review newReview = new Review(author, target, s, comment, stars);
	        target.addReview(newReview);
	        
	        db.persist(newReview);
	        db.getTransaction().commit();
	    } catch (Exception e) {
	        db.getTransaction().rollback();
	        throw e;
	    }
	}
	
	public List<Sale> getBoughtsBySeller(String email) {
	    Seller seller = db.find(Seller.class, email);
	    if (seller == null) {
	        return new ArrayList<Sale>();
	    }
	    return seller.getBoughts();
	}

	public List<Sale> getSalesBySeller(String email) {
	    Seller seller = db.find(Seller.class, email);
	    if (seller == null) {
	        return new ArrayList<Sale>();
	    }
	    return seller.getSales();
	}
	public List<Review> getReviewsBySeller(String email) {
	    open();

	    
	        Seller seller = db.find(Seller.class, email);

	        if (seller == null) {
	            return new ArrayList<Review>();
	        }

	        seller.getReviewsReceived().size();

	        return new ArrayList<Review>(seller.getReviewsReceived());

	    
	}
	public String getSellerEmailBySale(Integer saleId) {

			Sale sale = db.find(Sale.class, saleId);

			if (sale == null || sale.getSeller() == null) {
				return null;
			}

			return sale.getSeller().getEmail();
	}
	
}
