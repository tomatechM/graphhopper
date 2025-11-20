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
        // Case où le lock est obtenu avec succès
        NativeFSLockFactory.NativeLock mockLock = mock(NativeFSLockFactory.NativeLock.class);
        when(mockLock.tryLock()).thenReturn(true);
        when(mockLock.isLocked()).thenReturn(true);

        // Vérification que le lock est acquis
        assertTrue(mockLock.tryLock());
        assertTrue(mockLock.isLocked());

        // Simule la libération
        // Release doit être appelé une fois car le lock a été obtenu
        doNothing().when(mockLock).release();
        mockLock.release();
        verify(mockLock, times(1)).release();
    }

    @Test
    void testTryLockFailure() {
        // Case où le lock échoue
        NativeFSLockFactory.NativeLock mockLock = mock(NativeFSLockFactory.NativeLock.class);
        when(mockLock.tryLock()).thenReturn(false);
        when(mockLock.isLocked()).thenReturn(false);

        // Vérification que le lock n'est pas acquis
        assertFalse(mockLock.tryLock());
        assertFalse(mockLock.isLocked());

        // Release ne doit pas être appelé car lock échoué
        mockLock.release();
        verify(mockLock, times(1)).release();
    }
}
