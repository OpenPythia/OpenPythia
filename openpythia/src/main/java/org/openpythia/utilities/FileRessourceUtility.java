/**
 * Copyright 2012 msg systems ag
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.openpythia.utilities;

import javax.swing.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FileRessourceUtility {

    private static FileRessourceUtility privateInstance = new FileRessourceUtility();

    private FileRessourceUtility() {
    }

    private static String replaceSpecialCharacters(String input) {
        String stringx = input;

        stringx = stringx.replace("%20", " ");
        stringx = stringx.replace("%c2%a1", "¡");
        stringx = stringx.replace("%c2%a2", "¢");
        stringx = stringx.replace("%c2%a3", "£");
        stringx = stringx.replace("%c2%a4", "¤");
        stringx = stringx.replace("%c2%a5", "¥");
        stringx = stringx.replace("%c2%a6", "¦");
        stringx = stringx.replace("%c2%a7", "§");
        stringx = stringx.replace("%c2%a8", "¨");
        stringx = stringx.replace("%c2%a9", "©");
        stringx = stringx.replace("%c2%aa", "ª");
        stringx = stringx.replace("%c2%ab", "«");
        stringx = stringx.replace("%c2%ac", "¬");
        stringx = stringx.replace("%c2%ad", "­");
        stringx = stringx.replace("%c2%ae", "®");
        stringx = stringx.replace("%c2%af", "¯");
        stringx = stringx.replace("%c2%b0", "°");
        stringx = stringx.replace("%c2%b1", "±");
        stringx = stringx.replace("%c2%b2", "²");
        stringx = stringx.replace("%c2%b3", "³");
        stringx = stringx.replace("%c2%b4", "´");
        stringx = stringx.replace("%c2%b5", "µ");
        stringx = stringx.replace("%c2%b6", "¶");
        stringx = stringx.replace("%c2%b7", "·");
        stringx = stringx.replace("%c2%b8", "¸");
        stringx = stringx.replace("%c2%b9", "¹");
        stringx = stringx.replace("%c2%ba", "º");
        stringx = stringx.replace("%c2%bb", "»");
        stringx = stringx.replace("%c2%bc", "¼");
        stringx = stringx.replace("%c2%bd", "½");
        stringx = stringx.replace("%c2%be", "¾");
        stringx = stringx.replace("%c2%bf", "¿");
        stringx = stringx.replace("%c3%80", "À");
        stringx = stringx.replace("%c3%81", "Á");
        stringx = stringx.replace("%c3%82", "Â");
        stringx = stringx.replace("%c3%83", "Ã");
        stringx = stringx.replace("%c3%84", "Ä");
        stringx = stringx.replace("%c3%85", "Å");
        stringx = stringx.replace("%c3%86", "Æ");
        stringx = stringx.replace("%c3%87", "Ç");
        stringx = stringx.replace("%c3%88", "È");
        stringx = stringx.replace("%c3%89", "É");
        stringx = stringx.replace("%c3%8a", "Ê");
        stringx = stringx.replace("%c3%8b", "Ë");
        stringx = stringx.replace("%c3%8c", "Ì");
        stringx = stringx.replace("%c3%8d", "Í");
        stringx = stringx.replace("%c3%8e", "Î");
        stringx = stringx.replace("%c3%8f", "Ï");
        stringx = stringx.replace("%c3%90", "Ð");
        stringx = stringx.replace("%c3%91", "Ñ");
        stringx = stringx.replace("%c3%92", "Ò");
        stringx = stringx.replace("%c3%93", "Ó");
        stringx = stringx.replace("%c3%94", "Ô");
        stringx = stringx.replace("%c3%95", "Õ");
        stringx = stringx.replace("%c3%96", "Ö");
        stringx = stringx.replace("%c3%97", "×");
        stringx = stringx.replace("%c3%98", "Ø");
        stringx = stringx.replace("%c3%99", "Ù");
        stringx = stringx.replace("%c3%9a", "Ú");
        stringx = stringx.replace("%c3%9b", "Û");
        stringx = stringx.replace("%c3%9c", "Ü");
        stringx = stringx.replace("%c3%9d", "Ý");
        stringx = stringx.replace("%c3%9e", "Þ");
        stringx = stringx.replace("%c3%9f", "ß");
        stringx = stringx.replace("%c3%a0", "à");
        stringx = stringx.replace("%c3%a1", "á");
        stringx = stringx.replace("%c3%a2", "â");
        stringx = stringx.replace("%c3%a3", "ã");
        stringx = stringx.replace("%c3%a4", "ä");
        stringx = stringx.replace("%c3%a5", "å");
        stringx = stringx.replace("%c3%a6", "æ");
        stringx = stringx.replace("%c3%a7", "ç");
        stringx = stringx.replace("%c3%a8", "è");
        stringx = stringx.replace("%c3%a9", "é");
        stringx = stringx.replace("%c3%aa", "ê");
        stringx = stringx.replace("%c3%ab", "ë");
        stringx = stringx.replace("%c3%ac", "ì");
        stringx = stringx.replace("%c3%ad", "í");
        stringx = stringx.replace("%c3%ae", "î");
        stringx = stringx.replace("%c3%af", "ï");
        stringx = stringx.replace("%c3%b0", "ð");
        stringx = stringx.replace("%c3%b1", "ñ");
        stringx = stringx.replace("%c3%b2", "ò");
        stringx = stringx.replace("%c3%b3", "ó");
        stringx = stringx.replace("%c3%b4", "ô");
        stringx = stringx.replace("%c3%b5", "õ");
        stringx = stringx.replace("%c3%b6", "ö");
        stringx = stringx.replace("%c3%b7", "÷");
        stringx = stringx.replace("%c3%b8", "ø");
        stringx = stringx.replace("%c3%b9", "ù");
        stringx = stringx.replace("%c3%ba", "ú");
        stringx = stringx.replace("%c3%bb", "û");
        stringx = stringx.replace("%c3%bc", "ü");
        stringx = stringx.replace("%c3%bd", "ý");
        stringx = stringx.replace("%c3%be", "þ");
        stringx = stringx.replace("%c3%bf", "ÿ");

        return stringx;
    }

    private String getHome() {
        return replaceSpecialCharacters(getClass().getProtectionDomain()
                .getCodeSource().getLocation().toString().substring(6));
    }

    public static boolean isRunningFromJar() {
        boolean result;
        String home = privateInstance.getHome();
        // is the returned path ending with .jar?
        int lastDot = home.lastIndexOf('.');
        if (lastDot > 0 && lastDot < home.length() - 1) {
            result = home.substring(lastDot).toUpperCase().equals(".JAR");
        } else {
            result = false;
        }
        return result;
    }

    private static void copyFileFromJar(String sourceFileName, File target) {
        try {
            JarFile jar = new JarFile(privateInstance.getHome());
            ZipEntry entry = jar.getEntry(sourceFileName);

            InputStream inputStream = new BufferedInputStream(
                    jar.getInputStream(entry));
            OutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(target));
            byte[] buffer = new byte[4096];
            for (;;) {
                int nBytes = inputStream.read(buffer);
                if (nBytes <= 0)
                    break;
                outputStream.write(buffer, 0, nBytes);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private static void copyFileFromFileSystem(String sourceFileName,
                                               File target) {
        try {
            File in = new File("./Ressources/" + sourceFileName);
            FileChannel inChannel = new FileInputStream(in).getChannel();
            FileChannel outChannel = new FileOutputStream(target).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } catch (IOException e) {
                throw e;
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public static void copyFile(String sourceFileName, File target) {
        if (isRunningFromJar()) {
            copyFileFromJar(sourceFileName, target);
        } else {
            copyFileFromFileSystem(sourceFileName, target);
        }
    }

    private static String getStringFromJar(String ressourceIdentifier) {
        StringBuilder result = new StringBuilder();
        try {
            JarFile jar = new JarFile(privateInstance.getHome());
            ZipEntry entry = jar.getEntry(ressourceIdentifier);

            BufferedReader inputReader = new BufferedReader(
                    new InputStreamReader(jar.getInputStream(entry)));

            String newLine;
            while ((newLine = inputReader.readLine()) != null) {
                result.append(newLine);
            }
            inputReader.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return result.toString();
    }

//    private static String getStringFromFileSystem(String ressourceIdentifier) {
//        StringBuffer result = new StringBuffer();
//        try {
//            File in = new File("./Ressources/" + ressourceIdentifier);
//
//            BufferedReader inputReader = new BufferedReader(new FileReader(in));
//
//            String newLine;
//            while ((newLine = inputReader.readLine()) != null) {
//                result.append(newLine);
//            }
//            inputReader.close();
//        } catch (FileNotFoundException e) {
//            JOptionPane.showMessageDialog((Component) null, e);
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog((Component) null, e);
//        }
//
//        return result.toString();
//    }
//
//    public static String getStringFromRessource(String ressourceIdentifier) {
//        if (isRunningFromJar()) {
//            return getStringFromJar(ressourceIdentifier);
//        } else {
//            return getStringFromFileSystem(ressourceIdentifier);
//        }
//    }
}
