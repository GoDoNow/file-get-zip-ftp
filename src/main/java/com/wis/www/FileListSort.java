package com.wis.www;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**   
 * @ClassName:  FileListSort   
 * @Description: 获取指定目录下的文件名
 * @author: lihy
 * @date:   2018年6月29日 下午6:14:54
 * @Copyright: 2018 wis
 */
public class FileListSort {

	public static long timeStampTemp = 0;
	// 只扫描当前目录下的文件
	public static List<String> getFilePathName(String filePath, long timeStamp, String nameRule, Logger logger) {
		timeStampTemp = timeStamp;
		List<String> filePathList = getFiles(filePath, timeStamp, nameRule, logger);
		filePathList.add(timeStampTemp + "");
		return filePathList;
	}
	// 递归扫描当前目录下的所有目录
	public static List<String> getFilePathNameRecursion(String filePath, long timeStamp, String nameRule, Logger logger) {
		timeStampTemp = timeStamp;
		// 递归搜索文件夹内文件
		List<String> filePathList = getFilesByRecursion(filePath, timeStamp, nameRule, logger);
		filePathList.add(timeStampTemp + "");
		return filePathList;
	}

	/**   
	 * @Title: getFilePathName   
	 * @Description: //正则匹配文件名 ，返回新增文件路径
	 * @param: @param fileNameFilter
	 * @param: @param filePath
	 * @param: @param timeStamp
	 * @param: @param logger
	 * @param: @return      
	 * @return: List<String>      
	 * @throws   
	 */
	public static List<String> getFiles(String filePath, long timeStamp, String nameRule, Logger logger) {
		List<String> filePathList = new ArrayList<String>();
		try {
			File rootFile = new File(filePath);
			if (rootFile.isDirectory()) {
				// ----文件筛选规则-----
				final String regex = nameRule; // 命名规则
				FilenameFilter fileFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						// 判断是否符合文件名规则
						if (Pattern.matches(regex, name)) {
							return true;
						}
						return false;
					}
				};
				// ----文件筛选规则-----
				File[] files = rootFile.listFiles(fileFilter);
				for (File file : files) {
					// 如果文件是普通文件，则将文件句柄放入集合中
					long stamp = new Long(file.lastModified());
					// 更新最新文件时间戳
					if (stamp > timeStampTemp) {
						timeStampTemp = stamp;
					}
					// 文件时间戳大于上次的时间戳
					if (stamp > timeStamp) {
						filePathList.add(file.getName());
						filePathList.add(file.getPath());
						logger.info("---- get file : " + file.getPath() + " ---- stamp : " +stampToDate(stamp+"") + " ---- size : " +file.length());
					}
				}
			}
		} catch (Exception e) {
			logger.info("---- get file fail---- : " + e);
		}
		return filePathList;
	}

	/**   
	 * @Title: iteratorGetFiles   
	 * @Description: //正则匹配文件名和递归搜索文件夹内文件 ，返回新增文件路径
	 * @param: @param fileNameFilter
	 * @param: @param filePath
	 * @param: @param timeStamp
	 * @param: @param logger
	 * @param: @return      
	 * @return: List<String>      
	 * @throws   
	 */
	public static List<String> getFilesByRecursion(String filePath, long timeStamp, String nameRule, Logger logger) {
		List<String> filePathList = new ArrayList<String>();
		try {
			File rootFile = new File(filePath);
			if (rootFile.isDirectory()) {
				// ----文件筛选规则-----
				final String path = filePath;
				final String regex = nameRule; // 命名规则
				FilenameFilter fileFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						// 判断是否是文件夹
						if (new File(path + File.separator + name).isDirectory()) {
							return true;
						}
						// 判断是否符合文件名规则
						if (Pattern.matches(regex, name)) {
							return true;
						}
						return false;
					}
				};
				// ----文件筛选规则-----
				File[] files = rootFile.listFiles(fileFilter);
				for (File file : files) {
					// 如果这个文件是目录，则进行递归搜索
					if (file.isDirectory()) {
						String parh = file.getPath();
						filePathList.addAll(getFilesByRecursion(parh, timeStamp, nameRule, logger));
					} else {
						// 如果文件是普通文件，则将文件句柄放入集合中
						long stamp = new Long(file.lastModified());
						// 更新最新文件时间戳
						if (stamp > timeStampTemp) {
							timeStampTemp = stamp;
						}
						// 文件时间戳大于上次的时间戳
						if (stamp > timeStamp) {
							filePathList.add(file.getName());
							filePathList.add(file.getPath());
							logger.info("---- get file : " + file.getPath() + " ---- stamp : " +stampToDate(stamp+"") + " ---- size : " +file.length());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("---- get file fail---- : " + e);
		}
		return filePathList;
	}

	public static String stampToDate(String s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}
}