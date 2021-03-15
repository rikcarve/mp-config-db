package ch.carve.microprofile.config.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatasourceConfigSource implements ConfigSource {

    private static final String VALIDITY_KEY = "configsource.db.validity";
    private static final String CONFIGSOURCE_ORDINAL_KEY = "configsource.db.ordinal";

    private Config config;
    Repository repository = null;
    ExpiringMap<String, String> cache = null;

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
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public String getValue(String propertyName) {
        initRepository();
        initCache();

        return cache.getOrCompute(propertyName, p -> repository.getConfigValue(p), () -> clearRepository());
    }

    @Override
    public String getName() {
        return "DatasourceConfigSource";
    }

    @Override
    public int getOrdinal() {
        return config.getOptionalValue(CONFIGSOURCE_ORDINAL_KEY, Integer.class).orElse(450);
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

    private void initCache() {
        if (cache == null) {
            long validity = config.getOptionalValue(VALIDITY_KEY, Long.class).orElse(30000L);
            cache = new ExpiringMap<>(validity);
        }
    }

    private Config createConfig() {
        return ConfigProviderResolver.instance()
                .getBuilder()
                .addDefaultSources()
                .build();
    }

}
