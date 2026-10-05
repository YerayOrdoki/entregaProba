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
        testSeller = new Seller("admin", "123456789", "admin@shop.com");
        sut.isRegistered("admin@shop.com", "admin", "123456789");
    }

    /*
    @Test
    public void testIsLogged_WhiteBox_1_LogNull() {
        Emaitza res = sut.isLogged(null, "123456789");
        assertFalse(res.getLog());
    }
    */

    @Test
    public void testIsLogged_WhiteBox_2_PassNull() {
        Emaitza res = sut.isLogged("admin", null);
        assertFalse(res.getLog());
    }

    @Test
    public void testIsLogged_WhiteBox_3_SellerExists() {
        Emaitza res = sut.isLogged("admin", "123456789");
        assertTrue(res.getLog());
        assertEquals("admin@shop.com", res.getEmail());
    }

    @Test
    public void testIsLogged_WhiteBox_4_SellerDoesNotExist() {
        Emaitza res = sut.isLogged("admin", "wrong");
        assertFalse(res.getLog());
    }
}
