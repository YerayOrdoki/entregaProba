package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import dataAccess.DataAccess;
import domain.Seller;
import javax.persistence.*;

public class GetEmailBDWhiteTest {
    private DataAccess sut;
    private EntityManager db;
    private EntityManagerFactory emf;
    private String mail = "test@gmail.com";
    private String name = "TestUser";
    private String pass = "pass123";

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("objectdb:market.odb");
        db = emf.createEntityManager();
        sut = new DataAccess(db);
        
        db.getTransaction().begin();
        db.persist(new Seller(mail, name, pass)); 
        db.getTransaction().commit();
    }

    @After
    public void tearDown() {
        db.getTransaction().begin();
        Seller s = db.find(Seller.class, mail);
        if (s != null) db.remove(s);
        db.getTransaction().commit();
        db.close(); emf.close();
    }

    @Test // name es null
    public void test1_NameNull() {
        assertEquals("", sut.getEmail(null, pass));
    }

    @Test // pass es null
    public void test2_PassNull() {
        assertEquals("", sut.getEmail(name, null));
    }

    @Test // Erabiltzailea ez da aukitzen
    public void test3_NotFound() {
        assertEquals("", sut.getEmail("Inexistente", pass));
    }

    @Test // Ondo
    public void test4_Success() {
        assertEquals(mail, sut.getEmail(name, pass));
    }
}