package com.ehsan.wsds.util;

import java.util.List;

import com.ehsan.wsds.Service;

public class CommunityUtils {
	
	public static double findMinRt (List<List<Integer>> templateVector, List<Service> services) {
		double minRt = 10000;
		for (List<Integer> communities: templateVector) {
			if (communities.size() != 1) continue;
			Service service = services.get(communities.get(0));
			if (minRt > service.getRt())
				minRt = service.getRt();
		}
		return minRt;
	}
	
	public static double findMaxAv (List<List<Integer>> templateVector, List<Service> services) {
		double maxAv = 0;
		for (List<Integer> communities: templateVector) {
			if (communities.size() != 1) continue;
			Service service = services.get(communities.get(0));
			if (maxAv < service.getAv()) 
				maxAv = service.getAv();
		}
		return maxAv;
	}
	
}
