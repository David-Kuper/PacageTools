package caculate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.w3c.dom.Node;

import caculate.FileSize.Unit;

public class Main {
    private static FileNode root;
    private static List<PackageTag> packagePathList;
    private static String sourcePath;
    private static String parseSourcePath;
    private static FileSize apkSize;
    private static FileSize apkUnCompressedSize;
    private static boolean isUseCombineSmali = false;  //是否使用将分包的smali合并的方式
    
    private static ApkTag apkTag;
    private static Map<String,DexData> dexDatas = new HashMap();
	public static void main(String[] args) {
		if (args == null || args.length <= 0) {
			throw new IllegalArgumentException("请至少提供源文件路径！");
		}
		if (args != null && args.length > 0) {
			sourcePath = args[0];
			if (sourcePath == null) {
				throw new NullPointerException("sourcePath can't be null!");
			}
			if (apkTag == null) {
				apkTag = new ApkTag();
			}
			
			if (args.length > 1) {
				apkTag.setOutputPath(args[1]);
			}else {
			    File sourceFile = new File(sourcePath);
			    if (sourceFile.getParentFile().isDirectory()) {
			     	apkTag.setOutputPath(sourceFile.getParentFile().getPath());
				}else {
					throw new IllegalArgumentException();
				}
			}
		}
		
		if (sourcePath != null) {
			packagePathList = new ArrayList<PackageTag>();
			parseXML();
			executeApkTool();
			parseZip();
			parseSourcePath = apkTag.getOutputPath() + apkTag.getName().replace(".apk", "");
			root = parseTree(new File(parseSourcePath));
			getDexCompressedPercentage(root);
			caculateDexSizeForAllFile(root);
			outputResult();
		}else {
			System.out.println("请指定配置文件路径！");
		}
		Set<String> dexSet = dexDatas.keySet();
		for (String string : dexSet) {
			System.out.println(dexDatas.get(string));
			System.out.println();
		}
		System.out.println("apkSize = " + apkSize + "    apkUnCompressedSize = " + apkUnCompressedSize);
	}
	
