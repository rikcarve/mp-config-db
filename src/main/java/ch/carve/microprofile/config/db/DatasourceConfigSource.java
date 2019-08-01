package ch.carve.microprofile.config.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatasourceConfigSource implements ConfigSource {

    private static final String VALIDITY_KEY = "configsource.db.validity";

    private Config config;
    private Map<String, TimedEntry> cache = new ConcurrentHashMap<>();
    Repository repository = null;

    Long validity;

    public DatasourceConfigSource() {
        config = createConfig();
    }

    @Override
    public Map<String, String> getProperties() {
        initRepository();
        try {
            return repository.getAllConfigValues();
        } catch (SQLException e) {
            log.info("query failed: " + e.getMessage());
            clearRepository();
        }
        return new HashMap<>();
    }

    @Override
    public String getValue(String propertyName) {
        initRepository();
        initValidity();

        TimedEntry entry = cache.get(propertyName);
        if (entry == null || entry.isExpired()) {
            log.debug("load {} from db", propertyName);
            try {
                String value = repository.getConfigValue(propertyName);
                cache.put(propertyName, new TimedEntry(value));
                return value;
            } catch (SQLException e) {
                log.info("query failed: " + e.getMessage());
                clearRepository();
            }
        }
        // if the entry was never cached, then it will be null
        return entry != null ? entry.getValue() : null;
    }

    @Override
    public String getName() {
        return "DatasourceConfigSource";
    }

    @Override
    public int getOrdinal() {
        return 120;
    }

    private void initRepository() {
        if (repository == null) {
            // late initialization is needed because of the EE datasource.
            repository = new Repository(config);
        }
    }

    void clearRepository() {
        repository = null;
    }

    private void initValidity() {
        if (validity == null) {
            validity = config.getOptionalValue(VALIDITY_KEY, Long.class).orElse(30000L);
        }
    }

    private Config createConfig() {
        return ConfigProviderResolver.instance()
                .getBuilder()
                .addDefaultSources()
                .build();
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
            return (timestamp + validity) < System.currentTimeMillis();
        }
    }
}
