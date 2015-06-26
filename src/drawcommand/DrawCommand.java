package drawcommand;

import application.GridCanvasController;

public abstract class DrawCommand implements Command {
	protected GridCanvasController canvasController;

	protected DrawCommand(GridCanvasController canvasController){
		this.canvasController = canvasController;
	}
}
