package ch.carve.microprofile.config.db;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repository {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    PreparedStatement statement = null;
    
    public Repository(Configuration config) {
        DataSource datasource = getDatasource(config.getDatasourceJndi());
        if (datasource != null) {
            String query = "select value from " + config.getTable() + " where " + config.getKeyColumn() + " = ?";
            try {
                statement = datasource.getConnection().prepareStatement(query);
            } catch (SQLException e) {
                logger.debug("Configuration query could not be prepared: {}", e.getMessage());
            }
        }
    }
    
    public String getConfigValue(String key) {
        if (statement != null) {
            try {
                statement.setString(1, key);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                }
            } catch (SQLException e) {
                logger.debug("query for config value failed: {}", e.getMessage());
            }
        }
        return null;
    }
    
    private DataSource getDatasource(String jndi) {
        try {
            return (DataSource) InitialContext.doLookup(jndi);
        } catch (NamingException e) {
            logger.debug("Could not get datasource: {}", e.getMessage());
            return null;
        }
    }

}
