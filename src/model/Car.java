package model;

import java.util.Random;

import model.Roxel.DIRECTION;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import com.j_spaces.core.IJSpace;

public class Car extends Thread {
  
	private Roxel lastRoxel = null;
	private Roxel currentRoxel = null;
	private Roxel nextRoxel = null;
	private DIRECTION direction = null;
	private ConfigTuple map = null;
	private int speedMeterPerSecond = 14;
  
	private GigaSpace gigaSpace = null;
	private String url = "/*/myGrid";
	
	private Integer id = -1;
	
	public Car(){
		
	}
	
	public Car(Integer id, GigaSpace gs){
		this.id = id;
		gigaSpace = gs;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
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
				System.out.println("Car #"+id+" on "+currentRoxel.toString()+" direction "+direction);
				moveForward();
			} catch (InterruptedException e) {
				continue;
			}
		}
		
	}
  
	private void moveForward() throws InterruptedException{
		Thread.sleep((1000/speedMeterPerSecond)*currentRoxel.getLength());
	
		Roxel template = null;
		
		int limitX = map.getXTiles();//(map.getBlocksX() * map.getRoxelPerBlock()) -1;
		int limitY = map.getYTiles();//(map.getBlocksY() * map.getRoxelPerBlock()) -1;
		
		if(direction == DIRECTION.SOUTH){
			int y = currentRoxel.getPosition().y + 1;
			if(y >= limitY){
				y = 0;
			}
			template = new Roxel(new Position(currentRoxel.getPosition().x,y), currentRoxel.getDirection(), currentRoxel.getLength());
		}else if(direction == DIRECTION.EAST){
			int x = currentRoxel.getPosition().x + 1;
			if(x >= limitX){
				x = 0;
			}
			template = new Roxel(new Position(x,currentRoxel.getPosition().y), currentRoxel.getDirection(), currentRoxel.getLength());
		}
		
		enterRoxel(template.getPosition());
	}
  
   
	private void enterRoxel(Position p){
		nextRoxel = gigaSpace.takeById(Roxel.class, p);
				
		while(nextRoxel == null || nextRoxel.isOccupied()){
			
			if(nextRoxel != null){
				gigaSpace.write(nextRoxel);
			}
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nextRoxel = gigaSpace.takeById(Roxel.class, p);
		}
		
		if(direction == null){
			
			direction = nextRoxel.getDirection();
			
			Random r = new Random();
			if(direction == DIRECTION.CROSSING){
				if(r.nextBoolean()){
					direction = DIRECTION.EAST;
				}else{
					direction = DIRECTION.SOUTH;
				}
			}
		}
		
		nextRoxel.setOccupied(true); 
		gigaSpace.write(nextRoxel);
		
		//System.out.println("CONTROL: "+gigaSpace.readById(Roxel.class, nextRoxel.getPosition()));
		
		if(currentRoxel != null){
			currentRoxel.setOccupied(false);
			gigaSpace.write(currentRoxel);
		}
		
		if(currentRoxel == null){
			currentRoxel = nextRoxel;
		}
		
		lastRoxel = currentRoxel;
		currentRoxel = nextRoxel;
	}
	  
	private void enterInitialRoxel(){
		int x = 0;
		int y = 0;
		
		Random r = new Random();
		
		x = r.nextInt(map.getXTiles());
		y = r.nextInt(map.getYTiles());
		
		Position p = new Position(x,y);
			
		/*
		if(r.nextBoolean()){
			x = r.nextInt(map.getBlocksX());
			x = x - (x%(map.getBlocksX()/2));
			if(x < map.getBlocksX()/2){
				x = map.getBlocksX()/2;
			}
			direction = DIRECTION.SOUTH;
		}else{
			y = r.nextInt(map.getBlocksY());
			y = y - (y % (map.getBlocksY()/2));
			if(y < map.getBlocksY()/2){
				y = map.getBlocksY()/2;
			}
			direction = DIRECTION.EAST;
		}*/
		  
		  //Roxel template = new Roxel(new Position(x, y), direction, map.getRoxelLength());
		  
		  enterRoxel(p);
	  }
	  
}
