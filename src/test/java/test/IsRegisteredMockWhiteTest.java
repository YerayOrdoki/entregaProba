package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import dataAccess.Emaitza;
import domain.Seller;

public class IsRegisteredMockWhiteTest {

    private DataAccess sut;

    private MockedStatic<Persistence> persistenceMock;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager db;

    @Mock
    private EntityTransaction et;

    @Mock
    private TypedQuery<Seller> query;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        persistenceMock = Mockito.mockStatic(Persistence.class);

        persistenceMock.when(() ->
            Persistence.createEntityManagerFactory(Mockito.any())
        ).thenReturn(entityManagerFactory);

        Mockito.doReturn(db)
               .when(entityManagerFactory)
               .createEntityManager();

        Mockito.doReturn(et)
               .when(db)
               .getTransaction();

        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    /*
     * P1:
     * B1.1(T): mail == null
     */
    @Test
    public void test1_mailNull() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * P2:
     * B1.1(F) -> B1.2(T): user == null
     */
    @Test
    public void test2_userNull() {
        Emaitza result = sut.isRegistered(
            "ane2@ehu.eus",
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * P3:
     * B1.1(F) -> B1.2(F) -> B1.3(T): password == null
     */
    @Test
    public void test3_passwordNull() {
        Emaitza result = sut.isRegistered(
            "ane3@ehu.eus",
            "ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * P4:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B2(T)
     * Mail hori DBan dago.
     */
    @Test
    public void test4_mailAlreadyExists() {
        String mail = "ane4@ehu.eus";
        String user = "ane4";
        String password = "1234";

        Seller sellerInDb = new Seller(mail, user, password);

        Mockito.when(db.find(Seller.class, mail))
               .thenReturn(sellerInDb);

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verify(db, Mockito.times(1))
               .find(Seller.class, mail);

        Mockito.verify(db, Mockito.never())
               .createQuery(
                   Mockito.anyString(),
                   Mockito.eq(Seller.class)
               );

        Mockito.verify(db, Mockito.never())
               .persist(Mockito.any(Seller.class));

        Mockito.verify(et, Mockito.never()).begin();
        Mockito.verify(et, Mockito.never()).commit();
    }

    /*
     * P5:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B2(F) -> B3(T)
     * Maila libre dago, baina user izena DBan badago.
     */
    @Test
    public void test5_userAlreadyExists() {
        String mail = "ane5@ehu.eus";
        String user = "ane5";
        String password = "1234";

        Seller sellerWithSameName = new Seller(
            "bestehelbide@ehu.eus",
            user,
            "bestePassword"
        );

        Mockito.when(db.find(Seller.class, mail))
               .thenReturn(null);

        Mockito.when(db.createQuery(
                "SELECT s FROM Seller s WHERE s.name=?1",
                Seller.class
        )).thenReturn(query);

        Mockito.when(query.setParameter(1, user))
               .thenReturn(query);

        Mockito.when(query.getResultList())
               .thenReturn(List.of(sellerWithSameName));

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verify(db, Mockito.times(1))
               .find(Seller.class, mail);

        Mockito.verify(db, Mockito.times(1))
               .createQuery(
                   "SELECT s FROM Seller s WHERE s.name=?1",
                   Seller.class
               );

        Mockito.verify(query, Mockito.times(1))
               .setParameter(1, user);

        Mockito.verify(query, Mockito.times(1))
               .getResultList();

        Mockito.verify(db, Mockito.never())
               .persist(Mockito.any(Seller.class));

        Mockito.verify(et, Mockito.never()).begin();
        Mockito.verify(et, Mockito.never()).commit();
    }

    /*
     * P6:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B2(F) -> B3(F)
     * Maila eta user izena libre daude: seller berria erregistratzen da.
     */
    @Test
    public void test6_registersSeller() {
        String mail = "ane6@ehu.eus";
        String user = "ane6";
        String password = "1234";

        Mockito.when(db.find(Seller.class, mail))
               .thenReturn(null);

        Mockito.when(db.createQuery(
                "SELECT s FROM Seller s WHERE s.name=?1",
                Seller.class
        )).thenReturn(query);

        Mockito.when(query.setParameter(1, user))
               .thenReturn(query);

        Mockito.when(query.getResultList())
               .thenReturn(Collections.emptyList());

        Emaitza result = sut.isRegistered(mail, user, password);

        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        Mockito.verify(et, Mockito.times(1)).begin();

        ArgumentCaptor<Seller> sellerCaptor =
            ArgumentCaptor.forClass(Seller.class);

        Mockito.verify(db, Mockito.times(1))
               .persist(sellerCaptor.capture());

        Seller persistedSeller = sellerCaptor.getValue();

        assertEquals(mail, persistedSeller.getEmail());
        assertEquals(user, persistedSeller.getName());

        Mockito.verify(et, Mockito.times(1)).commit();
    }
}