package domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


import java.io.Serializable;


@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Admin implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
@Id
private int ID;
private String pass;

public Admin() {
	super();
}
public Admin(int ID, String pass) {
	this.ID = ID;
	this.pass = pass;
}


public int getID() {
	return ID;
}

public String getPass() {
	return pass;
}
public void setPass(String pass) {
	this.pass = pass;
}

}