	public static void outputResult() {
		FileWriter fileWriter = null;
		try {
			File file = new File(apkTag.getOutputPath());
			if (file.isDirectory()) {
				if (apkTag.getOutputPath().endsWith(File.separator)) {
		     		apkTag.setOutputPath(apkTag.getOutputPath() + "output.csv");
				}else {
					apkTag.setOutputPath(apkTag.getOutputPath()+ File.separator + "output.csv");
				}
			}
			
			fileWriter = new FileWriter(new File(apkTag.getOutputPath()));
			fileWriter.append("包名,压缩大小(KB),原大小(KB),压缩比,注释,所属Dex,DexSize\n");
			double totalDexSize = 0.0d;
			double totalFileSize = 0.0d;
			for (PackageTag path : packagePathList) {
				FileNode fileNode = findFileNode(root,path.getPackagePath());
				if (fileNode !=  null) {
					totalDexSize += fileNode.dataNode.getDexSize().size;
					totalFileSize += fileNode.dataNode.getFileSize().size;
					fileNode.getDataNode().setNotes(path.getNotes());
					fileWriter.append((CharSequence)fileNode.dataNode.toString()+"\n");
					System.out.println("find " + path.getPackageName() + " success!");
				}else {
					fileWriter.append(path.getPackageName()+"\n");
					System.out.println("find " + path.getPackageName()  + " failed!");
				}
			}
			fileWriter.append("总大小,"+totalDexSize+","+totalFileSize+","+"   ,"+" , "+"  ,");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void parseXML() {
		List<Node> apkPaths = XmlBuilder.readNode(sourcePath,"apk");
		if (apkPaths != null && apkPaths.size() > 0) {
			apkTag.setName(XmlBuilder.readNodeAttrValue(apkPaths.get(0), "apkName"));
			apkTag.setApkZipName(XmlBuilder.readNodeAttrValue(apkPaths.get(0), "zipName"));
			apkTag.setOutputPath(XmlBuilder.readNodeAttrValue(apkPaths.get(0), "output"));
			apkTag.setApkBasePath(XmlBuilder.readNodeAttrValue(apkPaths.get(0), "path"));
			apkTag.setPackageBasePath(XmlBuilder.readNodeAttrValue(apkPaths.get(0), "packageBasePath"));
			if (apkTag.getPackageBasePath() != null && !apkTag.getPackageBasePath().isEmpty()) {
				isUseCombineSmali = true;
			}else {
				isUseCombineSmali = false;
			}
			apkTag.setApkAbsPath(apkTag.getApkBasePath() + apkTag.getName());
			
			if (apkTag.getApkAbsPath() == null) {
				throw new IllegalStateException("apk path not illegal!");
			}else {
				File apkFile = new File(apkTag.getApkAbsPath());
				apkSize = new FileSize(apkFile.length() / 1024.0d, Unit.UNIT_KB);
				
				if (apkTag.getOutputPath() == null || apkTag.getOutputPath().isEmpty()) {
					 apkTag.setOutputPath(apkTag.getApkAbsPath().replace(".apk", "/"));
				}
			}
		}else {
			throw new IllegalStateException("you must has a apk tag!");
		}
		
		List<Node> nodes = XmlBuilder.readNode(sourcePath,"package");
		for (Node node : nodes) {
			String packageName = XmlBuilder.readNodeAttrValue(node, "name");
			String notes = XmlBuilder.readNodeAttrValue(node, "note");
			PackageTag packageTag = new PackageTag();
			packageTag.setNotes(notes);
			packageTag.setPackageName(packageName);
			if (apkTag.getPackageBasePath() != null) {
				packageTag.setPackagePath(apkTag.getPackageBasePath() + packageName.replace(".", File.separator));
				packagePathList.add(packageTag);
			}else {
				packageTag.setPackagePath(packageName.replace(".", File.separator));
				packagePathList.add(packageTag);
			}
		}
	}
	
	/**
	 * 执行APKTOOL反编译APK文件
	 */
	public static void executeApkTool() {
		String[] cmdStrs = {"apktool","d",apkTag.getApkAbsPath()};
		exeCmd(cmdStrs);
	}
	/**
	 * 解析ZIP\APK文件，获取DEX信息
	 */
	public static void parseZip() {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(new File(apkTag.getApkAbsPath()));
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			apkUnCompressedSize = new FileSize();
			FileNode apkUnCompressedFile = parseTree(new File(apkTag.getOutputPath() + apkTag.getName()));
			if (apkUnCompressedFile != null) {
				apkUnCompressedSize.addSize(apkUnCompressedFile.dataNode.fileSize);
			}
			for (; zipEntries.hasMoreElements();) {
				ZipEntry entry = zipEntries.nextElement();
				if(entry.getName().endsWith(".dex")) {
					String temp = entry.getName().replace(".dex", "").replace("classes", "");
					DexData dexData = new DexData();
					dexData.dexName = entry.getName();
					dexData.dexSize = new FileSize(entry.getCompressedSize() / 1024.0d,Unit.UNIT_KB);
					dexData.smaliName = "smali"+ (temp.isEmpty() ? "" : "_"+entry.getName().replace(".dex", ""));
					dexDatas.put(dexData.smaliName.trim(), dexData);
				}
			}
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 1.计算dex文件夹与压缩文件的占比
	 * @param fileNode
	 */
	public static void getDexCompressedPercentage(FileNode fileNode) {
		if (fileNode == null) {
			throw new NullPointerException();
		}
		if (dexDatas.containsKey(fileNode.dataNode.fileName.trim())) {
			DexData dexData = dexDatas.get(fileNode.dataNode.fileName.trim());
			dexData.smaliSize = fileNode.dataNode.getFileSize();
			dexData.dexUnCompressedPer = dexData.smaliSize.size / dexData.dexSize.size;
			dexData.apkUnCompressedPer = apkUnCompressedSize.size / apkSize.size;
			dexData.sizeInApk = new FileSize(dexData.dexSize.size / dexData.apkUnCompressedPer,Unit.UNIT_KB);
		}
		
		if (fileNode.isDirectory()) {
			List<FileNode> fileNodes = fileNode.fileTrees;
			for (FileNode node : fileNodes) {
				getDexCompressedPercentage(node);
			}
		}
		
	}
	
	/**
	 * 根据文件所属的dex还原所有文件在dex压缩状态下的大小
	 * @param fileNode
	 */
	public static void caculateDexSizeForAllFile(FileNode fileNode) {
		if (fileNode == null) {
			throw new NullPointerException();
		}
		
		Set<String> dexSet = dexDatas.keySet();
		if (isUseCombineSmali) {
			double dexUnCompressedPer = 0;
			double apkUnCompressedPer = 0;
			DexData dexData = new DexData();
			for (String string : dexSet) {
				DexData item = dexDatas.get(string);	
				dexData.dexName += item.dexName + "|";
				dexData.dexSize.addSize(item.dexSize);
				dexUnCompressedPer += item.dexUnCompressedPer;
				apkUnCompressedPer += item.apkUnCompressedPer;
			}
			dexUnCompressedPer = dexUnCompressedPer / 2.0d;
			apkUnCompressedPer = dexUnCompressedPer / 2.0d;
			if (fileNode.dataNode.filePath.contains("combine")) {
				FileSize dexSize = new FileSize(fileNode.dataNode.fileSize.size / dexUnCompressedPer,Unit.UNIT_KB);
				fileNode.dataNode.setDexSize(dexSize);
				fileNode.dataNode.dexToFilePer = dexUnCompressedPer;
				fileNode.dataNode.apkToFilePer = apkUnCompressedPer;
				fileNode.dataNode.setApkSize(new FileSize(fileNode.dataNode.getDexSize().size / apkUnCompressedPer, Unit.UNIT_KB));
				fileNode.dataNode.dexData = dexData;
			}
		}else {
			for (String string : dexSet) {
				if (fileNode.dataNode.filePath.trim().contains(string.trim())) {
					DexData dexData = dexDatas.get(string);
					FileSize dexSize = new FileSize(fileNode.dataNode.fileSize.size / dexData.dexUnCompressedPer,Unit.UNIT_KB);
					fileNode.dataNode.setDexSize(dexSize);
					fileNode.dataNode.dexData = dexData;
					fileNode.dataNode.dexToFilePer = dexData.dexUnCompressedPer;
					fileNode.dataNode.apkToFilePer = dexData.apkUnCompressedPer;
					fileNode.dataNode.setApkSize(new FileSize(fileNode.dataNode.getDexSize().size / dexData.apkUnCompressedPer, Unit.UNIT_KB));
				}
			}
		}
		
		if (fileNode.isDirectory()) {
			List<FileNode> fileNodes = fileNode.fileTrees;
			for (FileNode node : fileNodes) {
				caculateDexSizeForAllFile(node);
			}
		}
	}
	
	/**
	 * 根据输入的rootNode,在它之下的目录搜索指定路径的文件并返回
	 * @param rootNode
	 * @param filePath
	 * @return
	 */
	public static FileNode findFileNode(FileNode rootNode,String filePath) {
		if (rootNode == null) {
			throw new NullPointerException();
		}
		if (isEquals(rootNode, filePath)) {
			return rootNode;
		}
		
		if (rootNode.isDirectory()) {
			List<FileNode> fileNodes = rootNode.fileTrees;
			for (FileNode node : fileNodes) {
				FileNode target = findFileNode(node, filePath);
				if (target != null) {
					return target;
				}
			}
		}
		return null;
	}
	
	public static boolean isEquals(FileNode fileNode,String filePath) {
		if (fileNode.dataNode.getFilePath().trim().equals(filePath.trim()) || fileNode.dataNode.getPointPath().trim().equals(filePath.trim())) {
			return true;
		}else {
			return false;
		}
	}
	
	/** 
     * 计算文件或者文件夹的大小 ，单位 MB 
     * @param file 要计算的文件或者文件夹 ， 类型：java.io.File 
     * @return 大小，单位：MB 
     */  
    public static FileNode parseTree(File file) {  
    	    if (file == null) {
    	    		throw new NullPointerException();
		}
        //判断文件是否存在  
        if (file.exists()) {  
            //如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小  
        		FileNode fileNode = new FileNode();
        		DataNode dataNode = new DataNode();
        		FileNode childFileNode;
        		long fileNums = 1L;
        		FileSize size = new FileSize(); 
        		 
        		dataNode = new DataNode();
        		dataNode.setFileName(file.getName());
        		dataNode.setFilePath(file.getPath());
        		dataNode.setFileSize(size);
        		dataNode.setBasePackagePath(apkTag.getPackageBasePath());
            if (!file.isFile()) {  
                //获取文件大小  
                File[] fl = file.listFiles();
                for (File f : fl) {
                		childFileNode = parseTree(f);
                		fileNode.addChild(childFileNode);
                     dataNode.fileSize.addSize(childFileNode.dataNode.fileSize);
                     fileNums += childFileNode.dataNode.getFileNums();
                }
            } else {
            		switch (size.unit) {
					case UNIT_B:
						size.size = (double)file.length();
						break;

					case UNIT_KB:
						size.size = (double)file.length() / 1024.0d;
						break;
						
					case UNIT_MB:
						size.size = (double)file.length() / (1024.0d * 1024.0d);
						break;
						
					default:
						break;
					}
            }
          
            dataNode.setFileNums(fileNums);
            fileNode.setDataNode(dataNode);
            return fileNode;  
        } else {  
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");  
            return null;  
        }  
    }
    
    public static void exeCmd(String[] commandStr) { 
        Process p = null;
        try {  
            p = Runtime.getRuntime().exec(commandStr,null,new File(apkTag.getOutputPath())); 
            p.waitFor();
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
        finally  
        {  
            if (p != null ) {
				p.destroy();
			}
        }  
    }  
}
