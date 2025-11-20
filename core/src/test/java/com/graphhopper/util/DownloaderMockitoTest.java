package com.graphhopper.util;

import com.graphhopper.util.Downloader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DownloaderMockitoTest {

    @Test
    void testDownloadAsStringMock() throws Exception {
        // 1. Créer un mock de la classe Downloader
        Downloader mockedDownloader = Mockito.mock(Downloader.class);

        // 2. Définir le comportement simulé
        // Nous simulons ici une réponse simple renvoyée par downloadAsString
        when(mockedDownloader.downloadAsString("http://fake-url.com", false))
                .thenReturn("FAKE_RESPONSE");

        // 3. Appeler la méthode simulée
        String result = mockedDownloader.downloadAsString("http://fake-url.com", false);

        // 4. Vérifier le résultat
        assertEquals("FAKE_RESPONSE", result);

        // 5. Vérifier que la méthode a bien été appelée
        verify(mockedDownloader).downloadAsString("http://fake-url.com", false);
    }


    @Test
    void testFetchMock() throws Exception {
        Downloader mockedDownloader = mock(Downloader.class);

        InputStream fakeStream = new ByteArrayInputStream("Hello".getBytes());

        when(mockedDownloader.fetch("http://test.com"))
                .thenReturn(fakeStream);

        InputStream result = mockedDownloader.fetch("http://test.com");

        byte[] buffer = result.readNBytes(5);
        assertEquals("Hello", new String(buffer));

        verify(mockedDownloader).fetch("http://test.com");
    }

}
