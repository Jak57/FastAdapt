package com.reve.util;

//import com.reve.conference.Application;
//import com.reve.utils.Functions;
//import com.reve.utils.configuration.OSDetector;
//import com.reve.utils.file.FileUtil;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static com.reve.util.FileUtil.dropExtension;

public class LibraryLoader{

    private static final Set<String> loadedMap = new HashSet<>();

    private static boolean localDebug = true;

    private static String addFileName(String libraryName){
        String fileNameWithoutExtension
                = libraryName.split("/")[libraryName.split("/").length - 1];
        return libraryName + "/" + fileNameWithoutExtension;
    }

    /**
     * @return the expected extension format for the conference OS
     */
    private static String addExtensionString(String libraryName) {
        return libraryName+".dll";
    }


    /**
     *
     * @param libraryFilePathInResources is the library file path in respect to "resources" folder
     *                                   ex : "lib/audio/opus/opus11.dll"
     * @return a temporary copy of the original library file to be used for an ongoing processing
     */
    public static File createTempCopyOfLibFile(String libraryFilePathInResources){
        File libFile = null;

        try {
            String fileName = libraryFilePathInResources.split("/") [libraryFilePathInResources.split("/").length - 1];

            InputStream inputStream = Functions.class.getClassLoader().getResourceAsStream(libraryFilePathInResources);
            if(inputStream == null){
                inputStream = new FileInputStream(new File(Paths.get("src", "main", "resources").toAbsolutePath().toString()+"/"+libraryFilePathInResources));
                if(inputStream == null){
                    //if (Application.LOG_DEBUG) logger.debug("couldn't even load manually from \"resources\" folder");
                    System.out.println("couldn't even load manually from \"resources\" folder");
                }
            }
            Throwable var3 = null;

            try {
                libFile = File.createTempFile(dropExtension(fileName), fileName.substring(fileName.indexOf(".")));
                if(LibraryLoader.localDebug) {
                    //if (Application.LOG_DEBUG) logger.debug("temp file : {}",libFile.getAbsolutePath());
                    System.out.println("temp file : " + libFile.getAbsolutePath());
                }
                libFile.deleteOnExit();
                FileOutputStream outputStream = new FileOutputStream(libFile);
                Throwable var5 = null;

                try {
                    byte[] buffer = new byte[4096];

                    int bytesRead;
                    while((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (Throwable var31) {
                    var5 = var31;
                    throw var31;
                } finally {
                    if (outputStream != null) {
                        if (var5 != null) {
                            try {
                                outputStream.close();
                            } catch (Throwable var30) {
                                var5.addSuppressed(var30);
                            }
                        } else {
                            outputStream.close();
                        }
                    }

                }
            } catch (Throwable var33) {
                var3 = var33;
                throw var33;
            } finally {
                if (inputStream != null) {
                    if (var3 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var29) {
                            var3.addSuppressed(var29);
                        }
                    } else {
                        inputStream.close();
                    }
                }

            }

            return libFile;
        } catch (IOException var35) {
            throw new RuntimeException(var35);
        }
    }

    /**
     * loads a copy of that library file so that original library file
     * remains untouched from ongoing processing
     * @param libraryFilePathInResources library file path from "resources" folder
     *                          ex : "lib/audio/opus"
     */
    public static synchronized void loadLibrary(String libraryFilePathInResources){
        libraryFilePathInResources = addFileName(libraryFilePathInResources);
        libraryFilePathInResources = addExtensionString(libraryFilePathInResources);
        if(loadedMap.contains(libraryFilePathInResources)) {
//            if(LibraryLoader.localDebug) logger.debug("{} library already exist",libraryFilePathInResources);
            System.out.println("library already loaded : "+libraryFilePathInResources);
            return;
        }
        File tempLibFileCopy = LibraryLoader.createTempCopyOfLibFile(libraryFilePathInResources);

//        System.out.println("successfully created file....temp .....\n\n");

        assert tempLibFileCopy != null;
        if(LibraryLoader.localDebug){
//            if (Application.LOG_DEBUG) logger.debug("Absolute path is {}", tempLibFileCopy.getAbsolutePath());
            System.out.println("Absolute path is " + tempLibFileCopy.getAbsolutePath());
        }

        Runtime.getRuntime().load(tempLibFileCopy.getAbsolutePath());
        loadedMap.add(libraryFilePathInResources);
    }

}
