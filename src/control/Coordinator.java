package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitAlreadyDeployedException;
import org.openspaces.admin.pu.elastic.ElasticStatefulProcessingUnitDeployment;
import org.openspaces.admin.pu.elastic.config.ManualCapacityScaleConfigurer;
import org.openspaces.admin.space.SpaceDeployment;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.core.util.MemoryUnit;
import org.openspaces.events.polling.SimplePollingContainerConfigurer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;

import model.Car;
import model.ConfigTuple;
import model.Position;
import model.Roxel;
import model.Roxel.DIRECTION;
import view.JGameViewer;

/**
 * Startklasse für das Projekt
 * @author Abdul-Wahed (aber nur zu einem Teil ...)
 *
 */
public class Coordinator {

  private GigaSpace gigaSpace;
  private String name = "myGrid";
  
    
  /** Launch it! */
  public static void main (String [] args) {
    new Coordinator ();
  }  
  
  
  /** Main class. */
  public Coordinator () {
    
    // Grid creation method call.
    //createTupleSpace (name);
    
    // Connect to a remote GigaSpaces grid.
    //UrlSpaceConfigurer configurer = new UrlSpaceConfigurer ("jini://*/*/"+name);
    //gigaSpace = new GigaSpaceConfigurer(configurer).create ();   
    
    // Creates a local tuple space and connects to it.
    //gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer("/./"+name)).gigaSpace();
    
	createTupleSpace();
	  
	System.out.println ("Connected to tuple space \""+name+"\".");
    
    initMap ("map1.txt");
    
    deployTrafficLightPU();
    
    System.out.println("Traffic lights PU deployed...");
    
    new JGameViewer (gigaSpace);
    
    System.out.println("JGAME running...");
    
    ExecutorService pool = Executors.newCachedThreadPool();   
    SecureRandom sr = new SecureRandom();
    
    for (int i = 0; i < 25; i ++){   	
      Car current = new Car(i+1, gigaSpace); 
    	current.setMeterPerSecond (sr.nextInt (28)+10);
    	current.setColorCodeR (sr.nextInt (256));
    	current.setColorCodeG (sr.nextInt (256));
    	current.setColorCodeB (sr.nextInt (256));   	
    	pool.execute(current);
    }
    
  }


  
  /** This function reads a text file containing the street map and creates the roxel layout.
   * @param file Text file input. */
  private void initMap (String file) {    
    try {       
      BufferedReader reader = new BufferedReader (new FileReader (file));      
      String input;
      int i, j = 0, nr_roxels = 0;
      
      // Iterates through the file: Param i: downwards, param j to the right.
      for (i = 0; (input = reader.readLine ()) != null; i ++) {
        for (j = 0; j < input.length (); j ++) { 
          DIRECTION direction;
          switch (input.charAt (j)) {
            case '↑': direction = Roxel.DIRECTION.NORTH;    break; 
            case '↓': direction = Roxel.DIRECTION.SOUTH;    break;
            case '→': direction = Roxel.DIRECTION.EAST;     break;
            case '←': direction = Roxel.DIRECTION.WEST;     break;
            case '+': direction = Roxel.DIRECTION.TODECIDE; break;
            case '#': direction = Roxel.DIRECTION.BLOCKED;  break;
            default : continue;
          } 
          gigaSpace.write (new Roxel (new Position(j, i), direction, 5));
          
          nr_roxels ++;
        }        
      }  
      reader.close ();
      gigaSpace.write (new ConfigTuple (j, i));
      System.out.println ("Map loaded, "+nr_roxels+" roxel tuples stored.");
    }
    catch (Exception e) {
      e.printStackTrace();
    } 
  }  
 
  public void registerPollinglistener(){
	  SimplePollingEventListenerContainer pollingEventListenerContainer = new SimplePollingContainerConfigurer(
		         gigaSpace).eventListenerAnnotation(new EventListener())
		         .pollingContainer();
		     pollingEventListenerContainer.start();
  }
  
  /** This method deploys a 1,1 tuple space in memory.
   * @param name A name for the space. */
  private void createTupleSpace () {
    try {
      // Wait for the discovery of the managers and at least one agent
  	  Admin admin = new AdminFactory().addGroup("myGroup").create();
  	  admin.getGridServiceAgents().waitForAtLeastOne();
  	  admin.getElasticServiceManagers().waitForAtLeastOne();
  	  GridServiceManager gsm = admin.getGridServiceManagers().waitForAtLeastOne();
      ProcessingUnit pu = gsm.deploy (new SpaceDeployment(name).partitioned (1, 1));
      //pu.waitFor(2, 30, TimeUnit.SECONDS);
      gigaSpace = pu.waitForSpace(5, TimeUnit.MINUTES).getGigaSpace();
      System.out.println ("Tuple space \""+name+"\" successfully created.");
    } catch (ProcessingUnitAlreadyDeployedException e)  {
      System.err.println ("Error creating \""+name+"\". Space already deployed.");
    }      
  }
  
  private void deployTrafficLightPU(){
	// Wait for the discovery of the managers and at least one agent
	  Admin admin = new AdminFactory().addGroup("myGroup").create();
	  admin.getGridServiceAgents().waitForAtLeastOne();
	  admin.getElasticServiceManagers().waitForAtLeastOne();
	  GridServiceManager gsm = admin.getGridServiceManagers().waitForAtLeastOne();

	  // Deploy the Elastic Processing Unit. 
	  // Set the maximum memory and CPU capacity and initial capacity
	  File puArchive = new File("./myPU.jar");
	  ProcessingUnit pu = gsm.deploy(
	          new ElasticStatefulProcessingUnitDeployment(puArchive)
	             .memoryCapacityPerContainer(16,MemoryUnit.MEGABYTES)        
	             .maxMemoryCapacity(512,MemoryUnit.MEGABYTES)
	             .maxNumberOfCpuCores(32)     
	             // uncomment when working with a single machine agent             
	             .singleMachineDeployment() 
	             // set the initial memory and CPU capacity
	             .scale(new ManualCapacityScaleConfigurer()
	                    .memoryCapacity(128,MemoryUnit.MEGABYTES)
	                    .numberOfCpuCores(4)
	                    .create())
	  );

	  // Wait until the deployment is complete.
	  pu.waitForSpace().waitFor(pu.getTotalNumberOfInstances());
  }
  
}
