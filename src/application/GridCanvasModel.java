package application;

import java.util.ArrayList;
import java.util.List;

public class GridCanvasModel {
	private ArrayList<ArrayList<PixelState>> pixelData;
	private int numPixX;
	private int numPixY;

	public GridCanvasModel(int numPixX, int numPixY){
		this.numPixX = numPixX;
		this.numPixY = numPixY;

		pixelData = new ArrayList<>(numPixX);
		for(int i = 0; i < numPixX; i++)
			pixelData.add(new ArrayList<>(numPixY));

		for(List<PixelState> row: pixelData){
			for(int j = 0; j < numPixY; j++)
				row.add(PixelState.WHITE);
		}
		System.out.println(pixelData.size() + "," + pixelData.get(0).size());
	}

	public void set(int x, int y, PixelState pixel){
		pixelData.get(x).set(y, pixel);
	}

	public PixelState get(int x, int y){
		return pixelData.get(x).get(y);
	}

	public void clearAll(){
		for(List<PixelState> row: pixelData){
			for(int j = 0; j < numPixY; j++)
				row.set(j, PixelState.WHITE);
		}
	}
	public void resize(int newX, int newY){
		int difX = newX - numPixX;
		int difY = newY - numPixY;

		if(difY > 0){
			for(List<PixelState> row: pixelData){
				for(int j = 0; j < difY; j++)
					row.add(PixelState.WHITE);
			}
		}
		if(difX > 0){
			for(int i = 0; i < difX; i++){
				ArrayList<PixelState> newRow = new ArrayList<>(newY);
					for(int j = 0; j < newY; j++)
						newRow.add(j, PixelState.WHITE);
				pixelData.add(newRow);
			}
		}
		if(difX < 0){
			for(int i = 0; i > difX; i--)
				pixelData.remove(pixelData.size() - 1);
			pixelData.trimToSize();
		}
		if(difY < 0){
			for(ArrayList<PixelState> row: pixelData){
				for(int j = 0; j > difY ; j--)
					row.remove(row.size() - 1);
				row.trimToSize();
			}
		}

		numPixX = newX;
		numPixY = newY;
	}
}
