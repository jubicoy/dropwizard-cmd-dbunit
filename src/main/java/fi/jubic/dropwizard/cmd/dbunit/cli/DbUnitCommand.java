package fi.jubic.dropwizard.cmd.dbunit.cli;

import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.dbunit.database.IDatabaseConnection;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1, 27.6.2016.
 */
public class DbUnitCommand<T extends Configuration> extends AbsDbUnitCommand<T> {
    //
    // Constants
    // **************************************************************
    private static final String COMMAND_NAME = "db-unit";
    private static final String COMMAND_NAME_ATTR = "subcommand";

    //
    // Fields
    // **************************************************************
    private final SortedMap<String, AbsDbUnitCommand<T>> subcommands = new TreeMap<>();

    //
    // Constructor(s)
    // **************************************************************
    public DbUnitCommand(
            DatabaseConfiguration<T> strategy,
            Class<T> configurationClass
    ) {
        super(
                COMMAND_NAME,
                "Manage test data",
                strategy,
                configurationClass
        );

        addSubcommand(new DbUnitPopulateCommand<>(strategy, configurationClass));
        addSubcommand(new DbUnitGenerateDtdCommand<>(strategy, configurationClass));
    }

    //
    // AbsDbUnitCommand impl
    // **************************************************************
    @Override
    public void configure (Subparser subparser) {
        for (AbsDbUnitCommand<T> subcommand : subcommands.values()) {
            final Subparser cmdParser = subparser
                    .addSubparsers()
                    .addParser(subcommand.getName())
                    .description(subcommand.getDescription())
                    .setDefault(COMMAND_NAME_ATTR, subcommand.getName());
            subcommand.configure(cmdParser);
        }
    }

    @Override
    protected void run (
            Bootstrap<T> bootstrap,
            Namespace namespace,
            IDatabaseConnection connection
    ) throws Exception {
        subcommands.get(namespace.getString(COMMAND_NAME_ATTR))
                .run(bootstrap, namespace, connection);
    }

    //
    // Private methods
    // *************************************************************
    private void addSubcommand (AbsDbUnitCommand<T> subcommand) {
        subcommands.put(subcommand.getName(), subcommand);
    }
}
