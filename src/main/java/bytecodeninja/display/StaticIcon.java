package bytecodeninja.display;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class StaticIcon
{

    private static final Map<String, Icon> ICON_CACHE = new HashMap<>();

    /**
     * Searches for an icon at the given resource path and caches it.
     * @param path the path the icon is located in
     * @return the cached icon or null, if the icon was not found
     */
    public static Icon get(String path) {
        if(path == null) return null;

        Icon icon = ICON_CACHE.get(path);
        if(icon == null) {
            if(path.endsWith(".svg"))
                icon = new FlatSVGIcon(path);
            else {
                try(InputStream stream = StaticIcon.class.getResourceAsStream(path)) {
                    if(stream == null) throw new IOException("Resource not found!");
                    icon = new ImageIcon(ImageIO.read(stream));
                }
                catch (IOException e) {
                    return null;
                }
            }

            ICON_CACHE.put(path, icon);
        }
        return icon;
    }

    /**
     * Clears the icon cache.
     */
    public static void clear() {
        ICON_CACHE.clear();
    }

}
