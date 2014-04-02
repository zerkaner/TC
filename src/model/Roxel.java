package model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class Roxel {

  public static enum DIRECTION {NORTH, SOUTH, EAST, WEST, CROSSING, TODECIDE, BLOCKED};   
  
  private Integer  length = 5;
  private Position position;
  private boolean  occupied;
  private DIRECTION direction;  
  
  
  public Roxel(){
	  
  }
  
  public Roxel(Position position, DIRECTION dir, Integer length) {
    this.position = position;
	this.occupied = false;
    direction = dir;
    
    //System.out.println ("Roxel an "+position.x+"/"+position.y+" mit Direction "+direction);
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
	
  public DIRECTION getDirection() {
	  return direction;
  }
	
  public void setDirection(DIRECTION direction) {
	  this.direction = direction;
  }

@Override
public String toString() {
	return "Roxel [length=" + length + ", position=" + position + ", occupied="
			+ occupied + ", direction=" + direction + "]";
}

  
  
}
