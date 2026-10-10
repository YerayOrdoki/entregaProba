package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.Emaitza;
import domain.Seller;
import dataAccess.DataAccess; // Zure DB kudeatzailea

public class IsLoggedBDWhiteTest {

    private DataAccess sut;
    private Seller testSeller;

    @Before
    public void setUp() {
        sut = new DataAccess();
        sut.open(); // BD ireki
        
        // Sartu probako Seller bat DBan
        testSeller = new Seller("seller1", "123", "seller1@shop.com");
        sut.isRegistered("seller1@shop.com", "seller1", "123");
    }


    @Test
    public void testIsLogged_WhiteBox_1_LogNull() {
        Emaitza res = sut.isLogged(null, "123");
        
        assertFalse(res.getLog());
        assertNull(res.getSeller());
        assertNull(res.getAdmin());
        assertEquals("",res.getEmail());
    }

    
    @Test
    public void testIsLogged_WhiteBox_2_PassNull() {
        Emaitza res = sut.isLogged("seller1", null);
        
        assertFalse(res.getLog());
        assertNull(res.getSeller());
        assertNull(res.getAdmin());
        assertEquals("",res.getEmail());
    }

    @Test
    public void testIsLogged_WhiteBox_3_SellerExists() {
        Emaitza res = sut.isLogged("seller1", "123");
        
        assertTrue(res.getLog());
        assertEquals("seller1@shop.com", res.getEmail());
        assertNotNull(res.getSeller());
        
        
        assertEquals("seller1", res.getSeller().getName());
        assertEquals("seller1@shop.com", res.getSeller().getEmail());
    }

    @Test
    public void testIsLogged_WhiteBox_4_SellerDoesNotExist() {
        Emaitza res = sut.isLogged("seller1", "wrong");
        
        assertFalse(res.getLog());
        assertNull(res.getAdmin());
        assertNull(res.getSeller());
        assertEquals("",res.getEmail());
    }
}
