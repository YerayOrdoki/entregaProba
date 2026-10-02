package test;

import static org.junit.Assert.*;
import org.junit.Test;
import dataAccess.DataAccess;

public class GetEmailBDBlackTest {
    private DataAccess sut = new DataAccess();

    @Test // Denak ondo
    public void testCorrectData() {
        sut.open();
        String res = sut.getEmail("Aitor Fernandez", "aurrera");
        sut.close();
        assertEquals("seller1@gmail.com", res);
    }

    @Test // Izena gaizki 
    public void testWrongUser() {
        sut.open();
        assertEquals("", sut.getEmail("UsuarioFalso", "aurrera"));
        sut.close();
    }

    @Test // Pasahitza gaizki dago
    public void testWrongPassword() {
        sut.open();
        assertEquals("", sut.getEmail("Aitor Fernandez", "error123"));
        sut.close();
    }

    @Test // Null
    public void testNullInputs() {
        sut.open();
        assertEquals("", sut.getEmail(null, null));
        sut.close();
    }
}