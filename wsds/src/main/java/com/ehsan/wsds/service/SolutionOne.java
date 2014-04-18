package com.ehsan.wsds.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ehsan.wsds.util.CommunityUtils;

public class SolutionOne {

	public int[][] findAllBestArray(List<List<Integer>> templateVector, String filename) { 
		int[][] best = new int[templateVector.size()][64];

		for (int time = 0; time < 63; time++) {
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

	public void run (List<Set<Integer>> templateVector, String filename) {
		
		//int[][] bestArray = findAllBestArray(templateVector, filename);
			
		//List<List<Integer>> initialSingleServices = CommunityUtils.extractSingleServices(templateVector);
		List<Set<Integer>> test = new ArrayList<Set<Integer>>();
		
		HashSet<Integer> newCommunity = new HashSet<Integer>();
		newCommunity.add(2571);
		test.add(newCommunity);
		
		newCommunity = new HashSet<Integer>();
		newCommunity.add(3035);		
		test.add(newCommunity);
		
		newCommunity = new HashSet<Integer>();
		newCommunity.add(1731);		
		test.add(newCommunity);
		
		newCommunity = new HashSet<Integer>();
		newCommunity.add(1024);
		newCommunity.add(938);
		newCommunity.add(2074);
		newCommunity.add(1789);
		test.add(newCommunity);

		int[] bestTest = findVectorBestArrayTime(templateVector, test, filename, 0);
		
		for (int i : bestTest) {
			System.out.println(i);
		}
	}
}
