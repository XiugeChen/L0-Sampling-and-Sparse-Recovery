import java.util.ArrayList;
import java.util.HashMap;

/**
 * K Sparse recovery data structure that could work over arbitrary integer vectors.
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class KSparseRecovery implements ISparseRecovery {
	/** Output message when there is 0 items in the stream */
	public static final String ZERO_SPARSE = "zero items in the stream";
	/** Output message when there are more than k items in the stream */
	public static String MORE_K_SPARSE = "more than k items in the stream";
	
	/** 2D array of size log(k/delta) * 2k, each cell contains an instance of One Sparse Recovery structure */
	private OneSparseRecovery[][] cells;
	/** 1D array of size log(k/delta), each cell is a randomly selected pair-wise independent hash function */
	private IHash[] hashs;
	/** output domain of the hash functions */
	private int domain;
	/** K */
	private int k;
	
	/**
	 * Constructor, initialize variables
	 * @param k K
	 * @param delta Error rate
	 */
	public KSparseRecovery(int k, double delta) {
		int numRows = (int) Math.log(k / delta);
		this.k = k;
		this.domain = 2 * k;
		MORE_K_SPARSE = "more than " + k + " items in the stream";
		
		hashs = new IHash[numRows];
		for (int i = 0; i < numRows; i++) {
			hashs[i] = new KWiseHash(2);
		}
		
		this.cells = new OneSparseRecovery[numRows][domain];
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new OneSparseRecovery();
			}
		}
	}

	@Override
	public void update(Object item, int update) {
		for (int i = 0; i < hashs.length; i++) {
			int col = hashs[i].hash(item, this.domain);
			
			cells[i][col].update(item, update);
		}
	}

	@Override
	public Object output() {
		ArrayList<Object> items = new ArrayList<>();
		ArrayList<Integer> frequencies = new ArrayList<>();
		int lowerboundTotal = 0;
		
		// loop through all one sparse recovery structure, extract the unique item stored if there is one
		for (int i = 0; i < cells.length; i++) {
			int lowerboundRow = 0;
			
			for (int j = 0; j < cells[i].length; j++) {
				Object output = cells[i][j].output();
				
				if (output.equals(OneSparseRecovery.MORE_K_SPARSE)) {
					lowerboundRow += 2;
				}
				else if (!output.equals(OneSparseRecovery.ZERO_SPARSE)) {
					HashMap<Object, Integer> map = (HashMap<Object, Integer>) output;
					
					for (Object key : map.keySet()) {
						if (!items.contains(key)) {
							items.add(key);
							frequencies.add(map.get(key));
						}
						else if (frequencies.get(items.indexOf(key)) > map.get(key)) {
							frequencies.set(items.indexOf(key), map.get(key));
						}
					}
					
					lowerboundRow += 1;
				}
			}
			
			if (lowerboundRow > lowerboundTotal) {
				lowerboundTotal = lowerboundRow;
			}
		}
		
		if (lowerboundTotal > k) {
			return this.MORE_K_SPARSE;
		}
		
		if (items.size() == 0) {
			return this.ZERO_SPARSE;
		}
		else if (items.size() <= k) {
			ArrayList<HashMap<Object, Integer>> results = new ArrayList<>();
			
			for (int i = 0; i < items.size(); i++) {
				HashMap<Object, Integer> map = new HashMap<>();
				map.put(items.get(i), frequencies.get(i));
				results.add(map);
			}
			
			return results;
		}
		
		return this.MORE_K_SPARSE;
	}

}
