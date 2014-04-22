package view;

import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;

import model.ConfigTuple;
import model.Roxel;
import view.jgame.JGColor;
import view.jgame.platform.JGEngine;


/** This Viewer uses the JGame libraries to visualize roxel occupation. */
public class JGameViewer extends JGEngine {

  private static final long serialVersionUID = -5269227483585285966L;
  private GigaSpace gigaSpace; 
  private int xTiles, yTiles, tileSize;
  private Roxel [] roxels;
  
  
  
  /** Creates a new view based on the JGame package. */
  public JGameViewer (GigaSpace space) {
    this.gigaSpace = space;
    ConfigTuple configTuple = gigaSpace.read (new ConfigTuple ());
    
    xTiles = configTuple.getXTiles ();
    yTiles = configTuple.getYTiles ();
    tileSize = ConfigTuple.tilesize;
    initEngine (xTiles*tileSize, yTiles*tileSize);
  }

  
  
  /** Executed between application and engine loading steps. */
  public void initCanvas () {
    setCanvasSettings (xTiles, yTiles, tileSize, tileSize, 
      JGColor.black, JGColor.white, null);  
  }

  
  
  /** Post graphics engine initialization routine. */
  public void initGame () {
    setFrameRate (25, 0);
    defineMedia ("tc.tbl");
    setBGColor (new JGColor (175, 175, 175));  
  }

  
  
  /** This method is called whenever a new frame is required. */
  public void doFrame () {  
    
    // Acquires all roxels.
    roxels = gigaSpace.readMultiple(new SQLQuery<Roxel>(Roxel.class, "occupied = true OR occupied = false"));   
    for (Roxel roxel : roxels) {
      int xPos = roxel.getXPos();
      int yPos = roxel.getYPos();
      
      //TODO This is really retarded. The street map won't change. 
      //     One should track car objects instead and update their positions ...
      
      // Paint the roxel tile.
      switch (roxel.getDirection ()) {       
        
        case NORTH:    setTile (xPos, yPos, "vrt");  break; 
        case WEST:     setTile (xPos, yPos, "hor");  break;
        case TODECIDE: setTile (xPos, yPos, "crs");  break;
        
        case EAST:
          if (roxel.isCrossing ()) setTile (xPos, yPos, "c_e");
          else                     setTile (xPos, yPos, "hor");  
          break;
          
        case SOUTH: 
          if (roxel.isCrossing ()) setTile (xPos, yPos, "c_s");
          else                     setTile (xPos, yPos, "vrt");  
          break;
          
        default: break; 
      }
    }
  }
  
  
  
  /** Here, the painting for every new frame is done. */
  public void paintFrame () {

	  //System.out.println("painting..."+roxels.length);
    for (Roxel roxel : roxels) {
      int xPos = roxel.getXPos();
      int yPos = roxel.getYPos();

      // When the roxel is occupied, paint color of car.
      if (roxel.isOccupied()) {
    	  int r = roxel.getCar().getColorCodeR();
    	  int g = roxel.getCar().getColorCodeG();
    	  int b = roxel.getCar().getColorCodeB();
        setColor (new JGColor(r, g, b));
        drawRect (xPos*tileSize + tileSize/2, yPos*tileSize + tileSize/2, 8, 8, true, true);         
      }
    }
  }
}
