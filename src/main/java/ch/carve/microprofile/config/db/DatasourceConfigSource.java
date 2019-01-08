package ch.carve.microprofile.config.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasourceConfigSource implements ConfigSource {

    private static final Logger logger = LoggerFactory.getLogger(DatasourceConfigSource.class);

    Configuration config = new Configuration();
    private Map<String, TimedEntry> cache = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> getProperties() {
        return cache.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().getValue()));
    }

    @Override
    public String getValue(String propertyName) {
        try {
            PreparedStatement statement = getDatasource().getConnection().prepareStatement("select * from user");
            ResultSet rs = statement.executeQuery();
            rs.next();
            System.out.println(rs.getString(1));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TimedEntry entry = cache.get(propertyName);
        if (entry == null || entry.isExpired()) {
            logger.debug("load {} from db", propertyName);
        }
        return entry.getValue();
    }

    @Override
    public String getName() {
        return "ConsulConfigSource";
    }

    @Override
    public int getOrdinal() {
        return 220;
    }

    private DataSource getDatasource() {
        try {
            return (DataSource) InitialContext.doLookup("java:comp/DefaultDataSource");
        } catch (NamingException e) {
            logger.debug("Could not get datasource: {}", e.getMessage());
            return null;
        }
    }
    
    class TimedEntry {
        private final String value;
        private final long timestamp;

        public TimedEntry(String value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public String getValue() {
            return value;
        }

        public boolean isExpired() {
            return (timestamp + config.getValidity()) < System.currentTimeMillis();
        }
    }
}
