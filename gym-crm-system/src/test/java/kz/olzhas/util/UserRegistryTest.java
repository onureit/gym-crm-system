package kz.olzhas.util;

import kz.olzhas.dao.UserDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class UserRegistryTest {
    @Mock private UserDao userDao;
    private UserRegistry registry;

    @Before
    public void setUp() {
        registry = new UserRegistry(userDao);
    }

    @Test
    public void usernameIsFirstNameDotLastName() {
        when(userDao.existsByUsername("Alex.Smith")).thenReturn(false);

        assertEquals("Alex.Smith", registry.generateUsername("Alex", "Smith"));
    }

    @Test
    public void usernameGetsSuffixWhenTaken() {
        when(userDao.existsByUsername("Alex.Smith")).thenReturn(true);
        when(userDao.existsByUsername("Alex.Smith1")).thenReturn(true);
        when(userDao.existsByUsername("Alex.Smith2")).thenReturn(false);

        assertEquals("Alex.Smith2", registry.generateUsername("Alex", "Smith"));
    }

    @Test
    public void usernameRequiresBothNames() {
        assertThrows(NullPointerException.class, () -> registry.generateUsername(null, "Smith"));
        assertThrows(NullPointerException.class, () -> registry.generateUsername("Alex", null));
        verifyNoInteractions(userDao);
    }

    @Test
    public void passwordHasTenAlphanumericCharacters() {
        assertTrue(registry.generatePassword().matches("[A-Za-z0-9]{10}"));
    }
}
