package fi.jubic.dropwizard.cmd.dbunit.cli;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

import java.sql.SQLException;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1, 27.6.2016.
 */
abstract class AbsDbUnitCommand<T extends Configuration> extends ConfiguredCommand<T> {
    //
    // Fields
    // **************************************************************
    private final DatabaseConfiguration<T> strategy;
    private final Class<T> configurationClass;
    //
    // Constructor(s)
    // **************************************************************
    AbsDbUnitCommand(
            String commandName,
            String description,
            DatabaseConfiguration<T> strategy,
            Class<T> configurationClass
    ) {
        super(commandName, description);
        this.strategy = strategy;
        this.configurationClass = configurationClass;
    }

    //
    // Abstract methods
    // **************************************************************
    abstract void run (
            Bootstrap<T> bootstrap,
            Namespace namespace,
            IDatabaseConnection connection
    ) throws Exception;

    //
    // ConfiguredCommand impl
    // **************************************************************
    @Override
    protected Class<T> getConfigurationClass () {
        return configurationClass;
    }

    @Override
    protected void run (
            Bootstrap<T> bootstrap,
            Namespace namespace,
            T config
    ) throws Exception {
        IDatabaseConnection connection = getDatabaseConnection(config);
        run(
                bootstrap,
                namespace,
                connection
        );
        connection.close();
    }

    //
    // Methods
    // **************************************************************
    private IDatabaseConnection getDatabaseConnection (
            T config
    ) throws
            SQLException,
            DatabaseUnitException {
        final PooledDataSourceFactory sourceFactory = strategy.getDataSourceFactory(config);
        ManagedDataSource dataSource = sourceFactory.build(new MetricRegistry(), "db-unit");
        DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection());
        DatabaseConfig dbConfig = dbConnection.getConfig();

        DefaultDataTypeFactory dataTypeFactory;
        switch (sourceFactory.getDriverClass()) {
            case "org.postgresql.Driver":
                dataTypeFactory = new PostgresqlDataTypeFactory();
                break;

            case "com.mysql.jdbc.Driver":
            case "com.mysql.cj.jdbc.Driver":
                dataTypeFactory = new MySqlDataTypeFactory();
                break;

            case "org.hsqldb.jdbcDriver":
                dataTypeFactory = new HsqldbDataTypeFactory();
                break;

            default:
                dataTypeFactory = new DefaultDataTypeFactory();
                break;
        }
        dbConfig.setProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                dataTypeFactory
        );
        return dbConnection;
    }
}
