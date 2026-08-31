package com.crms.ds;

import com.crms.model.Investigation;

public class InvestigationBST {
    private Node root;

    private static class Node {
        Investigation investigation;
        Node left, right;
        Node(Investigation investigation) {
            this.investigation = investigation;
        }
    }


    public void insert(Investigation investigation) {
        root = insert(root, investigation);
    }

    private Node insert(Node node, Investigation investigation) {
        if (node == null) return new Node(investigation);
        int cmp = investigation.getFirNumber().compareTo(node.investigation.getFirNumber());
        if (cmp < 0) node.left = insert(node.left, investigation);
        else if (cmp > 0) node.right = insert(node.right, investigation);
        else {
            // FIR already exists – update or ignore
        }
        return node;
    }

    // Public search method – call this from outside
    public  Investigation searchByFirNumber(String firNumber) {
        return searchByFirNumber(root, firNumber);
    }

    // Private recursive helper
    private  Investigation searchByFirNumber(Node node, String firNumber) {
        if (node == null) {
            return null; // not found
        }

        int cmp = firNumber.compareTo(node.investigation.getFirNumber());
        if (cmp == 0) {
            return node.investigation; // found
        } else if (cmp < 0) {
            return searchByFirNumber(node.left, firNumber);
        } else {
            return searchByFirNumber(node.right, firNumber);
        }
    }
}