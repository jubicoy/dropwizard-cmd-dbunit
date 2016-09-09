package fi.jubic.dropwizard.cmd.dbunit;

import fi.jubic.dropwizard.cmd.dbunit.cli.DbUnitCommand;
import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1, 27.6.2016.
 */
public class DbUnitBundle<T extends Configuration> implements Bundle, DatabaseConfiguration<T> {
    private static final Logger logger = LoggerFactory.getLogger(DbUnitBundle.class);
    //
    // Fields
    // **************************************************************
    private final Function<T, PooledDataSourceFactory> extractor;
    private final DatabaseConfigurator databaseConfigurator;

    //
    // Constructor(s)
    // **************************************************************
    public DbUnitBundle (Function<T, PooledDataSourceFactory> extractor) {
        this(extractor, null);
    }

    public DbUnitBundle (
            Function<T, PooledDataSourceFactory> extractor,
            DatabaseConfigurator databaseConfigurator
    ) {
        this.extractor = extractor;
        this.databaseConfigurator = databaseConfigurator;
    }

    //
    // Bundle impl
    // **************************************************************
    @Override
    public void initialize (Bootstrap<?> bootstrap) {
        logger.info("Initializing DbUnitBundle ...");
        @SuppressWarnings("unchecked")
        final Class<T> klass = (Class<T>)bootstrap.getApplication().getConfigurationClass();
        bootstrap.addCommand(new DbUnitCommand<T>(this, klass, databaseConfigurator));
    }

    @Override
    public void run (Environment environment) {
        // no-op
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(T t) {
        return extractor.apply(t);
    }
}
