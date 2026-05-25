import javax.swing.ImageIcon;
import java.io.File;
import java.net.URL;
import java.awt.Image;

public class ImageLoader {
    public static ImageIcon load(String path) {
        if (path == null || path.isEmpty()) {
            return load("Icons/UserDefaultpfp.png");
        }
        
        // 1. External file (like custom profile picture)
        File externalFile = new File(path);
        if (externalFile.exists() && externalFile.isFile()) {
            return new ImageIcon(externalFile.getAbsolutePath());
        }
        
        // 2. Internal resource (inside JAR)
        String resourcePath = path.startsWith("/") ? path : "/" + path;
        URL resource = ImageLoader.class.getResource(resourcePath);
        
        if (resource != null) {
            return new ImageIcon(resource);
        }
        
        // 3. Fallback
        if (!path.equals("Icons/UserDefaultpfp.png") && !path.equals("/Icons/UserDefaultpfp.png")) {
            return load("Icons/UserDefaultpfp.png");
        }
        
        return new ImageIcon();
    }

    public static ImageIcon load(Image img) {
        return new ImageIcon(img);
    }
    
    public static ImageIcon load(URL url) {
        return new ImageIcon(url);
    }
    
    public static ImageIcon load() {
        return new ImageIcon();
    }
}
