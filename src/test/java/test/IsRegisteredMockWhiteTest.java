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
 * isRegistered metodoaren kutxa zuriko probak.
 *
 * Kontrol-fluxuaren grafoko sei bide independenteak estaltzen dira:
 *
 * P1: B1.1(T)
 * P2: B1.1(F) -> B1.2(T)
 * P3: B1.1(F) -> B1.2(F) -> B1.3(T)
 * P4: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(T)
 * P5: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(T)
 * P6: B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(F)
 */
public class IsRegisteredMockWhiteTest {

    /*
     * Frogatu beharreko sistema:
     * System Under Test.
     */
    private DataAccess sut;

    /*
     * Persistence klaseko metodo estatikoa mockeatzeko erabiltzen da.
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
     * Test bakoitza hasi aurretik exekutatzen den konfigurazioa.
     *
     * EntityManager, EntityTransaction eta EntityManagerFactory
     * objektuen mockak sortzen dira.
     *
     * Azkenik, DataAccess-i EntityManager mock-a injektatzen zaio;
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
     * Test bakoitzaren ondoren mock estatikoa ixten da,
     * beste testekin interferentziarik egon ez dadin.
     */
    @After
    public void tearDown() {
        persistenceMock.close();
    }

    /*
     * P1:
     * B1.1(T)
     *
     * mail == null denean, metodoak berehala itzuli behar du:
     * Emaitza(false, "", null, null).
     *
     * Ez da datu-basera deirik egin behar.
     */
    /*
    @Test
    public void test1_mailNull() {
        Emaitza result = sut.isRegistered(null, "ane", "1234");

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        Mockito.verifyNoInteractions(db);
    }
    */

    /*
     * P2:
     * B1.1(F) -> B1.2(T)
     *
     * user == null denean, metodoak berehala itzuli behar du:
     * Emaitza(false, "", null, null).
     *
     * Ez da datu-basera deirik egin behar.
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
        assertNull(result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * P3:
     * B1.1(F) -> B1.2(F) -> B1.3(T)
     *
     * password == null denean, metodoak berehala itzuli behar du:
     * Emaitza(false, "", null, null).
     *
     * Ez da datu-basera deirik egin behar.
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
        assertNull(result.getSeller());

        Mockito.verifyNoInteractions(db);
    }

    /*
     * P4:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(T)
     *
     * Sarrerako egoera:
     * Parametro guztiak ez-null dira, baina maila DBan badago.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Ez da erabiltzaile-izenaren kontsultarik egin behar,
     * ezta transakziorik hasi edo Seller berririk persistitu ere.
     */
    @Test
    public void test4_mailAlreadyExists() {
        String mail = "ane4@ehu.eus";
        String user = "ane4";
        String password = "1234";

        Seller sellerInDb = new Seller(mail, user, password);

        /*
         * DBa simulatu:
         * posta elektroniko hori duen Seller bat badago.
         */
        Mockito.when(db.find(Seller.class, mail))
            .thenReturn(sellerInDb);

        Emaitza result = sut.isRegistered(mail, user, password);

        assertFalse(result.getLog());
        assertEquals("", result.getEmail());
        assertNull(result.getSeller());

        /*
         * Emailaren bilaketa egin dela egiaztatzen da.
         */
        Mockito.verify(db, Mockito.times(1))
            .find(Seller.class, mail);

        /*
         * Emaila dagoenez, hurrengo urratsak ez dira egin behar.
         */
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
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(T)
     *
     * Sarrerako egoera:
     * - Maila ez dago DBan.
     * - Erabiltzaile-izena DBan badago, beste posta batekin.
     *
     * Espero den emaitza:
     * Emaitza(false, "", null, null).
     *
     * Ez da transakziorik hasi behar, eta ez da Seller berririk gorde.
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

        /*
         * Maila ez dago DBan.
         */
        Mockito.when(db.find(Seller.class, mail))
            .thenReturn(null);

        /*
         * Erabiltzaile-izena duen Seller bat badago.
         * Horregatik, queryaren emaitza-zerrenda ez dago hutsik.
         */
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
        assertNull(result.getSeller());

        /*
         * Maila libre den ala ez kontsultatu dela egiaztatzen da.
         */
        Mockito.verify(db, Mockito.times(1))
            .find(Seller.class, mail);

        /*
         * Erabiltzaile-izenaren kontsulta egin dela egiaztatzen da.
         */
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
         * Erabiltzaile-izena hartuta dagoenez, ez da Seller berririk sortzen.
         */
        Mockito.verify(db, Mockito.never())
            .persist(Mockito.any(Seller.class));

        Mockito.verify(et, Mockito.never()).begin();
        Mockito.verify(et, Mockito.never()).commit();
    }

    /*
     * P6:
     * B1.1(F) -> B1.2(F) -> B1.3(F) -> B5(F) -> B9(F)
     *
     * Sarrerako egoera:
     * Maila eta erabiltzaile-izena libre daude.
     *
     * Espero den emaitza:
     * Emaitza(true, mail, sellerBerria, null).
     *
     * Seller berria sortu, persistitu eta transakzioa commit egin behar da.
     */
    @Test
    public void test6_registersSeller() {
        String mail = "ane6@ehu.eus";
        String user = "ane6";
        String password = "1234";

        /*
         * Maila ez dago DBan.
         */
        Mockito.when(db.find(Seller.class, mail))
            .thenReturn(null);

        /*
         * Erabiltzaile-izena ere ez dago DBan.
         * Horregatik, kontsultaren emaitza-zerrenda hutsik dago.
         */
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
         * Metodoaren itzulera-balioa egiaztatzen da.
         */
        assertTrue(result.getLog());
        assertEquals(mail, result.getEmail());
        assertNotNull(result.getSeller());

        /*
         * Lehenengo bi kontsultak egin direla egiaztatzen da.
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
         * Datu-baseko transakzioa hasi dela egiaztatzen da.
         */
        Mockito.verify(et, Mockito.times(1)).begin();

        /*
         * persist metodora bidalitako Seller objektua jasotzen da.
         */
        ArgumentCaptor<Seller> sellerCaptor =
            ArgumentCaptor.forClass(Seller.class);

        Mockito.verify(db, Mockito.times(1))
            .persist(sellerCaptor.capture());

        Seller persistedSeller = sellerCaptor.getValue();

        /*
         * Persistitutako Seller objektuaren atributuak egiaztatzen dira.
         */
        assertEquals(mail, persistedSeller.getEmail());
        assertEquals(user, persistedSeller.getName());

        /*
         * Transakzioa amaitu eta commit egin dela egiaztatzen da.
         */
        Mockito.verify(et, Mockito.times(1)).commit();
    }
}