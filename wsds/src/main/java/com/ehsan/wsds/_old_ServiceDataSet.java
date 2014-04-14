package com.ehsan.wsds;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ehsan.wsds.util.Utils;

public class _old_ServiceDataSet {

	public static final int SIZE_2_C = 80;
	public static final int SIZE_3_C = 22;
	public static final int SIZE_4_C = 13;

	public void extractAverageServices (String inputfile, String outputfile, double ignoreValue) {

		HashMap <Integer, HashMap<Integer, Stat>> serviceDataTime = new HashMap <Integer, HashMap<Integer, Stat>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(inputfile));
			String line;
			long num = 0;				
			while ((line = br.readLine()) != null) {
				num++;
				if (line.trim().isEmpty()) continue;
				String[] splits = line.split("\\s+");

				int time, serviceId;
				double value;
				try {										
					//					int userId = Integer.parseInt(splits[0]);
					serviceId = Integer.parseInt(splits[1]);
					time = Integer.parseInt(splits[2]);
					value = Double.parseDouble(splits[3]);
				} catch (Exception e) {
					System.out.println("Line number: " + num);
					e.printStackTrace();
					continue;
				}

				if (value == ignoreValue) continue;

				HashMap<Integer, Stat> serviceData = serviceDataTime.get(time);
				if (serviceData == null) {
					Stat stat = new Stat();
					stat.observeStat(value);
					serviceData = new HashMap<Integer, Stat>();
					serviceData.put (serviceId, stat);
					serviceDataTime.put(time, serviceData);
				} else {
					if (serviceData.get(serviceId) == null) {
						Stat stat = new Stat();
						stat.observeStat(value);
						serviceData.put (serviceId, stat);
					} else {
						serviceData.get(serviceId).observeStat(value);
					}
				}

				if (num % 1000000 == 1) System.out.println("Reading line #" + num);			
			}
			br.close();

			System.out.println("Time Size: " + serviceDataTime.keySet().size());

