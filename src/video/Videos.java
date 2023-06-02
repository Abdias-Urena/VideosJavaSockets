/*
 * To change this license header, choose License Headers in 
 * Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

/**
 *
 * @author Abdias
 */
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Sebastian
 */
public class Videos {

    private String name;
    private String path;
    private String type;
    private static final int NAME_PART = 0;
    private static final int TYPE_PART = 1;

    public Videos(String path) {
        this.path = path;
        Path pa = Paths.get(path);
        String file = pa.getFileName().toString();
        this.name = file.substring(0, file.lastIndexOf("."));
        this.type = file.substring(file.lastIndexOf(".") + 1);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        String allInformationVideo = "\nVideo: " + name + "\nType: " + type;
        return allInformationVideo;
    }
}
