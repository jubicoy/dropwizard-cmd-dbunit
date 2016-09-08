package fi.jubic.dropwizard.cmd.dbunit.template.base64;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.Base64;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.2.1, 8.9.2016.
 */
public class Base64Encoder implements TemplateMethodModelEx {

    //
    // TemplateMethodEx impl
    // **************************************************************
    @Override
    public Object exec(List list) throws TemplateModelException {
        if (list.size() != 1)
            throw new TemplateModelException("Invalid number of arguments");

        try {
            return Base64.getEncoder().encodeToString(
                    ((String) list.get(0)).getBytes()
            );
        } catch (ClassCastException ignore) {
            throw new TemplateModelException("Invalid argument type.");
        }
    }
}
