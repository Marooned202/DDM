package com.ehsan.wsds;

import java.util.ArrayList;
import java.util.List;


public class ServiceCommunity {

	List<Service> services = new ArrayList<Service>();
	double ex1, ex2;
	
	public void addService (Service service) {
		if (service == null) 
			return;
		if (services == null)
			services = new ArrayList<Service>();
		if (!services.contains(service))
			services.add(service);
	}

	public double getRt() {
		double rt=0;
		for (Service service: services) {
			rt = Math.max(rt, service.getRt());
		}		
		return rt;
	}

	public double getAv() {
		double av=1;
		for (Service service: services) {
			av *= (1 - service.getAv());
		}		
		return 1-av;
	}
	
	public double getTp() {
		double tp=0;
		for (Service service: services) {
			tp += service.getTp();
		}		
		return tp;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}		

	public double getEx1() {
		return ex1;
	}

	public void setEx1(double ex1) {
		this.ex1 = ex1;
	}

	public double getEx2() {
		return ex2;
	}

	public void setEx2(double ex2) {
		this.ex2 = ex2;
	}

	@Override
	public String toString() {
		String str = "<";
		for (int i = 0; i < services.size()-1; i++) {
			str += services.get(i).id + ";";
		}
		str += services.get(services.size()-1).id + ">";	
		str += String.format(", %.6f, %.6f, %.6f, %.6f, %.6f", getRt(), getTp(), getAv(), ex1, ex2);
		return str;
	}
	
}
