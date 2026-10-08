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
 * isRegistered metodoaren kutxa beltzeko probak.
 * Test bakoitzak bere mock objektuak sortzen ditu.
 *
 * CE1: email nulua.
 * CE2: erabiltzaile-izen nulua.
 * CE3: pasahitz nulua.
 * CE4: emaila erregistratuta dago.
 * CE5: emaila libre dago, baina erabiltzaile-izena hartuta dago.
 * CE6: emaila eta erabiltzaile-izena libre daude.
 * CE7: email hutsa.
 * CE8: erabiltzaile-izen hutsa.
 * CE9: pasahitz hutsa.
 */
public class IsRegisteredMockBlackTest {
    /*
     * DataAccess objektua sortzeko konfigurazioa.
     * Persistence-ren mock estatikoa konstruktorea exekutatzen
     * den bitartean mantentzen da, eta ondoren automatikoki ixten da.
     * Test bakoitzak sortutako DB mock-a jasotzen du.
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
     * CE1: email nulua.
     * Erregistroa baztertu behar da, DBra deitu gabe.
     */
    @Test
    public void testCE1_nullMail() {
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
     * CE2: erabiltzaile-izen nulua.
     * Erregistroa baztertu behar da, DBra deitu gabe.
     */
    @Test
    public void testCE2_nullUser() {
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
     * CE3: pasahitz nulua.
     * Erregistroa baztertu behar da, DBra deitu gabe.
     */
    @Test
    public void testCE3_nullPassword() {
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
     * CE4: emaila dagoeneko erregistratuta dago.
     * Erregistroa baztertu behar da.
     */
    @Test
    public void testCE4_existingMail() {
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
        // Seller berririk gordetzen ez dela egiaztatu.
        Mockito.verify(db, Mockito.never()).persist(Mockito.any(Seller.class));
    }
    /*
     * CE5: emaila libre dago, baina erabiltzaile-izena hartuta dago.
     * Erregistroa baztertu behar da.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCE5_existingUser() {
        // Test honetarako mock-ak sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        TypedQuery<Seller> query = Mockito.mock(TypedQuery.class);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "ane5@ehu.eus";
        String user = "ane5";
        String password = "1234";
        Seller existingSeller =
            new Seller("bestea@ehu.eus", user, "bestePasahitza");
        // Emaila libre dagoela simulatu.
        Mockito.when(db.find(Seller.class, mail)).thenReturn(null);
        // Erabiltzaile-izena hartuta dagoela simulatu.
        Mockito.when(db.createQuery(
            "SELECT s FROM Seller s WHERE s.name=?1",
            Seller.class
        )).thenReturn(query);
        Mockito.when(query.setParameter(1, user)).thenReturn(query);
        Mockito.when(query.getResultList())
            .thenReturn(Collections.singletonList(existingSeller));
        // Metodoa exekutatu.
        Emaitza result = sut.isRegistered(mail, user, password);
        // Espero den emaitza egiaztatu.
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
        Mockito.verify(db, Mockito.never()).persist(Mockito.any(Seller.class));
    }
    /*
     * CE6: emaila eta erabiltzaile-izena libre daude.
     * Erregistroa onartu eta Seller berria gordetzera bidali behar da.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCE6_validRegistration() {
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
    /*
     * CE7: email hutsa.
     * Aurreko testaren espero den portaera mantentzen da:
     * email hutsa onartzea.
     * Zehaztapenak debekatzen badu, espero den emaitza aldatu behar da.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCE7_emptyMail() {
        // Test honetarako mock-ak sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        EntityTransaction et = Mockito.mock(EntityTransaction.class);
        TypedQuery<Seller> query = Mockito.mock(TypedQuery.class);
        Mockito.when(db.getTransaction()).thenReturn(et);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "";
        String user = "aneEmptyMail";
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
        // Aurreko testaren espero den emaitza egiaztatu.
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());
        Mockito.verify(db).persist(result.getSeller());
    }
    /*
     * CE8: erabiltzaile-izen hutsa.
     * Aurreko testaren espero den portaera mantentzen da:
     * erabiltzaile-izen hutsa onartzea.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCE8_emptyUser() {
        // Test honetarako mock-ak sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        EntityTransaction et = Mockito.mock(EntityTransaction.class);
        TypedQuery<Seller> query = Mockito.mock(TypedQuery.class);
        Mockito.when(db.getTransaction()).thenReturn(et);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "emptyuser@ehu.eus";
        String user = "";
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
        // Aurreko testaren espero den emaitza egiaztatu.
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());
        Mockito.verify(db).persist(result.getSeller());
    }
    /*
     * CE9: pasahitz hutsa.
     * Aurreko testaren espero den portaera mantentzen da:
     * pasahitz hutsa onartzea.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCE9_emptyPassword() {
        // Test honetarako mock-ak sortu.
        EntityManager db = Mockito.mock(EntityManager.class);
        EntityTransaction et = Mockito.mock(EntityTransaction.class);
        TypedQuery<Seller> query = Mockito.mock(TypedQuery.class);
        Mockito.when(db.getTransaction()).thenReturn(et);
        DataAccess sut = sortuSut(db);
        // Sarrerako datuak.
        String mail = "emptypassword@ehu.eus";
        String user = "emptyPassword";
        String password = "";
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
        // Aurreko testaren espero den emaitza egiaztatu.
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());
        Mockito.verify(db).persist(result.getSeller());
    }
}