package application;

/**
 * Created by jiftech on 2015/07/06.
 */
public enum FileFormat {
    BMP("BMPファイル", "bmp"),
    PNG("PNGファイル", "png"),
    GIF("GIFファイル", "gif"),
    JPEG("JPEGファイル", "jpg"),;

    private String description;
    private String extension;

    FileFormat(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }

    public String description() {
        return description;
    }

    public String extension() {
        return extension;
    }
}