package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import dataAccess.DataAccess;
import dataAccess.Emaitza;
import domain.Seller;
/*
 * isRegistered metodoaren kutxa zuriko probak.
 * Test bakoitzak bere mock objektuak sortzen ditu.
 *
 * Kontrol-fluxuaren grafoko bideak:
 * P1: B1.1(T)
 * P2: B1.1(F) -> B1.2(T)
 * P3: B1.1(F) -> B1.2(F) -> B1.3(T)
 * P4: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(T)
 * P5: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(T)
 * P6: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(F)
 */
public class IsRegisteredMockWhiteTest {
    /*
     * DataAccess objektua DB simulatuarekin sortzen du.
     * Persistence-ren mock estatikoa konstruktorea exekutatzen
     * den bitartean mantentzen da, eta ondoren ixten da.
     */
    private DataAccess sortuSut(EntityManager db) {
        EntityManagerFactory factory =
            Mockito.mock(EntityManagerFactory.class);
        Mockito.when(factory.createEntityManager()).thenReturn(db);
        try (MockedStatic<Persistence> persistenceMock =
                Mockito.mockStatic(Persistence.class)) {
            persistenceMock.when(() ->
                Persistence.createEntityManagerFactory(Mockito.anyString())
            ).thenReturn(factory);
            return new DataAccess(db);
        }
    }
    /*
     * P1: B1.1(T)
     * Emaila null da.
     * Erregistroa baztertu behar da, DBra deitu gabe.
     */
    @Test
    public void test1_mailNull() {
        // Datu-basearen mock-a eta SUT objektua sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        DataAccess sut = sortuSut(db);
        // Metodoa parametro baliogabearekin exekutatu.
        Emaitza result = sut.isRegistered(null, "ane", "1234");
        // Espero den emaitza egiaztatu.
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
        // Datu-basera deirik egin ez dela egiaztatu.
        Mockito.verifyNoInteractions(db);
    }
    /*
     * P2: B1.1(F) -> B1.2(T)
     * Emaila ez da null, baina erabiltzaile-izena null da.
     * Erregistroa baztertu behar da, DBra deitu gabe.
     */
    @Test
    public void test2_userNull() {
        // Datu-basearen mock-a eta SUT objektua sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        DataAccess sut = sortuSut(db);
        // Metodoa parametro baliogabearekin exekutatu.
        Emaitza result = sut.isRegistered("ane2@ehu.eus", null, "1234");
        // Espero den emaitza egiaztatu.
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
        Mockito.verifyNoInteractions(db);
    }
    /*
     * P3: B1.1(F) -> B1.2(F) -> B1.3(T)
     * Emaila eta erabiltzaile-izena ez dira null,
     * baina pasahitza null da.
     * Erregistroa baztertu behar da, DBra deitu gabe.
     */
    @Test
    public void test3_passwordNull() {
        // Datu-basearen mock-a eta SUT objektua sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        DataAccess sut = sortuSut(db);
        // Metodoa parametro baliogabearekin exekutatu.
        Emaitza result = sut.isRegistered("ane3@ehu.eus", "ane3", null);
        // Espero den emaitza egiaztatu.
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
        Mockito.verifyNoInteractions(db);
    }
    /*
     * P4: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(T)
     * Parametro guztiak ez-null dira, baina emaila DBan dago.
     * Erregistroa baztertu behar da.
     */
    @Test
    public void test4_mailAlreadyExists() {
        // Datu-basearen mock-a eta SUT objektua sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "ane4@ehu.eus";
        String user = "ane4";
        String password = "1234";
        // Emaila DBan erregistratuta dagoela simulatu.
        Seller sellerInDb = new Seller(mail, user, password);
        Mockito.when(db.find(Seller.class, mail)).thenReturn(sellerInDb);
        // Metodoa exekutatu.
        Emaitza result = sut.isRegistered(mail, user, password);
        // Espero den emaitza egiaztatu.
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
        // Emaila bilatu dela egiaztatu.
        Mockito.verify(db).find(Seller.class, mail);
        // Erabiltzaile-izena ez dela kontsultatu egiaztatu.
        Mockito.verify(db, Mockito.never()).createQuery(
            Mockito.anyString(), Mockito.eq(Seller.class)
        );
        // Seller berririk gordetzen ez dela egiaztatu.
        Mockito.verify(db, Mockito.never()).persist(Mockito.any(Seller.class));
        // Transakziorik eskatu ez dela egiaztatu.
        Mockito.verify(db, Mockito.never()).getTransaction();
    }
    /*
     * P5: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(T)
     * Emaila libre dago, baina erabiltzaile-izena hartuta dago.
     * Erregistroa baztertu behar da.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test5_userAlreadyExists() {
        // Test honetarako mock-ak sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        TypedQuery<Seller> query = Mockito.mock(TypedQuery.class);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "ane5@ehu.eus";
        String user = "ane5";
        String password = "1234";
        Seller sellerWithSameName =
            new Seller("bestehelbide@ehu.eus", user, "bestePassword");
        // Emaila libre dagoela simulatu.
        Mockito.when(db.find(Seller.class, mail)).thenReturn(null);
        // Erabiltzaile-izena hartuta dagoela simulatu.
        Mockito.when(db.createQuery(
            "SELECT s FROM Seller s WHERE s.name=?1",
            Seller.class
        )).thenReturn(query);
        Mockito.when(query.setParameter(1, user)).thenReturn(query);
        Mockito.when(query.getResultList())
            .thenReturn(Collections.singletonList(sellerWithSameName));
        // Metodoa exekutatu.
        Emaitza result = sut.isRegistered(mail, user, password);
        // Espero den emaitza egiaztatu.
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
        // Erabiltzaile-izenaren kontsulta egin dela egiaztatu.
        Mockito.verify(query).setParameter(1, user);
        Mockito.verify(query).getResultList();
        // Seller berririk gordetzen ez dela egiaztatu.
        Mockito.verify(db, Mockito.never()).persist(Mockito.any(Seller.class));
        // Transakziorik eskatu ez dela egiaztatu.
        Mockito.verify(db, Mockito.never()).getTransaction();
    }
    /*
     * P6: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(F)
     * Emaila eta erabiltzaile-izena libre daude.
     * Erregistroa onartu eta Seller berria gordetzera bidali behar da.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test6_registersSeller() {
        // Test honetarako mock-ak sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        EntityTransaction et = Mockito.mock(EntityTransaction.class);
        TypedQuery<Seller> query = Mockito.mock(TypedQuery.class);
        Mockito.when(db.getTransaction()).thenReturn(et);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "ane6@ehu.eus";
        String user = "ane6";
        String password = "1234";
        // Emaila eta erabiltzaile-izena libre daudela simulatu.
        Mockito.when(db.find(Seller.class, mail)).thenReturn(null);
        Mockito.when(db.createQuery(
            "SELECT s FROM Seller s WHERE s.name=?1",
            Seller.class
        )).thenReturn(query);
        Mockito.when(query.setParameter(1, user)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Collections.emptyList());
        // Metodoa exekutatu.
        Emaitza result = sut.isRegistered(mail, user, password);
        // Espero den emaitza egiaztatu.
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());
        assertEquals(mail, result.getSeller().getEmail());
        assertEquals(user, result.getSeller().getName());
        // Gordetzeko deia eta transakzioa egiaztatu.
        Mockito.verify(et).begin();
        Mockito.verify(db).persist(result.getSeller());
        Mockito.verify(et).commit();
    }
}