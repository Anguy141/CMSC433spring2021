package tests;

public class MultiThread extends Thread {
	private String itemDataFilename;
	private String orderFilename;
	private String resultFilename;

	public MultiThread(String itemDataFilename, String orderFilename, String resultFilename) {
		this.itemDataFilename= itemDataFilename;
		this.orderFilename = orderFilename;
		this.resultFilename = resultFilename;
	}
	MyFileInputReader mfir = new MyFileInputReader();

	@Override
	public void run() {
		mfir.readItemData(itemDataFilename);

	}
}
