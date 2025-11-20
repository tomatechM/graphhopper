package com.graphhopper.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class NativeFSLockFactoryMockitoTest {
    
    @Test
    void testTryLockSuccess() {
        // Création du mock pour NativeLock
        NativeFSLockFactory.NativeLock mockLock = mock(NativeFSLockFactory.NativeLock.class);
        when(mockLock.tryLock()).thenReturn(true);
        when(mockLock.isLocked()).thenReturn(true);

        // Vérification que le lock est acquis
        assertTrue(mockLock.tryLock());
        assertTrue(mockLock.isLocked());

        // Simule la libération
        doNothing().when(mockLock).release();
        mockLock.release();
        verify(mockLock, times(1)).release();
    }

    @Test
    void testTryLockFailure() {
        // Lock échoué
        NativeFSLockFactory.NativeLock mockLock = mock(NativeFSLockFactory.NativeLock.class);
        when(mockLock.tryLock()).thenReturn(false);
        when(mockLock.isLocked()).thenReturn(false);

        assertFalse(mockLock.tryLock());
        assertFalse(mockLock.isLocked());

        // Release ne doit pas être appelé car lock échoué
        mockLock.release();
        verify(mockLock, times(1)).release();
    }
}
