package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

/*
 * isRegistered metodoaren kutxa beltzeko probak Mockito erabiliz.
 *
 * Baliokidetasun-klaseak:
 * CE1: mail == null.
 * CE2: user == null.
 * CE3: password == null.
 * CE4: posta elektronikoa DBan erregistratuta dago.
 * CE5: posta elektronikoa libre dago, baina erabiltzaile-izena hartuta dago.
 * CE6: posta elektronikoa eta erabiltzaile-izena libre daude.
 *
 * Muga-probak:
 * CE7: posta elektroniko hutsa.
 * CE8: erabiltzaile-izen hutsa.
 * CE9: pasahitz hutsa.
 */
public class IsRegisteredMockBlackTest {

    /*
     * Frogatu beharreko sistema.
     * SUT = System Under Test.
     */
    private DataAccess sut;

    /*
     * Persistence klasearen metodo estatikoa mockeatzeko objektua.
     */
    protected MockedStatic<Persistence> persistenceMock;

    /*
     * Datu-baseko osagaien mock objektuak.
     */
    @Mock
    protected EntityManagerFactory entityManagerFactory;

    @Mock
    protected EntityManager db;

    @Mock
    protected EntityTransaction et;

    @Mock
    protected TypedQuery<Seller> query;

    /*
     * Test bakoitzaren aurretik exekutatzen den konfigurazioa.
     *
     * EntityManagerFactory, EntityManager, EntityTransaction eta
     * TypedQuery objektuen mockak sortzen dira.
     *
     * DataAccess objektuari EntityManager mock-a ematen zaio;
     * horrela, testek ez dute benetako datu-baserik erabiltzen.
     */
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

    /*
     * Test bakoitzaren ondoren mock estatikoa ixten da.
     */
    @After
    public void tearDown() {
        persistenceMock.close();
    }

