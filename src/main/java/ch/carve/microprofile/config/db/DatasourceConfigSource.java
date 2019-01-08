package ch.carve.microprofile.config.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasourceConfigSource implements ConfigSource {

    
    private static final Logger logger = LoggerFactory.getLogger(DatasourceConfigSource.class);

    Configuration config = new Configuration();
    private Map<String, TimedEntry> cache = new ConcurrentHashMap<>();
    Repository repository = new Repository(config);

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
        TimedEntry entry = cache.get(propertyName);
        if (entry == null || entry.isExpired()) {
            logger.debug("load {} from db", propertyName);
            String value = repository.getConfigValue(propertyName);
            cache.put(propertyName, new TimedEntry(value));
            return value;
        }
        return entry.getValue();
    }

    @Override
    public String getName() {
        return "DatasourceConfigSource";
    }

    @Override
    public int getOrdinal() {
        return 120;
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
