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

public class GetEmailMockBlackTest {
    private DataAccess sut;

    @Mock protected EntityManager db;
    @Mock protected TypedQuery<Seller> query; 
    @Mock protected EntityTransaction et;
    @Mock protected EntityManagerFactory emf;
    
    private MockedStatic<Persistence> persistenceMock;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(any())).thenReturn(emf);
        doReturn(db).when(emf).createEntityManager();
        doReturn(et).when(db).getTransaction();
        
        sut = new DataAccess(db); 
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test // ondo
    public void test1_Success() {
        String email = "ane@gmail.com";
        Seller s = new Seller(email, "Ane", "123");
        when(db.createQuery(anyString(), eq(Seller.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(s));
        
        assertEquals(email, sut.getEmail("Ane", "123"));
    }

    @Test // izena gaizki
    public void test2_WrongUser() {
        when(db.createQuery(anyString(), eq(Seller.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<Seller>());
        
        assertEquals("", sut.getEmail("UsuarioFalso", "123"));
    }

    @Test // pasahitza gaizki
    public void test3_WrongPass() {
        when(db.createQuery(anyString(), eq(Seller.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<Seller>());
        
        assertEquals("", sut.getEmail("Ane", "error_pass"));
    }

    @Test // null parametroak
    public void test4_NullParameters() {
        assertEquals("", sut.getEmail(null, null));
    }
}