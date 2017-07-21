package utils;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;


public class ClosestPointReferer2D {

	 public class NearestComparator implements Comparator<Entry> {

	        @Override
	        public int compare(Entry e1, Entry e2) {
	            return Double.compare((Double) e1.getValue(), (Double) e2.getValue());
	        }
	    }

	    /**
	     * @param args the command line arguments
	     */
	   public LinkedList<Entry> getClosestPoint(String sortType, List<Point2D> pointsList, Point2D destination){
	    	List<Map.Entry<Point2D, Double>> list = new LinkedList<Map.Entry<Point2D, Double>>();
	        LinkedList<Entry> sendOrderList = new LinkedList<Entry>();
	        Entry<Point2D, Double> pointWithDist = null;
	        Iterator<Point2D> itr = pointsList.iterator();
	        while(itr.hasNext()) {
	            Point2D p = itr.next();
	            switch (sortType) {
				case "point":  pointWithDist = new SimpleEntry<>(p, p.distance(destination));
							   break;
				case "xPos":  pointWithDist = new SimpleEntry<>(p, Math.abs(destination.getX() - p.getX()));
	            			   break;
				case "yPos":  pointWithDist = new SimpleEntry<>(p, Math.abs(destination.getY() - p.getY()));
				               break;
				default: System.err.println("Invalid sort type paramter");
	            			  break;
				}
	            sendOrderList.add(pointWithDist);
	         }
	        
	     // sort list by distance from destination
	        Collections.sort(sendOrderList, new NearestComparator());
	        return sendOrderList;
	    }
}
