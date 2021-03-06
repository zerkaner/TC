package model;

import java.io.Serializable;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;


@SpaceClass
public class Roxel implements Serializable{

	private static final long serialVersionUID = -1999871710653825142L;

	public static enum DIRECTION {NORTH, SOUTH, EAST, WEST, CROSSING, TODECIDE, BLOCKED};   
  
  private Integer  length = 5;
  private Position position;
  private boolean  occupied;
  private boolean isCrossing;
  private DIRECTION direction;  
  private AbstractCar car = new EmptyCar();

  
	public Roxel(){
	}

  
  public Roxel(Position position, DIRECTION dir, Integer length) {
    this.position = position;
    this.occupied = false;
    if (dir == DIRECTION.TODECIDE) isCrossing = true;  // Roxel ggf. als Kreuzung markieren.
    direction = dir;
  }
  
  public int getLength() {
	  return length;
  }
	
  public void setLength(Integer length) {
	  this.length = length;
  }

  @SpaceId(autoGenerate = false)
  public Position getPosition() {
	  return position;
  }
	
  public void setPosition(Position position) {
	  this.position = position;
  }
  
  public Integer getXPos(){
	  return getPosition().x;
  }
  
  public Integer getYPos(){
	  return getPosition().y;
  }
  
  public boolean isOccupied() {
	  return occupied;
  }
	
  public void setOccupied(boolean occupied) {
	  this.occupied = occupied;
  }
	
  public boolean isCrossing() {
    return isCrossing;
  }
  
  public void setCrossing(boolean crossing) {
    this.isCrossing = crossing;
  }  
  
  public DIRECTION getDirection() {
	  return direction;
  }
	
  public void setDirection(DIRECTION direction) {
	  this.direction = direction;
  }

  public AbstractCar getCar(){
	  return car;
  }
  
  public void setCar(AbstractCar car){
	  this.car = car;
  }
  
@Override
public String toString() {
	return "Roxel [length=" + length + ", position=" + position + ", occupied="
			+ occupied + ", direction=" + direction + "]" + "Car: "+car.toString();
}

  
  
}
