package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import caculate.DexData;
import caculate.FileNode;
import caculate.FileSize;
import caculate.FileSize.Unit;

public class Main {

	public static void main(String[] args) {
		File unCompFile = new File("/Users/malingyi/Desktop/Files/NewsArticle-master-release 2");
		File compFile = new File("/Users/malingyi/Desktop/Files/NewsArticle-master-release.apk");
		List<File> unCompFileList  = Arrays.asList(unCompFile.listFiles());
		for (File file : unCompFileList) {
			if (file.getName().endsWith(".dex")) {
				System.out.println(file.getName() + " = " + file.length()/1024.0d + "KB");
			}
		}
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(compFile);
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			for (; zipEntries.hasMoreElements();) {
				ZipEntry entry = zipEntries.nextElement();
				if(entry.getName().endsWith(".dex")) {
					System.out.println(entry.getName() + "    CompressedSize = " + (entry.getCompressedSize() / 1024.0d) + "    unCompressedSize = " + (entry.getSize()/ 1024.0d));
				}
			}
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.getProperty("user.dir")); 
	}
	
	public static void exeCmd(String[] commandStr) {  
//        System.out.println("command = " + commandStr);
        Process p = null;
        BufferedReader bStream = null;
        try {  
            p = Runtime.getRuntime().exec(commandStr,null,new File("/Users/malingyi/Desktop/Files"));
            p.waitFor();
            InputStream inputStream = p.getInputStream();
            bStream = new  BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuffer sBuffer = new StringBuffer();
            while ((line = bStream.readLine()) != null) {
				sBuffer.append(line+"\n");
			}
            System.out.println(sBuffer);
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
        finally  
        {  
            if (bStream != null) {
				try {
					bStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
            if (p != null ) {
				p.destroy();
			}
        }  
    }
	
	public static void parseApk() {
		String[] cmdStrs = {"/usr/local/bin/apktool","d","/Users/malingyi/Desktop/Files/NewsArticle-master-debug.apk"};
		exeCmd(cmdStrs);
	}

}
