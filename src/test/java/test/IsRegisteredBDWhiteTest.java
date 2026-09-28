package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
         * El constructor abre la BD, la inicializa si está configurada y la cierra.
         * Por eso volvemos a abrirla para ejecutar el test.
         */
        sut = new DataAccess();
        sut.open();

        /*
         * Recuperamos la misma conexión EntityManager que DataAccess usa.
         * No se modifica el código de producción.
         */
        db = getEntityManager(sut);

        cleanTestSellers();
    }

    @After
    public void tearDown() {
        if (db != null && db.isOpen()) {
            cleanTestSellers();
        }

        if (sut != null) {
            sut.close();
        }
    }

    private EntityManager getEntityManager(DataAccess dataAccess) {
        try {
            Field field = DataAccess.class.getDeclaredField("db");
            field.setAccessible(true);
            return (EntityManager) field.get(dataAccess);
        } catch (Exception e) {
            throw new RuntimeException(
                "No se ha podido acceder al EntityManager de DataAccess",
                e
            );
        }
    }

    /*
     * Borra solo usuarios de prueba creados por esta clase.
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

    private void insertSeller(String email, String name, String password) {
        EntityTransaction tx = db.getTransaction();

        tx.begin();
        db.persist(new Seller(email, name, password));
        tx.commit();

        db.clear();
    }

    /*
     * P1:
     * B1.1(T): mail == null.
     */
    @Test
    public void test1_mailNull() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * P2:
     * B1.1(F) -> B1.2(T): user == null.
     */
    @Test
    public void test2_userNull() {
        Emaitza result = sut.isRegistered(
            "whitebd_ane2@ehu.eus",
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * P3:
     * B1.1(F) -> B1.2(F) -> B1.3(T): password == null.
     */
    @Test
    public void test3_passwordNull() {
        Emaitza result = sut.isRegistered(
            "whitebd_ane3@ehu.eus",
            "whitebd_ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * P4:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B2(T).
     * Ya existe un Seller con ese correo.
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
        assertEquals(null, result.getSeller());
    }

    /*
     * P5:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B2(F) -> B3(T).
     * El correo está libre, pero el nombre ya existe.
     */
    @Test
    public void test5_userAlreadyExists() {
        String existingMail = "whitebd_otro@ehu.eus";
        String repeatedUser = "whitebd_ane5";

        insertSeller(existingMail, repeatedUser, "otraClave");

        Emaitza result = sut.isRegistered(
            "whitebd_ane5@ehu.eus",
            repeatedUser,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * P6:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B2(F) -> B3(F).
     * Correo y nombre libres: se registra el Seller.
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

        db.clear();
 
        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }
}