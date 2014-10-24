<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN"
        "http://struts.apache.org/dtds/struts-2.0.dtd">
<struts>
    <constant name="struts.devMode" value="true" />
    <package name="helloworld" extends="struts-default">

        <action name="hello"
                class="com.tutorialspoint.struts2.HelloWorldAction"
                method="execute">
            <result name="success">/HelloWorld.jsp</result>
        </action>
        ${model.actions}

    </package>
    <-- more packages can be listed here -->

</struts>