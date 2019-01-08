package ch.carve.microprofile.config.db;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatasourceConfigSourceTest {

    private DatasourceConfigSource configSource;

    @BeforeEach
    public void init() {
        configSource = new DatasourceConfigSource();
        configSource.config = new Configuration();
    }

    @Test
    void testGetProperties_empty() {
        DatasourceConfigSource configSource = new DatasourceConfigSource();
        assertTrue(configSource.getProperties().isEmpty());
    }

}
