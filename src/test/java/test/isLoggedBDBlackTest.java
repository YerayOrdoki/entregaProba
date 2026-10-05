package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.Emaitza;
import domain.Seller;
import dataAccess.DataAccess;

public class isLoggedBDBlackTest {

    private DataAccess sut;
    private Seller testSeller;

    @Before
    public void setUp() {
        sut = new DataAccess();
        sut.open();
        
        // PK1 eta PK2 probatzeko erabiliko den bezeroa gorde
        testSeller = new Seller("seller1", "123", "seller1@shop.com");
        sut.isRegistered("seller1@shop.com", "seller1", "123");
    }


    @Test
    public void testPK1_ValidSellerAndPass() {
        Emaitza res = sut.isLogged("seller1", "123");
        assertTrue(res.getLog());
        assertEquals("seller1@shop.com", res.getEmail());
    }

    @Test
    public void testPK2_ValidSellerWrongPass() {
        Emaitza res = sut.isLogged("seller1", "wrongpass");
        assertFalse(res.getLog());
    }
    

    @Test
    public void testPK3_NullLog() {
        Emaitza res = sut.isLogged(null, "1234");
        assertFalse(res.getLog());
    }


    @Test
    public void testPK4_NullPass() {
        Emaitza res = sut.isLogged("seller1", null);
        assertFalse(res.getLog());
    }
}
