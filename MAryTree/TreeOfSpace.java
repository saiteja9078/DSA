import java.util.*;

public class TreeOfSpace
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int q = sc.nextInt();
        sc.nextLine();
        MTree mtree = new MTree(m);
        mtree.insertBfs(n,m,sc);

        int[] qs= new int[q];
        String[] names = new String[q];
        int[] ids = new int[q];
        for(int i=0;i<q;i++)
        {
            qs[i] = sc.nextInt();
            names[i] = sc.next();
            ids[i] = sc.nextInt();
        }
        for(int i=0;i<q;i++)
        {
            switch (qs[i])
            {
                case 1:
                    System.out.println(mtree.lock(names[i],ids[i]));
                    break;
                case 2:
                    System.out.println(mtree.unlock(names[i],ids[i]));
                    break;
                case 3:
                    System.out.println(mtree.upgrade(names[i],ids[i]));
                    break;
                default:
                    System.out.println("default case: "+ false);
                    break;
            }
        }
    }
}
class MTree
{
    private static class Node
    {
        public static int degree;
        public String name;
        public List<Node> children;
        public Node parent;

        public boolean locked;
        public int lockedBy;
        public int noOfLockedDescendents;

        Node(String name,Node parent)
        {
            this.name = name;
            this.parent = parent;
            this.children = new ArrayList<>(degree);
            this.locked = false;
            this.lockedBy = -1;
            this.noOfLockedDescendents = 0;
        }
    }
    private Node head;
    private Map<String,Node> map;
    MTree(int m)
    {
        Node.degree = m ;
        map = new HashMap<>();
    }
    public void insertBfs(int n, int m, Scanner sc)
    {
        List<String> names = new ArrayList<String>();
            for(int i=0;i<n;i++) names.add(sc.nextLine().strip());
        this.head = new Node(names.get(0),null);
        map.put(head.name,head);
        Queue<Node> qu = new ArrayDeque<>(n);
        qu.offer(head);
        int index = 1;
        while(!qu.isEmpty() && index<n)
        {
            Node par = qu.poll();
            for(int i=0;i<m && index<n; i++)
            {
                Node child= new Node(names.get(index),par);
                map.put(child.name,child);
                par.children.add(child);
                qu.offer(child);
                index++;
            }
        }
    }
    public boolean lock(String name,int uid)
    {
        if(!map.containsKey(name)) return false;
        Node node = map.get(name);
        if(node.locked || node.noOfLockedDescendents>0 || checkAncestors(node.parent)) return false;
        node.locked = true;
        node.lockedBy = uid;
        updateAncestors(node.parent,1);
        return true;
    }
    public boolean unlock(String name,int uid)
    {
        if(!map.containsKey(name)) return false;
        Node node = map.get(name);
        if(!node.locked || node.lockedBy!=uid) return false;
        node.locked = false;
        node.lockedBy = -1;
        updateAncestors(node.parent,-1);
        return true;
    }
    public boolean upgrade(String name,int uid)
    {
        if(!map.containsKey(name)) return false;
        Node node = map.get(name);
        List<Node> children = new ArrayList<>();
        if(            node.locked ||
                        !checkChildren(node,uid,children) ||
                        node.noOfLockedDescendents<=0 ||
                        checkAncestors(node.parent)) return false;

        for(Node child:children)
            unlock(child.name,uid);
        return lock(node.name,uid);
    }
    private boolean checkChildren(Node node, int uid, List<Node> children)
    {
        if(node.locked)
        {
            if (node.lockedBy == uid) children.add(node);
            else return false;
        }
        if(node.noOfLockedDescendents <= 0) return true;
        for(Node child:node.children)
        {
            if(!checkChildren(child,uid,children)) return false;
        }
        return true;
    }

    private void updateAncestors(Node parent, int i)
    {
        while(parent!=null)
        {
            parent.noOfLockedDescendents+=i;
            parent = parent.parent;
        }
    }

    private boolean checkAncestors(Node parent)
    {
        while(parent!=null)
        {
            if(parent.locked) return true;
            parent = parent.parent;
        }
        return false;
    }
}
