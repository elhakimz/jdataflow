package org.hakim.fbp.util;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Purpose: Utility
 *
 * @author abilhakim
 *         Date: 9/19/14.
 */
public class Util {

    public static <Item> List<Item> filter(List<Item> in, Predicate<Item> f) {
        List<Item> out = new ArrayList<Item>(in.size());
        for (Item inObj : in) {
            if (f.apply(inObj)) {
                out.add(inObj);
            }
        }
        return out;
    }

    public static String stringFromStream(InputStream stream) throws UnsupportedEncodingException, IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder buf = new StringBuilder();
        String str;
        while ((str = in.readLine()) != null) {
            buf.append(str);
        }
        in.close();
        return buf.toString();
    }

    public static void writeLog(String s) {
        System.out.println("[LOG] " + s);
    }

    public static String readFile(String fileName) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String date = fileName.substring(6);
            return sb.toString();
        }

    }

    /**
     * get json file tree
     *
     * @param path
     * @return
     */
    public static String getJsonFileTree(String path) {
        Gson gson = new Gson();
        FileStruct struct = traverseFile(new File(path));
        String json = gson.toJson(struct);
        return json;
    }

    public static FileStruct traverseFile(File node) {

        System.out.println(node.getAbsoluteFile());
        FileStruct struct = new FileStruct();

        Path p = Paths.get(node.getAbsolutePath());
        String s = p.getFileName().toString();
        struct.setText(s);
        struct.setPath(node.getAbsolutePath());
        if (node.isDirectory()) {
            String[] subNote = node.list();
            List<FileStruct> nodes = new ArrayList<>();
            for (String filename : subNote) {
                nodes.add(traverseFile(new File(node, filename)));
            }
            struct.setNodes(nodes);
        }
        return struct;
    }

    public List<String> getFiles(Date date) {
        List<String> results = new ArrayList<String>();
        if (date == null) {
            date = new Date();
        }

        File[] files = new File("/").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {

            if (file.isFile()) {
                Date fileDate = new Date();
                fileDate.setTime(date.getTime());
                String sfileDate = System.out.format("%d-%d-%d", fileDate.getYear(), fileDate.getMonth(), fileDate.getDate()).toString();
                if (file.getName().endsWith(".txt") && file.lastModified() == date.getTime())
                    results.add(file.getName());
            }
        }
        return results;
    }

    public static class JSONObjectKeysIterable implements Iterable {
        JSONObject mObject;

        public JSONObjectKeysIterable(JSONObject o) {
            mObject = o;
        }

        public Iterator<String> iterator() {
            return mObject.keys();
        }
    }

    public static abstract class Predicate<Item> {
        protected abstract boolean apply(Item i);
    }

    public static class FileStruct {
        private String text = "";
        private String href = "";
        private List<FileStruct> nodes;
        private String path;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public List<FileStruct> getNodes() {
            return nodes;
        }

        public void setNodes(List<FileStruct> nodes) {
            this.nodes = nodes;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }


}
