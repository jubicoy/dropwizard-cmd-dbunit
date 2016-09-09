package fi.jubic.dropwizard.cmd.dbunit.cli;

import fi.jubic.dropwizard.cmd.dbunit.DatabaseConfigurator;
import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1, 27.6.2016.
 */
class DbUnitGenerateDtdCommand<T extends Configuration> extends AbsDbUnitCommand<T> {
    private static Logger logger = LoggerFactory.getLogger(DbUnitGenerateDtdCommand.class);

    //
    // Fields
    // **************************************************************
    private final DatabaseConfigurator databaseConfigurator;

    //
    // Constructor(s)
    // **************************************************************
    DbUnitGenerateDtdCommand (
            DatabaseConfiguration<T> strategy,
            Class<T> configurationClass,
            DatabaseConfigurator databaseConfigurator
    ) {
        super(
                "generate-dtd",
                "Generate DTD schema from DB and prints it to System.out",
                strategy,
                configurationClass
        );
        this.databaseConfigurator = databaseConfigurator;
    }

    //
    // AbsDbUnitCommand impl
    // **************************************************************
    @Override
    void run (
            Bootstrap<T> bootstrap,
            Namespace namespace,
            IDatabaseConnection connection
    ) throws Exception {
        logger.info("DB schema DTD:");
        if (databaseConfigurator != null)
            databaseConfigurator.configure(connection.getConfig());

        FlatDtdDataSet.write(
                connection.createDataSet(),
                System.out
        );
    }
}
