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

public class isLoggedMockBlackTest {

    private EntityManager db;
    private TypedQuery<Seller> query;
    private DataAccess sut;

    @Before
    public void setUp() {
        db = Mockito.mock(EntityManager.class);
        query = Mockito.mock(TypedQuery.class);
        sut = new DataAccess(db);
    }

    // PK1 (BK 1, 2, 3): ("seller1", "123") -> Emaitza(true, "seller1@shop.com", sellerObj, null)
    @Test
    public void testPK1_ValidSellerAndPass() {
        String log = "seller1";
        String pass = "123";

        Seller seller = new Seller("seller1@shop.com",log, pass);
        List<Seller> list = new ArrayList<>();
        list.add(seller);

        Mockito.when(db.createQuery(Mockito.anyString(), Mockito.eq(Seller.class))).thenReturn(query);
        Mockito.when(query.setParameter(1, log)).thenReturn(query);
        Mockito.when(query.setParameter(2, pass)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(list);

        Emaitza res = sut.isLogged(log, pass);

        assertNotNull(res);
        assertTrue(res.getLog());
        assertEquals("seller1@shop.com", res.getEmail());
        assertNotNull(res.getSeller());
    }

    // PK2 (BK 1, 2, 4): ("seller1", "wrongpass") -> Emaitza(false, "", null, null)
    @Test
    public void testPK2_ValidSellerWrongPass() {
        String log = "seller1";
        String pass = "wrongpass";

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
    
    /*
    // PK3 (BK 5): (null, "1234") -> Emaitza(false, "", null, null)
    @Test
    public void testPK3_NullLog() {
        Emaitza res = sut.isLogged(null, "1234");

        assertNotNull(res);
        assertFalse(res.getLog());
        assertEquals("", res.getEmail());
        assertNull(res.getSeller());
    }
   */

    // PK4 (BK 6): ("seller1", null) -> Emaitza(false, "", null, null)
    @Test
    public void testPK4_NullPass() {
        Emaitza res = sut.isLogged("seller1", null);

        assertNotNull(res);
        assertFalse(res.getLog ());
        assertEquals("", res.getEmail());
        assertNull(res.getSeller());
    }
}
