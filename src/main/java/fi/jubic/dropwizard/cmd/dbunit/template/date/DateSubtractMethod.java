package fi.jubic.dropwizard.cmd.dbunit.template.date;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

import java.util.Date;
import java.util.List;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1.1, 10.7.2016.
 */
class DateSubtractMethod implements TemplateMethodModelEx {
    //
    // Fields
    // **************************************************************
    private final Date now;

    //
    // Constructor(s)
    // **************************************************************
    DateSubtractMethod(Date now) {
        this.now = now;
    }

    //
    // TemplateMethod impl
    // **************************************************************
    @Override
    public Object exec (List list) throws TemplateModelException {
        if (list.size() != 2)
            throw new TemplateModelException("Invalid number of arguments");

        return DateMethodUtil.toTimestamp(
                DateMethodUtil.subtract(
                        now,
                        DateMethodUtil.<TemplateNumberModel>safeGet(list, 0).getAsNumber().intValue(),
                        DateMethodUtil.<TemplateScalarModel>safeGet(list, 1).getAsString()
                )
        );
    }
}
