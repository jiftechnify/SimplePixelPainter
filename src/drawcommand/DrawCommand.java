package drawcommand;

import application.GridCanvasController;

public abstract class DrawCommand implements Command {
	protected GridCanvasController canvasController;
	protected boolean clear;

	protected DrawCommand(boolean clear, GridCanvasController canvasController){
		this.clear = clear;
		this.canvasController = canvasController;
	}

	public boolean isDrawCommand(){
		return true;
	}
}

