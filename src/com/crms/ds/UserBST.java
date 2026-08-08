package com.crms.ds;

import com.crms.model.User;

import java.util.LinkedList;

public class UserBST {
    private Node root;

    private static class Node {
        User user;
        Node left, right;

        Node(User user) {
            this.user = user;
            left = right = null;
        }
    }

    // ---------- Insert ----------
    public void insert(User user) {
        if (user == null || user.getUsername() == null) return;
        root = insertRec(root, user);
    }

    private Node insertRec(Node root, User user) {
        if (root == null) {
            return new Node(user);
        }
        int cmp = user.getUsername().compareToIgnoreCase(root.user.getUsername());
        if (cmp < 0) {
            root.left = insertRec(root.left, user);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, user);
        } else {
            // Duplicate username – update the node (optional)
            root.user = user;
        }
        return root;
    }

    private User searchByUsernameRec(Node root, String username) {
        if (root == null) return null;
        int cmp = username.compareToIgnoreCase(root.user.getUsername());
        if (cmp == 0) {
            return root.user;
        } else if (cmp < 0) {
            return searchByUsernameRec(root.left, username);
        } else {
            return searchByUsernameRec(root.right, username);
        }
    }

    // ---------- Search by Role (collects all users with matching role) ----------
    public LinkedList searchByRole(String role) {
        LinkedList result = new LinkedList();
        searchByRoleRec(root, result, role);
        return result;
    }

    private void searchByRoleRec(Node node, LinkedList result, String role) {
        if (node == null) return;

        // Inorder traversal: left -> current -> right
        searchByRoleRec(node.left, result, role);

        User u = node.user;
        if (u.getRole() != null && u.getRole().equalsIgnoreCase(role)) {
            result.add(u);
        }

        searchByRoleRec(node.right, result, role);
    }

    private void inorderRec(Node node, LinkedList list) {
        if (node != null) {
            inorderRec(node.left, list);
            list.add(node.user);
            inorderRec(node.right, list);
        }
    }

}