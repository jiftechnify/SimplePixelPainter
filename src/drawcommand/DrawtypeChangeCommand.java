package drawcommand;

import application.GridCanvasController;
import application.GridCanvasController.Drawtype;

public class DrawtypeChangeCommand implements Command {
	private GridCanvasController canvasController;
	private GridCanvasController.Drawtype drawType;

	public DrawtypeChangeCommand(GridCanvasController canvasController, Drawtype drawType){
		this.canvasController = canvasController;
		this.drawType = drawType;
	}
	
	public void execute(){
		canvasController.setDrawType(drawType);
	}
}
