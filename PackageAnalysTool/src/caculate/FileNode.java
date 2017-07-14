package caculate;

import java.util.LinkedList;
import java.util.List;

public class FileNode {
	DataNode dataNode;
	List<FileNode> fileTrees = new LinkedList<>();
	
	public boolean isDirectory() {
		return fileTrees != null && fileTrees.size() > 0 ? true:false;
	}
	public void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}
	
	public DataNode getDataNode() {
		return dataNode;
	}
	
	public void addChild(FileNode fileNode) {
		fileTrees.add(fileNode);
	}
	
	public void removeChild(FileNode fileNode) {
		fileTrees.remove(fileNode);
	}
	
	public FileNode findNodeByName(String fileName) {
		for (FileNode fileNode : fileTrees) {
			if (fileNode.dataNode.fileName.equals(fileName)) {
				return fileNode;
			}
		}
		return null;
	}
	public FileNode findNodeByPath(String filePath) {
		for (FileNode fileNode : fileTrees) {
			if (fileNode.dataNode.filePath.equals(filePath)) {
				return fileNode;
			}
		}
		return null;
	}
}
