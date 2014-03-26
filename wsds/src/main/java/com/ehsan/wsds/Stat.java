package com.ehsan.wsds;

public class Stat {	
	
	double totalValue = 0.0;
	long num = 0;	
	

	public void observeStat (double value) {
		num++;
		totalValue += value;
	}
	
	
	public double getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (num ^ (num >>> 32));
		long temp;
		temp = Double.doubleToLongBits(totalValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Stat other = (Stat) obj;
		if (num != other.num)
			return false;
		if (Double.doubleToLongBits(totalValue) != Double
				.doubleToLongBits(other.totalValue))
			return false;
		return true;
	}
	
	
	
}
