package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import dataAccess.Emaitza;
import domain.Seller;

public class IsRegisteredBDWhiteTest {

    private DataAccess sut;
    private EntityManager db;

    @Before
    public void setUp() {
        /*
         * DataAccess-en konstruktoreak DBa ireki, hasieratu eta itxi dezake.
         * Horregatik, test bakoitza exekutatzeko DBa berriro irekitzen dugu.
         */
        sut = new DataAccess();
        sut.open();

        /*
         * DataAccess-ek erabiltzen duen EntityManager bera lortzen dugu.
         * Ez da ekoizpeneko kodea aldatzen.
         */
        db = getEntityManager(sut);

        /*
         * Test honen aurrizkia duten Seller guztiak ezabatzen dira,
         * test bakoitza egoera ezagun eta isolatu batetik hasteko.
         */
        cleanTestSellers();
    }

    @After
    public void tearDown() {
        /*
         * Testak sortutako datuak kentzen dira, hurrengo exekuzioetan
         * edo beste testetan eraginik izan ez dezaten.
         */
        if (db != null && db.isOpen()) {
            cleanTestSellers();
        }

        if (sut != null) {
            sut.close();
        }
    }

    /*
     * DataAccess klasearen db atributu pribatua lortzen du.
     * Horrela, testak SUTak erabiltzen duen EntityManager bera erabiltzen du.
     */
    private EntityManager getEntityManager(DataAccess dataAccess) {
        try {
            Field field = DataAccess.class.getDeclaredField("db");
            field.setAccessible(true);
            return (EntityManager) field.get(dataAccess);
        } catch (Exception e) {
            throw new RuntimeException(
                "Ezin izan da DataAccess-eko EntityManager-a eskuratu",
                e
            );
        }
    }

    /*
     * whitebd_ aurrizkia duten testeko Seller guztiak ezabatzen ditu.
     * Ez dira aplikazioaren gainerako datuak ukitzen.
     */
    private void cleanTestSellers() {
        EntityTransaction tx = db.getTransaction();

        if (tx.isActive()) {
            tx.rollback();
        }

        tx.begin();

        TypedQuery<Seller> query = db.createQuery(
            "SELECT s FROM Seller s WHERE s.email LIKE :prefix",
            Seller.class
        );

        query.setParameter("prefix", "whitebd_%@ehu.eus");

        List<Seller> sellers = query.getResultList();

        for (Seller seller : sellers) {
            db.remove(seller);
        }

        tx.commit();
        db.clear();
    }

    /*
     * Test baterako Seller bat benetako DBan txertatzen du.
     * Horrela, testaren hasierako DB egoera zehazki prestatzen da.
     */
    private void insertSeller(String email, String name, String password) {
        EntityTransaction tx = db.getTransaction();

        tx.begin();
        db.persist(new Seller(email, name, password));
        tx.commit();

        db.clear();
    }

    /*
     * P1:
     * B1.1(T)
     *
     * mail == null denean, ez da erregistrorik egiten.
     * DBaren egoera ez da aldatzen.
     */
    /*
    @Test
    public void test1_mailNull() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
    }
	*/
    /*
     * P2:
     * B1.1(F) -> B1.2(T)
     *
     * user == null denean, ez da erregistrorik egiten.
     * DBaren egoera ez da aldatzen.
     */
    @Test
    public void test2_userNull() {
        String mail = "whitebd_ane2@ehu.eus";

        Emaitza result = sut.isRegistered(
            mail,
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        db.clear();
        assertNull(db.find(Seller.class, mail));
    }

    /*
     * P3:
     * B1.1(F) -> B1.2(F) -> B1.3(T)
     *
     * password == null denean, ez da erregistrorik egiten.
     * DBaren egoera ez da aldatzen.
     */
    @Test
    public void test3_passwordNull() {
        String mail = "whitebd_ane3@ehu.eus";

        Emaitza result = sut.isRegistered(
            mail,
            "whitebd_ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        db.clear();
        assertNull(db.find(Seller.class, mail));
    }

    /*
     * P4:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(T)
     *
     * Sarrerako DB egoera:
     * Posta elektroniko hori duen Seller bat dago.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Irteerako DB egoera:
     * Ez da aldatzen.
     */
    @Test
    public void test4_mailAlreadyExists() {
        String mail = "whitebd_ane4@ehu.eus";
        String user = "whitebd_ane4";
        String password = "1234";

        insertSeller(mail, user, password);

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        /*
         * DBan lehendik zegoen Sellerra mantentzen dela egiaztatzen da.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }

    /*
     * P5:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(T)
     *
     * Sarrerako DB egoera:
     * - Posta elektronikoa libre dago.
     * - Erabiltzaile-izena dagoeneko erregistratuta dago,
     *   baina beste posta elektroniko batekin.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Irteerako DB egoera:
     * Ez da aldatzen.
     */
    @Test
    public void test5_userAlreadyExists() {
        String existingMail = "whitebd_otro@ehu.eus";
        String repeatedUser = "whitebd_ane5";
        String newMail = "whitebd_ane5@ehu.eus";

        insertSeller(existingMail, repeatedUser, "bestePasahitza");

        Emaitza result = sut.isRegistered(
            newMail,
            repeatedUser,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        db.clear();

        /*
         * Ez dela Seller berririk sortu egiaztatzen da.
         */
        assertNull(db.find(Seller.class, newMail));

        /*
         * Hasierako Sellerra mantentzen dela egiaztatzen da.
         */
        Seller existingSeller = db.find(Seller.class, existingMail);

        assertNotNull(existingSeller);
        assertEquals(existingMail, existingSeller.getEmail());
        assertEquals(repeatedUser, existingSeller.getName());
    }

    /*
     * P6:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(F)
     *
     * Sarrerako DB egoera:
     * Posta elektronikoa eta erabiltzaile-izena libre daude.
     *
     * Espero den emaitza:
     * Emaitza(true, mail, s1, null).
     *
     * Irteerako DB egoera:
     * Seller(mail, user, password) DBan gordeta dago.
     */
    @Test
    public void test6_registersSeller() {
        String mail = "whitebd_ane6@ehu.eus";
        String user = "whitebd_ane6";
        String password = "1234";

        Emaitza result = sut.isRegistered(mail, user, password);

        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        /*
         * Persistence context-a garbitzen da.
         * Horrela, hurrengo find-a DBaren benetako egoerara doa,
         * eta ez memorian dagoen objektura soilik.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }
}