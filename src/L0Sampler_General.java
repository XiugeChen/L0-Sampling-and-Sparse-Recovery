
/**
 * L0 sampler that is designed to deal with all kinds of general stream
 * 
 * xiugec 961392
 * Xiuge Chen
 * xiugec@student.unimelb.edu.au
 * @author xiugechen
 */
public class L0Sampler_General extends L0Sampler {

	public L0Sampler_General(int domain, double delta) {
		super(domain, delta);
		
		int k = (int) (12 * Math.log(1 / delta));
		
		for (int i = 0; i < L; i++) {
			K[i] = new KSR_General(k, delta);
		}
	}
}
