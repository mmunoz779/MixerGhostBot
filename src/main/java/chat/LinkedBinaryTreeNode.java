package chat;

import java.util.ArrayList;

public class LinkedBinaryTreeNode<E> implements BinaryTreeNode<E> {
    private E data = null;

    private LinkedBinaryTreeNode<E> parent = null;
    private LinkedBinaryTreeNode<E> leftChild = null;
    private LinkedBinaryTreeNode<E> rightChild = null;

    public LinkedBinaryTreeNode(E data) {
        this.data = data;
    }

    /**
     * Returns the data stored in this node.
     */
    @Override
    public E getData() {
        return data;
    }

    /**
     * Modifies the data stored in this node.
     *
     * @param data
     */
    @Override
    public void setData(E data) {
        this.data = data;
    }

    /**
     * Returns true if the node has any children.
     * Otherwise, returns false.
     */
    @Override
    public boolean isParent() {
        if (hasLeftChild() || hasRightChild())
            return true;
        return false;
    }

    /**
     * Returns true if the node is childless.
     * Otherwise, returns false.
     */
    @Override
    public boolean isLeaf() {
        if (!hasLeftChild() && !hasRightChild())
            return true;
        return false;
    }

    /**
     * Returns true if the node has a left child
     */
    @Override
    public boolean hasLeftChild() {
        return leftChild != null;
    }

    /**
     * Returns true if the node has a right child
     */
    @Override
    public boolean hasRightChild() {
        return rightChild != null;
    }

    /**
     * Returns the path from this node to the specified descendant.
     * If no path exists, returns an empty list.
     *
     * @param descendant
     */
    @Override
    public ArrayList<BinaryTreeNode<E>> pathTo(BinaryTreeNode<E> descendant) {
        ArrayList<BinaryTreeNode<E>> path = descendant.pathFrom(this);

        for (int i = 0; i < path.size() / 2; i++) {
            BinaryTreeNode<E> temp = path.get(i);
            path.set(i, path.get(path.size() - 1 - i));
            path.set(path.size() - 1 - i, temp);
        }
        return path;
    }

    /**
     * Returns the path to this node from the specified ancestor.
     * If no path exists, returns an empty list.
     *
     * @param ancestor
     */
    @Override
    public ArrayList<BinaryTreeNode<E>> pathFrom(BinaryTreeNode<E> ancestor) {
        ArrayList<BinaryTreeNode<E>> path = new ArrayList<>();

        BinaryTreeNode<E> current = this;
        do {
            path.add(current);
            if (current == ancestor) {
                break;
            }
            current = current.getParent();
        } while (current != null);

        if (current == null) {
            path.clear();
        }

        return path;
    }

    /**
     * Returns the number of edges in the path from the root to this node.
     */
    @Override
    public int getDepth() {
        return getRoot().pathTo(this).size() - 1;
    }

    /**
     * Returns the number of edges in the path from the root
     * to the node with the maximum depth.
     */
    @Override
    public int getHeight() {
        final int maxDepth[] = {0};
        traversePreorder(node -> maxDepth[0] = Math.max(node.getDepth(), maxDepth[0]));
        return maxDepth[0];
    }

    @Override
    public int size() {
        final int[] size = {1};
        traverseInorder(e -> size[0]++);
        return size[0];
    }

    /**
     * Returns the ancestor of this node that has no parent,
     * or returns this node if it is the root.
     */
    @Override
    public BinaryTreeNode<E> getRoot() {
        if (getParent() == null) {
            return this;
        }
        return getParent().getRoot();
    }

    /**
     * Returns the parent of this node, or null if this node is a root.
     */
    @Override
    public BinaryTreeNode<E> getParent() {
        return parent;
    }

    /**
     * set the parent of this node
     */

    public void setParent(BinaryTreeNode<E> parent) {
        this.parent = (LinkedBinaryTreeNode<E>) parent;
    }

