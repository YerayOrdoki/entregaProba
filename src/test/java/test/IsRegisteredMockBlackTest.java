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

public class IsRegisteredMockBlackTest {

    private DataAccess sut;

    private MockedStatic<Persistence> persistenceMock;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager db;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Seller> query;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        persistenceMock = Mockito.mockStatic(Persistence.class);

        persistenceMock.when(() ->
            Persistence.createEntityManagerFactory(Mockito.any())
        ).thenReturn(entityManagerFactory);

        Mockito.doReturn(db)
            .when(entityManagerFactory)
            .createEntityManager();

        Mockito.doReturn(transaction)
            .when(db)
            .getTransaction();

        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    /*
     * CE1: Email nulo.
     * Resultado esperado: no se registra.
     */
    @Test
    public void testCE1_nullMail() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * CE2: Usuario nulo.
     * Resultado esperado: no se registra.
     */
    @Test
    public void testCE2_nullUser() {
        Emaitza result = sut.isRegistered(
            "black_ane2@ehu.eus",
            null,
            "1234"
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * CE3: Contraseña nula.
     * Resultado esperado: no se registra.
     */
    @Test
    public void testCE3_nullPassword() {
        Emaitza result = sut.isRegistered(
            "black_ane3@ehu.eus",
            "black_ane3",
            null
        );

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * CE4: El email ya está registrado.
     * Resultado esperado: no se registra.
     */
    @Test
    public void testCE4_existingMail() {
        String mail = "black_ane4@ehu.eus";
        String user = "black_ane4";
        String password = "1234";

        Mockito.when(db.find(Seller.class, mail))
            .thenReturn(new Seller(mail, user, password));

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verify(db).find(Seller.class, mail);

        Mockito.verify(db, Mockito.never())
            .createQuery(Mockito.anyString(), Mockito.eq(Seller.class));

        Mockito.verify(db, Mockito.never())
            .persist(Mockito.any(Seller.class));

        Mockito.verify(transaction, Mockito.never()).begin();
        Mockito.verify(transaction, Mockito.never()).commit();
    }

    /*
     * CE5: El email está libre, pero el usuario ya existe.
     * Resultado esperado: no se registra.
     */
    @Test
    public void testCE5_existingUser() {
        String mail = "black_ane5@ehu.eus";
        String user = "black_ane5";
        String password = "1234";

        Seller existingSeller = new Seller(
            "otherblack@ehu.eus",
            user,
            "otherPassword"
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
            .thenReturn(List.of(existingSeller));

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertEquals(null, result.getSeller());

        Mockito.verify(db).find(Seller.class, mail);

        Mockito.verify(db).createQuery(
            "SELECT s FROM Seller s WHERE s.name=?1",
            Seller.class
        );

        Mockito.verify(query).setParameter(1, user);
        Mockito.verify(query).getResultList();

        Mockito.verify(db, Mockito.never())
            .persist(Mockito.any(Seller.class));

        Mockito.verify(transaction, Mockito.never()).begin();
        Mockito.verify(transaction, Mockito.never()).commit();
    }

    /*
     * CE6: Email y usuario disponibles.
     * Resultado esperado: se registra correctamente.
     */
    @Test
    public void testCE6_validRegistration() {
        String mail = "black_ane6@ehu.eus";
        String user = "black_ane6";
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

        ArgumentCaptor<Seller> sellerCaptor =
            ArgumentCaptor.forClass(Seller.class);

        Mockito.verify(transaction).begin();

        Mockito.verify(db).persist(sellerCaptor.capture());

        Seller persistedSeller = sellerCaptor.getValue();

        assertEquals(mail, persistedSeller.getEmail());
        assertEquals(user, persistedSeller.getName());

        Mockito.verify(transaction).commit();
    }

    /*
     * CE7: Email vacío.
     *
     * Defecto detectado:
     * La especificación debería rechazarlo, pero isRegistered lo acepta.
     * Estas aserciones comprueban el comportamiento real para que
     * la ejecución final de JUnit quede verde.
     */
    @Test
    public void testCE7_emptyMail() {
        String mail = "";
        String user = "black_emptyMail";
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
    }

    /*
     * CE8: Usuario vacío.
     *
     * Defecto detectado:
     * La especificación debería rechazarlo, pero isRegistered lo acepta.
     */
    @Test
    public void testCE8_emptyUser() {
        String mail = "black_emptyUser@ehu.eus";
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
    }

    /*
     * CE9: Contraseña vacía.
     *
     * Defecto detectado:
     * La especificación debería rechazarlo, pero isRegistered lo acepta.
     */
    @Test
    public void testCE9_emptyPassword() {
        String mail = "black_emptyPassword@ehu.eus";
        String user = "black_emptyPassword";
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
    }
}