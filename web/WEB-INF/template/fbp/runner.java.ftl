import com.jpmorrsn.fbp.engine.*;

//FBP Runtime script
//${model.id}
//${model.name}
//${model.description}

def param=input
def program = new FbpRunner()
output=program.go()

public class FbpRunner extends Network{

public FbpRunner(){
//add inits here
}

@Override
protected void define() throws Exception {

//Components
<#list model.components as comp>
${comp}
</#list>

//Initial values
<#list model.iips as iip>
${iip}
</#list>

//Connections
<#list model.connections as conn>
${conn}
</#list>

}

}

//subnets
<#list model.subnets as subnet>
@ComponentDescription("${subnet.description}")
@InPort("IN")
@OutPort("OUT")
class ${subnet.name} extends SubNet{
protected void define() throws Exception {
    <#list subnet.components as comp2>
    ${comp2}
    </#list>

    <#list subnet.iips as iip2>
    ${iip2}
    </#list>
initialize("OUT", component("OUT"), port("NAME"));
initialize("IN", component("IN"), port("NAME"));

//Connections
    <#list subnet.connections as conn2>
    ${conn2}
    </#list>
}
}
</#list>
