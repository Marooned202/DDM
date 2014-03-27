package com.ehsan.wsds;



public class Service {
	int id = 0;
	double rt = 0;
	double av = 0;
	double tp = 0;	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getRt() {
		return rt;
	}
	public void setRt(double rt) {
		this.rt = rt;
	}
	public double getAv() {
		return av;
	}
	public void setAv(double av) {
		this.av = av;
	}
	public double getTp() {
		return tp;
	}
	public void setTp(double tp) {
		this.tp = tp;
	}		

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Service other = (Service) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Service [id=" + id + ", rt=" + rt + ", av=" + av + ", tp=" + tp
				+ "]";
	}
	
}