    /**
     * Returns the left child of this node, or null if it does
     * not have one.
     */
    @Override
    public BinaryTreeNode<E> getLeft() {
        return leftChild;
    }

    public BinaryTreeNode<E> findNode(E data) {
        ArrayList<BinaryTreeNode<E>> list = new ArrayList<>();
        getRoot().traversePreorder(node -> {
            if (data.equals(node.getData())) {
                list.add(node);
            }
        });
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    // findNode( null )

    /**
     * Removes child from its current parent and inserts it as the
     * left child of this node.  If this node already has a left
     * child it is removed.
     *
     * @param child
     * @throws IllegalArgumentException if the child is
     *                                  an ancestor of this node, since that would make
     *                                  a cycle in the tree.
     */
    @Override
    public void setLeft(BinaryTreeNode<E> child) {
        leftChild = (LinkedBinaryTreeNode<E>) child;
        if (hasLeftChild())
            leftChild.setParent(this);
    }

    /**
     * Returns the right child of this node, or null if it does
     * not have one.
     */
    @Override
    public BinaryTreeNode<E> getRight() {
        return rightChild;
    }

    /**
     * Removes child from its current parent and inserts it as the
     * right child of this node.  If this node already has a right
     * child it is removed.
     *
     * @param child
     * @throws IllegalArgumentException if the child is
     *                                  an ancestor of this node, since that would make
     *                                  a cycle in the tree.
     */
    @Override
    public void setRight(BinaryTreeNode<E> child) {
        rightChild = (LinkedBinaryTreeNode<E>) child;
        if (hasRightChild())
            rightChild.setParent(this);
    }

    /**
     * Removes this node, and all its descendants, from whatever
     * tree it is in.  Does nothing if this node is a root.
     */
    @Override
    public void removeFromParent() {
        BinaryTreeNode<E> p = getParent();
        if (this == p.getLeft()) {
            p.setLeft(null);
        } else {
            p.setRight(null);
        }
        setParent(null);
    }

    /**
     * Visits the nodes in this tree in preorder.
     *
     * @param visitor
     */
    @Override
    public void traversePreorder(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException();
        }
        visitor.visit(this);
        if (hasLeftChild()) {
            leftChild.traversePreorder(visitor);
        }
        if (hasRightChild()) {
            rightChild.traversePreorder(visitor);
        }
    }

    /**
     * Visits the nodes in this tree in postorder.
     *
     * @param visitor
     */
    @Override
    public void traversePostorder(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException();
        }
        if (hasLeftChild()) {
            leftChild.traversePostorder(visitor);
        }
        if (hasRightChild()) {
            rightChild.traversePostorder(visitor);
        }
        visitor.visit(this);
    }

    /**
     * Visits the nodes in this tree in inorder.
     *
     * @param visitor
     */
    @Override
    public void traverseInorder(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException();
        }
        if (hasLeftChild()) {
            leftChild.traverseInorder(visitor);
        }
        visitor.visit(this);
        if (hasRightChild()) {
            rightChild.traverseInorder(visitor);
        }
    }

    public String toString() {
        return String.format("<chat.BinaryTreeNode: %s, %s, %s>", data, leftChild == null ? null : leftChild.getData(), rightChild == null ? null : rightChild.getData());
    }

    public static void main(String[] args) {
        LinkedBinaryTreeNode<Integer> node1 = new LinkedBinaryTreeNode<>(1);
        LinkedBinaryTreeNode<Integer> node2 = new LinkedBinaryTreeNode<>(2);
        LinkedBinaryTreeNode<Integer> node3 = new LinkedBinaryTreeNode<>(3);
        LinkedBinaryTreeNode<Integer> node4 = new LinkedBinaryTreeNode<>(4);
        LinkedBinaryTreeNode<Integer> node5 = new LinkedBinaryTreeNode<>(5);

        node1.setLeft(node2);
        node1.setRight(node3);
        System.out.println(node1);

        node2.setLeft(node4);
        node2.setRight(node5);
    }
}
