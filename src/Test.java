
public class Test {
	public static void main(String[] args) {
		KWiseHashTest();
	}
	
	public static void KWiseHashTest() {
		IHash hash = new KWiseHash(2);
		
		int results = hash.hash("1", 7);
		
		System.out.println(results);
	}
}