			// OUTPUT
			for (int time: serviceDataTime.keySet()) {
				PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));		
				HashMap<Integer, Stat> serviceData = serviceDataTime.get(time);
				for (int serviceId: serviceData.keySet()) {
					pw.printf("%d %f %d\n", 
							serviceId, 
							(double)serviceData.get(serviceId).getTotalValue() / (double)serviceData.get(serviceId).getNum(), 
							serviceData.get(serviceId).getNum());

				}
				pw.close();
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//	public void wsCount (String inputfile) {
	//		HashMap <Integer, HashMap<Integer, Stat>> serviceDataTime = new HashMap <Integer, HashMap<Integer, Stat>>();
	//		try {
	//			BufferedReader br = new BufferedReader(new FileReader(inputfile));
	//			String line;
	//			long num = 0;			
	//			int maxServiceNumber = 0;
	//			while ((line = br.readLine()) != null) {
	//				num++;
	//				if (line.trim().isEmpty()) continue;
	//				String[] splits = line.split("\\s+");
	//
	//				int time, serviceId, userId;
	//				double value;
	//				try {					
	//					serviceId = Integer.parseInt(splits[0]);
	//					userId = Integer.parseInt(splits[1]);
	//					time = Integer.parseInt(splits[2]);
	//					value = Double.parseDouble(splits[3]);
	//				} catch (Exception e) {
	//					System.out.println("Line number: " + num);
	//					e.printStackTrace();
	//					continue;
	//				}
	//
	//				if (maxServiceNumber < userId)
	//					maxServiceNumber = userId;
	//			}
	//			System.out.println(maxServiceNumber);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}			
	//	}

	public void extractAvailability(String inputfile, String outputfile) {
		for (int time = 0; time < 64; time++) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(inputfile+time));
				PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));
				String line;			
				while ((line = br.readLine()) != null) {
					if (line.trim().isEmpty()) continue;
					String[] splits = line.split("\\s+");
					int avail = Integer.parseInt(splits[2]);
					int serviceId = Integer.parseInt(splits[0]);										
					pw.printf("%d %.6f\n", serviceId, (double)avail/142.0);					
				}
				br.close();
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}


	public void makeServiceVector(String rtInputFilename, String tpInputFilename, String avInputFilename, List<List<Integer>> templateVector, String outputfile) {

		// Load Services
		List<Service> services = null;
		List<ServiceCommunity> serviceCommunities = new ArrayList<ServiceCommunity>();

		List<Service> twoServices = new ArrayList<Service>();
		List<Service> threeServices = new ArrayList<Service>();
		List<Service> fourServices = new ArrayList<Service>();

		// Load Single Services from input files
		int time = 0;
		try {
			services = extractServicesFromFile(rtInputFilename, tpInputFilename, avInputFilename, time);		

			for (Service service: services) {
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				serviceCommunity.addService(service);
				serviceCommunities.add(serviceCommunity);
			}

			PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));
			// Adding 88 random services for 2 size communities
			Random rnd = new Random();
			while (twoServices.size() < SIZE_2_C) {
				int rndInt = rnd.nextInt(services.size());
				if (!twoServices.contains(services.get(rndInt)))
					twoServices.add(services.get(rndInt));
			}								
			for (Set<Service> s : Utils.getSubsets(twoServices,2)) {
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				for (Service service: s) {				
					serviceCommunity.addService(service);
				}
				serviceCommunities.add(serviceCommunity);
			}

			// size 3 communities
			while (threeServices.size() < SIZE_3_C) {
				int rndInt = rnd.nextInt(twoServices.size());
				if (!threeServices.contains(twoServices.get(rndInt)))
					threeServices.add(twoServices.get(rndInt));
			}			
			for (Set<Service> s : Utils.getSubsets(threeServices,3)) {
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				for (Service service: s) {				
					serviceCommunity.addService(service);
				}
				serviceCommunities.add(serviceCommunity);
			}

			// size 4 communities
			while (fourServices.size() < SIZE_4_C) {
				int rndInt = rnd.nextInt(threeServices.size());
				if (!fourServices.contains(threeServices.get(rndInt)))
					fourServices.add(threeServices.get(rndInt));
			}			
			for (Set<Service> s : Utils.getSubsets(fourServices,4)) {
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				for (Service service: s) {				
					serviceCommunity.addService(service);
				}
				serviceCommunities.add(serviceCommunity);
			}

			double minRt = 10000;
			double maxAv = 0;
			for (Service service: twoServices) {
				if (maxAv < service.getAv()) 
					maxAv = service.getAv();
				if (minRt > service.getRt())
					minRt = service.getRt();
			}


			System.out.println("Size:" + serviceCommunities.size());
			for (ServiceCommunity sc: serviceCommunities) {
				ExternalSetter.setEx1(sc, minRt);
				ExternalSetter.setEx2(sc, maxAv);
				pw.println(sc);
			}
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<Service> extractServicesFromFile(String rtInputFilename, String tpInputFilename, String avInputFilename, int time) throws FileNotFoundException, IOException {

		List<Service> services = new ArrayList<Service>();

		BufferedReader brRT = new BufferedReader(new FileReader(rtInputFilename+time));			
		BufferedReader brTP = new BufferedReader(new FileReader(tpInputFilename+time));
		BufferedReader brAV = new BufferedReader(new FileReader(avInputFilename+time));
		String line;			
		while ((line = brRT.readLine()) != null) {
			String[] splits = line.split("\\s+");
			int serviceId = Integer.parseInt(splits[0]);
			double rt = Double.parseDouble(splits[1]);

			line = brTP.readLine();
			splits = line.split("\\s+");
			double tp = Double.parseDouble(splits[1]);

			line = brAV.readLine();
			splits = line.split("\\s+");
			double av = Double.parseDouble(splits[1]);

			Service service = new Service();
			service.setAv(av);
			service.setRt(rt);
			service.setTp(tp);
			service.setId(serviceId);

			services.add(service);			
		}

		brRT.close();
		brTP.close();
		brAV.close();

		return services;
	}


	public List<List<Integer>> generateTemplateVector(String rtInputFilename, String tpInputFilename, String avInputFilename, String outputfile) {

		List<List<Integer>> templateVector = new ArrayList <List<Integer>>();

		List<Integer> twoServices = new ArrayList<Integer>();
		List<Integer> threeServices = new ArrayList<Integer>();
		List<Integer> fourServices = new ArrayList<Integer>();

		try {
			List<Service> services = extractServicesFromFile(rtInputFilename, tpInputFilename, avInputFilename, 0);		


			Random rnd = new Random();
			for (int i = 0; i < SIZE_2_C; i++) {
				int rndInt = rnd.nextInt(services.size());				
				twoServices.add(services.get(rndInt).getId());
				services.remove(rndInt);
			}	

			//inserting single ones
			for (Integer service: twoServices) {
				List<Integer> serviceList = new ArrayList<Integer>();
				serviceList.add(service);
				templateVector.add(serviceList);
			}

			//inserting group of twos
			for (Set<Integer> s : Utils.getSubsets(twoServices,2)) {
				List<Integer> serviceList = new ArrayList<Integer>();
				for (Integer service: s) {				
					serviceList.add(service);
				}
				templateVector.add(serviceList);
			}

			//inserting group of threes
			for (int i = 0; i < SIZE_3_C; i++) {
				int rndInt = rnd.nextInt(twoServices.size());				
				threeServices.add(twoServices.get(rndInt));
				twoServices.remove(rndInt);
			}								
			for (Set<Integer> s : Utils.getSubsets(threeServices,3)) {
				List<Integer> serviceList = new ArrayList<Integer>();
				for (Integer service: s) {				
					serviceList.add(service);
				}
				templateVector.add(serviceList);
			}

			//inserting group of fours
			for (int i = 0; i < SIZE_4_C; i++) {
				int rndInt = rnd.nextInt(threeServices.size());				
				fourServices.add(threeServices.get(rndInt));
				threeServices.remove(rndInt);
			}								
			for (Set<Integer> s : Utils.getSubsets(fourServices,4)) {
				List<Integer> serviceList = new ArrayList<Integer>();
				for (Integer service: s) {				
					serviceList.add(service);
				}
				templateVector.add(serviceList);
			}


			PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile));
			for (List<Integer> communities: templateVector) {
				for (Integer service: communities) {
					pw.print(service + " ");
				}
				pw.println();
			}

			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return templateVector;
	}
	
	public List<List<Integer>> loadTempalteVector (String filename) {
		
		List<List<Integer>> templateVector = new ArrayList <List<Integer>>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;			
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				List<Integer> serviceList = new ArrayList<Integer>();
				String[] splits = line.split("\\s+");
				for (String serviceId: splits) {
					serviceList.add(Integer.parseInt(serviceId));
				}
				templateVector.add(serviceList);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}			
		return templateVector;
	}

	public void run() {
		//extractAverageServices("../wsdsinput/rtRate","data/service_rt_t", 0.0);
		//extractAverageServices("../wsdsinput/tpRate","data/service_tp_t", 20.0);
		//wsCount("../wsdsinput/tpRate");
		//extractAvailability("data/service_rt_t","data/service_av_t");
		//makeTemplateVector();

		//generateTemplateVector("data/service_rt_t", "data/service_tp_t", "data/service_av_t", "data/vector_template");
		List<List<Integer>> templateVector = loadTempalteVector("data/vector_template");		

		makeServiceVector("data/service_rt_t", "data/service_tp_t", "data/service_av_t", templateVector, "data/vector_t");

	}





}
