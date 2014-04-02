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
   
  public Integer getXTiles () { return xTiles; }
  public Integer getYTiles () { return yTiles; }  
  
  public void setXTiles (Integer xTiles) { this.xTiles = xTiles; }  
  public void setYTiles (Integer yTiles) { this.yTiles = yTiles; }

public Integer getBlocksX() {
	// TODO Auto-generated method stub
	return xTiles;
}

public Integer getBlocksY() {
	// TODO Auto-generated method stub
	return yTiles;
}

public int getRoxelPerBlock() {
	// TODO Auto-generated method stub
	return tilesize;
}    
  
}
