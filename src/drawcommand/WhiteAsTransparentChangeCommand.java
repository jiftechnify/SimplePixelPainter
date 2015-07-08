package drawcommand;

import application.GridCanvasController;

/**
 * Created by jiftech on 2015/07/06.
 */
public class WhiteAsTransparentChangeCommand extends ChangeCommand {
    private GridCanvasController canvasController;
    private boolean whiteAsTransparent;

    public WhiteAsTransparentChangeCommand(boolean whiteAsTransparent, GridCanvasController canvasController) {
        this.whiteAsTransparent = whiteAsTransparent;
        this.canvasController = canvasController;
    }

    public void execute() {
        canvasController.changeWhiteAsTransparent(whiteAsTransparent);
    }
}
