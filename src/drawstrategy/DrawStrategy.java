package drawstrategy;

import data.PixelState;
import data.Point;

public interface DrawStrategy {
	public abstract void onPressed(Point pos, PixelState startPosPixel, boolean clear);
	public abstract void onReleased(Point pos);
	public abstract void onDragged(Point pos);
}
