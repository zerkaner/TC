package model;

public abstract class AbstractCar implements Runnable{

	private Integer carID = -1;
	private Integer meterPerSecond = 14;
	private Integer colorCodeR = 175;
	private Integer colorCodeG = 175;
	private Integer colorCodeB = 175;
	
	public Integer getMeterPerSecond() {
		return meterPerSecond;
	}

	public void setMeterPerSecond(Integer meterPerSecond) {
		this.meterPerSecond = meterPerSecond;
	}
	
	
	public Integer getCarID() {
		return carID;
	}

	public void setCarID(Integer carID) {
		this.carID = carID;
	}

	@Override
	public abstract void run();

	public Integer getColorCodeR() {
		return colorCodeR;
	}

	public void setColorCodeR(Integer colorCodeR) {
		this.colorCodeR = colorCodeR;
	}

	public Integer getColorCodeG() {
		return colorCodeG;
	}

	public void setColorCodeG(Integer colorCodeG) {
		this.colorCodeG = colorCodeG;
	}

	public Integer getColorCodeB() {
		return colorCodeB;
	}

	public void setColorCodeB(Integer colorCodeB) {
		this.colorCodeB = colorCodeB;
	}	
}
