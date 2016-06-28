package fi.jubic.dropwizard.cmd.dbunit.cli;

import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1, 27.6.2016.
 */
class DbUnitPopulateCommand<T extends Configuration> extends AbsDbUnitCommand<T> {
    private static Logger logger = LoggerFactory.getLogger(DbUnitPopulateCommand.class);
    //
    // Constructor(s)
    // **************************************************************
    DbUnitPopulateCommand (
            DatabaseConfiguration<T> strategy,
            Class<T> configurationClass
    ) {
        super(
                "populate",
                "Populate DB with test fixtures",
                strategy,
                configurationClass
        );
    }

    @Override
    void run (
            Bootstrap<T> bootstrap,
            Namespace namespace,
            IDatabaseConnection connection
    ) throws Exception {
        logger.info("Populating DB with dataset ...");
        ClassLoader cl = getClass().getClassLoader();
        InputStream dtdStream = cl.getResourceAsStream("dataset.dtd");
        InputStream dataSetStream = cl.getResourceAsStream("dataset.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder()
                .setMetaDataSetFromDtd(dtdStream)
                .build(dataSetStream);
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

        dtdStream.close();
        dataSetStream.close();
        logger.info("DB populated");
    }
}
