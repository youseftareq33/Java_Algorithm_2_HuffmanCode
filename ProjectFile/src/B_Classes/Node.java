package B_Classes;

public class Node implements Comparable<Node> {

    char ch;
    int freq;
    boolean isLeaf;

    Node right,left;

    public Node() {

    }

    public Node(char ch,int freq,boolean isLeaf) {
        left = right = null;
        this.ch = ch;
        this.freq = freq;
        this.isLeaf = isLeaf;
    }

    public int compareTo(Node t) {
        if (t.freq > this.freq)
            return -1;
        else if (t.freq < this.freq)
            return 1;
        else
            return 0;
    }


}

