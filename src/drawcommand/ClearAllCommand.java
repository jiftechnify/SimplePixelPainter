package drawcommand;

import application.GridCanvasController;

public class ClearAllCommand implements Command {
	private GridCanvasController canvasController;
	public ClearAllCommand(GridCanvasController canvasController){
		this.canvasController = canvasController;
	}
	@Override
	public void execute() {
		canvasController.clearAll();
	}

	@Override
	public boolean isDrawCommand() {
		return true;
	}
}
