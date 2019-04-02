package com.wis.www;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public class Constant {

	public static Map<String, List<List<String>>> EXCEL_DATA_MAP = new HashMap<String, List<List<String>>>();
	public static String FROM_ZIP_PATH = null;// 文件存放目录
	public static String TARGET_ZIP_PATH = null;// 文件压缩目录
	public static int START_DELAY = 2;// 线程延时启动时间
	public static Map<String, Integer> INTER_DAY_DELAY = new HashMap<String, Integer>();// 程序的轮训时间(分钟)
	// -------json参数------
	public static JSONArray JSON = null;
	// -------ftp参数------
	public static String url;// FTP服务器hostname
	public static int port;// FTP服务器端口
	public static String username; // FTP登录账号
	public static String password; // FTP登录密码
	public static String path; // FTP服务器保存目录
}
