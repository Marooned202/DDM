package com.ehsan.wsds.util;

import java.util.Arrays;
import java.util.HashMap;

import com.ehsan.wsds.Stat;

public class StatisticsUtils
{    

	public static double getMean(double[] data)
	{
		double size = data.length;
		double sum = 0.0;
		for(double a : data)
			sum += a;
		return sum/size;
	}

	public static double getVariance(double[] data)
	{
		double size = data.length;
		double mean = getMean(data);
		double temp = 0;
		for(double a :data)
			temp += (mean-a)*(mean-a);
		return temp/size;
	}

	public static double getStdDev(double[] data)
	{
		return Math.sqrt(getVariance(data));
	}

	public static double median(double[] data) 
	{
		double[] b = new double[data.length];
		System.arraycopy(data, 0, b, 0, b.length);
		Arrays.sort(b);

		if (data.length % 2 == 0) 
		{
			return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
		} 
		else 
		{
			return b[b.length / 2];
		}
	}

	public static HashMap<Integer, Double> normalizeStats(HashMap<Integer, Stat> serviceData) {
		
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();

		double data[] = new double[serviceData.size()];
		for (int i = 0; i < serviceData.size(); i++) {
			data[i] = (double)serviceData.get(i).getFinalValue();
		}
		double dev = getStdDev(data);
		double mean = getMean(data);

		for (int serviceId: serviceData.keySet()) {
			res.put(serviceId, (((double)serviceData.get(serviceId).getFinalValue())-mean) / dev);
		}

		return res;
	}

}
