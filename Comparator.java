import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Created by IntelliJ IDEA.
 * User: panuska
 * Date: Sep 2, 2009
 * Time: 9:06:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Comparator {

/*    private static interface FileEntry {
        String getName();
        boolean isDirectory();
        FileEntry[] listFiles();
        String getAbsolutePath();
        long length();
    }

    private static class FileEntryImpl implements FileEntry {

        private File f;

        public FileEntryImpl (File f) {
            this.f = f;
        }
        public String getName() {
            return f.getName();
        }

        public boolean isDirectory() {
            return f.isDirectory();
        }
        public FileEntry[] listFiles() {
            File[] files = f.listFiles();
            FileEntry[] fileEntries = new FileEntry[files.length];
            for (int i = 0; i < f.length(); i++) {
                fileEntries[i] = new FileEntryImpl (files[i]);
            }
            return fileEntries;
        }
        public String getAbsolutePath() {
            return f.getAbsolutePath();
        }

        public long length() {
            return f.length();
        }
    }

    private static class ZipFileEntryImpl implements FileEntry {

        ZipEntry entry;
        String path;

        ZipFileEntryImpl (ZipEntry entry, String path) {
            this.entry = entry;
            this.path = path;
        }

        public String getName() {
            return entry.getName();
        }

        public boolean isDirectory() {
            return false;               //
        }

        public FileEntry[] listFiles() {
            return null; // never called
        }

        public String getAbsolutePath() {
            return path+entry.getName();
        }

        public long length() {
            return entry.getSize();
        }
    }
*/
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Provide 2 directory names or 2 archive names");
            System.exit(-1);
        }
        File file1 = new File(args[0]);
        File file2 = new File(args[1]);
        System.out.println("Comparing [1] = "+file1+" and [2] = "+file2);
        if (file1.isDirectory() && file2.isDirectory()) {
            compareDirectories(file1, file2, "[1]", "[2]");
        } else if (isArchive(file1.getName()) && isArchive(file2.getName())) {
            compareArchive(file1, file2, "[1]", "[2]");
        } else {
            System.out.println("Both provided files must be either directories or archive files");
            System.exit(-1);
        }
//    for (String s : ignoreList ) {
//        ignoreSet.add(s);
//    }
    }

    // provided files must be directories
    static void compareDirectories(File dir1, File dir2, String name1, String name2) throws IOException {
        File[] files1 = dir1.listFiles();
        File[] files2 = dir2.listFiles();

        HashMap<String, File> map = new HashMap();
        for (File f: files2) {
            map.put(f.getName(),f);
        }

        for (File file1: files1) {
            String name = file1.getName();
            File file2 = map.remove(name);
            name1 = name1+"/"+file1.getName(); //todo this changes the input parameters!
            if (file2 == null) {
                System.out.println("[2] is missing; [1] = "+name1);
            } else {
                name2 = name2+"/"+file2.getName(); //todo this changes the input parameters!
                if (file1.isDirectory()) {
                    if (!file2.isDirectory()) {
                        System.out.println("One is dir, second one is not: "+name1+", "+name2);
                        break;
                    }
                    compareDirectories(file1, file2, name1, name2);
                }
                if (file2.isDirectory()) {
                    if (!file1.isDirectory()) {
                        System.out.println("One is file, second one is not: "+name1+", "+name2);
                        break;
                    }
                }
                if (file1.length() != file2.length()) {
                    if (isArchive(file1.getName())) {
                        compareArchive(file1, file2, file1.getAbsolutePath(), file2.getAbsolutePath());
                    } else {
                        System.out.println("Different sizes: "+name1);
//                       System.out.println("Different sizes: "+file1+", "+file2);
                    }
                } else {
                    // same sizes 

                }
            }
        }
        Set<String> names = map.keySet();

        for (String name : names) {
            System.out.println("[1] is missing; [2] = "+map.get(name)); // todo should be full name
        }
    }

//    private static
//    static void compareFileContent (File file1, File file2) {
        
        
//    }

