package com.ehsan.wsds.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ehsan.wsds.Service;
import com.ehsan.wsds.ServiceCommunity;
import com.ehsan.wsds.util.CommunityUtils;
import com.ehsan.wsds.util.Constants;

public class SolutionOne {
	
	public int[][] findAllBestArray(List<List<Integer>> templateVector, String filename) { 
		int[][] best = new int[templateVector.size()][Constants.MAX_TIME];

		for (int time = 0; time < Constants.MAX_TIME; time++) {
			System.out.println("Readiing collabration matrix: " + time);
			try {			
				BufferedReader br = new BufferedReader(new FileReader(filename + time));
				String line;			
				int row = 0;
				while ((line = br.readLine()) != null) {
					if (line.trim().isEmpty()) continue;
					String[] split = line.split("\\s+");
					double bestScore = -1000;
					int bestScoreIndex = -1;
					for (int i = 0;i < split.length; i++) {
						if (split[i].equals("X")) continue;
						double value = Double.parseDouble(split[i]);
						if (value > bestScore) {
							bestScore = value;
							bestScoreIndex = i;
						}
					}
					best[row][time] = bestScoreIndex;
					row++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return best;
	}

	public int[] findVectorBestArrayTime(List<Set<Integer>> templateVector, List<Set<Integer>> vector, String filename, int time) { 
		int[] best = new int[templateVector.size()];

		for (int i = 0; i < best.length; i++) {
			best[i] = -1;
		}

		try {			
			BufferedReader br = new BufferedReader(new FileReader(filename + time));
			String line;			
			int row = 0;
			int num = 0;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				if (!vector.contains(templateVector.get(num++))) continue;
				String[] split = line.split("\\s+");
				double bestScore = -1000;
				int bestScoreIndex = -1;
				for (int i = 0;i < split.length; i++) {
					if (!vector.contains(templateVector.get(i))) continue;
					if (split[i].equals("X")) continue;					
					double value = Double.parseDouble(split[i]);
					if (value > bestScore) {
						bestScore = value;
						bestScoreIndex = i;
					}
				}
				best[row] = bestScoreIndex;
				row++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return best;
	}

	public double[][] extractMatrix (List<Set<Integer>> templateVector, int[][] mark, String filename, int time) { 
		double[][] matrix = new double[templateVector.size()][templateVector.size()];

		for (int t = 0; t < Constants.MAX_TIME; t++) {
			for (int i = 0; i < matrix[0].length; i++) {
				for (int j = 0; i < matrix[0].length; i++) {
					matrix[i][j] = -1;
				}
			}
		}

		//for (int time = 0; time < MAX_TIME; time++) {
		try {			
			BufferedReader br = new BufferedReader(new FileReader(filename + time));
			String line;			
			int row = 0;
			int num = 0;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;				
				String[] split = line.split("\\s+");
				for (int i = 0;i < split.length; i++) {
					if (split[i].equals("X")) { 
						matrix[row][i] = -1;
						continue;
					}
					if (mark[row][i] == -1) {
						matrix[row][i] = -1;
						continue;
					}
					matrix[row][i] = Double.parseDouble(split[i]);
				}
				row++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//}

		return matrix;
	}

	public void markMatrix (double[][][] matrix, int i, int j, double value) {		
		for (int time = 0; time < Constants.MAX_TIME; time++) {
			matrix[i][j][time] = value;
		}
	}

	public void pickCommunities(List<Set<Integer>> templateVector, List<Set<Integer>> services, double[][] matrix, int[][] markMatrix, int time) {	
		List<Set<Integer>> copyForLoopServices = new ArrayList<Set<Integer>>();
		copyForLoopServices.addAll(services);
		for (Set<Integer> serviceGroup: copyForLoopServices) {
			Set<Integer> best = findBestCommunity (templateVector, services, serviceGroup, matrix, markMatrix, time);
			if (best != null && serviceGroup.equals(findBestCommunity (templateVector, services, best, matrix, markMatrix, time))) {
				//System.out.println("ServiceGroup: " + serviceGroup + " and " + best + " are double bests.");		

				Set<Integer> newGroup = new HashSet<Integer>();
				newGroup.addAll(serviceGroup);
				newGroup.addAll(best);				

				if (!services.contains(newGroup)) {
					System.out.println("New group formed: " + newGroup);
					services.add(newGroup);
				}

				int i = templateVector.indexOf(serviceGroup);
				int j = templateVector.indexOf(best);
				int n = templateVector.indexOf(newGroup);
				markMatrix[i][j] = -1;
				//				matrix[i][j] = -1;
				markMatrix[j][i] = -1;
				//				matrix[j][i] = -1;
				markMatrix[i][n] = -1;
				markMatrix[n][i] = -1;
				markMatrix[j][n] = -1;
				markMatrix[n][j] = -1;
			}
		}		
	}

	public Set<Integer> findBestCommunity(List<Set<Integer>> templateVector,
			List<Set<Integer>> services, Set<Integer> serviceGroup,	double[][] matrix, int[][] markMatrix, int time) {

		if (serviceGroup == null) return null;
		
		Set<Integer> result = null;

		double value = -0.5;
		for (Set<Integer> otherServiceGroup: services) {
//			System.out.println("serviceGroup: " + serviceGroup);
//			System.out.println("otherServiceGroup: " + otherServiceGroup);
			if (matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)] > 0 &&
					matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)] > value) {
				value = matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)];
				result = otherServiceGroup;
			}
		}		

		//System.out.println("Best Value: " + value + " , best Choice: " + result + ", for: " + serviceGroup);
		return result;		
	}
	
	public Set<Set<Integer>> findBestNCommunity(List<Set<Integer>> templateVector,
			List<Set<Integer>> services, Set<Integer> serviceGroup,	double[][] matrix, int[][] markMatrix, int time, int n) {

		if (serviceGroup == null) return null;
		
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();

		double value = -0.5;
		for (Set<Integer> otherServiceGroup: services) {
//			System.out.println("serviceGroup: " + serviceGroup);
//			System.out.println("otherServiceGroup: " + otherServiceGroup);
			if (matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)] > 0 &&
					matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)] > value) {
				value = matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)];
				//result = otherServiceGroup;
			}
		}		

		//System.out.println("Best Value: " + value + " , best Choice: " + result + ", for: " + serviceGroup);
		return result;		
	}

	public void run (List<Set<Integer>> templateVector, List<ServiceCommunity> serviceCommunityList, String filename) {

		//int[][] bestArray = findAllBestArray(templateVector, filename);
		//int[] bestTest = findVectorBestArrayTime(templateVector, initialSingleServices, filename, 0);

		int[][] markMatrix = new int[templateVector.size()][templateVector.size()];
		List<Set<Integer>> services = CommunityUtils.extractSingleServices(templateVector);

		for (int time = 0; time < Constants.MAX_TIME; time++) {			
			System.out.println("\nTime: " + time);
			double[][] matrix = extractMatrix(templateVector, markMatrix, filename, time);
			pickCommunities (templateVector, services, matrix, markMatrix, time);
		}
		
		System.out.println("List of services: ");
		for (Set<Integer> service: services) {						
			System.out.println(service);
			for (ServiceCommunity serviceCommunity: serviceCommunityList) {
				if (serviceCommunity.hasAllServicesOfSet(service)) {
					System.out.println(serviceCommunity);
					break;
				}				
			}
		}

	}

}
