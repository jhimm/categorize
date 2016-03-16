/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package categorizing;
import java.util.*;
import java.io.*;
import java.util.jar.*;


/**
 *
 * @author Jeff
 */
public class JarScanner {

    public JarScanner() {
    }

    public static Set<Class<?>> getFromJARFile(String jar, String pName) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        String packageName = pName.replace(".", "/");
        JarInputStream jarFile;
        try {
            jarFile = new JarInputStream(new FileInputStream(jar));
        } catch (Exception ex) {
            return null;
        }
        JarEntry jarEntry;
        do {
            try {
                jarEntry = jarFile.getNextJarEntry();
            } catch (Exception ex) {
                return null;
            }
            if (jarEntry != null) {
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
//                    className = stripFilenameExtension(className);
                    className = className.replace(".class", "");

                    if (className.startsWith(packageName)) {
                        try {
                            classes.add(Class.forName(className.replace('/', '.')));
                        } catch (Exception ex) {
                            return null;
                        }
                    }
                }
            }
        } while (jarEntry != null);
        return classes;
    }
}
