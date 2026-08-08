package com.crms.ds;

import com.crms.model.Investigation;

import java.util.LinkedList;

public class InvestigationBST {
    private Node root;

    private static class Node {
        Investigation inv;
        Node left, right;

        Node(Investigation inv) {
            this.inv = inv;
            left = right = null;
        }
    }

    // ---------- Insert ----------
    public void insert(Investigation inv) {
        if (inv == null || inv.getFirNumber() == null) return;
        root = insertRec(root, inv);
    }

    private Node insertRec(Node root, Investigation inv) {
        if (root == null) {
            return new Node(inv);
        }
        int cmp = inv.getFirNumber().compareToIgnoreCase(root.inv.getFirNumber());
        if (cmp < 0) {
            root.left = insertRec(root.left, inv);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, inv);
        } else {
            // Duplicate FIR number – update the node (optional)
            root.inv = inv;
        }
        return root;
    }


    private Investigation searchRec(Node root, String firNumber) {
        if (root == null) return null;
        int cmp = firNumber.compareToIgnoreCase(root.inv.getFirNumber());
        if (cmp == 0) {
            return root.inv;
        } else if (cmp < 0) {
            return searchRec(root.left, firNumber);
        } else {
            return searchRec(root.right, firNumber);
        }
    }

    private void inorderRec(Node node, LinkedList list) {
        if (node != null) {
            inorderRec(node.left, list);
            list.add(node.inv);
            inorderRec(node.right, list);
        }
    }

    public boolean isEmpty() {
        return root == null;
    }
}