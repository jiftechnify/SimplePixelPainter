package drawcommand;

import application.GridCanvasController;
import data.PixelState;
import data.Point;
import data.Table;

/**
 * Created by jiftech on 2015/07/06.
 */
public class MoveCommand implements Command {
    private Table<PixelState> pixelData;
    private GridCanvasController canvasController;
    private Point from;
    private Point dest;

    public MoveCommand(Table<PixelState> pixelData, Point from, Point dest, GridCanvasController canvasController) {
        this.pixelData = pixelData;
        this.from = from;
        this.dest = dest;
        this.canvasController = canvasController;
    }

    @Override
    public void execute() {
        canvasController.move(pixelData, from, dest);
    }
}
