package data;

import java.util.ArrayList;

public class Table<E> {
	private ArrayList<ArrayList<E>> dataTable;
	private int numX;
	private int numY;

	public Table(int numX, int numY, E initData){
		this.numX = numX;
		this.numY = numY;

		dataTable = new ArrayList<>(numX);
		for(int i = 0; i < numX; i++)
			dataTable.add(new ArrayList<>(numY));

		for(ArrayList<E> row: dataTable){
			for(int j = 0; j < numY; j++)
				row.add(initData);
		}
	}
	public void initiaizeWith(E initData){
		for(int x = 0; x < numX; x++){
			for(int y = 0; y < numY; y++)
				setAt(x, y, initData);
		}
	}
	public void setAt(int x, int y, E data){
		dataTable.get(x).set(y, data);
	}

	public E getAt(int x, int y){
		return dataTable.get(x).get(y);
	}

	public int numX(){
		return numX;
	}
	public int numY(){
		return numY;
	}

	public void resize(int newX, int newY, E initData){
		int difX = newX - numX;
		int difY = newY - numY;

		if(difY > 0){
			for(ArrayList<E> row: dataTable){
				for(int j = 0; j < difY; j++)
					row.add(initData);
			}
		}
		if(difY < 0){
			for(ArrayList<E> row: dataTable){
				for(int j = 0; j > difY ; j--)
					row.remove(row.size() - 1);
				row.trimToSize();
			}
		}
		if(difX > 0){
			for(int i = 0; i < difX; i++){
				ArrayList<E> newRow = new ArrayList<>(newY);
					for(int j = 0; j < newY; j++)
						newRow.add(j, initData);
				dataTable.add(newRow);
			}
		}
		if(difX < 0){
			for(int i = 0; i > difX; i--)
				dataTable.remove(dataTable.size() - 1);
			dataTable.trimToSize();
		}
		numX = newX;
		numY = newY;
	}
}
