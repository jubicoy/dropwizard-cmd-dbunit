package fi.jubic.dropwizard.cmd.dbunit.cli;

import fi.jubic.dropwizard.cmd.dbunit.DatabaseConfigurator;
import fi.jubic.dropwizard.cmd.dbunit.template.base64.Base64Encoder;
import fi.jubic.dropwizard.cmd.dbunit.template.date.DateObject;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1, 27.6.2016.
 */
class DbUnitPopulateCommand<T extends Configuration> extends AbsDbUnitCommand<T> {
    private static Logger logger = LoggerFactory.getLogger(DbUnitPopulateCommand.class);

    //
    // Fields
    // **************************************************************
    private final DatabaseConfigurator databaseConfigurator;

    //
    // Constructor(s)
    // **************************************************************
    DbUnitPopulateCommand (
            DatabaseConfiguration<T> strategy,
            Class<T> configurationClass,
            DatabaseConfigurator databaseConfigurator
    ) {
        super(
                "populate",
                "Populate DB with test fixtures",
                strategy,
                configurationClass
        );
        this.databaseConfigurator = databaseConfigurator;
    }

    //
    // DbUnitCommand impl
    // **************************************************************
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
                .build(processStream(dataSetStream));

        if (databaseConfigurator != null)
            databaseConfigurator.configure(connection.getConfig());

        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

        dtdStream.close();
        dataSetStream.close();
        logger.info("DB populated");
    }

    //
    // Private methods
    // **************************************************************
    private InputStream processStream (
            InputStream is
    ) throws IOException, TemplateException {
        Template t = new Template(
                "dataset",
                new InputStreamReader(is),
                new freemarker.template.Configuration(
                        freemarker.template.Configuration.VERSION_2_3_23
                )
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> model = new HashMap<>();
        model.put("t", new DateObject(new Date()));
        model.put("base64", new Base64Encoder());
        t.process(model, new OutputStreamWriter(out));
        return new ByteArrayInputStream(out.toByteArray());
    }
}
