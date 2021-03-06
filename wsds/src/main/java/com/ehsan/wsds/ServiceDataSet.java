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

import com.ehsan.wsds.service.SolutionOne;
import com.ehsan.wsds.service.SolutionOneEvaluation;
import com.ehsan.wsds.tree.Node;
import com.ehsan.wsds.util.CommunityUtils;
import com.ehsan.wsds.util.Constants;
import com.ehsan.wsds.util.StatisticsUtils;
import com.ehsan.wsds.util.Utils;

public class ServiceDataSet {

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
																
//				for (int serviceId: serviceData.keySet()) {
//					pw.printf("%d %f %d\n", 
//							serviceId, 
//							(double)serviceData.get(serviceId).getTotalValue() / (double)serviceData.get(serviceId).getNum(), 
//							serviceData.get(serviceId).getNum());
//
//				}
								
				// Normal
				HashMap<Integer, Double> serviceNormal = StatisticsUtils.normalizeStats (serviceData);
				for (int serviceId: serviceNormal.keySet()) {
					pw.printf("%d %f %d\n", 
							serviceId, 
							(double)serviceNormal.get(serviceId), 
							serviceData.get(serviceId).getNum());

				}
								
				pw.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
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
		for (int time = 0; time < Constants.MAX_TIME; time++) {
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


	public void makeServiceVector(String rtInputFilename, String tpInputFilename, String avInputFilename, List<Set<Integer>> templateVector, String outputfile) {

		// Load Services
		List<Service> services = null;
		List<ServiceCommunity> serviceCommunities = new ArrayList<ServiceCommunity>();

		// Load Single Services from input files
		int time = 0;
		try {
			services = extractServicesFromFile(rtInputFilename, tpInputFilename, avInputFilename, time);		

			for (Service service: services) {
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				serviceCommunity.addService(service);
				serviceCommunities.add(serviceCommunity);
			}

			for (Set<Integer> communities: templateVector) {
				if (communities.size() <= 1) continue;
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				for (Integer serviceId: communities) {									
					serviceCommunity.addService(services.stream().filter (s -> s.id == serviceId).findFirst().get());
				}
				serviceCommunities.add(serviceCommunity);
			}

			double minRt = CommunityUtils.findMinRt(templateVector, services);
			double maxAv = CommunityUtils.findMaxAv(templateVector, services);

			PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));
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

	public List<ServiceCommunity> extractServicesOfTemplateVectorFromFile(String rtInputFilename, String tpInputFilename, String avInputFilename, List<Set<Integer>> templateVector, int time) {

		// Load Services
		List<Service> services = null;
		List<ServiceCommunity> serviceCommunities = new ArrayList<ServiceCommunity>();

		try {
			services = extractServicesFromFile(rtInputFilename, tpInputFilename, avInputFilename, time);

			for (Service service: services) {
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				serviceCommunity.addService(service);
				serviceCommunities.add(serviceCommunity);
			}

			for (Set<Integer> communities: templateVector) {
				if (communities.size() <= 1) continue;
				ServiceCommunity serviceCommunity = new ServiceCommunity();
				for (Integer serviceId: communities) {									
					serviceCommunity.addService(services.stream().filter (s -> s.id == serviceId).findFirst().get());
				}
				serviceCommunities.add(serviceCommunity);
			}

			double minRt = CommunityUtils.findMinRt(templateVector, services);
			double maxAv = CommunityUtils.findMaxAv(templateVector, services);

			for (ServiceCommunity sc: serviceCommunities) {
				ExternalSetter.setEx1(sc, minRt);
				ExternalSetter.setEx2(sc, maxAv);			
			}
		} catch (Exception e) {			
			e.printStackTrace();
		} 	
		
		return serviceCommunities;
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

	public List<Set<Integer>> loadTempalteVector (String filename) {

		List<Set<Integer>> templateVector = new ArrayList <Set<Integer>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;			
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				Set<Integer> serviceList = new HashSet<Integer>();
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



	public void makeServiceMatrix (String rtInputFilename, String tpInputFilename, String avInputFilename, List<Set<Integer>> templateVector, String outputfile) {

		// Load Services
		List<Service> services = null;
		List<ServiceCommunity> serviceCommunities = new ArrayList<ServiceCommunity>();

		// Load Single Services from input files
		for (int time = 0; time < Constants.MAX_TIME; time++) {
			try {
				services = extractServicesFromFile(rtInputFilename, tpInputFilename, avInputFilename, time);
				serviceCommunities = new ArrayList<ServiceCommunity>();

				for (Set<Integer> communities: templateVector) {
					ServiceCommunity serviceCommunity = new ServiceCommunity();
					for (Integer serviceId: communities) {									
						serviceCommunity.addService(services.stream().filter (s -> s.id == serviceId).findFirst().get());
					}
					serviceCommunities.add(serviceCommunity);
				}

				double minRt = CommunityUtils.findMinRt(templateVector, services);
				double maxAv = CommunityUtils.findMaxAv(templateVector, services);

				System.out.println("Size:" + serviceCommunities.size());
				for (ServiceCommunity sc: serviceCommunities) {
					ExternalSetter.setEx1(sc, minRt);
					ExternalSetter.setEx2(sc, maxAv);				
				}

				Double[][] score = new Double[serviceCommunities.size()][serviceCommunities.size()];
				PrintWriter pw=new PrintWriter(new FileOutputStream(outputfile+time));
				int i = 0;
				for (ServiceCommunity scX: serviceCommunities) {
					int j = 0;
					for (ServiceCommunity scY: serviceCommunities) {
						score[i][j] = CommunityUtils.getBenefitOfJoining(scX, scY, templateVector, minRt, maxAv);						
						if (score[i][j] != null)
							pw.printf("%.6f ", score[i][j]);
						else 
							pw.printf("X ", score[i][j]);
						j++;
					}
					i++;
					pw.println();
				}
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void run() throws IOException {
		String rawIntputFilenamePrefix = "";
		String filenamePrefix = "score_0_05_";
		String vectorFileName = "data/vector_template";
				
		//extractAverageServices("../wsdsinput/rtRate","data/"+rawIntputFilenamePrefix+"service_rt_t", 0.0);
		//extractAverageServices("../wsdsinput/tpRate","data/"+rawIntputFilenamePrefix+"service_tp_t", 20.0);
		//wsCount("../wsdsinput/tpRate");
		//extractAvailability("data/"+rawIntputFilenamePrefix+"service_rt_t","data/"+rawIntputFilenamePrefix+"service_av_t");		

		//generateTemplateVector("data/"+rawIntputFilenamePrefix+"service_rt_t", "data/"+rawIntputFilenamePrefix+"service_tp_t", "data/"+rawIntputFilenamePrefix+"service_av_t", vectorFileName);
		List<Set<Integer>> templateVector = loadTempalteVector(vectorFileName);		

		//makeServiceVector("data/"+rawIntputFilenamePrefix+"service_rt_t", "data/"+rawIntputFilenamePrefix+"service_tp_t", "data/"+rawIntputFilenamePrefix+"service_av_t", templateVector, "data/"+filenamePrefix+"vector_t");
		//makeServiceMatrix("data/"+rawIntputFilenamePrefix+"service_rt_t", "data/"+rawIntputFilenamePrefix+"service_tp_t", "data/"+rawIntputFilenamePrefix+"service_av_t", templateVector, "data/"+filenamePrefix+"matrix_t");

		List<ServiceCommunity> serviceCommunityList = 
				extractServicesOfTemplateVectorFromFile("data/"+rawIntputFilenamePrefix+"service_rt_t", "data/"+rawIntputFilenamePrefix+"service_tp_t", "data/"+rawIntputFilenamePrefix+"service_av_t", templateVector, 0);
		
		List<Node<Set<Integer>>> tree5 = new SolutionOne().run(templateVector, serviceCommunityList, 5, "data/"+filenamePrefix+"matrix_t","data/"+filenamePrefix+"output_communities_time_"+5);		
		List<Node<Set<Integer>>> tree10 = new SolutionOne().run(templateVector, serviceCommunityList, 10, "data/"+filenamePrefix+"matrix_t","data/"+filenamePrefix+"output_communities_time_"+10);
		List<Node<Set<Integer>>> tree15 = new SolutionOne().run(templateVector, serviceCommunityList, 15, "data/"+filenamePrefix+"matrix_t","data/"+filenamePrefix+"output_communities_time_"+15);
		List<Node<Set<Integer>>> tree20 = new SolutionOne().run(templateVector, serviceCommunityList, 20, "data/"+filenamePrefix+"matrix_t","data/"+filenamePrefix+"output_communities_time_"+20);
	
		List<ServiceCommunity> testServiceCommunityList = new SolutionOneEvaluation().getTestServiceList(serviceCommunityList);
		double coef = 150;
		//for (coef = 300; coef > 50; coef -= 25) {			
			new SolutionOneEvaluation().run(templateVector, serviceCommunityList, tree5, testServiceCommunityList, coef, "data/"+filenamePrefix+"_output_eval_5_"+coef);		
			new SolutionOneEvaluation().run(templateVector, serviceCommunityList, tree10, testServiceCommunityList, coef, "data/"+filenamePrefix+"_output_eval_10_"+coef);
			new SolutionOneEvaluation().run(templateVector, serviceCommunityList, tree15, testServiceCommunityList, coef, "data/"+filenamePrefix+"_output_eval_15_"+coef);
			new SolutionOneEvaluation().run(templateVector, serviceCommunityList, tree20, testServiceCommunityList, coef, "data/"+filenamePrefix+"_output_eval_20_"+coef);
		//}

		
	}


}
