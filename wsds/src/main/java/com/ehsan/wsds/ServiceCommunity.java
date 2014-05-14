package com.ehsan.wsds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
	
	public double getScore () {
		double res = 0;
		//res = getRt() + getAv() + getTp() + getEx1() + getEx2();
		res = ((getTp() - getRt()) *  getAv()) - getEx1() - getEx2();
		if (services.size() > 0) {
			res = res / (0.95 + 0.05 * (double)services.size());
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((services == null) ? 0 : services.hashCode());
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
		ServiceCommunity other = (ServiceCommunity) obj;
		if (services == null) {
			if (other.services != null)
				return false;
		} else if (!services.equals(other.services))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "<";
		for (int i = 0; i < services.size()-1; i++) {
			str += services.get(i).id + ";";
		}
		str += services.get(services.size()-1).id + ">";	
		str += String.format(", %.6f, %.6f, %.6f, %.6f, %.6f, Utility: %.6f", getRt(), getTp(), getAv(), ex1, ex2, getScore());
		return str;
	}
	
	public boolean hasAllServicesOfSet (Set<Integer> servicesSet) {
		if (services == null) return false;
		if (servicesSet == null) return false;
		
		Set<Integer> temp = new HashSet<Integer>();
		for (Service service: services) 
			temp.add(service.getId());
		return temp.equals(servicesSet);		
	}
	
}
