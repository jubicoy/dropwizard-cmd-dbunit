package fi.jubic.dropwizard.cmd.dbunit;

import org.dbunit.database.DatabaseConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.3.2, 9.9.2016.
 */
public class DatabaseConfigurator {
    //
    // Fields
    // **************************************************************
    private Map<String, DbProperty> properties;

    //
    // Constructor(s)
    // **************************************************************
    public DatabaseConfigurator() {
        this.properties = new HashMap<>();
    }

    public DatabaseConfigurator(Map<String, DbProperty> properties) {
        this.properties = properties;
    }

    //
    // Methods
    // **************************************************************
    public DatabaseConfigurator setProperty (String key, Object value) {
        Map<String, DbProperty> newProps = new HashMap<>(properties);
        newProps.put(key, new DbProperty(key, value));
        return new DatabaseConfigurator(newProps);
    }

    public void configure (DatabaseConfig config) {
        this.properties.forEach((k, v) -> {
            config.setProperty(v.key, v.value);
        });
    }

    //
    // Inner classes
    // **************************************************************
    static class DbProperty {
        //
        // Fields
        // **********************************************************
        final String key;
        final Object value;

        //
        // Constructor(s)
        // **********************************************************
        public DbProperty (String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }


}
