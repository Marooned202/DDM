package com.ehsan.wsds.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ehsan.wsds.Service;
import com.ehsan.wsds.ServiceCommunity;
import com.ehsan.wsds.tree.Node;
import com.ehsan.wsds.util.CommunityUtils;
import com.ehsan.wsds.util.TreeNodeUtils;

public class SolutionOneEvaluation {

	

	public void run (List<Set<Integer>> templateVector, List<ServiceCommunity> serviceCommunityList, List<Node<Set<Integer>>> nodes, List<ServiceCommunity> testServiceCommunityList,  double coef, String output) throws FileNotFoundException {					
		
		List<ServiceCommunity> singleServiceCommunities = new ArrayList<ServiceCommunity>();
		List<Set<Integer>> singleServices = CommunityUtils.extractSingleServices(templateVector);
		
		for (Set<Integer> service: singleServices) {
			singleServiceCommunities.add(CommunityUtils.findServiceCommunity(service, serviceCommunityList));
		}
		
		
		PrintWriter pw=new PrintWriter(new FileOutputStream(output));
		for (ServiceCommunity serviceCommunity: testServiceCommunityList) {			
			pw.println("Community: " + serviceCommunity);
			
			ServiceCommunity closestServiceCommunity = findClosestServiceCommunity (serviceCommunity, singleServiceCommunities, pw);
			
			pw.println("Closest Community: " + closestServiceCommunity);
			
			ServiceCommunity bestServiceCommunity = TreeNodeUtils.findBestUtilityInTree(TreeNodeUtils.findNode(nodes, CommunityUtils.serviceCommunityToSet(closestServiceCommunity)), 0, serviceCommunityList);
			
			pw.println("Best of closest: " + bestServiceCommunity);		
			
			ServiceCommunity newServiceCommunity = new ServiceCommunity();
			for (Service service: bestServiceCommunity.getServices()) {
				if (service.getId() != closestServiceCommunity.getServices().get(0).getId())
				newServiceCommunity.addService(service);				
			}
			newServiceCommunity.addService(serviceCommunity.getServices().get(0));
			//newServiceCommunity.setEx1(bestServiceCommunity.getEx1());
			//newServiceCommunity.setEx2(bestServiceCommunity.getEx2());
			
			pw.println("New service community: " + newServiceCommunity);	
			
			//finding real best brute force
			
			double maxUtility = 0;
			ServiceCommunity best = new ServiceCommunity();
			for (ServiceCommunity sc: serviceCommunityList) {
				if (sc.getServices().size() == 1) {
					continue;
				}
				ServiceCommunity testSC = new ServiceCommunity();
				for (Service service: sc.getServices()) {
					testSC.addService(service);				
				}
				testSC.addService(serviceCommunity.getServices().get(0));
				if (testSC.getScore() > maxUtility) {
					maxUtility = testSC.getScore();
					best = testSC;					
				}
			}
			pw.println("Best: " + best);	
			
			pw.println("Best service community possible:: " + ((Math.abs(newServiceCommunity.getScore()) * (coef) > best.getScore())?1:0));	
			pw.println("Is only rational Best service community possible: " + ((best.getScore() * (25/100) > newServiceCommunity.getScore())?1:0));	
			
			pw.println("");
		}
		
		pw.close();				
	}

	public List<ServiceCommunity> getTestServiceList(List<ServiceCommunity> serviceCommunityList) {
		Random rnd = new Random();
		List<ServiceCommunity> testServiceCommunityList = new ArrayList<ServiceCommunity>();
		while (testServiceCommunityList.size() < 1000) {
			ServiceCommunity sc = serviceCommunityList.get(rnd.nextInt(serviceCommunityList.size()));
			if (sc.getServices().size() != 1) continue;
			if (!testServiceCommunityList.contains(sc)) testServiceCommunityList.add(sc);
		}
		return testServiceCommunityList;
	}

	public ServiceCommunity findClosestServiceCommunity(ServiceCommunity serviceCommunity,
			List<ServiceCommunity> serviceCommunities, PrintWriter pw) {

		double distance = Double.MAX_VALUE;
		ServiceCommunity res = null;
		for (ServiceCommunity sc: serviceCommunities) {
			if (distance (sc, serviceCommunity) < distance) {
				distance = distance (sc, serviceCommunity);
				res = sc;
			}
		}		
		pw.println("Closest Distance: " + distance);
		return res;
	}

	private double distance(ServiceCommunity sc1, ServiceCommunity sc2) {
		double res = 0;
		res += Math.abs(sc1.getAv() - sc2.getAv());
		res += Math.abs(sc1.getTp() - sc2.getTp());
		res += Math.abs(sc1.getRt() - sc2.getRt());
		return res;
	}

}
