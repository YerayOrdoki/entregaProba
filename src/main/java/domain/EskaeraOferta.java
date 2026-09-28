package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EskaeraOferta implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private float amount;
	private String message;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@ManyToOne
	private Seller seller;
	
	
	@XmlIDREF
	@ManyToOne
	private Eskaera eskaera;
	
	public EskaeraOferta() {
	}

	public EskaeraOferta(float amount, String message, Seller seller, Eskaera eskaera) {
		this.amount = amount;
		this.message = message;
		this.creationDate = new Date();
		this.seller = seller;
		this.eskaera = eskaera;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) { this.seller = seller; }

	public Eskaera getEskaera() {
		return eskaera;
	}

	public void setEskaera(Eskaera eskaera) {
		this.eskaera = eskaera;
	}
	
}