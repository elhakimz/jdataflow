package org.hakim.fbp.common.model

/**
 * Purpose:
 * @author abilhakim
 * Date: 9/19/14.
 */
class FbpCompDefModel implements Serializable {
   String color=""
   String compType
   String javaType
   String icon='book'
   String name
   String label
   String description
   int width
   int height
   List<FbpPort> inputs=[]
   List<FbpPort> outputs=[]

   public static class FbpPort{
     String name
     String type
     String value
     String description=""
     String multiple = "true"
     String widget
   }
}
