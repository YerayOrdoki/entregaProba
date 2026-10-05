package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


import dataAccess.Emaitza;
import domain.Seller;
import dataAccess.DataAccess; 

public class IsLoggedMockWhiteTest {

    private EntityManager db;
    private TypedQuery<Seller> query;
    private DataAccess sut; 

    @Before
    public void setUp() {
        db = Mockito.mock(EntityManager.class);
        query = Mockito.mock(TypedQuery.class);
        sut = new DataAccess(db); // Eraikitzaileak EntityManager jasotzen badu
    }

    // 1. Kasua: log == null -> (null, "123456789") -> Emaitza(false, "", null, null)
    /*
    @Test
    public void testIsLogged_WhiteBox_1_LogNull() {
        String log = null;
        String pass = "123456789";

        Emaitza res = sut.isLogged(log, pass);

        assertNotNull(res);
        assertFalse(res.getLog());
        assertEquals("", res.getEmail());
        assertNull(res.getSeller());
    }
    */

    // 2. Kasua: log != null, pass == null -> ("admin", null) -> Emaitza(false, "", null, null)
    @Test
    public void testIsLogged_WhiteBox_2_PassNull() {
        String log = "admin";
        String pass = null;

        Emaitza res = sut.isLogged(log, pass);

        assertNotNull(res);
        assertFalse(res.getLog());
        assertEquals("", res.getEmail());
        assertNull(res.getSeller());
    }

    // 3. Kasua: log != null, pass != null, Seller existitu -> ("admin", "123456789") -> True + Seller
    @Test
    public void testIsLogged_WhiteBox_3_SellerExists() {
        String log = "admin";
        String pass = "123456789";

        Seller seller = new Seller("admin@shop.com",log, pass);
        List<Seller> list = new ArrayList<>();
        list.add(seller);

        Mockito.when(db.createQuery(Mockito.anyString(), Mockito.eq(Seller.class))).thenReturn(query);
        Mockito.when(query.setParameter(1, log)).thenReturn(query);
        Mockito.when(query.setParameter(2, pass)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(list);

        Emaitza res = sut.isLogged(log, pass);

        assertNotNull(res);
        assertTrue(res.getLog());
        assertEquals("admin@shop.com", res.getEmail());
        assertNotNull(res.getSeller());
    }

    // 4. Kasua: log != null, pass != null, Seller ez existitu -> ("admin", "wrong") -> Emaitza(false, "", null, null)
    @Test
    public void testIsLogged_WhiteBox_4_SellerDoesNotExist() {
        String log = "admin";
        String pass = "wrong";

        List<Seller> emptyList = new ArrayList<>();

        Mockito.when(db.createQuery(Mockito.anyString(), Mockito.eq(Seller.class))).thenReturn(query);
        Mockito.when(query.setParameter(1, log)).thenReturn(query);
        Mockito.when(query.setParameter(2, pass)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(emptyList);

        Emaitza res = sut.isLogged(log, pass);

        assertNotNull(res);
        assertFalse(res.getLog());
        assertEquals("", res.getEmail());
        assertNull(res.getSeller());
    }
}