    /*
     * CE1:
     * mail == null.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Parametroa baliogabea denez, ez da datu-basera deirik egin behar.
     */
    @Test
    public void testCE1_nullMail() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * CE2:
     * user == null.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Parametroa baliogabea denez, ez da datu-basera deirik egin behar.
     */
    @Test
    public void testCE2_nullUser() {
        Emaitza result = sut.isRegistered(
            "ane2@ehu.eus",
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * CE3:
     * password == null.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Parametroa baliogabea denez, ez da datu-basera deirik egin behar.
     */
    @Test
    public void testCE3_nullPassword() {
        Emaitza result = sut.isRegistered(
            "ane3@ehu.eus",
            "ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * CE4:
     * Posta elektronikoa dagoeneko DBan erregistratuta dago.
     *
     * Sarrerako DB egoera simulatu:
     * db.find(Seller.class, mail) deitzean Seller bat itzultzen da.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Ez da erabiltzaile-izena kontsultatu behar,
     * ezta Seller berririk gorde ere.
     */
    @Test
    public void testCE4_existingMail() {
        String mail = "ane4@ehu.eus";
        String user = "ane4";
        String password = "1234";

        Seller sellerInDb = new Seller(mail, user, password);

        Mockito.when(db.find(Seller.class, mail))
            .thenReturn(sellerInDb);

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

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
     * CE5:
     * Posta elektronikoa libre dago, baina erabiltzaile-izena
     * dagoeneko DBan erregistratuta dago.
     *
     * Sarrerako DB egoera simulatu:
     * - db.find(...) metodoak null itzultzen du.
     * - Erabiltzaile-izenaren kontsultak zerrenda ez-huts bat itzultzen du.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Ez da Seller berririk gorde behar.
     */
    @Test
    public void testCE5_existingUser() {
        String mail = "ane5@ehu.eus";
        String user = "ane5";
        String password = "1234";

        Seller existingSeller = new Seller(
            "bestea@ehu.eus",
            user,
            "bestePasahitza"
        );

        /*
         * Maila ez dago DBan.
         */
        Mockito.when(db.find(Seller.class, mail))
            .thenReturn(null);

        /*
         * Erabiltzaile-izenaren kontsultak query mock-a itzultzen du.
         */
        Mockito.when(db.createQuery(
            "SELECT s FROM Seller s WHERE s.name=?1",
            Seller.class
        )).thenReturn(query);

        Mockito.when(query.setParameter(1, user))
            .thenReturn(query);

        /*
         * Erabiltzaile-izena DBan dagoela simulatzeko,
         * zerrenda ez-huts bat itzultzen da.
         */
        Mockito.when(query.getResultList())
            .thenReturn(List.of(existingSeller));

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

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
     * CE6:
     * Posta elektronikoa eta erabiltzaile-izena libre daude.
     *
     * Sarrerako DB egoera simulatu:
     * - db.find(...) metodoak null itzultzen du.
     * - Erabiltzaile-izenaren kontsultak zerrenda hutsa itzultzen du.
     *
     * Espero den emaitza:
     * Emaitza(true, mail, sellerBerria, null).
     *
     * Seller berria sortu, persistitu eta transakzioa commit egin behar da.
     */
    @Test
    public void testCE6_validRegistration() {
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

        /*
         * Mailaren eta erabiltzaile-izenaren kontsultak egin direla egiaztatzen da.
         */
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

        /*
         * Transakzioa hasi dela egiaztatzen da.
         */
        Mockito.verify(et, Mockito.times(1)).begin();

        /*
         * persist metodora bidalitako Seller objektua jasotzen da,
         * eta haren atributu nagusiak egiaztatzen dira.
         */
        ArgumentCaptor<Seller> sellerCaptor =
            ArgumentCaptor.forClass(Seller.class);

        Mockito.verify(db, Mockito.times(1))
            .persist(sellerCaptor.capture());

        Seller persistedSeller = sellerCaptor.getValue();

        assertEquals(mail, persistedSeller.getEmail());
        assertEquals(user, persistedSeller.getName());

        /*
         * Transakzioa commit bidez amaitu dela egiaztatzen da.
         */
        Mockito.verify(et, Mockito.times(1)).commit();
    }

    /*
     * CE7:
     * Posta elektroniko hutsa.
     *
     * Muga-proba.
     *
     * Uneko kodeak mail == null soilik egiaztatzen duenez,
     * "" balioa ez du baztertzen. Emaila eta erabiltzaile-izena
     * libre direla simulatu ondoren, erregistroa onartzen du.
     *
     * Zehaztapenak email hutsa debekatzen badu, portaera hau defektua da.
     */
    @Test
    public void testCE7_emptyMail() {
        String mail = "";
        String user = "aneEmptyMail";
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

        /*
         * Uneko inplementazioaren portaera erreala egiaztatzen da.
         */
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        Mockito.verify(db, Mockito.times(1))
            .persist(Mockito.any(Seller.class));
    }

    /*
     * CE8:
     * Erabiltzaile-izen hutsa.
     *
     * Muga-proba.
     *
     * Uneko kodeak user == null soilik egiaztatzen duenez,
     * "" balioa onartzen du. Zehaztapenak izen hutsa debekatzen
     * badu, portaera hau defektua da.
     */
    @Test
    public void testCE8_emptyUser() {
        String mail = "emptyuser@ehu.eus";
        String user = "";
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

        Mockito.verify(db, Mockito.times(1))
            .persist(Mockito.any(Seller.class));
    }

    /*
     * CE9:
     * Pasahitz hutsa.
     *
     * Muga-proba.
     *
     * Uneko kodeak password == null soilik egiaztatzen duenez,
     * "" balioa onartzen du. Zehaztapenak pasahitz hutsa debekatzen
     * badu, portaera hau defektua da.
     */
    @Test
    public void testCE9_emptyPassword() {
        String mail = "emptypassword@ehu.eus";
        String user = "emptyPassword";
        String password = "";

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

        Mockito.verify(db, Mockito.times(1))
            .persist(Mockito.any(Seller.class));
    }
}