//    private static byte buffer1[] = new byte[1024];
//    private static byte buffer2[]
//    public static void compareStreams (InputStream stream1, InputStream stream2) {

        
//    }

    private static boolean isArchive(String name) {
        return name.endsWith(".jar") || name.endsWith(".war") || name.endsWith(".ear") || name.endsWith(".sar") || name.endsWith(".zip");

    }

    static String[] ignoreList = {
            "META-INF/MANIFEST.MF",
            //"sdm.xml", "sdmConfig.xml", "uilabels.properties", "document.names.properties", "collection.names.properties", "pom.properties",
            //"configuration-properties.xml", "relation2propertyGenerator.xsl", "log4j.log", "shared.conf.exportImport.classes.xml"
    };
//    static Set ignoreSet = new HashSet();
    private static boolean isIgnored(String name) {
        for (String s : ignoreList) {
            if (name.endsWith(s)) {
                return true;
            }
        }
//        if (ignoreSet.contains(name)) {
//            return true;
//        }
//        if (name.endsWith("META-INF/MANIFEST.MF")) {
//            return true;
//        }
        return false;
    }

    /**
     *
     * @param file1 the file to be compared
     * @param file1 the file to be compared
     * @param file2 the file to be compared
     * @param name1 the original name where it resides (useful for archives stored in archives)
     * @param name2 the original name where it resides (useful for archives stored in archives)
     * @throws IOException
     */
    private static void compareArchive (File file1, File file2, String name1, String name2) throws IOException {
        ZipFile zipfile2 = new ZipFile(file2);
        Enumeration<? extends ZipEntry> entries2 = zipfile2.entries();
        Map <String,ZipEntry> map = new HashMap();
        while (entries2.hasMoreElements()) {
            ZipEntry entry2 = entries2.nextElement();
            map.put(entry2.getName(), entry2);
        }
        ZipFile zipfile1 = new ZipFile(file1);
        Enumeration<? extends ZipEntry> entries1 = zipfile1.entries();
        while (entries1.hasMoreElements()) {
            ZipEntry entry1 = entries1.nextElement();
            ZipEntry entry2 = map.remove(entry1.getName());
            if (entry2 == null) {
                System.out.println("[2] is missing: "+name1+"/"+entry1.getName());
            } else {
                // files are there
                if (entry1.getSize() != entry2.getSize()) {
                    if (isArchive(entry1.getName())) {
                        compareArchivedArchive(zipfile1, zipfile2, entry1, entry2, name1, name2);
                        // recursively check this archive
                    } else {
                        System.out.println("Different sizes: "+name1+"/"+entry1.getName());
//                        System.out.println("Different sizes: "+name1+"/"+entry1.getName()+", "+name2+"/"+entry2.getName());
                    }
                } else if (entry1.getCrc() != entry2.getCrc()) {
                    //todo is CRC reliable?
                    if (isArchive(entry1.getName())) {
                        compareArchivedArchive(zipfile1, zipfile2, entry1, entry2, name1, name2);
                        // recursively check this archive
                    } else {
                        if (!isIgnored(entry1.getName())) { // todo what else should be ignored? not only CRC...
                            System.out.println("Different CRCs: "+name1+"/"+entry1.getName());
//                            System.out.println("Different CRCs: "+name1+"/"+entry1.getName()+", "+name2+"/"+entry2.getName());
                        }
                    }
                } else {
                    // todo check content
                }
                // todo if different in size/CRC/content, check if they are archives and recurse
            }
        }
        Set<String> names = map.keySet();
        for (String name : names) {
            System.out.println("[1] is missing: "+name2+"/"+map.get(name));
        }

    }

    static byte[] buffer = new byte[16384];
    private static final void copyInputStream(InputStream in, OutputStream out)
      throws IOException
      {
        int len;

        while((len = in.read(buffer)) >= 0)
          out.write(buffer, 0, len);

        in.close();
        out.flush();
        out.close();
      }

    private static void compareArchivedArchive (ZipFile zipfile1, ZipFile zipfile2, ZipEntry entry1, ZipEntry entry2, String name1, String name2) throws IOException {
        File temp1 = File.createTempFile("comp", ".zip");
        File temp2 = File.createTempFile("comp", ".zip");
        copyInputStream(zipfile1.getInputStream(entry1), new BufferedOutputStream(new FileOutputStream(temp1)));
        copyInputStream(zipfile2.getInputStream(entry2), new BufferedOutputStream(new FileOutputStream(temp2)));
        compareArchive(temp1, temp2, name1+"/"+entry1.getName(), name2+"/"+entry2.getName());
//        temp1.delete();
//        temp2.delete();
        temp1.deleteOnExit();
        temp2.deleteOnExit();
    }

}
