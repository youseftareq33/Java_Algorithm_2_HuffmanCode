package B_Classes;

import java.util.Hashtable;
import java.util.PriorityQueue;

public class HuffmanCode {

    public Hashtable<Character,Code> codes = new Hashtable<>();

    public void generateCodes(int[] rep) {
        Node left,right,top;

        PriorityQueue<Node> minHeap = new PriorityQueue<>();

        for (int i = 0 ; i < rep.length ; i++)  {
            if (rep[i] > 0) {
                minHeap.add(new Node((char)i,rep[i],true));

            }
        }

        while (minHeap.size() != 1) {
            left = minHeap.poll();
            right = minHeap.poll();

            if (left == null)
                left = new Node();
            if (right == null)
                right = new Node();

            top = new Node('\0',left.freq + right.freq , false);
            top.left = left;
            top.right = right;

            minHeap.add(top);

        }

        getCodes(minHeap.peek(), "",rep);

    }

    int count = 0;
    void getCodes(Node root, String str,int[] rep)
    {
        if (root == null)
            return;

        if (root.isLeaf) {
            codes.put(root.ch,new Code(root.ch,str,rep[(int)root.ch]));
            count++;

        }

        getCodes(root.left, str + "0",rep);
        getCodes(root.right, str + "1",rep);
    }

}

