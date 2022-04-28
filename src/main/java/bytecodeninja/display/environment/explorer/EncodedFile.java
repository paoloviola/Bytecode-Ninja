package bytecodeninja.display.environment.explorer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

class EncodedFile extends File
{

    public EncodedFile(File file) {
        super(file.getAbsolutePath());
    }

    public EncodedFile(String parent, String child) {
        super(parent, encode(child));
    }

    public EncodedFile(File parent, String child) {
        super(parent, encode(child));
    }

    /**
     * Collects the original file path and decodes it.
     * @return the decoded file path.
     */
    public String getDecodedPath() {
        return decode(getAbsolutePath());
    }

    /**
     * Collects the original file name and decodes it.
     * @return the decoded file name.
     */
    public String getDecodedName() {
        return decode(getName());
    }

    /**
     * Collects the original content of this directory (if any)
     * and converts each component to an EncodedFile.
     * @return An array of EncodedFiles or null if the parens is not a directory.
     */
    @Override
    public EncodedFile[] listFiles() {
        String[] paths = list();
        if(paths == null) return null;

        EncodedFile[] files = new EncodedFile[paths.length];
        for(int i = 0; i < files.length; i++)
            files[i] = new EncodedFile(this, decode(paths[i]));
        return files;
    }

    @Override
    public String toString() {
        return getDecodedPath();
    }

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8")
                    .replace("%20", " ")
                    .replace("%2F", "/");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String decode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
