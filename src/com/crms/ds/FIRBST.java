package com.crms.ds;

import com.crms.model.FIR;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
public class FIRBST {
    private Node root;

    private static class Node {
        FIR fir;
        Node left, right;

        Node(FIR fir) {
            this.fir = fir;
            left = right = null;
        }
    }

    // ---------- Insert ----------
    public void insert(FIR fir) {
        if (fir == null || fir.getFirNumber() == null) return;
        root = insertRec(root, fir);
    }

    private Node insertRec(Node root, FIR fir) {
        if (root == null) {
            return new Node(fir);
        }
        int cmp = fir.getFirNumber().compareToIgnoreCase(root.fir.getFirNumber());
        if (cmp < 0) {
            root.left = insertRec(root.left, fir);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, fir);
        } else {
            // Duplicate FIR number – update existing (optional)
            root.fir = fir;
        }
        return root;
    }

    private FIR searchByNumberRec(Node root, String firNumber) {
        if (root == null) return null;
        int cmp = firNumber.compareToIgnoreCase(root.fir.getFirNumber());
        if (cmp == 0) {
            return root.fir;
        } else if (cmp < 0) {
            return searchByNumberRec(root.left, firNumber);
        } else {
            return searchByNumberRec(root.right, firNumber);
        }
    }

    // ---------- Filtered Search ----------
    public LinkedList search(String status, String complainant, String fromDate, String toDate) {
        LinkedList result = new LinkedList();
        searchRec(root, result, status, complainant, fromDate, toDate);
        return result;
    }

    private void searchRec(Node node, LinkedList result,
                           String status, String complainant,
                           String fromDate, String toDate) {
        if (node == null) return;

        FIR f = node.fir;
        boolean matches = true;

        if (status != null && !status.isEmpty()) {
            if (!status.equalsIgnoreCase(f.getStatus())) matches = false;
        }

        if (complainant != null && !complainant.isEmpty()) {
            if (f.getComplainantName() == null ||
                    !f.getComplainantName().toLowerCase().contains(complainant.toLowerCase())) {
                matches = false;
            }
        }

        // --- Date filters using dd-MM-yyyy format ---
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (fromDate != null && !fromDate.isEmpty()) {
            LocalDate from = LocalDate.parse(fromDate, formatter);
            LocalDateTime fromDateTime = from.atStartOfDay(); // 00:00:00
            if (f.getIncidentDate().isBefore(fromDateTime)) matches = false;
        }

        if (toDate != null && !toDate.isEmpty()) {
            LocalDate to = LocalDate.parse(toDate, formatter);
            LocalDateTime toDateTime = to.atTime(23, 59, 59); // end of day
            if (f.getIncidentDate().isAfter(toDateTime)) matches = false;
        }

        if (matches) {
            result.add(f);
        }

        // Inorder traversal
        searchRec(node.left, result, status, complainant, fromDate, toDate);
        searchRec(node.right, result, status, complainant, fromDate, toDate);
    }

    private void inorderRec(Node node, LinkedList list) {
        if (node != null) {
            inorderRec(node.left, list);
            list.add(node.fir);
            inorderRec(node.right, list);
        }
    }

    private void searchByOfficerIdRec(Node node, LinkedList result, int officerId) {
        if (node == null) return;

        // Inorder traversal – visit left, current, right
        searchByOfficerIdRec(node.left, result, officerId);

        FIR fir = node.fir;
        Integer assignedId = fir.getAssignedOfficerId();
        if (assignedId != null && assignedId == officerId) {
            result.add(fir);
        }

        searchByOfficerIdRec(node.right, result, officerId);
    }

    public FIR searchById(int id) {
        return searchByIdRec(root, id);
    }

    private FIR searchByIdRec(Node node, int id) {
        if (node == null) return null;

        // Inorder traversal: left -> current -> right
        FIR found = searchByIdRec(node.left, id);
        if (found != null) return found;

        if (node.fir.getId() == id) {
            return node.fir;
        }

        return searchByIdRec(node.right, id);
    }
}