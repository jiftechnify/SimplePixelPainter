package drawstrategy;

import application.Point;

public interface DrawStrategy {
	public abstract void onPressed(Point pos);
	public abstract void onReleased(Point pos);
	public abstract void onDragged(Point pos);
}
