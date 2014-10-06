package com.ehsan.wsds.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ehsan.wsds.Service;
import com.ehsan.wsds.ServiceCommunity;
import com.ehsan.wsds.tree.Node;
import com.ehsan.wsds.util.CommunityUtils;
import com.ehsan.wsds.util.Constants;
import com.ehsan.wsds.util.TreeNodeUtils;

public class SolutionOne {
	
	double utilityGain = 0;

	class ServiceScore implements Comparable<ServiceScore> {
		Set<Integer> services;
		double score;

		public Set<Integer> getServices() {
			return services;
		}

		public void setServices(Set<Integer> services) {
			this.services = services;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		@Override
		public int compareTo(ServiceScore s) {
			return Double.compare(s.score, this.score);
		}

		@Override
		public String toString() {
			return "ServiceScore [services=" + services + ", score=" + score
					+ "]";
		}	


	}

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

	public void pickBestCommunities(List<Set<Integer>> templateVector, List<Set<Integer>> services, double[][] matrix, int[][] markMatrix, int time) {	
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

	public void pickBestNCommunities(List<Set<Integer>> templateVector, List<Set<Integer>> services, double[][] matrix, int[][] markMatrix, int time, int num, List<Node<Set<Integer>>> nodes) {
		System.out.println("Num: " + num);
		List<Set<Integer>> copyForLoopServices = new ArrayList<Set<Integer>>();
		copyForLoopServices.addAll(services);
		for (Set<Integer> serviceGroup: copyForLoopServices) {
			//Set<Integer> best = findBestCommunity (templateVector, services, serviceGroup, matrix, markMatrix, time);
			Set<Set<Integer>> bestN = findBestNCommunity (templateVector, services, serviceGroup, matrix, markMatrix, time, num);
			for (Set<Integer> best: bestN) {
				Set<Set<Integer>> bestNofBest = findBestNCommunity (templateVector, services, best, matrix, markMatrix, time, num);			
				if (best != null && bestNofBest.contains(serviceGroup)) {
					//System.out.println("ServiceGroup: " + serviceGroup + " and " + best + " are double bests.");		

					Set<Integer> newGroup = new HashSet<Integer>();
					newGroup.addAll(serviceGroup);
					newGroup.addAll(best);				

					if (!services.contains(newGroup)) {
						System.out.println("New group formed: " + newGroup);
						services.add(newGroup);

						//Tree
						Node<Set<Integer>> serviceGroupNode = TreeNodeUtils.findNode (nodes, serviceGroup);
						Node<Set<Integer>> bestNode = TreeNodeUtils.findNode (nodes, best);
						Node<Set<Integer>> newGroupNode = new Node<Set<Integer>>(newGroup);

						bestNode.addChild(newGroupNode);
						serviceGroupNode.addChild(newGroupNode);	

						nodes.add(newGroupNode);		
						
						utilityGain += matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(best)];						
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

					break;
				}
			}		
		}
	}

	public Set<Set<Integer>> findBestNCommunity(List<Set<Integer>> templateVector,
			List<Set<Integer>> services, Set<Integer> serviceGroup,	double[][] matrix, int[][] markMatrix, int time, int n) {

		if (serviceGroup == null) return null;

		List<ServiceScore> serviceScores = new ArrayList<ServiceScore>();

		Set<Set<Integer>> result = new HashSet<Set<Integer>>();

		double value = -0.5;
		for (Set<Integer> otherServiceGroup: services) {
			if (matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)] > 0) {
				ServiceScore serviceScore = new ServiceScore();
				value = matrix[templateVector.indexOf(serviceGroup)][templateVector.indexOf(otherServiceGroup)];
				serviceScore.setServices(otherServiceGroup);
				serviceScore.setScore(value);	
				serviceScores.add(serviceScore);
			}
		}		

		Collections.sort(serviceScores);

		for (int i = 0; i < n && i < serviceScores.size();i++) {
			result.add(serviceScores.get(i).getServices());
			//System.out.println("i: " + i + ": " + serviceScores.get(i));
		}

		//System.out.println("Best Value: " + value + " , best Choice: " + result + ", for: " + serviceGroup);
		return result;		
	}

	public List<Node<Set<Integer>>> run (List<Set<Integer>> templateVector, List<ServiceCommunity> serviceCommunityList, int learningRate, String filename, String output) throws FileNotFoundException {

		//int[][] bestArray = findAllBestArray(templateVector, filename);
		//int[] bestTest = findVectorBestArrayTime(templateVector, initialSingleServices, filename, 0);

		int[][] markMatrix = new int[templateVector.size()][templateVector.size()];
		List<Set<Integer>> services = CommunityUtils.extractSingleServices(templateVector);
		List<Node<Set<Integer>>> nodes = TreeNodeUtils.extractNodes(services);
		
		utilityGain = 0.0;
		
		PrintWriter pw=new PrintWriter(new FileOutputStream(output));
		for (int time = 0; time < Constants.MAX_TIME; time++) {			
			System.out.println("\nTime: " + time);
			double[][] matrix = extractMatrix(templateVector, markMatrix, filename, time);
			pickBestNCommunities (templateVector, services, matrix, markMatrix, time, 1+(int)time/learningRate, nodes);
			
			int count = 0;
			double communitySize = 0;
			System.out.println("Gain: " + utilityGain);
			pw.println("Gain: " + utilityGain);									
			for (Set<Integer> service: services) {										
				count++;
				communitySize += service.size();
			}			
			pw.println ("Community Count: " + count);
			pw.println ("Community Size: " + communitySize/(double)count);											
		}
	
		System.out.println("List of services: ");		
		for (Set<Integer> service: services) {						
			//System.out.println(service);
			for (ServiceCommunity serviceCommunity: serviceCommunityList) {
				if (serviceCommunity.hasAllServicesOfSet(service)) {
					System.out.println(serviceCommunity);
					pw.println (serviceCommunity);
					break;
				}				
			}
		}
		pw.close();
		
		// Trees:
		pw=new PrintWriter(new FileOutputStream(output+"_tree"));
		for (Node<Set<Integer>> node: nodes) {
			if (node.getData().size() == 1) {
				System.out.println("");
				pw.println("");
				TreeNodeUtils.printTree(node, 0, serviceCommunityList, pw);
				
				ServiceCommunity bestServiceCommunity = TreeNodeUtils.findBestUtilityInTree(node, 0, serviceCommunityList);
				pw.println("Max Utility: " + bestServiceCommunity);
				pw.println("Max Utility: " + bestServiceCommunity.getScore());
				pw.println("Max Utility Gain: " + 
						(bestServiceCommunity.getScore() - CommunityUtils.findServiceCommunity((Set<Integer>)node.getData(),serviceCommunityList).getScore()));
				double highScore = bestServiceCommunity.getScore();
				double lowScore =  CommunityUtils.findServiceCommunity((Set<Integer>)node.getData(),serviceCommunityList).getScore();
				pw.printf("Max Utility Gain Pertentage 30: %.5f\n",((highScore + 30) - (lowScore + 30)) / (lowScore + 30)); 
				pw.printf("Max Utility Gain Pertentage 50: %.5f\n",((highScore + 50) - (lowScore + 50)) / (lowScore + 50));
				
			}
		}		
		pw.close();
		
		return nodes;
	}

}
