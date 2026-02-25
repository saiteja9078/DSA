import com.sun.source.tree.Tree;

import java.sql.ClientInfoStatus;
import java.util.*;
public class tree
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int q = sc.nextInt();
        MaryTree tree = new MaryTree(m);
        tree.buildTree(n,m,sc);
        for(int i=0;i<q;i++)
        {
            String line = sc.nextLine().strip();
            while (line.isEmpty() && sc.hasNextLine()) {
                line = sc.nextLine().trim();
            }
            String[] parts = line.split("\\s+");
            if(parts.length!=3)
            {
                System.out.println("false");
                continue;
            }

            int op = Integer.parseInt(parts[0]);
            String name = parts[1];
            int uid = Integer.parseInt(parts[2]);

            boolean res = false;

            switch (op)
            {
                case 1:
                    res = tree.lock(name,uid);
                    break;
                case 2:
                    res = tree.unlock(name,uid);
                    break;
                case 3:
                    res =tree.upgrade(name,uid);
                    break;
                default:
                    res = false;
                    break;
            }
            System.out.println(res ? "true" : "false");

        }
        sc.close();
    }
}
class MaryTree
{
    Map<String, TreeNode> map = new HashMap<>();
    public TreeNode head;
    MaryTree(int degree)
    {
        TreeNode.degree = degree;
    }

    public String delete(String val)
    {
        if(head==null || !map.containsKey(val)) return "Not present";
        if(val == head.name)
        {
            deleteSubTree(head);
            String va = head.name;
            head=null;
            return va;
        }
            TreeNode child = map.get(val);
            TreeNode parent = child.parent;
            parent.children.remove(child);
            deleteSubTree(child);
            return child.name;
    }
    private void deleteSubTree(TreeNode node)
    {
        for(TreeNode no: node.children)
        {
            deleteSubTree(no);
        }
        map.remove(node.name);
    }
    public void dfs()
    {
        dfs(head);
    }
    private void dfs(TreeNode head)
    {
        if(head==null) return;
        System.out.print(head.name+" ");
        for(TreeNode node: head.children)
        {
            dfs(node);
        }
    }
    public void buildTree(int n, int m,Scanner sc)
    {
        List<String> nodes = new ArrayList<>(n);
        for(int i=0;i<n;i++)
        {
            nodes.add(sc.next());
        }
        if(nodes.isEmpty()) return;
        this.head = new TreeNode(nodes.get(0),null);
        map.put(head.name,head);

        Queue<TreeNode> qu = new ArrayDeque<>();
        qu.offer(head);
        int index = 1;

        while(!qu.isEmpty() && index<n)
        {
            TreeNode parent = qu.poll();
            for(int i=0;i<m && index<n; i++)
            {
                TreeNode child = new TreeNode(nodes.get(index),parent);
                map.put(child.name,child);
                qu.add(child);
                parent.children.add(child);
                index++;
            }
        }
    }
    public boolean lock(String name,int uid)
    {
        if(!map.containsKey(name)) return false;
        TreeNode node = map.get(name);
        if(node.isLocked || node.lockedDescendantCount>0 || ancestorsLocked(node.parent)) return false;
        node.isLocked = true;
        node.lockedBy = uid;
        incrementAncestors(node.parent);
        return true;
    }
    public boolean unlock(String name,int uid)
    {
        if(!map.containsKey(name)) return false;
        TreeNode node = map.get(name);
        if(!node.isLocked || node.lockedBy!=uid) return false;
        node.isLocked = false;
        node.lockedBy = -1;
        decrementAncestors(node.parent);
        return true;
    }

    private void decrementAncestors(TreeNode parent)
    {
        while (parent!=null)
        {
            parent.lockedDescendantCount--;
            parent = parent.parent;
        }
    }
    public boolean upgrade(String name,int uid)
    {
        if(!map.containsKey(name)) return false;
        TreeNode node = map.get(name);
        if(node.isLocked || ancestorsLocked(node.parent) ||  node.lockedDescendantCount == 0) return false;

        List<TreeNode> children = new ArrayList<>();

        if(!checkUpgrade(node,uid,children)) return false;
        for(TreeNode child:children)
            unlock(child.name,uid);
        lock(node.name,uid);
        return true;
    }

    private void unlockDescendents(TreeNode node,int uid)
    {
        for (TreeNode child:node.children)
        {
            unlockDescendents(child,uid);
        }
        if (node.isLocked)unlock(node.name,uid);
    }

    private boolean checkUpgrade(TreeNode node,int uid,List<TreeNode> nodes)
    {
        for (TreeNode child: node.children)
        {
            if(!checkUpgrade(child,uid,nodes))
                return false;
        }
        if(node.isLocked)
        {
            if(node.lockedBy != uid) return false;
            nodes.add(node);
        }
        return true;
    }

    private boolean ancestorsLocked(TreeNode parent)
    {
        TreeNode cur = parent;
        while (cur!=null)
        {
            if (cur.isLocked) return true;
            cur = cur.parent;
        }
        return false;
    }

    private void incrementAncestors(TreeNode node)
    {
        while (node!=null)
        {
            node.lockedDescendantCount++;
            node = node.parent;
        }
    }
}
class TreeNode
{
    static int degree;
    public String name;
    public TreeNode parent;
    public List<TreeNode> children;
    public boolean isLocked = false;
    public int lockedDescendantCount = 0;
    public int lockedBy;
    public TreeNode(String val, TreeNode parent)
    {
        this.name = val;
        this.parent = parent;
        this.children = new ArrayList<>(degree);
        lockedBy = -1;
    }
}
