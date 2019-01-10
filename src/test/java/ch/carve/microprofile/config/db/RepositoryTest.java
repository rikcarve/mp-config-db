package ch.carve.microprofile.config.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void testGetConfigValue_exception() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.statement = mock(PreparedStatement.class);
        when(repository.statement.executeQuery()).thenThrow(SQLException.class);
        assertNull(repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue_none() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.statement = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(repository.statement.executeQuery()).thenReturn(rs);
        assertNull(repository.getConfigValue("test"));
    }

    @Test
    void testGetConfigValue() throws SQLException {
        Repository repository = new Repository(new Configuration());
        repository.statement = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn("value");
        when(repository.statement.executeQuery()).thenReturn(rs);
        assertEquals("value", repository.getConfigValue("test"));
    }

}
