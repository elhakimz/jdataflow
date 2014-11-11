package org.javafbp.runtime.components.zen;

import com.jpmorrsn.fbp.engine.*;

/**
 * Purpose:
 *
 * @author abilhakim
 *         Date: 11/8/14.
 */
@ComponentDescription("Create Application Artifact")
@InPorts({
        @InPort(value = "NAME", type = String.class, description = "Application name")
        , @InPort(value = "DESCR", type = String.class, description = "Description")
        , @InPort(value = "FRAMEWORK", type = String.class, description = "Application Framework")
        , @InPort(value = "URL", type = String.class, description = "URL of Applicatoon")
        , @InPort(value = "DIRPATH", type = String.class, description = "Directory of Applicatoon")
        , @InPort(value = "DATASOURCE", type = String.class, description = "Data source of applicatoon")
})
@OutPort("OUT")
public class CreateArtifact extends Component {

    @Override
    protected void execute() throws Exception {

    }

    @Override
    protected void openPorts() {

    }
}
