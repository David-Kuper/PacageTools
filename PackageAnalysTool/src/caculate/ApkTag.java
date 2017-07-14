package caculate;

public class ApkTag {
	private String apkBasePath;
	private String outputPath;
	private String apkZipName;
	private String packageBasePath;
	private String name;
	private String apkAbsPath;
	
	
	public void setApkAbsPath(String apkAbsPath) {
		this.apkAbsPath = apkAbsPath;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setApkZipName(String apkUnCompressedFileName) {
		this.apkZipName = apkUnCompressedFileName;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public void setPackageBasePath(String packageBasePath) {
		this.packageBasePath = packageBasePath;
	}
	public void setApkBasePath(String path) {
		this.apkBasePath = path;
	}
	public String getApkZipName() {
		return apkZipName;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public String getApkAbsPath() {
		return apkAbsPath;
	}
	public String getPackageBasePath() {
		return packageBasePath;
	}
	public String getApkBasePath() {
		return apkBasePath;
	}
	public String getName() {
		return name;
	}
}
