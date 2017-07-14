package caculate;

public class PackageTag {
	public String name;
	public String packagePath;
	public String notes;
	
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}
	
	public void setPackageName(String name) {
		this.name = name;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getPackagePath() {
		return packagePath;
	}
	public String getPackageName() {
		return name;
	}
	
	public String getNotes() {
		return notes;
	}
}
