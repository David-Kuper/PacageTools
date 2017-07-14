package caculate;

import java.io.File;

public class DataNode {
	public String filePath;    // com/google/
	public String fileName;    // rxjava
	public String pointPath;   // com.google.xx
	public FileSize fileSize;  
	public double percentage;  
	public double dexToFilePer;  //压缩比
	private FileSize dexSize;    //在dex中的大小
	private FileSize apkSize;    //在apk中的大小
	public double apkToFilePer;  //压缩比
	private String notes;  //所属的packageName
	private String basePackagePath;  //包名的offset路径
	long fileNums;              //子文件数量
    boolean isDirectorys;       //是否是文件夹
    public DexData dexData;
	
   
	public void setBasePackagePath(String basePackagePath) {
		this.basePackagePath = basePackagePath;
	}
	
	public String getBasePackagePath() {
		return basePackagePath;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setApkSize(FileSize apkSize) {
		this.apkSize = apkSize;
	}
	public FileSize getApkSize() {
		return apkSize;
	}
	public void setFileNums(long fileNums) {
		this.fileNums = fileNums;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		this.pointPath = filePath.replaceAll(File.separator, ".");
	}
	
	public void setFileSize(FileSize fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileName() {
		return fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getPointPath() {
		return pointPath;
	}
	public void setPointPath(String packagePath) {
		this.pointPath = packagePath;
	}
	public FileSize getFileSize() {
		return fileSize;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	public long getFileNums() {
		return fileNums;
	}
	
	public void setDexSize(FileSize dexSize) {
		this.dexSize = dexSize;
	}
	
	public FileSize getDexSize() {
		return dexSize;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return filePath.replace(basePackagePath, "").replace("/", ".") 
				+"," + dexSize.size
				+"," + fileSize.size
				+"," + String.format("%1$.2f", dexToFilePer)
				+"," + notes
				+"," + dexData.dexName
				+"," + dexData.dexSize.size;
	}
}
