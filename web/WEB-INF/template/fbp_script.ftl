//imports
import java.util.*

<#list model.importList as importItem>
   ${importItem}
</#list>

//routine
FbpRunner runner = new FbpRunner()
runner.go()

//class definition

/**
* runner class
* filled with FBP routines
**/
class FbpRunner extends Network {

  protected void define() {
    //components
  <#list model.componentList as component>
     ${component}
  </#list>

   //initializer
  <#list model.initList as init>
    ${init}
  </#list>

    //networks
  <#list model.networkList as network>
    ${network}
  </#list>



}


}