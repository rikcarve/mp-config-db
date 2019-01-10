package ch.carve.microprofile.config.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatasourceConfigSourceTest {

    private DatasourceConfigSource configSource;

    @BeforeEach
    public void init() {
        configSource = new DatasourceConfigSource();
        configSource.repository = mock(Repository.class);
    }

    @Test
    void testGetProperties_empty() {
        assertTrue(configSource.getProperties().isEmpty());
    }

    @Test
    public void testGetProperties_null() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn(null);
        configSource.getValue("test");
        assertTrue(configSource.getProperties().isEmpty());
    }

    @Test
    public void testGetProperties_one() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        configSource.getValue("test");
        assertEquals(1, configSource.getProperties().size());
    }

    @Test
    public void testGetValue() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        assertEquals("123", configSource.getValue("test"));
    }
    
    @Test
    public void testGetValue_cache() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        configSource.getValue("test");
        configSource.getValue("test");
        verify(configSource.repository, times(1)).getConfigValue(anyString());
    }

}
