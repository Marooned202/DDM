package com.ehsan.wsds.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ehsan.wsds.ExternalSetter;
import com.ehsan.wsds.Service;
import com.ehsan.wsds.ServiceCommunity;
import com.ehsan.wsds.tree.Node;

public class CommunityUtils {

	public static double findMinRt (List<Set<Integer>> templateVector, List<Service> services) {
		double minRt = 10000;
		for (Set<Integer> communities: templateVector) {
			if (communities.size() != 1) continue;
			Service service = services.get(communities.iterator().next());
			if (minRt > service.getRt())
				minRt = service.getRt();
		}
		return minRt;
	}

	public static double findMaxAv (List<Set<Integer>> templateVector, List<Service> services) {
		double maxAv = 0;
		for (Set<Integer> communities: templateVector) {
			if (communities.size() != 1) continue;
			Service service = services.get(communities.iterator().next());
			if (maxAv < service.getAv()) 
				maxAv = service.getAv();
		}
		return maxAv;
	}			

	public static boolean communityInTemplateVector(ServiceCommunity joinedCommunity, List<Set<Integer>> templateVector) {
		boolean found = false;
		for (Set<Integer> serviceIds: templateVector) {
			if (joinedCommunity.getServices().size() == serviceIds.size()) {
				found = true;
				for (Service service: joinedCommunity.getServices()) {
					if (!serviceIds.contains(service.getId())) {
						found = false;
					}
				}
				if (found == true) return true;
			}
		}
		return false;
	}

	public static Double getBenefitOfJoining(ServiceCommunity source, ServiceCommunity joiningCommunity, List<Set<Integer>> templateVector, double minRt, double maxAv) {
		Double result = null;
		ServiceCommunity mergedCommunity = new ServiceCommunity();
		mergedCommunity.getServices().addAll(source.getServices());
		for (Service service: joiningCommunity.getServices()) {
			mergedCommunity.addService(service);
		}					
		ExternalSetter.setEx1(mergedCommunity, minRt);
		ExternalSetter.setEx2(mergedCommunity, maxAv);

		if (mergedCommunity.getServices().size() > 4) {
			result = null;
		} else if (communityInTemplateVector (mergedCommunity, templateVector)){
			result = mergedCommunity.getScore() - source.getScore();
		} else {
			result = null;
		}	
		return result;
	}

	public static List<Set<Integer>> extractSingleServices (List<Set<Integer>> vector) {
		List<Set<Integer>> singleServices = new ArrayList<Set<Integer>>();

		for (Set<Integer> community: vector) {
			if (community.size() == 1) {
				HashSet<Integer> newCommunity = new HashSet<Integer>();
				newCommunity.add(community.iterator().next());
				singleServices.add(newCommunity);
			}
		}

		return singleServices;
	}
	
	public static ServiceCommunity findServiceCommunity (Set<Integer> vector, List<ServiceCommunity> serviceCommunityList) {
		for (ServiceCommunity serviceCommunity: serviceCommunityList) {
			if (serviceCommunity.hasAllServicesOfSet(vector)) {
				return serviceCommunity;
			}
		}
		return null;
	}
	
	
	public static Set<Integer> serviceCommunityToSet (ServiceCommunity serviceCommunity) {
		Set<Integer> serviceSet = new HashSet<Integer>();
		
		for (Service service: serviceCommunity.getServices()) {
			serviceSet.add(service.getId());
		}
		return serviceSet;
	}
}
