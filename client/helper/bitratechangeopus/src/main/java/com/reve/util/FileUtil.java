package com.reve.util;

import java.io.*;

public class FileUtil {
    public FileUtil() {
    }

    public static String readResourceFile(String fileLocation) {
        return readResourceFile(fileLocation, (String)null);
    }

    public static String readResourceFile(String fileLocation, String lineSeparator) {
        StringBuilder builder = new StringBuilder();

        try {
            InputStream inputStream = Functions.class.getClassLoader().getResourceAsStream(fileLocation);

            try {
                assert inputStream != null;

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                try {
                    while((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                        if (lineSeparator != null) {
                            builder.append(lineSeparator);
                        }
                    }
                } catch (Throwable var9) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }

                    throw var9;
                }

                bufferedReader.close();
            } catch (Throwable var10) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                    }
                }

                throw var10;
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException var11) {
            IOException e = var11;
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    public static void renameAllTempFiles(File watchDir) {
        File[] allFiles = watchDir.listFiles();
        if (allFiles != null) {
            String parentDir = watchDir.getAbsolutePath();
            File[] var3 = allFiles;
            int var4 = allFiles.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File srcFile = var3[var5];
                if (srcFile.isFile()) {
                    String fileName = srcFile.getName();
                    if (fileName.contains(".tmp")) {
                        fileName = fileName.replace(".tmp", ".csv");
                        File destinationFile = new File(parentDir, fileName);
                        srcFile.renameTo(destinationFile);
                    }
                }
            }

        }
    }

    public static boolean renameTmpFile(String fileName, boolean changeFolder, String newFolderExtension) {
        File originalFile = new File(fileName);
        int indexOfDot = fileName.indexOf(".");
        String destFileName = fileName;
        if (indexOfDot > 0) {
            destFileName = fileName.substring(0, indexOfDot) + ".csv" + fileName.substring(indexOfDot + 4);
        }

        File destinationFile;
        if (changeFolder) {
            File parentDir = originalFile.getParentFile();
            String destinationDirStr = parentDir.toString() + newFolderExtension;
            File destinationDir = new File(destinationDirStr);
            if (!destinationDir.exists()) {
                destinationDir.mkdir();
            }

            File destinationFileWithDirectory = new File(destFileName);
            destinationFile = new File(destinationDir, destinationFileWithDirectory.getName());
        } else {
            destinationFile = new File(destFileName);
        }

        return originalFile.renameTo(destinationFile);
    }

    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf(46);
        return index < 0 ? fileName : fileName.substring(index + 1);
    }

    public static String dropExtension(String fileName) {
        int index = fileName.lastIndexOf(46);
        return index < 0 ? fileName : fileName.substring(0, index);
    }

    public static File createTempFile(String fileNameInsideJar) {
        File libFile = null;

        try {
            InputStream inputStream = Functions.class.getClassLoader().getResourceAsStream(fileNameInsideJar);

            try {
                libFile = File.createTempFile(dropExtension(fileNameInsideJar), fileNameInsideJar.substring(fileNameInsideJar.indexOf(".")));
                libFile.deleteOnExit();
                FileOutputStream outputStream = new FileOutputStream(libFile);

                try {
                    byte[] buffer = new byte[4096];

                    int bytesRead;
                    while((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (Throwable var8) {
                    try {
                        outputStream.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }

                    throw var8;
                }

                outputStream.close();
            } catch (Throwable var9) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var6) {
                        var9.addSuppressed(var6);
                    }
                }

                throw var9;
            }

            if (inputStream != null) {
                inputStream.close();
            }

            return libFile;
        } catch (IOException var10) {
            IOException e = var10;
            throw new RuntimeException(e);
        }
    }
}
