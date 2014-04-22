package control;

import model.Position;
import model.Roxel;
import model.Roxel.DIRECTION;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.events.adapter.SpaceDataEvent;

public class TrafficLightPU implements Runnable{

	private DIRECTION lastDirection = DIRECTION.EAST;
	private GigaSpace gigaSpace = null;
	private Position position = null;
	
	/**
     * Process the given Crossing Roxel and return it to the caller.
     * This method is invoked using OpenSpaces Events when a matching event
     * occurs.
     */
    @SpaceDataEvent
    public Roxel decide(Roxel crossing) {
    	if (this.gigaSpace == null) {
    		UrlSpaceConfigurer configurer = new UrlSpaceConfigurer ("jini://*/*/myGrid");//We access space created in Coordinator class
    	    gigaSpace = new GigaSpaceConfigurer(configurer).create ();
    	}
    	//System.out.println("TL decide..."+crossing);
    	if(lastDirection == DIRECTION.EAST){
    		crossing.setDirection(DIRECTION.SOUTH);
    		System.out.println("TL at "+crossing.getPosition()+" set to SOUTH");
    	}else{
    		crossing.setDirection(DIRECTION.EAST);
    		System.out.println("TL at "+crossing.getPosition()+" set to EAST");
    	}
    	lastDirection = crossing.getDirection();
    	gigaSpace.write(crossing);
    	return crossing;
    }

    public TrafficLightPU() {
        System.out.println("TrafficLights are running");
    }
	
    public TrafficLightPU(Position p, GigaSpace space){
    	position = p;
    	gigaSpace = space;
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				Thread.sleep (1000);
				Roxel crossing = gigaSpace.takeById(Roxel.class, position, null, Long.MAX_VALUE);
				decide(crossing);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
}
