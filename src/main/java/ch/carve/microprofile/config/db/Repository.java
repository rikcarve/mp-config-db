package ch.carve.microprofile.config.db;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repository {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    PreparedStatement selectOne = null;
    PreparedStatement selectAll = null;
    
    public Repository(Configuration config) {
        DataSource datasource = getDatasource(config.getDatasourceJndi());
        if (datasource != null) {
            String queryOne = "select " + config.getValueColumn() + " from " + config.getTable() + " where " + config.getKeyColumn() + " = ?";
            String queryAll = "select " + config.getKeyColumn() + ", " + config.getValueColumn() + " from " + config.getTable();
            try {
                selectOne = datasource.getConnection().prepareStatement(queryOne);
                selectAll = datasource.getConnection().prepareStatement(queryAll);
            } catch (SQLException e) {
                logger.debug("Configuration query could not be prepared: {}", e.getMessage());
            }
        }
    }
    
    public Map<String, String> getAllConfigValues() {
        Map<String, String> result = new HashMap<>();
        if (selectAll != null) {
            try {
                ResultSet rs = selectAll.executeQuery();
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getString(2));
                }
            } catch (SQLException e) {
                logger.debug("query for config values failed: {}", e.getMessage());
            }
        }
        return result;
    }
    
    public String getConfigValue(String key) {
        if (selectOne != null) {
            try {
                selectOne.setString(1, key);
                ResultSet rs = selectOne.executeQuery();
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
            logger.warn("Could not get datasource: {}", e.getMessage());
            return null;
        }
    }

}
