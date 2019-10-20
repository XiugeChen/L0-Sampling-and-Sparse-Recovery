/**
 * L0 sampler variant that uses pairwise rather than k-wise hash function
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class L0Sampler_Pairwise_General implements IL0Sampler {
	/** Fail message */
	public final static String FAIL = "FAIL";
	/** distinct element count */
	private DistinctElement de;
	/** one sparse recoveries */
	private OneSR_General[][] oneSRs;
	/** alphas */
	private int[][] alphas;
	/** hash functions */
	private IHash[][] hashs;
	
	int hashDomain[];
	
	public L0Sampler_Pairwise_General(int domain, double epsilon, double delta) {
		de = new DistinctElement(domain, 1, delta / 2);
		
		int numRow = (int)(Math.log(domain) / Math.log(2) + 1e-10) + 1;
		int numCol = (int)((Math.log(1 / delta) / Math.log(2) + 1e-10) / epsilon);
		
		oneSRs = new OneSR_General[numRow][numCol];
		alphas = new int[numRow][numCol];
		hashs = new IHash[numRow][numCol];
		hashDomain = new int[numRow];
		
		for (int i = 0; i < numRow; i++) {
			hashDomain[i] = (int)(Math.pow(2, i) / epsilon);
			
			for (int j = 0; j < numCol; j++) {
				oneSRs[i][j] = new OneSR_General();
				alphas[i][j] = 0; 
				hashs[i][j] = new KWiseHash(2);
			}
		}
	}

	@Override
	public void update(Object item, int update) {
		for (int j = 0; j < hashs.length; j++) {
			for (int k = 0; k < hashs[j].length; k++) {
				if (hashs[j][k].hash(item, this.hashDomain[j]) == alphas[j][k]) {
					oneSRs[j][k].update(item, update);
				}
			}
		}
		
		de.update(item, update);
	}

	@Override
	public Object output() {
		int j = (int)(Math.log(de.output()) / Math.log(2) + 1e-10);
		
		if (j >= hashs.length) {
			j = hashs.length - 1;
		}
		
		for (int k = 0; k < hashs[j].length; k++) {
			String result = oneSRs[j][k].output().toString();
			
			if (!result.equals(OneSR_General.MORE_K_SPARSE) && !result.equals(OneSR_General.ZERO_SPARSE)) {
				return result.split(",")[0];
			}
		}
		
		return FAIL;
	}
}
