package model;

import java.security.SecureRandom;
import java.util.Random;

import model.Roxel.DIRECTION;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import com.j_spaces.core.IJSpace;

public class Car extends AbstractCar {
  
	//private Roxel lastRoxel = null;
	private Roxel currentRoxel = null;
	//private Roxel nextRoxel = null;
	private DIRECTION direction = null;
	private ConfigTuple map = null;

	private GigaSpace gigaSpace = null;
	private String url = "/*/myGrid";
	
	
	
	public Car(){
		
	}
	
	public Car(Integer id, GigaSpace gs){
		super.setCarID(id);
		gigaSpace = gs;
	}
	
	@Override
	public void run() {
		
		if(gigaSpace == null){
			// connect to the space using its URL
		    IJSpace space = new UrlSpaceConfigurer(url).space();
		    
		    // use gigaspace wrapper for simpler API
		    gigaSpace = new GigaSpaceConfigurer(space).gigaSpace();
		}
		
		
	    //map = gigaSpace.readById(Roadmap.class, "ROADMAP");
	    map = gigaSpace.read (new ConfigTuple ());
	    
		enterInitialRoxel();
		
		while(true){
			try {
				//System.out.println("Car #"+getCarID()+" on "+currentRoxel.toString()+" direction "+direction);
				moveForward();
			} catch (InterruptedException e) {
				continue;
			}
		}
		
	}
  
	private void moveForward() throws InterruptedException{
		
		Thread.sleep((1000/getMeterPerSecond())*currentRoxel.getLength());
	
		Position position = new Position();
				
		if(direction == DIRECTION.SOUTH){
			
			int y = currentRoxel.getPosition().y + 1;
			if(y >= map.getYTiles()){
				y = 0;
			}
			
			position.x = currentRoxel.getXPos();
			position.y = y;
			
		}else if(direction == DIRECTION.EAST){
			
			int x = currentRoxel.getPosition().x + 1;
			if(x >= map.getXTiles()){
				x = 0;
			}
			
			position.x = x;
			position.y = currentRoxel.getYPos();
			
		}
		
		enterRoxel(position);
	}
  
   
	private void enterRoxel(Position p){
		Roxel nextRoxel = gigaSpace.takeById(Roxel.class, p);
		
		int waiting = 0;
		
		while(nextRoxel == null || nextRoxel.isOccupied()){
			
			if(nextRoxel != null){
				gigaSpace.write(nextRoxel);
				
				if((++waiting % 5) == 0){
					System.out.println("Car: "+getCarID()+" waiting "+waiting+" direction is "+direction
							+" try to get to "+p+" occupied by "+nextRoxel.getCar().getCarID()+" current in "+currentRoxel.getPosition());
				}
			}
			
			try {
				Thread.sleep((1000/getMeterPerSecond()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nextRoxel = gigaSpace.takeById(Roxel.class, p);
		}
		
		if(direction == null){
			
			direction = nextRoxel.getDirection();
			
			if(direction == DIRECTION.CROSSING){
				
				SecureRandom r = new SecureRandom();
				
				if(r.nextBoolean()){
					direction = DIRECTION.EAST;
				}else{
					direction = DIRECTION.SOUTH;
				}
			}
			
			setColorAccordingToDirection();
			
		}
		
		nextRoxel.setOccupied(true);
		nextRoxel.setCar(this);
		gigaSpace.write(nextRoxel);
		
		//System.out.println("CONTROL: "+gigaSpace.readById(Roxel.class, nextRoxel.getPosition()));
		
		if(currentRoxel != null){
			currentRoxel.setOccupied(false);
			currentRoxel.setCar(new EmptyCar());
			gigaSpace.write(currentRoxel);
		}
		
		currentRoxel = nextRoxel;
	}
	  
	private void setColorAccordingToDirection() {
		
		if(direction == DIRECTION.EAST){
			setColorCodeR(255);
			setColorCodeG(0);
			setColorCodeB(0);
		}else if(direction == DIRECTION.SOUTH){
			setColorCodeR(0);
			setColorCodeG(255);
			setColorCodeB(0);
		}
	}

	private void enterInitialRoxel(){
		int x = 0;
		int y = 0;
		
		SecureRandom r = new SecureRandom();
		Roxel temp = null;
		Position p = null;
		
		do{
		
			x = r.nextInt(map.getXTiles());
			y = r.nextInt(map.getYTiles());
			
			p = new Position(x,y);
			
			temp = gigaSpace.readById(Roxel.class, p);
			
		}while(temp.getDirection() == DIRECTION.BLOCKED);
		
		enterRoxel(p);
	  }
	  
}
