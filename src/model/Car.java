package model;

import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import java.security.SecureRandom;
import model.Roxel.DIRECTION;

/** Klasse für ein Auto. */
public class Car extends AbstractCar {

  private Roxel currentRoxel;
  private DIRECTION direction;
  private ConfigTuple map;
  private GigaSpace gigaSpace;

  
  /** Leerer Standardkonstruktor für GigaSpaces. */
  public Car () {}
  
  /** Erzeugt ein neues Auto. */
  public Car (Integer id, GigaSpace gs) {
    super.setCarID (id);
    gigaSpace = gs;
  }

  

  /** Thread-Routine der Autos. Löst eine Fortbewegung auf ein Nachbarsroxel aus. */
  public void run () {
    
    // Initialisierung: Parameter, zufälliges Startroxel und Fahrtrichtung einlesen.
    map = gigaSpace.read (new ConfigTuple ());
    currentRoxel = gigaSpace.take (new SQLQuery <Roxel> (Roxel.class, "occupied = false"));
    direction = currentRoxel.getDirection ();

    // Wenn wir uns auf einer Kreuzung befinden, zufällig Ost- oder Südfahrtrichtung.
    if (direction == DIRECTION.CROSSING) {
      SecureRandom r = new SecureRandom ();
      if (r.nextBoolean ()) direction = DIRECTION.EAST;
      else                  direction = DIRECTION.SOUTH;
    }
    setColorAccordingToDirection ();    // RGB-Farbe setzen: Ostrichtung=rot, Südrichtung=grün.
    setRoxelState (currentRoxel, true); // Roxel als belegt zurückschreiben.
    
    // Endlosschleife der Fortbewegung. Verzögerung (Sleep) erfolgt innerhalb!
    while (true) {
      try { moveForward (); }
      catch (Exception e) {
        System.err.print ("[Car] Thread Exception: ");
        e.printStackTrace ();
        //continue;
      }
    }
  }
  
  

  /** Erzeugt ein Positions-Objekt für das nächstbenötigte Roxel und versucht, dieses zu aquirieren. */
  private void moveForward () throws InterruptedException {

    Thread.sleep ((1000 / getMeterPerSecond ()) * currentRoxel.getLength ());
    Position position = new Position ();

    if (direction == DIRECTION.SOUTH) {
      int y = currentRoxel.getPosition ().y + 1;
      if (y >= map.getYTiles ()) y = 0;
      position.x = currentRoxel.getXPos ();
      position.y = y;
    }   
    else if (direction == DIRECTION.EAST) {
      int x = currentRoxel.getPosition ().x + 1;
      if (x >= map.getXTiles ()) x = 0;
      position.x = x;
      position.y = currentRoxel.getYPos ();
    }
    enterRoxel (position);  // Roxel in Beschlag nehmen!
  }
  
  
  /** Neues Roxel anfordern, als "belegt" setzen und voriges Roxel wieder freigeben.
   * @param position Ein Objekt der Zielposition, dient als ID für die Roxel-Anfrage. */
  private void enterRoxel (final Position position) {
    Roxel targetRoxel = gigaSpace.takeById (Roxel.class, position, null, Long.MAX_VALUE);    
    setRoxelState (targetRoxel, true);
    setRoxelState (currentRoxel, false);
    currentRoxel = targetRoxel;
  }


  
  /** Schreibt ein Roxel als "belegt" oder "frei" in den Tupel-Space zurück.
   * @param roxel Referenz auf das betreffende Roxel.
   * @param occupied Der Belegungszustand. */
  private void setRoxelState (final Roxel roxel, final boolean occupied) {
    if (roxel == null) return;
    roxel.setOccupied (occupied);
    if (occupied) roxel.setCar (this);
    else          roxel.setCar (new EmptyCar ());
    gigaSpace.write (roxel);
  } 
  
  
  
  /** Setzt die Auto-Farbe. Bei Ostrichtung rot, bei Südrichtung grün. */
  private void setColorAccordingToDirection () {
    if (direction == DIRECTION.EAST) {
      setColorCodeR (255);
      setColorCodeG (0);
      setColorCodeB (0);
    }
    else if (direction == DIRECTION.SOUTH) {
      setColorCodeR (0);
      setColorCodeG (255);
      setColorCodeB (0);
    }
  }
}
