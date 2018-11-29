import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * 
 * 
 * 
 * WRITEUP:
 * 1. I chose to call brighten() after assingning a random color to assure
 * that the regions would be visible. I also chose a lower maxColorDiff, so that small differences in color would 
 * not prevent a region from forming. 
 * 
 * 2. The limitations of region growing in this context are that it is hard to distinguish between
objects with little difference in color, but to the human eye are obviously separate. They
may have pixels that when examined individually possess the same RGB values, but
taken as a whole object are different.
 * 
 * 
 * 
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		//keep track of which pixels are visited
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<ArrayList<Point>>();
		//loop over pixels
		for (int x=0; x<image.getWidth();x++) {
			
			for (int y=0; y<image.getHeight();y++) {
				//System.out.println("here");

				//if pixel is unvisited and correct color
				if (visited.getRGB(x, y)==0  && colorMatch(targetColor,new Color(image.getRGB(x, y)))) {

					//start a new region
					ArrayList<Point> newregion = new ArrayList<Point>();
					//keep track of which pixels to visit
					ArrayList<Point> toVisit = new ArrayList<Point>();
					toVisit.add(new Point(x,y));
					//System.out.println("here");

					//as long as there is pixel to be visited
					while (!toVisit.isEmpty()) {
						//get a pixel to visit
						Point pixel = toVisit.get(0);
						toVisit.remove(0);
						//check that the pixel hasn't been visited		
						if (visited.getRGB(pixel.x, pixel.y)==0) {
						
							newregion.add(pixel);
							
							//mark point as visited
							visited.setRGB(pixel.x, pixel.y, 1);

							//loop through neighbors
							for (int y1 = Math.max(0, pixel.y-1); y1 <= Math.min(image.getHeight()-1, pixel.y+1); y1++) {
								for (int x1 = Math.max(0, pixel.x-1); x1 <= Math.min(image.getWidth()-1, pixel.x+1); x1++) {
									if (visited.getRGB(x1, y1)==0 && colorMatch(targetColor, new Color(image.getRGB(x1,y1)))) {
										toVisit.add(new Point(x1, y1));
									}	
								}	
							}		
						}
						
					}
					if (newregion.size()>minRegion) {
						regions.add(newregion);
						//System.out.println("here");
					}
				
				}
				
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		
		//checks each color channel and returns true is the difference between 
		//	each is less than maxColorDiff
		return (Math.abs(c1.getBlue() - c2.getBlue())) < maxColorDiff 
				&& (Math.abs(c1.getGreen() - c2.getGreen())) < maxColorDiff
				&& (Math.abs(c1.getRed() - c2.getRed())) < maxColorDiff;
		
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		//if (regions.isEmpty())
		//loop over regions
		ArrayList<Point> largest = null;
		if (!regions.isEmpty()) {
		largest = regions.get(0);
		for (ArrayList<Point> region:regions) {
			if (largest.size()<region.size()) {
				largest = region;
			}
			
		}}
		return largest;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
		//loop over regions
		for (ArrayList<Point> region:regions) {
			//choose a random color
			Color color = new Color((int)(Math.random()*16777216));	
			color.brighter();
			//for each point in the region
			for (Point point:region) {
				//set the point to the chosen color
				recoloredImage.setRGB((int)point.getX(), (int)point.getY(), color.getRGB());
			}
		}
	}
}