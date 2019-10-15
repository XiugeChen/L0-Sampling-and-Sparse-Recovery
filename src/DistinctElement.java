import java.util.Arrays;

/**
 * Distinct elements estimator
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class DistinctElement {
	
	private int hashedDomain;
	
	private IHash[][] hashs;
	
	private boolean[][][] bitmap;
	
	public DistinctElement(int domain, double epsilon, double delta) {
		this.hashedDomain = domain;
		int numRow = (int)(Math.log(1 / delta) / Math.log(2) + 1e-10);
		int numCol = (int)(1 / (Math.pow(epsilon, 1)));
		int bitmapSize = (int)(Math.log(domain) / Math.log(2) + 1e-10) + 1;
		
		this.hashs = new IHash[numRow][numCol];
		this.bitmap = new boolean[numRow][numCol][bitmapSize];
		
		for (int i = 0; i < numRow; i++) {
			for (int j = 0; j < numCol; j++) {
				hashs[i][j] = new KWiseHash(2);
				
				for (int k = 0; k < bitmapSize; k++) {
					bitmap[i][j][k] = false;
				}
			}
		}
	}
	
	/**
	 * Update sketch with an item and its update
	 * @param item Item
	 * @param update Update
	 */
	public void update(Object item, int update) {
		for (int i = 0; i < bitmap.length; i++) {
			for (int j = 0; j < bitmap[i].length; j++) {
				int k = lsb(hashs[i][j].hash(item, hashedDomain-1) + 1);
				
				bitmap[i][j][k] = true;	
			}
		}
	}
	
	/**
	 * Output the estimated number of distinct elements
	 * @return Estimated number of distinct elements, Double
	 */
	public double output() {
		int sum[] = new int[bitmap.length];
		
		// obtain the average value per row
		for (int i = 0; i < bitmap.length; i++) {
			sum[i] = 0;
			
			for (int j = 0; j < bitmap[i].length; j++) {
				int leftMostZero = 0;
				
				for (int k = bitmap[i][j].length - 1; k >= 0; k--) {
					
					if (!bitmap[i][j][k]) {
						leftMostZero = k;
					}	
				}
				
				sum[i] += leftMostZero;
			}
			
			sum[i] = (int)(sum[i] / bitmap[i].length) + 1;
		}
		
		// obtain the median value of all row
		double median = 0;
		Arrays.sort(sum);
		int medianIndex = sum.length / 2;
		
		if (sum.length % 2 == 0) {
			median = (sum[medianIndex - 1] + sum[medianIndex])/2;
		} else {
			median = sum[medianIndex];
		}
		
		return 1.2928 * Math.pow(2, median);
	}
	
	/**
	 * Get the position of the least-significant 1 bit in the binary string representation of value
	 * @param value Value
	 * @return The position of the least-significant 1 bit in the binary string representation of value
	 */
	private int lsb(int value) {
		int lsb = 0;
		
		while(true) {
			if (value % 2 == 0) {
				value /= 2;
				lsb++;
			}
			else {
				break;
			}
		}
		
		return lsb;
	}
}
