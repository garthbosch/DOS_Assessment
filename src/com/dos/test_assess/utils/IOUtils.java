package com.oldmutual.tsa.selenium.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;

/**
 * <p>Contains File/IO oriented functions.
 *
 * <p>Belongs to Magmatec Commons Core library
 *
 * @author Bruce Wayne Hart - Magmatec (Pty) Ltd
 * @since JDK1.5
 */
public final class IOUtils {

    private IOUtils() {
    }

    /**
     * <p>This method implements a recursive 'remove directory' function.
     *
     * <p>NOTE: This method is inherently dangerous - please use carefully!
     *
     * @param dir directory to remove
     * @throws IllegalArgumentException if an invalid File object (directory)
     * has been requested for deletion.
     */
    public static void removeDirectory(File dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (!dir.exists()) {
            throw new IllegalArgumentException("Directory " + dir.getAbsolutePath() + " does not exist");
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    removeDirectory(files[i]);
                } else {
                    remove(files[i]);
                }
            }
        }

        files = dir.listFiles();
        if (files == null || files.length == 0) {
            remove(dir);
        }

        dir.delete();
    }

    /**
     * This method will delete one file.
     *
     * @param file File to delete
     */
    public static void remove(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " does not exist");
        }
        file.delete();
    }

    /**
     * Copies contents of an InputStream to an OutputStream.
     *
     * @param in input stream
     * @param out output stream
     * @throws IOException if a file could not be opened or read if there was a
     * problem performing the operation
     */
    public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        if (in == null || out == null) {
            throw new IllegalArgumentException("InputStream is compulsory!");
        }

        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * Reads a file into a byte array.
     *
     * @param file the file to read
     * @return a byte[] representing the file
     * @throws IOException if a file could not be opened or read if there was a
     * problem performing the operation
     */
    public static byte[] readFileIntoByteArray(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File parameter is compulsory!");
        }/*
         * else if (file == null) { throw new IllegalArgumentException("File ["
         * + file.getAbsolutePath() + "] does not exist!");
        }
         */

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(file);
        while (fis.available() > 0) {
            byte[] b = new byte[64];
            fis.read(b);
            baos.write(b);
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * Reads from a File into a String.
     *
     * @param file the input file
     * @return a String containing contents of File
     * @throws IOException if a file could not be opened or read thrown if the
     * file could not be found, or opened
     */
    public static String readFileIntoString(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File parameter is compulsory!");
        } else if (file == null) {
            throw new IllegalArgumentException("File [" + file.getAbsolutePath() + "] does not exist!");
        }

        StringBuilder sb = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            sb.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Reads from an InputStream into a String.
     *
     * @param stream the input stream to read from
     * @return a String containing contents of input stream
     * @throws IOException if a file could not be opened or read
     */
    public static String readInputStreamIntoString(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("InputStream parameter is compulsory!");
        }

        StringBuilder sb = new StringBuilder();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            stream.close();
        }
        return sb.toString();

    }

    /**
     * Writes a String to the end of a File.
     *
     * @param s the String
     * @param file the target file to write to
     * @throws IOException if a file could not be opened or read
     */
    public static void writeStringToEndOfFile(String s, File file) throws IOException {
        if (file.getAbsoluteFile() == null) {
            file.mkdir();
        }
        if (file == null) {
            throw new IllegalArgumentException("File parameter is compulsory!");
        }
        if (s == null) {
            throw new IllegalArgumentException("String to be written is missing!");
        } else {
            if (!file.exists()) {
                FileUtils.writeStringToFile(file, s);
            } else {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(s);
                bw.newLine();
                bw.close();
                return;
            }
        }
    }

    /**
     * Writes a String to a File.
     *
     * @param s the String
     * @param file the target file to write to
     * @throws IOException if a file could not be opened or read
     */
    public static void writeStringToFile(String s, File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File parameter is compulsory!");
        }
        if (s == null) {
            throw new IllegalArgumentException("String to be written is missing!");
        } else {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(s);
            pw.flush();
            pw.close();
            fos.flush();
            fos.close();
            return;
        }
    }

    /**
     * Writes a byte array to a File.
     *
     * @param ba the byte array to write
     * @param file the target file to write to
     * @throws IOException if a file could not be opened or read
     */
    public static void writeBytesToFile(byte[] ba, File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File parameter is compulsory!");
        }
        if (ba == null) {
            throw new IllegalArgumentException("Byte[] to be written is missing!");
        } else {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(ba);
            fos.flush();
            fos.close();
            return;
        }
    }

    public static void deleteContentFromFile(File file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("File parameter is compulsory!");
        } else {
//            StringBuilder sb = new StringBuilder();
            String line = null;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
//                sb.append(line).append("\n");
                    line.replaceAll(line, "");
                }
                line = null;
            } catch (Exception ex) {
//            do nothing here
            }
        }

    }

    public static void zipDirectory(String evidenceFolder) throws Exception {
        List<String> fileList = new ArrayList<String>();               
        String zippedEvidenceFolder = evidenceFolder + ".zip";
        
        generateFileList(new File(evidenceFolder), fileList, evidenceFolder);
        zipIt(evidenceFolder, zippedEvidenceFolder, fileList);
    }
    
    /**
     * Traverse a directory and get all files, and add the file into fileList
     *
     * @param node file or directory
     */
    private static void generateFileList(File node, List<String> fileList, String evidenceFolder) {
        //add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), evidenceFolder));
        }
        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename), fileList, evidenceFolder);
            }
        }
    }

    /**
     * Zip it
     *
     * @param zippedEvidenceFolder output ZIP file location 
     */
    private static void zipIt(String evidenceFolder, String zippedEvidenceFolder, List<String> fileList) {
        byte[] buffer = new byte[1024];
        try {
            FileOutputStream fos = new FileOutputStream(zippedEvidenceFolder);           
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zippedEvidenceFolder);

            for (String file : fileList) {

                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in =
                        new FileInputStream(evidenceFolder + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private static String generateZipEntry(String file, String evidenceFolder) {
        return file.substring(evidenceFolder.length() + 1, file.length());
    }    
}
