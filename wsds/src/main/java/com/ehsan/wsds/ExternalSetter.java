package com.ehsan.wsds;

public class ExternalSetter {

	public static void setEx1 (ServiceCommunity sc, double h1) {
		double minRt = h1;
		sc.setEx1(sc.getRt() - minRt);
	}

	public static void setEx2 (ServiceCommunity sc, double h1) {
		double maxAv = h1;
		sc.setEx2(maxAv - sc.getAv());		
	}

}
