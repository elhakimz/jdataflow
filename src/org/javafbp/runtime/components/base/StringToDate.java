package org.javafbp.runtime.components.base;

import com.jpmorrsn.fbp.engine.*;
import org.joda.time.DateTime;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 10/3/14.
 */
@ComponentDescription("Convert a string IN input to date with format FORMAT to date type OUT")
@InPorts({@InPort(value = "IN", description = "date string"),@InPort(value = "FORMAT", description = "date format eg: dd-MMM-yyyy")})
@OutPort(value = "OUT", type = DateTime.class, description = "output as date")
public class StringToDate extends Component {
    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
