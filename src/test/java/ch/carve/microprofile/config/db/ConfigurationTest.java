package ch.carve.microprofile.config.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConfigurationTest {

    @Test
    public void testGetValidity() throws Exception {
        Configuration config = new Configuration();
        assertEquals(30000, config.getValidity());
    }

    @Test
    public void testGetValidity_fromSys() throws Exception {
        System.setProperty("consul.configsource.validity", "10");
        Configuration config = new Configuration();
        assertEquals(10000, config.getValidity());
        System.clearProperty("consul.configsource.validity");
    }

    @Test
    public void testGetTable() throws Exception {
        Configuration config = new Configuration();
        assertEquals("configurations", config.getTable());
    }

}
