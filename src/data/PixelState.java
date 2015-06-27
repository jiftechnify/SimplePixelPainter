package data;

import java.util.Collections;
import java.util.EnumMap;

import javafx.scene.paint.Color;

public enum PixelState {
	BLACK    (Color.BLACK),
	DARKGRAY (Color.DARKGRAY),
	LIGHTGRAY(Color.LIGHTGRAY),
	WHITE    (Color.WHITE),;

	private Color pxColor;
	private static EnumMap<PixelState, PixelState> nextState;

	private PixelState(Color pxColor){
		this.pxColor = pxColor;
	}

	private static void initNextState(){
		nextState = new EnumMap<>(PixelState.class);
		nextState.put(BLACK, DARKGRAY);
		nextState.put(DARKGRAY, LIGHTGRAY);
		nextState.put(LIGHTGRAY, BLACK);
		nextState.put(WHITE, BLACK);
		Collections.unmodifiableMap(nextState);
	}

	public Color color(){
		return pxColor;
	}
	public static PixelState nextState(PixelState state){
		if(nextState == null)
			initNextState();

		return nextState.get(state);
	}
}


