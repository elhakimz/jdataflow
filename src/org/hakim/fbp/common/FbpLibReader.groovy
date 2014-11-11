package org.hakim.fbp.common

import com.jpmorrsn.fbp.engine.*
import org.hakim.zen.pageflow.components.PageFlowComponent
import org.reflections.Reflections

/**
 * @author abilhakim
 * Date: 9/19/14.
 */
class FbpLibReader {

    /**
     * get set of fbp component class
     * @param packageName
     * @return
     */
    static Set<Class<?>> getFbpComponentClasses(String packageName) {

        Reflections reflections = new Reflections(packageName)

        Set<Class<?>> annotated
        annotated = reflections.getTypesAnnotatedWith(ComponentDescription.class);
        if (annotated.empty) {
            annotated = reflections.getTypesAnnotatedWith(PageFlowComponent.class);
        }

        return annotated
    }

    /**
     * get description string of an FBP component
     * @param aClass
     * @return
     */
    static String getFbpComponentDescription(Class<?> aClass) {
        if (aClass.isAnnotationPresent(ComponentDescription.class)) {
            ComponentDescription desc = aClass.getAnnotation(ComponentDescription.class)
            return desc.value()
        } else if (aClass.isAnnotationPresent(PageFlowComponent.class)) {
            PageFlowComponent desc = aClass.getAnnotation(PageFlowComponent.class)
            return desc.value()
        } else {
            return null
        }
    }

    /**
     * get inports from an FBP component
     * @param aClass
     * @return
     */
    static List<InPort> getInPortsFrom(Class<?> aClass) {
        List<InPort> lst = []
        if (aClass.isAnnotationPresent(InPorts)) {
            InPorts ports = aClass.getAnnotation(InPorts)
            ports.value().each {
                lst.add(it)
            }
        } else if (aClass.isAnnotationPresent(InPort)) {
            InPort port = aClass.getAnnotation(InPort)
            lst.add(port)
        }
        return lst
    }

    /**
     * get outports
     * @param aClass
     * @return
     */
    static List<OutPort> getOutPortsFrom(Class<?> aClass) {

        List<OutPort> lst = []
        if (aClass.isAnnotationPresent(OutPorts)) {
            OutPorts ports = aClass.getAnnotation(OutPorts)
            ports.value().each {
                lst.add(it)
            }
        } else if (aClass.isAnnotationPresent(OutPort)) {
            OutPort port = aClass.getAnnotation(OutPort)
            lst.add(port)
        }
        return lst

    }


}
