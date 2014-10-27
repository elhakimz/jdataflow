package org.javafbp.runtime.components.io

import com.google.gson.Gson
import com.jpmorrsn.fbp.engine.*
import org.javafbp.runtime.pattern.InPortWidget

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/27/14.
 */
@ComponentDescription("List files in a directory ")
@InPorts([@InPort(value = "PATH", description = "Absolute path")
        , @InPort(value = "FILTER", description = "File filter", optional = true)])
@OutPort("OUT")
@InPortWidget(value = "PATH", widget = InPortWidget.SELECT_ABS_DIR)
class ListFiles extends Component {

    InputPort inPath
    InputPort inFilter
    OutputPort outputPort

    @Override
    protected void execute() throws Exception {

        Packet p1 = inPath.receive()
        String path = p1.content


        String filter
        Packet p2

        if ((p2 = inFilter.receive())) {
            filter = p2.content
        }

        def files = []

        if (filter && !filter.empty) {
            new File(path).eachFileMatch(filter) { f ->
                files.add(f.path)
            }
        } else {
            new File(path).eachFile { f2 ->
                files.add(f2.path)
            }
        }

        Gson gson = new Gson()
        outputPort.send(create(gson.toJson(files)))

        inPath.close()
        inFilter.close()
        drop(p1)

        if (filter) drop(p2)

    }

    @Override
    protected void openPorts() {
        inPath = openInput("PATH")
        inFilter = openInput("FILTER")
        outputPort = openOutput("OUT")
    }

}
