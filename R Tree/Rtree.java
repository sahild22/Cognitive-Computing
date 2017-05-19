import java.util.*;
import java.io.*;

class Rtree{
	public static void main(String[] args) {
		TreeMap<Long, Point> pointMap = new TreeMap<>();
        //Set<Long> sortedSet = new TreeSet<>();
        ArrayList<Node> rootList = new ArrayList<>();
        int c = Integer.parseInt(args[0]);
        int d = Integer.parseInt(args[1]);
        System.out.println("Enter a point:");

        Scanner s = new Scanner(System.in);
        
        int x,y,z = 0;
        int numberOfPoints = 0;
        int noOfNodes = 0;
        double numberOfNodes = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(args[2]))) {
    		String line;
    		while ((line = br.readLine()) != null) {
                numberOfPoints++;
    			//System.out.println(line);
    			String[] data = line.split(",");
    			int x1 = Integer.parseInt(data[0]);
    			int x2 = Integer.parseInt(data[1]);

                String xy = data[0] + "," + data[1];
                Point p = new Point(x1,x2);
    			long value = getHilbertValue(x1, x2);
                pointMap.put(value, p);
    			//sortedSet.add(value);
    			//System.out.print("("+ x1 + "," + x2 + ") : ");
    			//System.out.println("Hilbert Value: " + value);
    		}

            numberOfNodes = Math.ceil(numberOfPoints/c);
            noOfNodes = (int) numberOfNodes;
            //System.out.println("numberOfPoints " + numberOfPoints);
            //System.out.println("numberOfNodes " + noOfNodes);
    	}catch(Exception e){
    		System.out.println(e);
    	}
        //printMap(pointMap);

        String[] carray = new String[c];

        ArrayList<Point> sortedPoints = new ArrayList<>();
        Iterator iterator = pointMap.keySet().iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            Point value = pointMap.get(key);
            sortedPoints.add(value);
        }
        //System.out.println(sortedPoints.get(0));
        
        for(int i = 0; i < numberOfPoints; i = i+c){
            Node n1 = new Node();	
        	for(int yy = 0; yy < c; yy++){
        		n1.pointset.add(sortedPoints.get(i+yy));
        	}
            MBR(n1);
            rootList.add(n1);
        }
        
        while(rootList.size() >= d){

            ArrayList<Node> rootList1 = new ArrayList<>();

            for(int i = 0; i < rootList.size(); i = i+d){
                Node n1 = new Node();   
                for(int yy = 0; yy < c; yy++){
                    n1.nodeset.add(rootList.get(i+yy));
                }
                 MBR1(n1);
                rootList1.add(n1);
            }

            rootList = rootList1;
            rootList.trimToSize();
        }
        System.out.println("Load "+ numberOfPoints +" points completed.");
        while(s.hasNext()){
            String ip[] = s.nextLine().split(",");
            Point xx = new Point(Integer.parseInt(ip[0]), Integer.parseInt(ip[1])) ;
            //System.out.println(xx);
            search(rootList, xx);
        }   
     //    for(int i =0; i < rootList.size(); i++){
     //        System.out.println(rootList.get(i));
     //    }

    	// System.out.println();
     //    System.out.println();
	}

	public static long getHilbertValue(int x1, int x2) {
  		long res = 0;
  		int BITS_PER_DIM = 16;
  		int ix;
  		for (ix = BITS_PER_DIM; ix >= 0; ix--) {
    		long h = 0;
    		long b1 = (x1 & (1 << ix)) >> ix;
    		long b2 = (x2 & (1 << ix)) >> ix;
            //System.out.println("ix: "+ix+" b1: "+b1+" b2: "+b2);
    		if(b1 == 0 && b2 == 0){
    			h = 0;
    		}else if(b1 == 0 && b2 == 1){
    			h = 1;
    		}else if(b1 == 1 && b2 == 0){
    			h = 3;
    		}else if(b1 == 1 && b2 == 1){
    			h = 2;
    		}
    		res += h << (2 * ix);
  		}
  		return res;
	}



    public static void printMap(Map<Long,String> pointMap){
        Iterator iterator = pointMap.keySet().iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            String value = pointMap.get(key);
            System.out.println(key + " : " + value);
        }
        
    }
    

    public static class Point{
        int x,y;
        public Point(){}
        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        public String toString(){
            return (this.x + "," + this.y);
        }
    }

    public static class Node{
        ArrayList<Node> nodeset;
        ArrayList<Point> pointset;
        Point left;
        Point right;
        
        public Node(){
            this.nodeset = new ArrayList<>();
            this.pointset = new ArrayList<>();
            this.left = new Point();
            this.right = new Point();
        }
        
        public String toString(){
            return (this.left + "," + this.right);
        }
    }

    public static void MBR(Node n){
        int minX = n.pointset.get(0).x, minY = n.pointset.get(0).y,maxX = n.pointset.get(0).x,maxY=n.pointset.get(0).y;

        for(int i = 1; i < n.pointset.size(); i++){
            if(n.pointset.get(i).x > maxX){
                maxX = n.pointset.get(i).x;
            }
            if(n.pointset.get(i).y > maxY){
                maxY = n.pointset.get(i).y;
            }
            if(n.pointset.get(i).x < minX){
                minX = n.pointset.get(i).x;
            }
            if(n.pointset.get(i).y < minY){
                minY = n.pointset.get(i).y;
            }
        }
        n.left = new Point(minX, minY);
        n.right = new Point(maxX, maxY);

    }



    public static void MBR1(Node n){
        ArrayList<Point> nal = new ArrayList<>();

        for(int i = 0; i < n.nodeset.size(); i++){
            nal.add(n.nodeset.get(i).left);
            nal.add(n.nodeset.get(i).right);
        }

        int minX = nal.get(0).x, minY = nal.get(0).y,maxX = nal.get(0).x,maxY=nal.get(0).y;


        for(int i = 1; i < nal.size(); i++){
            if(nal.get(i).x > maxX){
                maxX = nal.get(i).x;
            }
            if(nal.get(i).y > maxY){
                maxY = nal.get(i).y;
            }
            if(nal.get(i).x < minX){
                minX = nal.get(i).x;
            }
            if(nal.get(i).y < minY){
                minY = nal.get(i).y;
            }
        }
        n.left = new Point(minX, minY);
        n.right = new Point(maxX, maxY);

    }

    public static boolean search(ArrayList<Node> roots, Point p){
        int found = 0;
        Stack<Node> s = new Stack<>();
        Queue<Node> q = new LinkedList<>();
        for (int i = 0; i < roots.size() ; i++) {
            Node n = roots.get(i);
            if(checkRange(p, n)){
                // q.add(n);
                s.push(n);
            }
        }
        
        // while(!q.isEmpty()){
        while(!s.isEmpty()){
            found++;
            // Base case
            // Node current = q.remove();
            Node current = s.pop();
            
            if(current.nodeset.size() > 0){
                System.out.println("Enter MBR " + current);
                // Not leaf, check further
                for (int i = 0; i < current.nodeset.size() ; i++) {
                    Node c = current.nodeset.get(i);
                    if(checkRange(p, c)){
                        // q.add(c);
                        s.push(c);
                    }
                }
            } else {
                // Leaf Node
                if(current.pointset.size()> 0){
                    System.out.printf("Enter LEAF %s of %d points\n", current, current.pointset.size());
                    for (int i = 0; i < current.pointset.size(); i++) {
                        Point pnt = current.pointset.get(i);
                        if(pnt.x == p.x && pnt.y == p.y){
                            System.out.printf("Point %s found (nodes=%d)\n", p, found);
                            return true;
                        }
                    }
                }
            }
        } // End While
    
        System.out.printf("Point %s not found (nodes=%d)\n", p, found);
        return false;
    }

    public static boolean checkRange(Point p, Node n){
        boolean x = (p.x >= n.left.x) && (p.x <= n.right.x);
        boolean y = (p.y >= n.left.y) && (p.y <= n.right.y);
        return (x && y);
    }

}