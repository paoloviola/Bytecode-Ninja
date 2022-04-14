package bytecodeninja.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class IOUtil
{

    /**
     * Creates a directory on the specified path
     * @param file the directory file to be created
     * @return the file which has been passed through the arguments
     */
    public static File createDirectory(File file) {
        try {
            createPathIfNotExists(file, true);
            return file;
        }
        catch (IOException e) {
            // This is supposed to never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a file on the specified path
     * @param file the file to be created
     * @return the file which has been passed through the arguments
     * @throws IOException if an I/O error occurred
     */
    public static File createFile(File file) throws IOException {
        createPathIfNotExists(file, false);
        return file;
    }

    /**
     * Creates a path file or directory
     * @param file the file path to be created
     * @param dir if the path is a directory or a regular file
     * @return the file which has been passed through the arguments
     * @throws IOException if an I/O error occurred
     */
    public static File createPath(File file, boolean dir) throws IOException {
        createPathIfNotExists(file, dir);
        return file;
    }

    /**
     * Creates a path file or directory
     * @param file the file path to be created
     * @param dir if the path is a directory or a regular file
     * @return the file on successful creation, otherwise null
     * @throws IOException if an I/O error occurred
     */
    public static boolean createPathIfNotExists(File file, boolean dir) throws IOException {
        // Do not create file if already exists
        if(file.exists() && file.isDirectory() == dir)
            return false;

        if(dir)
            return file.mkdirs();
        else {
            File parent = file.getParentFile();
            if(!parent.exists() || !parent.isDirectory()) {
                // Can not create file if parent does not exist
                if(!parent.mkdirs())
                    return false;
            }

            return file.createNewFile();
        }
    }

}
