package com.wis.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class SendFile {
	private static Logger logger = Logger.getLogger("runlog");;

	/**
	 * Description: 向FTP服务器上传文件
	 * @Version 1.0
	 * @param url FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param path FTP服务器保存目录
	 * @param filename 上传到FTP服务器上的文件名
	 * @param input 输入流
	 * @return 成功返回true，否则返回false *
	 */
	public static String uploadFile(String url, int port, String username, String password, String path,
			String filename, String orginfilename) {
		String success = "fail";
		FTPClient ftp = new FTPClient();
		ftp.setControlEncoding("UTF-8");
		// 超时时间(防止FTP僵死)
		ftp.setDataTimeout(1000 * 60 * 10);
		ftp.setDefaultTimeout(1000 * 60 * 10);
		InputStream input = null;
		File zipFile = new File(orginfilename + filename);
		try {
			input = new FileInputStream(zipFile);
			ftp.connect(url, port);// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);// 登录
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				logger.info("----FTP 登录失败----");
				return success;
			}
			// 被动模式
			ftp.enterLocalPassiveMode();
			// 切换到上传目录
			ftp.changeWorkingDirectory(path);
			// 设置传输类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			// ftp.makeDirectory(path);
			boolean storeFile = ftp.storeFile(filename + ".temp", input);
			if (storeFile) {
				if (!ftp.rename(filename + ".temp", filename)) {
					logger.info("----FTP 重命名失败----" + filename + ".temp");
				}
				success = "success";
			}
			ftp.logout();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.info("----FTP 文件读取失败----" + e);
		} catch (SocketException e) {
			logger.info("----FTP 连接失败----" + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("----FTP 上传文件失败----" + e);
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
				if (ftp.isConnected())
					ftp.disconnect();
			} catch (IOException ioe) {
				logger.info("----FTP 关闭失败----");
			}
		}
		if (success.equals("success")) {
			// 文件发送成功删除压缩包
			deleteDir(zipFile);
		}
		return success;
	}

	/**
	 * 压缩结束后删除源文件
	 * @param dir
	 */
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 将本地文件上传到FTP服务器上 *
	 */
	public static String upLoadFromProduction(String filename, String orginfilename) {
		return uploadFile(Constant.url, Constant.port, Constant.username, Constant.password, Constant.path, filename,
				orginfilename);
	}
}