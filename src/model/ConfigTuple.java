package model;

import com.gigaspaces.annotation.pojo.SpaceId;

/** This class contains metadata information. */
public class ConfigTuple {

  public static int tilesize = 24;
  private String id;
  private Integer xTiles;
  private Integer yTiles; 
   
  /** XAP default constructor. */
  public ConfigTuple () {}
  
  /** Creates a new configuration tuple.
   * @param xTiles Number of tiles (x axis).
   * @param yTiles Number of tiles (y axis). */
  public ConfigTuple (int xTiles, int yTiles) {
    this.xTiles = xTiles;
    this.yTiles = yTiles;
  }
  
  @SpaceId(autoGenerate=true)
  public String getId () { return id; }
  public void setId (String s) {this.id = s; }
     
  public void setXTiles (Integer xTiles) { this.xTiles = xTiles; }  
  public void setYTiles (Integer yTiles) { this.yTiles = yTiles; }

public Integer getXTiles() {
	// TODO Auto-generated method stub
	return xTiles;
}

public Integer getYTiles() {
	// TODO Auto-generated method stub
	return yTiles;
}

public int getTileSize() {
	// TODO Auto-generated method stub
	return tilesize;
}    
  
}
