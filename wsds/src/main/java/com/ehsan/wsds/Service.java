package com.ehsan.wsds;

import java.util.ArrayList;
import java.util.List;


public class Service {
	int id = 0;
	double rt = 0;
	double av = 0;
	double tp = 0;	
	
	List<Integer> serviceIds = new ArrayList<Integer>();
	
	public void addServiceId (int id) {
		if (serviceIds == null)
			serviceIds = new ArrayList<Integer>();
		if (!serviceIds.contains(id))
			serviceIds.add(id);
	}
	
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
	public List<Integer> getServiceIds() {
		return serviceIds;
	}
	public void setServiceIds(List<Integer> serviceIds) {
		this.serviceIds = serviceIds;
	}

	@Override
	public String toString() {
		return "Service [id=" + id + ", rt=" + rt + ", av=" + av + ", tp=" + tp
				+ ", serviceIds=" + serviceIds + "]";
	}
	
}
