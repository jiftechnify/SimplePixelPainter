package drawcommand;

import java.util.ArrayList;
import java.util.List;

import application.GridCanvasController;

public class FreelineDrawCommand extends DrawCommand {
	private List<LineDrawCommand> lines = new ArrayList<>();

	public FreelineDrawCommand(boolean clear, GridCanvasController canvasController){
		super(clear, canvasController);
	}

	public void addLine(LineDrawCommand line){
		lines.add(line);
	}
	@Override
	public void execute() {
		for(LineDrawCommand l: lines)
			l.execute();
	}
}
