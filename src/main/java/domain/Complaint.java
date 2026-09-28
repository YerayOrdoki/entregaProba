package domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Complaint implements Serializable {
    @Id 
    @GeneratedValue
    private Integer id;
    private String message;
    private Date date;
    private boolean managed = false;;

    @ManyToOne
    @XmlIDREF
    private Seller complainant;
    @ManyToOne
    @XmlIDREF
    private Sale sale;        

    public Complaint() { super(); }

    public Complaint(String message, Seller complainant, Sale sale) {
        this.message = message;
        this.complainant = complainant;
        this.sale = sale;
        this.date = new Date();
    }

   
    public String getMessage() { return message; }
    public Date getDate() { return date; }
    public Seller getComplainant() { return complainant; }
    public Sale getSale() { return sale; }
    public Integer getID() {
    	return this.id;
    }
    public boolean isManaged() {
    	return this.managed;
    }
    public void setManaged(boolean a) {
    	this.managed = a;
    }


}