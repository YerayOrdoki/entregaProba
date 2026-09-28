package domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer reviewId;
    
    private String comment;
    private int stars; 
    
    @OneToOne
    private Sale sale; 
    
    @ManyToOne
    private Seller author; 
    
    @ManyToOne
    private Seller target; 

    public Review() {}

    public Review(Seller author, Seller target, Sale sale, String comment, int stars) {
        this.author = author;
        this.target = target;
        this.sale = sale;
        this.comment = comment;
        this.stars = stars;
    }

    // Getters
    public int getStars() { return stars; }
    public String getComment() { return comment; }
}