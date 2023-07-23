package utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


// a util which is used to load the class we need
public final class PackageUtil {


    @FunctionalInterface
    static public interface IClassFilter{
        boolean accept(Class<?> clazz);
    }

    private PackageUtil(){
    }

    static public Set<Class<?>> listSubClass(String packageName, boolean recursive, Class<?> superClass) {
        if (superClass == null) {
            return Collections.emptySet();
        } else {
            return listClass(packageName, recursive, superClass::isAssignableFrom);
        }
    }


    // get the set of Object which is
    static public Set<Class<?>> listClass(String packageName, boolean recursive, IClassFilter filter) {
        if (packageName == null || packageName.isEmpty()) return null;
        final String packagePath = packageName.replace("." ,"/");

        //get the classloaderjava.lang.RuntimeException: Cannot reconnect.
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // get the set of results
        Set<Class<?>> result = new HashSet<>();

        try {
            Enumeration<URL> urlEnumeration = loader.getResources(packagePath);
            while (urlEnumeration.hasMoreElements()) {
                URL currURL = urlEnumeration.nextElement();

                final String protocol = currURL.getProtocol();
                Set<Class<?>> tmpSet = null;

                if ("FILE".equalsIgnoreCase(protocol)) {
                    tmpSet = listClassFromPath(new File(currURL.getFile()), packageName, recursive, filter);
                } else if ("JAR".equalsIgnoreCase(protocol)) {
                    String fileStr = currURL.getFile();

                    // eliminate the starting "file"
                    if (fileStr.startsWith( "file:")) {
                        fileStr = fileStr.substring(5);
                    }

                    if (fileStr.lastIndexOf("!") > 0) {
                        fileStr = fileStr.substring(0, fileStr.lastIndexOf("!"));
                    }

                    tmpSet = listClassFromJar(new File(fileStr), packageName, recursive, filter);
                }
                if (tmpSet != null) {
                    result.addAll(tmpSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static private String join(String[] strArr, String conn) {
        if (null == strArr || strArr.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strArr.length; i ++) {
            if (i > 0) {
                sb.append(conn);
            }
            sb.append(strArr[i]);
        }
        return sb.toString();
    }

    static private String trimLeft(String src, String trimStr) {
        if (null == src || src.isEmpty()) {
            return "";
        }

        if (null == trimStr || trimStr.isEmpty()) {
            return src;
        }

        if (src.equals(trimStr)) return "";

        while (src.startsWith(trimStr)) {
            src = src.substring(trimStr.length());
        }
        return src;
    }



    static private Set<Class<?>> listClassFromJar(final File jarFilePath, String packageName,
                                                  final boolean recursive, IClassFilter filter) {
        if (jarFilePath == null || jarFilePath.isDirectory()) {
            return null;
        }

        Set<Class<?>> result = new HashSet<>();
        try {
            // create .jar stream
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));

            JarEntry entry;

            while ((entry = jarIn.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                String entryName = entry.getName();
                // if it is not ended with .class. it is not a java class,
                if (!entryName.endsWith(".class")) {
                    continue;
                }

                String tmpStr = entryName.substring(0, entryName.lastIndexOf('/'));

                tmpStr = join(tmpStr.split("/"), ".");

                //if it is recursive, check if it is in
                if (!recursive){
                    if (!packageName.equals(tmpStr)) continue;
                } else if (!tmpStr.startsWith(packageName)) {
                    continue;
                }

                String className;

                className = entryName.substring(0, entryName.lastIndexOf("."));
                className = join(className.split("/"), ".");

                Class<?> classObj = Class.forName(className);

                if (null != filter && !filter.accept(classObj)) {
                    continue;
                }
                result.add(classObj);
            }
            jarIn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    static public Set<Class<?>> listClassFromPath(final File dirFile, final String packageName, final boolean recursive,
                                                  IClassFilter filter) {

        if (!dirFile.exists() || ! dirFile.isDirectory()) {
            return null;
        }

        // get sub file list
        File[] subFileArr = dirFile.listFiles();

        if (subFileArr == null || subFileArr.length <= 0) return null;

        Queue<File> fileQ = new LinkedList<>(Arrays.asList(subFileArr));

        // result object
        Set<Class<?>> resultSet = new HashSet<>();


        while (!fileQ.isEmpty()) {
            // 从队列中获取文件
            File currFile = fileQ.poll();

            if (currFile.isDirectory() &&
                    recursive) {
                // 如果当前文件是目录,
                // 并且是执行递归操作时,
                // 获取子文件列表
                subFileArr = currFile.listFiles();

                if (subFileArr != null &&
                        subFileArr.length > 0) {
                    // 添加文件到队列
                    fileQ.addAll(Arrays.asList(subFileArr));
                }
                continue;
            }

            if (!currFile.isFile() ||
                    !currFile.getName().endsWith(".class")) {
                // 如果当前文件不是文件,
                // 或者文件名不是以 .class 结尾,
                // 则直接跳过
                continue;
            }

            // 类名称
            String clazzName;

            // 设置类名称
            clazzName = currFile.getAbsolutePath();
            // 清除最后的 .class 结尾
            clazzName = clazzName.substring(dirFile.getAbsolutePath().length(), clazzName.lastIndexOf('.'));
            // 转换目录斜杠
            clazzName = clazzName.replace('\\', '/');
            // 清除开头的 /
            clazzName = trimLeft(clazzName, "/");
            // 将所有的 / 修改为 .
            clazzName = join(clazzName.split("/"), ".");
            // 包名 + 类名
            clazzName = packageName + "." + clazzName;

            try {
                // 加载类定义
                Class<?> clazzObj = Class.forName(clazzName);

                if (null != filter &&
                        !filter.accept(clazzObj)) {
                    // 如果过滤器不为空,
                    // 且过滤器不接受当前类,
                    // 则直接跳过!
                    continue;
                }

                // 添加类定义到集合
                resultSet.add(clazzObj);
            } catch (Exception ex) {
                // 抛出异常
                throw new RuntimeException(ex);
            }
        }

        return resultSet;
    }

}
