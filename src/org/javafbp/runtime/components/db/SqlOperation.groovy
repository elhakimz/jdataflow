package org.javafbp.runtime.components.db

import com.jpmorrsn.fbp.engine.*

import java.sql.Connection

/**
 * Purpose:
 * @author abilhakim
 * Date: 10/23/14.
 */

@ComponentDescription("SQL Dataset")
@InPorts([@InPort("TABLE"),@InPort("OPERATION"), @InPort("DATA"), @InPort("CONN")])
@OutPort("OUT")
class SqlOperation extends Component {

    InputPort inName
    InputPort inOperation
    InputPort inData
    InputPort inConn

    OutputPort outOut
    Connection conn

    @Override
    protected void execute() throws Exception {
      String name = inName.receive().content as String
      String operation = inOperation.receive().content as String
      def data = inData.receive().content
      conn = inConn.receive().content as Connection
        switch(operation.toUpperCase().trim()){
          case "INSERT": addRows(name,data); break;
          case "DELETE":addRows(name,data);break;
          case "UPDATE":addRows(name,data);break;
        }

    }

    @Override
    protected void openPorts() {
      inName=openInput("TABLE")
      inOperation=openInput("OPERATION")
      inData = openInput("DATA")
      inConn = openInput("CONN")
      outOut = openOutput("OUT")
    }

    /**
     * add rows
     * @param name
     * @param rows
     */
    def addRows(String name,def rows){

        rows.each{

        }

    }

    /**
     * del rows
     * @param name
     * @param rows
     */
    def delRows(String name,def rows){
       rows.each{

       }

    }

    def updateRow(String name, def row){


    }
}
