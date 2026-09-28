package test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import javax.persistence.*;
import dataAccess.DataAccess;
import domain.Seller;
import java.util.*;

public class GetEmailMockWhiteTest {
    private DataAccess sut;
    @Mock protected EntityManager db;
    @Mock protected TypedQuery<Seller> query;
    @Mock protected EntityManagerFactory emf;
    private MockedStatic<Persistence> persistenceMock;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(any())).thenReturn(emf);
        doReturn(db).when(emf).createEntityManager();
        sut = new DataAccess(db);
    }

    @After public void tearDown() { persistenceMock.close(); }

    @Test //name null
    public void test1_NameNull() {
        assertEquals("", sut.getEmail(null, "123"));
    }

    @Test //pass null
    public void test2_PassNull() {
        assertEquals("", sut.getEmail("User", null));
    }

    @Test // isEmpty = true
    public void test3_EmptyResult() {
        when(db.createQuery(anyString(), eq(Seller.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<Seller>());
        assertEquals("", sut.getEmail("User", "pw"));
    }

    @Test // Ondo
    public void test4_Found() {
        Seller s = new Seller("mail@test.com", "User", "pw");
        when(db.createQuery(anyString(), eq(Seller.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(s));
        assertEquals("mail@test.com", sut.getEmail("User", "pw"));
    }
}