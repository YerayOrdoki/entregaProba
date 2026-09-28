package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlID;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Eskaera implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@XmlID
	@Transient
	private String xmlId;

	private String title;
	private String status;
	private String description;
	private boolean bought = false;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@ManyToOne
	private Seller seller;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private ArrayList<EskaeraOferta> ofertak = new ArrayList<>();
	
	@PostLoad
	@PostPersist
	private void actualizarXmlId() {
		if (id != null) {
			this.xmlId = "eskaera_" + id;
		}
	}

	public String getXmlId() {
		if (xmlId == null && id != null) {
			xmlId = "eskaera_" + id;
		}
		return xmlId;
	}
	
	public Eskaera() {
	}

	public Eskaera(String title, String status, String description, Seller s) {
		this.title = title;
		this.status = status;
		this.description = description;
		this.creationDate = new Date();
		this.seller = s;
	}

	public boolean isBought() {
		return bought;
	}

	public void setBought(boolean bought) {
		this.bought = bought;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public Seller getSeller() {
		return this.seller;
	}
	
	public List<EskaeraOferta> getOfertak(){
		return  ofertak;
	}
	
	public EskaeraOferta addOferta(float amount, String message, Seller seller) {
		EskaeraOferta oferta = new EskaeraOferta(amount, message, seller, this);
		ofertak.add(oferta);
		return oferta;

		
	}
	@Override
	public String toString() {
		return title + " - " + status + " - " + creationDate;
	}

}