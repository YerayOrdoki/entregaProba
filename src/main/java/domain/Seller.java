package domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Seller implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlID
	@Id 
	private String email;
	private String name; 
	private String pass;
	private float dirua;
	
	@XmlTransient
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private List<Sale> sales = new ArrayList<Sale>();
	@XmlTransient
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private List<Sale> boughts = new ArrayList<Sale>();
	@XmlTransient
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Sale> basket = new ArrayList<Sale>();
	@XmlTransient
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Eskaera> eskaerak = new ArrayList<Eskaera>();
	@XmlTransient
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Review> reviewsReceived = new ArrayList<Review>();

	public Seller() {
		super();
	}

	public Seller(String email, String name, String password) {
		this.email = email;
		this.name = name;
		this.pass = password;
		this.dirua = 0;
		
	}
	
	public void addBought(Sale s) {
		this.boughts.add(s);
	}
	
	public void addSell(Sale s) {
		this.sales.add(s);
	}
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPass() {
		return pass;
	}
	
	public float getDirua() {
		return this.dirua;
	}
	
	public void setDirua(float dirua) {
		this.dirua += dirua;
	}
	
	public void addToBasket(Sale s) {
	    if (this.basket == null) {
	        this.basket = new ArrayList<Sale>();
	    }
	    this.basket.add(s);
	}

	public List<Sale> getBasket() { 
	    return basket; 
	}
	
	public void clearBasket() {
	    if (this.basket != null) {
	        this.basket.clear();
	    }
	}
	public void setBasket(List<Sale> basket) {
	    this.basket = basket;
	}
	
	public String toString(){
		return email+";"+name+sales;
	}
	
	public List<Sale> getSales(){return sales;}
	public List<Sale> getBoughts() {return boughts;}
	
	public void addReview(Review r) {
	    this.reviewsReceived.add(r);
	}

	public List<Review> getReviewsReceived() {
	    return reviewsReceived;
	}

	public float getAverageRating() {
	    if (reviewsReceived == null || reviewsReceived.isEmpty()) return 0;
	    float sum = 0;
	    for (Review r : reviewsReceived) sum += r.getStars();
	    return sum / reviewsReceived.size();
	}
	
	/**
	 * This method creates/adds a sale to a seller
	 * 
	 * @param title of the sale
	 * @param description of the sale
	 * @param status 
	 * @param selling price
	 * @param publicationDate
	 * @return Sale
	 */
	
	


	public Sale addSale(String title, String description, int status, float price,  Date pubDate, File file)  {
		
		Sale sale=new Sale(title, description, status, price,  pubDate, file, this);
        sales.add(sale);
        return sale;
	}
	/**
	 * This method checks if the ride already exists for that driver
	 * 
	 * @param from the origin location 
	 * @param to the destination location 
	 * @param date the date of the ride 
	 * @return true if the ride exists and false in other case
	 */
	public boolean doesSaleExist(String title)  {	
		for (Sale s:sales)
			if ( s.getTitle().compareTo(title)==0 )
			 return true;
		return false;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Seller other = (Seller) obj;
		if (email != other.email)
			return false;
		return true;
	}

	public void setPass(String newPassword) {
		// TODO Auto-generated method stub
		this.pass= newPassword;
	}
	public Eskaera addEskaera(String title, String description, String status) {
		Eskaera eskaera = new Eskaera(title, description, status,this);
		eskaerak.add(eskaera);
		return eskaera;
	}

	
}
