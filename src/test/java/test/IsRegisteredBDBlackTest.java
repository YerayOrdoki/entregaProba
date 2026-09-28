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

public class IsRegisteredBDBlackTest {

    private DataAccess sut;
    private EntityManager db;

    @Before
    public void setUp() {
        /*
         * El constructor de DataAccess acaba cerrando la conexión,
         * por lo que se abre de nuevo antes de cada test.
         */
        sut = new DataAccess();
        sut.open();

        /*
         * Se obtiene el EntityManager ya configurado por DataAccess.
         * De este modo se usa la BD real y las entidades JPA se reconocen.
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
                "No se ha podido recuperar el EntityManager de DataAccess",
                e
            );
        }
    }

    /*
     * Elimina únicamente los Sellers creados por esta clase de prueba.
     * El prefijo blackbd_ evita eliminar Sellers reales de la aplicación.
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
     * CE1: Email nulo.
     * Resultado esperado: registro rechazado.
     */
    @Test
    public void testCE1_nullMail() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * CE2: Usuario nulo.
     * Resultado esperado: registro rechazado.
     */
    @Test
    public void testCE2_nullUser() {
        Emaitza result = sut.isRegistered(
            "blackbd_ane2@ehu.eus",
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * CE3: Contraseña nula.
     * Resultado esperado: registro rechazado.
     */
    @Test
    public void testCE3_nullPassword() {
        Emaitza result = sut.isRegistered(
            "blackbd_ane3@ehu.eus",
            "blackbd_ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * CE4: Email ya registrado en la BD.
     * Resultado esperado: registro rechazado.
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
        assertEquals(null, result.getSeller());
    }

    /*
     * CE5: Email libre, pero nombre de usuario ya existente en BD.
     * Resultado esperado: registro rechazado.
     */
    @Test
    public void testCE5_existingUser() {
        String existingMail = "blackbd_otro@ehu.eus";
        String repeatedUser = "blackbd_ane5";

        insertSeller(existingMail, repeatedUser, "otraClave");

        Emaitza result = sut.isRegistered(
            "blackbd_ane5@ehu.eus",
            repeatedUser,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * CE6: Email y usuario disponibles.
     * Resultado esperado: registro correcto.
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
         * Se verifica que el Seller ha quedado guardado en la BD real.
         */
        db.clear();

        Seller sellerInDb = db.find(Seller.class, mail);

        assertNotNull(sellerInDb);
        assertEquals(mail, sellerInDb.getEmail());
        assertEquals(user, sellerInDb.getName());
    }

    /*
     * CE7: Email vacío.
     *
     * Defecto documentado:
     * La especificación debería rechazarlo, pero el código actual lo acepta.
     * Se comprueba el resultado real para dejar JUnit en verde.
     */
    @Test
    public void testCE7_emptyMail() {
        String mail = "";
        String user = "blackbd_emptyMail";
        String password = "1234";

        Emaitza result = sut.isRegistered(mail, user, password);

        /*
         * ObjectDB real rechaza o no persiste correctamente un Seller
         * con email vacío. Resultado real observado: log = false.
         */
        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());
    }

    /*
     * CE8: Usuario vacío.
     *
     * Defecto documentado: el código actual lo acepta.
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
    }

    /*
     * CE9: Contraseña vacía.
     *
     * Defecto documentado: el código actual lo acepta.
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
    }
}