package control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitAlreadyDeployedException;
import org.openspaces.admin.space.SpaceDeployment;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import model.Car;
import model.ConfigTuple;
import model.Position;
import model.Roxel;
import model.Roxel.DIRECTION;
import view.JGameViewer;

/**
 * Startklasse für das Projekt
 * @author Abdul-Wahed
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
    gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer("/./"+name)).gigaSpace();
    System.out.println ("Connected to tuple space \""+name+"\".");
    
    initMap ("map1.txt"); 
    new JGameViewer (gigaSpace);
    for (int i = 0; i < 250; i ++) new Car (i,gigaSpace).start ();
    
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
            case '+': direction = Roxel.DIRECTION.CROSSING; break;
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
 
  
  
  /** This method deploys a 1,1 tuple space in memory.
   * @param name A name for the space. */
  private void createTupleSpace (String name) {
    try {
      // Create a 1,1-partitioned grid (=> for Lite license). 
      Admin admin = new AdminFactory().createAdmin ();
      GridServiceManager esm = admin.getGridServiceManagers().waitForAtLeastOne ();
      ProcessingUnit pu = esm.deploy (new SpaceDeployment(name).partitioned (1, 1));
      pu.waitFor(2, 30, TimeUnit.SECONDS);   
      System.out.println ("Tuple space \""+name+"\" successfully created.");
    } catch (ProcessingUnitAlreadyDeployedException e)  {
      System.err.println ("Error creating \""+name+"\". Space already deployed.");
    }      
  }  
}
