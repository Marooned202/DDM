package com.ehsan.wsds.scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractChart2 {
	
	public void run () throws IOException {
		String filename = "data/score_0_05__output_eval_5";
		
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
				System.out.println(gain1);
			}
		}
	}

	private int parseFirstNumber(String line) {
		String s = new String(line);
		Matcher matcher = Pattern.compile("\\d+").matcher(s);
		matcher.find();
		return Integer.valueOf(matcher.group());
	}

	public static void main(String[] args) {
		try {
			new ExtractChart2().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
