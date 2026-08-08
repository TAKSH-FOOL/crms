package com.crms.ds;

import com.crms.model.Criminal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CriminalBST {
    private Node root;

    private static class Node {
        Criminal criminal;
        Node left, right;

        Node(Criminal criminal) {
            this.criminal = criminal;
            left = right = null;
        }
    }

    // ------------------ INSERT ------------------
    public void insert(Criminal criminal) {
        if (criminal == null) return;
        root = insertRec(root, criminal);
    }

    private Node insertRec(Node root, Criminal criminal) {
        if (root == null) {
            return new Node(criminal);
        }

        // Compare by full name (last name + first name)
        String newName = getFullName(criminal);
        String currentName = getFullName(root.criminal);

        int cmp = newName.compareToIgnoreCase(currentName);
        if (cmp < 0) {
            root.left = insertRec(root.left, criminal);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, criminal);
        } else {
            // Duplicate name – you can decide to update or ignore
            // Here we simply ignore duplicates (or you could update)
        }
        return root;
    }


    private Criminal searchRec(Node root, String fullName) {
        if (root == null) return null;

        String currentName = getFullName(root.criminal);
        int cmp = fullName.compareToIgnoreCase(currentName);

        if (cmp == 0) {
            return root.criminal;
        } else if (cmp < 0) {
            return searchRec(root.left, fullName);
        } else {
            return searchRec(root.right, fullName);
        }
    }

    private void inorderRec(Node root, List<Criminal> list) {
        if (root != null) {
            inorderRec(root.left, list);
            list.add(root.criminal);
            inorderRec(root.right, list);
        }
    }

    // ------------------ HELPER ------------------
    private String getFullName(Criminal c) {
        // Use last name + first name for sorting
        return (c.getLastName() + " " + c.getFirstName()).trim();
    }

    private Node deleteRec(Node root, String fullName) {
        if (root == null) return null;

        String currentName = getFullName(root.criminal);
        int cmp = fullName.compareToIgnoreCase(currentName);

        if (cmp < 0) {
            root.left = deleteRec(root.left, fullName);
        } else if (cmp > 0) {
            root.right = deleteRec(root.right, fullName);
        } else {
            // Node to delete found
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // Node with two children: get inorder successor (smallest in right subtree)
            Node minNode = findMin(root.right);
            root.criminal = minNode.criminal;
            root.right = deleteRec(root.right, getFullName(minNode.criminal));
        }
        return root;
    }

    private Node findMin(Node root) {
        while (root.left != null) root = root.left;
        return root;
    }

    // Inside CriminalBST.java
    public LinkedList<Criminal> searchCriminals(String firstName, String lastName, String wantedStatus) {
        LinkedList<Criminal> results = new LinkedList<>();
        searchRec(root, results, firstName, lastName, wantedStatus);
        return results;
    }

    private void searchRec(Node node, LinkedList<Criminal> results,
                           String firstName, String lastName, String wantedStatus) {
        if (node == null) return;

        // In-order traversal: left, current, right
        searchRec(node.left, results, firstName, lastName, wantedStatus);

        Criminal c = node.criminal;
        boolean matches = true;

        if (firstName != null && !firstName.isEmpty()) {
            if (c.getFirstName() == null || !c.getFirstName().toLowerCase().contains(firstName.toLowerCase())) {
                matches = false;
            }
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (c.getLastName() == null || !c.getLastName().toLowerCase().contains(lastName.toLowerCase())) {
                matches = false;
            }
        }
        if (wantedStatus != null && !wantedStatus.isEmpty()) {
            if (c.getWantedStatus() == null || !c.getWantedStatus().equalsIgnoreCase(wantedStatus)) {
                matches = false;
            }
        }

        if (matches) {
            results.add(c);
        }

        searchRec(node.right, results, firstName, lastName, wantedStatus);
    }
}