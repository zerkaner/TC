package model;

import java.io.Serializable;


public abstract class AbstractCar implements Runnable, Serializable {

	private static final long serialVersionUID = 5039621975173641862L;
	
	private Integer carID = -1;
	private Integer meterPerSecond = 14;
	private Integer colorCodeR = 175;
	private Integer colorCodeG = 175;
	private Integer colorCodeB = 175;
	
	public AbstractCar(){
		
	}
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((carID == null) ? 0 : carID.hashCode());
		result = prime * result
				+ ((colorCodeB == null) ? 0 : colorCodeB.hashCode());
		result = prime * result
				+ ((colorCodeG == null) ? 0 : colorCodeG.hashCode());
		result = prime * result
				+ ((colorCodeR == null) ? 0 : colorCodeR.hashCode());
		result = prime * result
				+ ((meterPerSecond == null) ? 0 : meterPerSecond.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCar other = (AbstractCar) obj;
		if (carID == null) {
			if (other.carID != null)
				return false;
		} else if (!carID.equals(other.carID))
			return false;
		if (colorCodeB == null) {
			if (other.colorCodeB != null)
				return false;
		} else if (!colorCodeB.equals(other.colorCodeB))
			return false;
		if (colorCodeG == null) {
			if (other.colorCodeG != null)
				return false;
		} else if (!colorCodeG.equals(other.colorCodeG))
			return false;
		if (colorCodeR == null) {
			if (other.colorCodeR != null)
				return false;
		} else if (!colorCodeR.equals(other.colorCodeR))
			return false;
		if (meterPerSecond == null) {
			if (other.meterPerSecond != null)
				return false;
		} else if (!meterPerSecond.equals(other.meterPerSecond))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractCar [carID=" + carID + ", meterPerSecond="
				+ meterPerSecond + ", colorCodeR=" + colorCodeR
				+ ", colorCodeG=" + colorCodeG + ", colorCodeB=" + colorCodeB
				+ "]";
	}
	
	
}
