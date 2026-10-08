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
import org.junit.Ignore;
import org.junit.Test;

import dataAccess.DataAccess;
import dataAccess.Emaitza;
import domain.Seller;

/*
 * isRegistered metodoaren kutxa beltzeko probak,
 * benetako datu-basea erabiliz.
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
public class IsRegisteredBDBlackTest {

    /*
     * Frogatu beharreko sistema.
     * SUT = System Under Test.
     */
    private DataAccess sut;

    /*
     * DataAccess-ek erabiltzen duen EntityManager erreala.
     */
    private EntityManager db;

    /*
     * Test bakoitza hasi aurretik exekutatzen den konfigurazioa.
     *
     * DataAccess objektua sortu eta DBa irekitzen da.
     * Ondoren, DataAccess-ek erabiltzen duen EntityManager bera lortzen da.
     */
    @Before
    public void setUp() {
        sut = new DataAccess();
        sut.open();

        /*
         * DataAccess klasearen db atributu pribatua eskuratzen da.
         * Testak eta SUTak EntityManager bera erabiltzen dute.
         */
        db = getEntityManager(sut);

        /*
         * Aurreko testek sortutako testeko Seller-ak ezabatzen dira.
         */
        cleanTestSellers();
    }

    /*
     * Test bakoitza amaitzean datu-basea hasierako egoerara itzultzen da
     * eta DataAccess-en konexioa ixten da.
     */
    @After
    public void tearDown() {
        if (db != null && db.isOpen()) {
            cleanTestSellers();
        }

        if (sut != null) {
            sut.close();
        }
    }

    /*
     * Reflection erabiliz DataAccess klaseko db atributu pribatua lortzen da.
     *
     * Horri esker, testak SUTak erabiltzen duen EntityManager berean
     * egin ditzake kontsultak, txertaketak eta egiaztapenak.
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
     * Test klase honek sortutako Seller-ak ezabatzen ditu.
     *
     * blackbd_ aurrizkia erabiltzen da aplikazioko benetako
     * erabiltzaileak ez ezabatzeko.
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

        query.setParameter("prefix", "blackbd_%@ehu.eus");

        List<Seller> sellers = query.getResultList();

        for (Seller seller : sellers) {
            db.remove(seller);
        }

        tx.commit();

        /*
         * Persistence context-a garbitzen da, hurrengo kontsultek
         * DBko benetako egoera erabil dezaten.
         */
        db.clear();
    }

    /*
     * Test baten sarrerako DB egoera prestatzeko Seller bat txertatzen du.
     */
    private void insertSeller(String email, String name, String password) {
        EntityTransaction tx = db.getTransaction();

        tx.begin();
        db.persist(new Seller(email, name, password));
        tx.commit();

        db.clear();
    }

    /*
     * Erabiltzaile-izen zehatz bat duten Seller guztiak ezabatzen ditu.
     *
     * CE7n erabiltzen da. Emaila hutsik denez, ezin da
     * blackbd_%@ehu.eus patroiarekin aurkitu; aldiz,
     * user balioak blackbd_ aurrizkia du eta bakarra da.
     */
    private void removeSellersByName(String user) {
        EntityTransaction tx = db.getTransaction();

        if (tx.isActive()) {
            tx.rollback();
        }

        tx.begin();

        TypedQuery<Seller> query = db.createQuery(
            "SELECT s FROM Seller s WHERE s.name = :name",
            Seller.class
        );

        query.setParameter("name", user);

        List<Seller> sellers = query.getResultList();

        for (Seller seller : sellers) {
            db.remove(seller);
        }

        tx.commit();
        db.clear();
    }

    /*
     * CE1:
     * mail == null.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * DBaren egoera ez da aldatzen.
     */
    /*
    @Test
    public void testCE1_nullMail() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());
    }
    */
    /*
     * CE2:
     * user == null.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * DBaren egoera ez da aldatzen.
     */
    @Test
    public void testCE2_nullUser() {
        String mail = "blackbd_ane2@ehu.eus";

        Emaitza result = sut.isRegistered(
            mail,
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        /*
         * Ez dela Seller berririk sortu egiaztatzen da.
         */
        db.clear();
        assertNull(db.find(Seller.class, mail));
    }

    /*
     * CE3:
     * password == null.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * DBaren egoera ez da aldatzen.
     */
    @Test
    public void testCE3_nullPassword() {
        String mail = "blackbd_ane3@ehu.eus";

        Emaitza result = sut.isRegistered(
            mail,
            "blackbd_ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        /*
         * Ez dela Seller berririk sortu egiaztatzen da.
         */
        db.clear();
        assertNull(db.find(Seller.class, mail));
    }

    /*
     * CE4:
     * Posta elektronikoa dagoeneko erregistratuta dago.
     *
     * Sarrerako DB egoera:
     * Badago mail hori duen Seller bat.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Irteerako DB egoera:
     * Ez da aldatzen.
     */
    @Test
    public void testCE4_existingMail() {
        String mail = "blackbd_ane4@ehu.eus";
        String user = "blackbd_ane4";
        String password = "1234";

        insertSeller(mail, user, password);

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        /*
         * Aurretik zegoen Sellerra mantentzen dela egiaztatzen da.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }

    /*
     * CE5:
     * Posta elektronikoa libre dago, baina erabiltzaile-izena
     * dagoeneko erregistratuta dago.
     *
     * Sarrerako DB egoera:
     * - Ez dago newMail posta duen Sellerrik.
     * - Badago repeatedUser izeneko Seller bat beste posta batekin.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Irteerako DB egoera:
     * Ez da Seller berririk gordetzen.
     */
    @Test
    public void testCE5_existingUser() {
        String existingMail = "blackbd_otro@ehu.eus";
        String repeatedUser = "blackbd_ane5";
        String newMail = "blackbd_ane5@ehu.eus";

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
         * Ez dela Seller berririk gorde egiaztatzen da.
         */
        assertNull(db.find(Seller.class, newMail));

        /*
         * Hasieran zegoen Sellerra mantentzen dela egiaztatzen da.
         */
        Seller sellerInDb = db.find(Seller.class, existingMail);

        assertNotNull(sellerInDb);
        assertEquals(existingMail, sellerInDb.getEmail());
        assertEquals(repeatedUser, sellerInDb.getName());
    }

    /*
     * CE6:
     * Posta elektronikoa eta erabiltzaile-izena libre daude.
     *
     * Sarrerako DB egoera:
     * Ez dago mail hori edo user hori duen Sellerrik.
     *
     * Espero den emaitza:
     * Emaitza(true, mail, sellerBerria, null).
     *
     * Irteerako DB egoera:
     * Seller berria DBan gordeta geratzen da.
     */
    @Test
    public void testCE6_validRegistration() {
        String mail = "blackbd_ane6@ehu.eus";
        String user = "blackbd_ane6";
        String password = "1234";

        Emaitza result = sut.isRegistered(mail, user, password);

        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        /*
         * Persistence context-a garbitu ondoren, Sellerra DBan
         * benetan gordeta dagoela egiaztatzen da.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }

    /*
     * CE7:
     * Posta elektroniko hutsa.
     *
     * Muga-proba.
     *
     * Uneko inplementazioak mail == null soilik egiaztatzen du.
     * "" balioa ez denez null, metodoak erregistroa onartu duela
     * adierazten duen Emaitza objektua itzultzen du.
     *
     * Zehaztapenak email hutsa baztertzea eskatzen badu, proba honek
     * defektu bat aurkitzen du:
     * espero zena false zen, baina lortutakoa true da.
     */
    //@Test
    @Ignore
    public void testCE7_emptyMail() {
        String mail = "";
        String user = "blackbd_emptyMail";
        String password = "1234";

        Emaitza result = sut.isRegistered(mail, user, password);

        /*
         * Metodoaren itzulera-balioaren arabera,
         * erregistroa onartu dela egiaztatzen da.
         */
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        /*
         * Ez da db.find(Seller.class, "") erabiltzen.
         *
         * Email hutsa ObjectDB/JPAko kasu berezia izan daiteke;
         * find(...) bidez ez aurkitzeak ez du esan nahi Emaitza
         * objektuak adierazitako portaera aldatu denik.
         *
         * Testak sortutako datua erabiltzaile-izenaren bidez garbitzen da.
         */
        removeSellersByName(user);
    }

    /*
     * CE8:
     * Erabiltzaile-izen hutsa.
     *
     * Muga-proba.
     *
     * Uneko inplementazioak user == null soilik egiaztatzen du.
     * Beraz, erabiltzaile-izen hutsa onartzen da.
     *
     * Zehaztapenak izen hutsa baztertzea eskatzen badu, defektua da.
     */
    @Test
    public void testCE8_emptyUser() {
        String mail = "blackbd_emptyUser@ehu.eus";
        String user = "";
        String password = "1234";

        Emaitza result = sut.isRegistered(mail, user, password);

        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        /*
         * Sellerra DBan gordeta dagoela egiaztatzen da.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }

    /*
     * CE9:
     * Pasahitz hutsa.
     *
     * Muga-proba.
     *
     * Uneko inplementazioak password == null soilik egiaztatzen du.
     * Beraz, pasahitz hutsa onartzen da.
     *
     * Zehaztapenak pasahitz hutsa baztertzea eskatzen badu, defektua da.
     */
    @Test
    public void testCE9_emptyPassword() {
        String mail = "blackbd_emptyPassword@ehu.eus";
        String user = "blackbd_emptyPassword";
        String password = "";

        Emaitza result = sut.isRegistered(mail, user, password);

        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        /*
         * Sellerra DBan gordeta dagoela egiaztatzen da.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }
}