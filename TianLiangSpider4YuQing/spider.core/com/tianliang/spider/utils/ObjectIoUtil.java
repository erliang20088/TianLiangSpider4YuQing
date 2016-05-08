package com.tianliang.spider.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public class ObjectIoUtil {
	// 做日志用
	private static Logger logger = Logger.getLogger(ObjectIoUtil.class);

	public static Object readObjectByFileInputStream(String file_path) {
		InputStream input = null;
		ObjectInputStream objectInput = null;
		try {
			long begin = System.currentTimeMillis();

			input = new FileInputStream(file_path);
			objectInput = new ObjectInputStream(input);
			Object bloomFilter = objectInput.readObject();
			objectInput.close();
			input.close();
			long end = System.currentTimeMillis();
			return bloomFilter;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean writeObject(String file_path, Object bloom) {
		FileOutputStream output = null;
		ObjectOutputStream objectOutput = null;
		// 判断父路径是否存在，若存在则不做创建操作，若不存在，首先创建父路径
		File f = new File(file_path);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		try {
			output = new FileOutputStream(file_path);
			output.write(ObjectAndByteArrayConvertUtil.ObjectToByteArray(bloom));
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean writeObjectByFileOutputStream(String file_path, Object bloom) {
		FileOutputStream output = null;
		ObjectOutputStream objectOutput = null;
		// 判断父路径是否存在，若存在则不做创建操作，若不存在，首先创建父路径
		File f = new File(file_path);
		if (f.getParentFile()!=null && !f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		try {
			output = new FileOutputStream(file_path);
			objectOutput = new ObjectOutputStream(output);
			objectOutput.writeObject(bloom);
			objectOutput.close();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static byte[] b = new byte[1024 * 5];

	public static boolean fileBak(String sourceFile, String destinationFile) {
		try {
			FileInputStream input = new FileInputStream(sourceFile);
			FileOutputStream output = new FileOutputStream(destinationFile);
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		InputStream input = null;
		int status = 0;
		int bytes_length = 20480000;
		byte[] bytes = new byte[bytes_length];
		input = new ByteArrayInputStream(bytes);

		FileInputStream fis = new FileInputStream(
				"D:\\workspaces-2013\\02-18\\DAT_SkyLightAnalyzer_V2\\cache\\Trie.dat");
		if ((status = fis.read(bytes)) < bytes_length) {
			System.out.println("文件读取结束!");
		}

		System.out.println(status);

		Object obj = ObjectAndByteArrayConvertUtil.ByteArrayToObject(bytes);
		System.out.println(obj);
	}
}
