package dataAccess;

import domain.Admin;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import domain.Seller;

@XmlAccessorType(XmlAccessType.FIELD)
public class Emaitza implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean logeatu;
    private String email;
    private Seller seller;
    private Admin admin;

    public Emaitza() {
    	
    }
    
    public Emaitza(boolean logeatu, String email, Seller s, Admin a) {
		this.logeatu = logeatu;
		this.email=email;
		this.seller = s;
		this.admin = a;
	}
	public boolean getLog() {
		return this.logeatu;
	}
	public String getEmail() {
		return email;
	}
	public Seller getSeller() {
		return seller;
	}
	public Admin getAdmin(){
		return admin;
	}
    public void setLog(boolean logeatu) {
        this.logeatu = logeatu;
    }


    public void setEmail(String email) {
        this.email = email;
    }
}