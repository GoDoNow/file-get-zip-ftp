package go.donow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Init {
	private static Logger logger = Logger.getLogger(Init.class);

	public static void init() {
		InputStream config_input = null;
		try {
			logger.info("----项目初始化：读取配置文件----");
			// 读取FTP和定时器参数
			config_input = new FileInputStream(new File("./config.properties"));
			Properties prop = new Properties();
			prop.load(config_input);
			// 定时器参数读取
			Constant.FROM_ZIP_PATH = prop.getProperty("FROM_ZIP_PATH");
			Constant.TARGET_ZIP_PATH = prop.getProperty("TARGET_ZIP_PATH");
			Constant.START_DELAY = Integer.parseInt(prop.getProperty("START_DELAY"));
			// FTP参数读取
			Constant.url = prop.getProperty("url");// FTP服务器 hostname
			Constant.port = Integer.parseInt(prop.getProperty("port"));// FTP服务器端口
			Constant.username = prop.getProperty("username"); // FTP登录账号
			Constant.password = prop.getProperty("password"); // FTP登录密码
			Constant.path = prop.getProperty("path"); // FTP服务器保存目录
		} catch (IOException e) {
			logger.info("----*.properties 文件不存在----");
			return;
		} finally {
			try {
				if (config_input != null)
					config_input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader bfrd = null;
		try {
			// 本地开发放开
			InputStream input = new FileInputStream(new File("./test.json"));
			InputStreamReader isr = new InputStreamReader(input);
			bfrd = new BufferedReader(isr);
			String json = "";
			String line = "";
			while ((line = bfrd.readLine()) != null) {
				json = json + line;
			}
			JSONObject obj = JSONObject.parseObject(json);
			logger.info(obj);
			JSONArray array = obj.getJSONArray("product");
			Constant.JSON = array;
		} catch (Exception e) {
			logger.info("----Exception----" + e);
		} finally {
			if (bfrd != null) {// 关闭流
				try {
					bfrd.close();
				} catch (Exception e) {
					logger.info("通道关闭异常：" + e);
				}
			}
		}
	}
}
