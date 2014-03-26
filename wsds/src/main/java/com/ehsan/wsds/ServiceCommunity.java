package com.ehsan.wsds;

import java.util.ArrayList;
import java.util.List;


public class ServiceCommunity {

	int id;
	List<Service> services = new ArrayList<Service>();
	
	public void addServiceId (Service id) {
		if (id == null) 
			return;
		if (services == null)
			services = new ArrayList<Service>();
		if (!services.contains(id))
			services.add(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getRt() {
		return 0;
	}

	public double getAv() {
		return 0;
	}
	
	public double getTp() {
		return 0;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "ServiceCommunity [id=" + id + ", services=" + services + "]";
	}
	
}
