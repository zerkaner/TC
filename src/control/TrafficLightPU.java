package control;

import model.Roxel;
import model.Roxel.DIRECTION;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.events.adapter.SpaceDataEvent;

import com.j_spaces.core.IJSpace;

public class TrafficLightPU {

	private DIRECTION lastDirection = DIRECTION.CROSSING;
	private GigaSpace gigaSpace = null;
	
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
    	
    	crossing.setDirection(DIRECTION.EAST);
    	
    	return crossing;
    }

    public TrafficLightPU() {
        System.out.println("TrafficLights are running");
    }
	
}
