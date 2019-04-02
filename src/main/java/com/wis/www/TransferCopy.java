package com.wis.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.apache.log4j.*;

public class TransferCopy {
	/**   
	 * @Title: transferCopy   
	 * @Description: 根据文件源和目的路径复制文件并且返回结果   
	 * @param: @param source
	 * @param: @param target
	 * @param: @param logger
	 * @param: @return      
	 * @return: boolean      
	 * @throws   
	 */
	public static boolean transferCopy(String source, String target, Logger logger) {
		logger.info("---get file----  source: " + source + "  target：" + target);
		// 判断文件是否存在
		File file = new File(target);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		// 进行复制操作
		FileInputStream inStream = null;
		FileChannel in = null;
		FileChannel out = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(source);
			in = inStream.getChannel();
			outStream = new FileOutputStream(target);
			out = outStream.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (FileNotFoundException e1) {
			logger.error("----源文件不存在----:source: " + source + "  target：" + target);
			return false;
		} catch (IOException e) {
			logger.error("----文件复制失败----source: " + source + "  target：" + target + "  " + e);
			return false;
		} finally {
			try {
				if (null != inStream)
					inStream.close();
				if (null != in)
					in.close();
				if (null != outStream)
					outStream.close();
				if (null != out)
					out.close();
			} catch (IOException e) {
				logger.error("----io通道关闭失败----source: " + source + "  target：" + target);
				return false;
			}
		}
		return true;
	}
}
