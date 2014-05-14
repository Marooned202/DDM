package com.ehsan.wsds;

import java.io.IOException;

import com.ehsan.wsds.tree.Node;
import com.ehsan.wsds.tree.Tree;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args ) throws IOException
    {
        //new ServiceDataSet().run();
    	
    	Node<Integer> node1 = new Node<Integer>(1);
    	Node<Integer> node2 = new Node<Integer>(2);
    	Node<Integer> node10 = new Node<Integer>(10);
    	node10.addChild(node1);
    	node10.addChild(node2);
    	
    	Tree<Integer> tree = new Tree<Integer>(node10);
    	for (Node<Integer> i : tree.getPreOrderTraversal()) {
    		System.out.println(i);
    	}
    	System.out.println("Root: " + tree.getRoot());
    }
}
