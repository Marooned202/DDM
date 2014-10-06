package com.ehsan.wsds.scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ehsan.wsds.Service;
import com.ehsan.wsds.ServiceCommunity;
import com.ehsan.wsds.service.SolutionOneEvaluation;
import com.ehsan.wsds.util.CommunityUtils;

public class ExtractChart2 {
	
	public void run () throws IOException {
		String filename = "data/score_0_00__output_eval_15_125.0";
		
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;			
		while ((line = input.readLine()) != null) {
			if (line.startsWith("Community:")) {				
				int communityId = parseFirstNumber(line);
				int gain1 = 0;
				int gain2 = 0;
								
				while ((line = input.readLine()) != null) {
					if (line.startsWith("Best service community possible::")) {	
						gain1 = parseFirstNumber(line);
						line = input.readLine();
						gain2 = parseFirstNumber(line);
						break;
					}
				}				
				//System.out.printf("<%d>,%d,%d\n", communityId, gain1, gain2);
				System.out.println(gain2);
			}
		}
	}
	
	public void run2 () throws IOException {
		String filename = "data/score_0_05__output_eval_10_150.0";
		
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;			
		while ((line = input.readLine()) != null) {
			if (line.startsWith("Community:")) {		
				
				int communityId = parseFirstNumber(line);				
				Service service = new Service();
				String[] lines = line.split(",");
				service.setRt(Double.parseDouble(lines[1]));
				service.setTp(Double.parseDouble(lines[2]));
				service.setAv(Double.parseDouble(lines[3]));

				line = input.readLine();
				int closestCommunityId = parseFirstNumber(line);
				Service closestService = new Service();
				lines = line.split(",");
				closestService.setRt(Double.parseDouble(lines[1]));
				closestService.setTp(Double.parseDouble(lines[2]));
				closestService.setAv(Double.parseDouble(lines[3]));
														
				//System.out.printf("<%d>,%d,%d\n", communityId, gain1, gain2);
				System.out.printf("%.5f\n", distance(service, closestService));
			}
		}
	}
	
	public void run3 () throws IOException {
		String filename = "data/_score_0_00__output_eval_20_150.0";
		
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;			
		int num0 = 0;
		while ((line = input.readLine()) != null) {
			if (line.startsWith("Community:")) {		
				int communityId = parseFirstNumber(line);
				double utilityInitial = Double.parseDouble(line.substring(line.indexOf("Utility:")+9).trim());	
				line = input.readLine();
				line = input.readLine();
				line = input.readLine();
				double utilityNew = Double.parseDouble(line.substring(line.indexOf("Utility:")+9).trim());	
				//System.out.println();
				//System.out.println(utilityNew - utilityInitial);
				line = input.readLine();
				line = input.readLine();
				line = input.readLine();
				int rational = parseFirstNumber(line);

				//System.out.println((int)rational);				
				
				if (rational == 0) num0++;
														
				//System.out.printf("<%d>,%d,%d\n", communityId, gain1, gain2);
				//System.out.printf("%.5f\n", distance(service, closestService));
			}
			//System.out.println("Num0: " + num0);
		}
	}
	
	public void run4 () throws IOException {
		String filename = "data/score_0_05_output_communities_time_20";
		
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;			
		int num0 = 0;
		while ((line = input.readLine()) != null) {
			if (line.startsWith("Community Size:")) {												
				int gain = parseFirstNumber(line);
				double num = Double.parseDouble(line.substring(line.indexOf("Community Size:")+15).trim());	
				System.out.println(num);
			}
			//System.out.println("Num0: " + num0);
		}
	}

	private int parseFirstNumber(String line) {
		String s = new String(line);
		Matcher matcher = Pattern.compile("\\d+").matcher(s);
		matcher.find();
		return Integer.valueOf(matcher.group());
	}

	private double distance(Service sc1, Service sc2) {
		double res = 0;
		res += Math.abs(sc1.getAv() - sc2.getAv());
		res += Math.abs(sc1.getTp() - sc2.getTp());
		res += Math.abs(sc1.getRt() - sc2.getRt());
		return res;
	}
	
	public static void main(String[] args) {
		try {
			new ExtractChart2().run4();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
