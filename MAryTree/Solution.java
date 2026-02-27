import java.io.*;
import java.util.*;

class FastReader 
{
    BufferedReader br;
    StringTokenizer st;
    public FastReader() 
    {
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    String next() 
    {
        while (st == null || !st.hasMoreElements()) 
        {
            try 
            {
                st = new StringTokenizer(br.readLine());
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    int nextInt() 
    {
        return Integer.parseInt(next());
    }
}

public class Solution 
{
    public static List<Boolean> handleActions() 
    {
        FastReader sc = new FastReader();
        // Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int q = sc.nextInt();

        Tree tree = new Tree(m);
        tree.insertBfs(m, n, sc);

        int[] ops = new int[q];
        String[] names = new String[q];
        int[] uids = new int[q];
        
        for (int i = 0; i < q; i++) 
        {
            ops[i] = sc.nextInt();
            names[i] = sc.next();
            uids[i] = sc.nextInt();
        }

        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < q; i++) 
        {
            switch (ops[i]) 
            {
                case 1:
                list.add(tree.lock(names[i], uids[i]));
                break;
                case 2:
                list.add(tree.unlock(names[i], uids[i]));
                break;
                case 3:
                list.add(tree.upgrade(names[i], uids[i]));
                break;
                default:
                list.add(false);
                break;
            }
        }
        return list;
    }

    public static void main(String[] args) 
    {
        List<Boolean> result = handleActions();
        for (Boolean b : result) 
        {
            System.out.println(b);
        }
    }
}

class Tree 
{
    public Node head;
    public Map<String, Node> map;

    static class Node 
    {
        public static int degree;
        public String name;
        public Node parent;
        public List<Node> children;
        public boolean locked;
        public int lockedBy;
        public int noOfLockedDescendents;

        Node(String name, Node parent) 
        {
            this.name = name;
            this.parent = parent;
            locked = false;
            lockedBy = -1;
            noOfLockedDescendents = 0;
            children = new ArrayList<>(degree);
        }
    }

    Tree(int m) 
    {
        Node.degree = m;
        map = new HashMap<>();
    }

    public void insertBfs(int m, int n, FastReader sc) 
    {
        List<String> names = new ArrayList<>(n);
        for (int i = 0; i < n; i++) 
        {
            names.add(sc.next());
        }
        this.head = new Node(names.get(0), null);
        map.put(this.head.name, this.head);
        Queue<Node> qu = new ArrayDeque<>(n);
        qu.offer(this.head);
        int index = 1;
        while (!qu.isEmpty() && index < n) 
        {
            Node par = qu.poll();
            for (int i = 0; i < m && index < n; i++) 
            {
                Node child = new Node(names.get(index), par);
                par.children.add(child);
                map.put(child.name, child);
                qu.offer(child);
                index++;
            }
        }
    }

    public boolean checkForAncestors(Node parent) 
    {
        while (parent != null) 
        {
            if (parent.locked) return true;
            parent = parent.parent;
        }
            return false;
    }

    public void updateAncestors(Node parent, int cnt) 
    {
        while (parent != null) 
        {
            parent.noOfLockedDescendents += cnt;
            parent = parent.parent;
        }
    }

    public boolean lock(String name, int uid) 
    {
        Node node = map.get(name);
        if (node == null || node.locked || node.noOfLockedDescendents > 0 || checkForAncestors(node.parent))
        return false;
        node.locked = true;
        node.lockedBy = uid;
        updateAncestors(node.parent, 1);
        return true;
    }

    public boolean unlock(String name, int uid) 
    {
        Node node = map.get(name);
        if (node == null || !node.locked || node.lockedBy != uid) return false;
        node.locked = false;
        node.lockedBy = -1;
        updateAncestors(node.parent, -1);
        return true;
    }

    public boolean upgrade(String name, int uid) 
    {
        Node node = map.get(name);
        if (node == null || node.locked || checkForAncestors(node.parent) || node.noOfLockedDescendents <= 0)
        return false;

        List<Node> lockedList = new ArrayList<>();
        if (!checkForChildren(node, uid, lockedList)) return false;

        for (Node nd : lockedList) 
        {
            nd.locked = false;
            nd.lockedBy = -1;
            updateAncestors(nd.parent, -1);
        }

            node.locked = true;
            node.lockedBy = uid;
            updateAncestors(node.parent, 1);
            return true;
    }

    public boolean checkForChildren(Node node, int uid, List<Node> list) {
        if (node.locked) 
        {
            if (node.lockedBy == uid) list.add(node);
            else return false;
        }
        for (Node child : node.children) 
        {
            if (child.noOfLockedDescendents > 0 || child.locked) 
            {
                if (!checkForChildren(child, uid, list)) return false;
            }
        }
        return true;
    }
}
