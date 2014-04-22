package model;

import java.io.Serializable;

public class Position implements Serializable{

	private static final long serialVersionUID = 8557452790005828996L;
	public int x;
	public int y;
	
	public Position(){
		
	}
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public String toString(){
		return "Position x "+x+" y "+y;
	}
}
