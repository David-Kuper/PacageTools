package caculate;

public class FileSize {
	double size;
	Unit unit = Unit.UNIT_KB;
	public FileSize() {
		
	}
	
	public FileSize(double size, Unit unit) {
		this.size = size;
		this.unit = unit;
	}
	public void addSize(FileSize fileSize) {
		if (this.unit == fileSize.unit) {
			this.size += fileSize.size;
		}else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public String toString() {
		switch (unit) {
		case UNIT_B:
			return size + "  B";

		case UNIT_KB:
			return size + " KB";
			
		case UNIT_MB:
			return size + " MB";
			
		case UNIT_GB:
			return size + " GB";
			
		default:
			break;
		}
		return super.toString();
	}
	public static enum Unit{
		UNIT_B(1),
		UNIT_KB(2),
		UNIT_MB(3),
		UNIT_GB(4);
		int unit;
		private Unit(int unit) {
			this.unit = unit;
		}
	}
}
