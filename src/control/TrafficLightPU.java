package control;

import model.ConfigTuple;
import model.Position;
import model.Roxel;
import model.Roxel.DIRECTION;
import org.openspaces.core.GigaSpace;
import org.openspaces.events.adapter.SpaceDataEvent;


import com.j_spaces.core.client.SQLQuery;

public class TrafficLightPU implements Runnable{

	private DIRECTION lastDirection = DIRECTION.EAST;
	private GigaSpace gigaSpace = null;
  private ConfigTuple map;
	private Position position = null;
	
	/** Process the given Crossing Roxel and return it to the caller.
   * This method is invoked using OpenSpaces Events when a matching event occurs. */
  @SpaceDataEvent
  public Roxel decide (Roxel crossing) {
    	
    //if (this.gigaSpace == null) {
    //		UrlSpaceConfigurer configurer = new UrlSpaceConfigurer ("jini://*/*/myGrid");//We access space created in Coordinator class
    //	    gigaSpace = new GigaSpaceConfigurer(configurer).create ();
    //	}

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
	
    
    public TrafficLightPU (Position p, GigaSpace space) {
    	position = p;
    	gigaSpace = space;
    }
    

	public void run() {
	  map = gigaSpace.read (new ConfigTuple (), Long.MAX_VALUE);
		
	  while (true) {
			try {			
				Roxel crossing = gigaSpace.readById (Roxel.class, position, null, Long.MAX_VALUE);
				int sleep = trafficControl (crossing);
		    gigaSpace.write (crossing);
		    Thread.sleep (sleep);
		    
			} 
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	
	

	
	/** Schaltregel für die Ampelprozesse.
	 * @param roxel Das Roxel, für das geschaltet werden soll. 
	 * @return Verzögerung (in ms) vor Neuüberprüfung. */
  private int trafficControl (Roxel roxel) {
        
    // Mehrfache Durchläufe mit Offset (vorausschauendes Schalten).
    for (int offset = 0; offset < 2; offset ++) {
      
      // Die Anzahl der wartenden Autos ermitteln.
      int waitingNorth = getWaitingCars (DIRECTION.NORTH, offset);
      int waitingWest  = getWaitingCars (DIRECTION.WEST,  offset);   
         
      // Wenn Autos warten müssen, die längere Schlange priorisieren.
      if (waitingNorth > 0 || waitingWest > 0) {
        if      (waitingNorth > waitingWest) roxel.setDirection (DIRECTION.SOUTH);
        else if (waitingWest > waitingNorth) roxel.setDirection (DIRECTION.EAST);
        else;   // Ansonsten Fahrtrichtung beibehalten!            
        
        // Wenn wir umgeschaltet haben, die Schaltrichtung für gegebene Zeit halten.
        if (waitingNorth>waitingWest || waitingWest>waitingNorth) return 1338;
      }
      
//      else {
//        roxel.setDirection (DIRECTION.TODECIDE);
//        break;
//      }
    }
    
    // Kein Schaltkriterium eingetreten, Neuüberprüfung in Kürze vornehmen.
    return 50;
  } 
  
  
  
  /** Ermittelt die Anzahl der wartenden Autos. 
   * @param direction Die Fahrtrichtung, für die abgefragt werden soll.
   * @param offset Der erweiterte Roxelabstand (Standard: Ein Feld).
   * @return Die Anzahl der belegten Roxel. */
  private int getWaitingCars (DIRECTION direction, int offset) {
    
    int waiting = 0;
    DIRECTION crsDir = null;
    int x = position.x;
    int y = position.y;
    
    // Offset gemäß der Richtung und des Schleifendurchlaufs ergänzen
    for (int i = offset + 1; true; i ++) {
      switch (direction) {
        case NORTH: y -= (i); crsDir = DIRECTION.SOUTH;  break;
        case WEST:  x -= (i); crsDir = DIRECTION.EAST;   break;
        default:                                         break;
      }   
      
      // Überlauf abfangen.
      if (x < 0) x = map.getXTiles () - 1;
      if (y < 0) y = map.getYTiles () - 1;
      
      // Roxel lesen und (falls belegt) den Zählerwert erhöhen.
      Roxel roxel = gigaSpace.read (new SQLQuery <Roxel> (Roxel.class, 
        "position.x='"+x+"' AND position.y='"+y+"'"), Long.MAX_VALUE);     
      if (roxel.isOccupied () && roxel.getDirection () == crsDir) waiting ++;
      else break;      
    }

    return waiting;
  }  
}
