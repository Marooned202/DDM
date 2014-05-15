package com.ehsan.wsds.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ehsan.wsds.ServiceCommunity;
import com.ehsan.wsds.tree.Node;

public class TreeNodeUtils {

	@SuppressWarnings("unchecked")
	public static <T> void printTree (Node<T> node, int level, List<ServiceCommunity> serviceCommunityList, PrintWriter pw) {
		if (node == null) return;
		for (int i = 0; i < level-1; i++) {
			System.out.print("|  ");
			pw.print("|  ");
		}
		if (level > 0) {
			System.out.print("|- ");
			pw.print("|- ");
		}
		System.out.println(CommunityUtils.findServiceCommunity((Set<Integer>)node.getData(), serviceCommunityList));
		pw.println(CommunityUtils.findServiceCommunity((Set<Integer>)node.getData(), serviceCommunityList));
		for (Node<T> children : node.getChildren()) {
			printTree(children, level+1, serviceCommunityList, pw);
		}				
	}
	
	public static List<Node<Set<Integer>>> extractNodes (List<Set<Integer>> vector) {
		List<Node<Set<Integer>>> nodes = new ArrayList<Node<Set<Integer>>>();

		for (Set<Integer> community: vector) {
			if (community.size() == 1) {
				HashSet<Integer> newCommunity = new HashSet<Integer>();
				newCommunity.add(community.iterator().next());
				//singleServices.add(newCommunity);
				Node<Set<Integer>> node = new Node<Set<Integer>>(newCommunity);
				nodes.add(node);
			}
		}

		return nodes;
	}

	public static Node<Set<Integer>> findNode(List<Node<Set<Integer>>> nodes, Set<Integer> serviceGroup) {
		for (Node<Set<Integer>> node: nodes) {
			if (node.getData().equals(serviceGroup)) return node;
		}
		return null;
	}
}
