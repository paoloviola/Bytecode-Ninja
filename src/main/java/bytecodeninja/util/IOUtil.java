package bytecodeninja.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class IOUtil
{

    public static File createDirectory(File file) throws IOException {
        createPathIfNotExists(file, true);
        return file;
    }

    public static File createFile(File file) throws IOException {
        createPathIfNotExists(file, false);
        return file;
    }

    public static File createPath(File file, boolean dir) throws IOException {
        createPathIfNotExists(file, dir);
        return file;
    }

    public static boolean createPathIfNotExists(File file, boolean dir) throws IOException {
        // Do not create file if already exists
        if(file.exists() && file.isDirectory() == dir)
            return false;

        File parent = file.getParentFile();
        if(!parent.exists() || !parent.isDirectory()) {
            // Can not create file if parent does not exist
            if(!parent.mkdirs())
                return false;
        }

        return file.createNewFile();
    }

}
