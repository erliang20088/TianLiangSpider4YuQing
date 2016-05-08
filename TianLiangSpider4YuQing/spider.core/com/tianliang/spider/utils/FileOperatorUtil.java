package com.tianliang.spider.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 字符串操作工具类
 * 
 * @author zel
 * 
 */
public class FileOperatorUtil {
	public static MyLogger logger = new MyLogger(FileOperatorUtil.class);

	// 创建root_path的所在文件夹的所有路径
	public static boolean createRootDir(String root_path) {
		File f = new File(root_path);
		if (f.exists() && f.isDirectory()) {
			return true;
		} else {
			try {
				if (f.mkdirs()) {
					return true;
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// 如果父路径不存在的话，创建一个文件的父路径，
	public static boolean createParentDirFromFile(String file) {
		File f = new File(file);
		if (f.exists()) {// 说明已经存在
			return false;
		} else {
			try {
				String parentPath = f.getParent();
				if (StringOperatorUtil.isBlank(parentPath)) {
					// 如果是null,则不需要再创建了，说明是同级目录
					return false;
				} else {
					// 创建父目录
					new File(parentPath).mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// 创建root_path的所在文件夹的所有路径
	public static void createNewFile(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {// 说明文件已存在
			return;
		} else {
			try {
				String temp = null;
				if ((temp = f.getParent()) != null) {
					new File(temp).mkdirs();
				}
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 判断文件是否存在，且是个文件夹
	public static boolean isExistAndDirecory(File file) {
		return file.exists() && file.isDirectory();
	}

	public static boolean existFile(String filepath){
		return new File(filepath).exists();
	}
	
	/**
	 * 得到某个文件夹下,某个过滤条件下文件名称值最大的那个文件
	 */
	public static String getLastFileNameStr(String desc_dir,
			final String filter_regex) {
		File dir = new File(desc_dir);

		// 文件夹过滤单元类
		FileFilterUnit fileFilterUnit = new FileFilterUnit() {
			public boolean accept(File pathname) {
				if (pathname.toString().contains(filter_regex)) {
					return true;
				}
				return false;
			}
		};

		File[] files = dir.listFiles(new MyFileFilter(fileFilterUnit));
		if (files == null || files.length == 0) {
			return null;
		}
		Arrays.sort(files, new FileComparator());
		return files[0].toString();
	}

	public static HashSet<String> getStringSetFromFile(String filePath)
			throws Exception {
		HashSet<String> hs = new HashSet<String>();
		File aidFile = new File(filePath);
		if (!aidFile.exists()) {
			return hs;
		}
		FileReader fr = new FileReader(aidFile);
		String line = null;
		BufferedReader br = new BufferedReader(fr);

		while ((line = br.readLine()) != null) {
			hs.add(line);
		}
		br.close();

		return hs;
	}

	public static void writeStrToFile(String aidFile, String source) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(aidFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		PrintStream ps = new PrintStream(fos);

		ps.print(source);

		ps.flush();
		ps.close();
	}

	public static void removeFile(String filePath) {
		if (StringOperatorUtil.isBlank(filePath)) {
			logger.info("to remove filepath is null,will return!");
			return;
		}
		File f = new File(filePath);
		boolean delFlag = false;
		if (f.exists()) {
			delFlag = f.delete();
			if (delFlag) {
				logger.info("the file " + filePath + ",remove successfully~");
			}else {
				logger.info("the file " + filePath + ",remove fail,please check~");
			}
		} else {
			logger.info("the file " + filePath
					+ " is not exsit ,will ignore this operator!");
		}
	}

	// 加入sourceFile指定的文件夹内的class文件名称
	// subPackPath主要是做各子层的路径导航
	public static List<String> getFirstLevelFileName(File sourceFile) {
		List<String> subFileStrList = new LinkedList<String>();
		// 如是.class则直接加入集合当中
		if (sourceFile.getName().endsWith(".class")) {
			subFileStrList.add(sourceFile.getName());
		} else if (sourceFile.isDirectory()) {
			// 过滤器
			FileFilterUnit fileFilterUnit = new FileFilterUnit() {
				public boolean accept(File pathname) {
					if (pathname.getName().endsWith(".class")
							|| pathname.isDirectory()) {
						return true;
					}
					return false;
				}
			};
			File[] files = sourceFile
					.listFiles(new MyFileFilter(fileFilterUnit));
			for (File file : files) {
				subFileStrList.add(file.getName());
			}
		}
		return subFileStrList;
	}

	/**
	 * 得到指定目录或文件中所有的文件路径，即isFile的所有路径
	 * 
	 * @param rootDirString
	 * @return
	 */
	public static List<String> getAllFilePathList(String rootDirString,
			String suffix) {
		if (StringOperatorUtil.isBlank(rootDirString)) {
			return null;
		}
		List<String> allFileList = new LinkedList<String>();
		File rootDir = new File(rootDirString);

		if (rootDir.isDirectory()) {
			File[] fileArray = rootDir.listFiles();
			for (File file : fileArray) {
				allFileList.addAll(getAllFilePathList(file.toString(), suffix));
			}
		} else if (rootDir.isFile()) {
			if (StringOperatorUtil.isBlank(suffix)
					|| rootDir.toString().endsWith(suffix)) {
				allFileList.add(rootDir.toString());
			}
		}
		return allFileList;
	}

	public static String getFileName(String filePath) {
		if (StringOperatorUtil.isBlank(filePath)) {
			return null;
		}
		File file = new File(filePath);
		if (file.isFile()) {
			return file.getName();
		}
		return null;
	}

	public static void main(String[] args) {
		File f = new File("D:/test");

		List<String> list = getAllFilePathList(f.toString(), null);

		for (String file : list) {
			System.out.println(file);
		}

		// System.out.println("f space--" + f.length());
		// System.out.println(264 * 1024);
		// createNewFile("D://tttt//kkt.txt");
		// createRootDir("D://eeee//kkt.txt");
		// File f=new File("kkt.txt");
		// createParentDirFromFile("D://bbbb//kkt");
		// System.out.println(f.getParent());
		// String root = "d://seeds";
		// List<String> list = getAllFilePathList(root, null);
		// System.out.println(list);

		// System.out.println(f.toString());
		// String fileName = getFileName(f.toString());
		// System.out.println(fileName);

		// FileOperatorUtil
		// .createRootDir(SystemParas.phantojs_seg_output_root_path);
	}
}

/**
 * 文件名称过滤器
 * 
 * @author zel
 */
class MyFileFilter implements FileFilter {
	private FileFilterUnit fileFilterUnit;

	public MyFileFilter(FileFilterUnit fileFilterUnit) {
		this.fileFilterUnit = fileFilterUnit;
	}

	@Override
	public boolean accept(File pathname) {
		return fileFilterUnit.accept(pathname);
	}
}

class FileFilterUnit {
	public boolean accept(File pathname) {
		return true;
	}
}

/**
 * 逆序排列的文件比较器
 * 
 * @author zel
 * 
 */
class FileComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		String o1_str = o1.toString();
		String o2_str = o2.toString();
		if (o2_str.length() > o1_str.length()) {
			return 1;
		}
		if (o2_str.length() == o1_str.length()) {
			return o2_str.compareTo(o1_str);
		}
		return -1;
	}

}
