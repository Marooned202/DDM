package com.ehsan.wsds.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
	
	private static <T> void getSubsets(List<T> superSet, int k, int idx, Set<T> current,List<Set<T>> solution) {
		//successful stop clause
		if (current.size() == k) {
			solution.add(new HashSet<>(current));
			return;
		}
		//unseccessful stop clause
		if (idx == superSet.size()) return;
		T x = superSet.get(idx);
		current.add(x);
		//"guess" x is in the subset
		getSubsets(superSet, k, idx+1, current, solution);
		current.remove(x);
		//"guess" x is not in the subset
		getSubsets(superSet, k, idx+1, current, solution);
	}
	
	public static <T> List<Set<T>> getSubsets(List<T> superSet, int k) {
		List<Set<T>> res = new ArrayList<>();
		getSubsets(superSet, k, 0, new HashSet<T>(), res);
		return res;
	}


	public static <T> List<List<T>> powerSet(List<T> originalList) {
		List<List<T>> Lists = new ArrayList<List<T>>();
		if (originalList.isEmpty()) {
			Lists.add(new ArrayList<T>());
			return Lists;
		}
		List<T> list = new ArrayList<T>(originalList);
		T head = list.get(0);
		List<T> rest = new ArrayList<T>(list.subList(1, list.size())); 
		for (List<T> List : powerSet(rest)) {
			List<T> newList = new ArrayList<T>();
			newList.add(head);
			newList.addAll(List);
			Lists.add(newList);
			Lists.add(List);
		}		
		return Lists;
	}
}
