package application;

import drawstrategy.*;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiftech on 2015/07/04.
 */
enum Shapes {
    FREELINE("自由線", new Image("application/res/freeline.png"), new FreelineDrawStrategy(GridCanvasController.getInstance())),
    LINE("直線", new Image("application/res/line.png"), new LineDrawStrategy(GridCanvasController.getInstance())),
    FILLED_RECT("長方形(塗りつぶし)", new Image("application/res/fillRect.png"), new FilledRectDrawStrategy(GridCanvasController.getInstance())),
    EMPTY_RECT("長方形(塗りつぶしなし)", new Image("application/res/emptyRect.png"), new EmptyRectDrawStrategy(GridCanvasController.getInstance())),;

    private final String name;
    private final Image icon;
    private final DrawStrategy strategy;
    private static Map<String, DrawStrategy> nameTostrategy;

    Shapes(String name, Image icon, DrawStrategy strategy) {
        this.name = name;
        this.icon = icon;
        this.strategy = strategy;
    }

    public Image getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public DrawStrategy getStrategy() {
        return strategy;
    }

    public static DrawStrategy nameToStrategy(String name) {
        if (nameTostrategy == null) {
            nameTostrategy = new HashMap<>();
            Shapes[] shapes = Shapes.values();
            for (Shapes shape : shapes)
                nameTostrategy.put(shape.name, shape.strategy);
        }
        return nameTostrategy.get(name);
    }
}
