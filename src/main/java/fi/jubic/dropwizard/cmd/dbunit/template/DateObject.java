package fi.jubic.dropwizard.cmd.dbunit.template;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Vilppu Vuorinen, vilppu.vuorinen@jubic.fi
 * @since 0.1.1, 10.7.2016.
 */
public class DateObject extends HashMap<String, Object> {
    //
    // Constructor(s)
    // **************************************************************
    public DateObject (Date now) {
        super();
        put("now",   DateMethodUtil.toTimestamp(DateMethodUtil.convertDate(now)));
        put("add",   new DateAddMethod(now));
        put("sub",   new DateSubtractMethod(now));
        put("year",  "year");
        put("month", "month");
        put("week",  "week");
        put("day",   "day");
        put("hour",  "hour");
        put("min",   "min");
        put("sec",   "sec");
    }
}
