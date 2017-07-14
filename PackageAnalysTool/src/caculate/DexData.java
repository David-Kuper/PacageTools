package caculate;

import java.awt.event.MouseWheelEvent;

public class DexData {
	String dexName = "";
	String smaliName = "";
	FileSize dexSize = new FileSize();
	FileSize sizeInApk; //暂时无用，目前计算方法也不对
	FileSize smaliSize;
	double dexUnCompressedPer;
	public double apkUnCompressedPer;
	
	@Override
	public String toString() {
		return "dexName = " + dexName
				+"\ndexSize = " + dexSize
				+"\nsmaliName = " + smaliName
				+"\nsmaliSize = " + smaliSize
//				+"\nsizeInApk = " + sizeInApk
				+"\napkUnCompressedPer = " + apkUnCompressedPer*100 + "%"
				+"\ndexUnCompressedPer = " + dexUnCompressedPer*100 + "%";
	}
}
