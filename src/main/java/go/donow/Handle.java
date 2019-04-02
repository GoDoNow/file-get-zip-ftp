package go.donow;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Handle {
	private static SimpleDateFormat sdf_ymdhms = new SimpleDateFormat("yyyyMMddHHmmss");
	private static FileToZip fileToZip = new FileToZip();
	private static Map<String, Long> timeStampMap = new HashMap<String, Long>();

	public static void handerService(String fileKey, JSONObject json) {
		Logger logger = Logger.getLogger("runlog");
		boolean flag = false; // 是否获取到新增文件的记录
		JSONArray array = json.getJSONArray("path");
		for (Object pro : array) {
			JSONObject product = (JSONObject) pro;
			String getRootPath = json.getString("root"); // 抓取根目录
			String getPath = getRootPath + product.getString("src"); // 抓取目录
			Long timeStamp = timeStampMap.get(getPath);
			// ----第一次加载以当前时间为时间戳并记录下来
			if (timeStamp == null || timeStamp == 0) {
				Calendar calendar = Calendar.getInstance();
				long firstStamp = calendar.getTimeInMillis();
				timeStampMap.put(getPath, firstStamp);
				logger.info("---first get firstStamp = nowStamp -----" + firstStamp);
				continue;
			}
			// ----文件筛选规则-----------
			String nameRule = json.getString("regex"); // 命名规则
			String recursion = json.getString("recursion"); // 是否递归搜索
			List<String> pathList = new ArrayList<String>();
			if (recursion == "true") {
				pathList = FileListSort.getFilePathNameRecursion(getPath, timeStamp, nameRule, logger);
			} else {
				pathList = FileListSort.getFilePathName(getPath, timeStamp, nameRule, logger);
			}
			// 判断历史文件的时间戳，并返回新增文件名列表
			int size = pathList.size() - 1;
			if (size == 0) {// 没有新文件
				continue;
			}
			// 有新文件更新截至时间戳
			timeStampMap.put(getPath, Long.valueOf(pathList.get(size)));
			logger.info("--------path: " + getPath + "----file number: " + (size - 1));
			// 目标路径 = name + dst
			String fn = json.getString("name");// 产品名
			String putPath = Constant.FROM_ZIP_PATH + fn + File.separator + fn + product.getString("dst"); // 目标路径
			for (int j = 0; j < size; j += 2) {
				String fileName = pathList.get(j);
				String filePath = pathList.get(j + 1);
				String replacePath = putPath + fileName;
				boolean ret = TransferCopy.transferCopy(filePath, replacePath, logger);
				if (!ret) {// 文件抓取失败补抓一次
					ret = TransferCopy.transferCopy(filePath, replacePath, logger);
				} else {
					flag = true;
				}
			}
		}
		logger.info("--------fileKey: " + fileKey + "----get flag: " + flag);
		if (flag) {
			String dt = sdf_ymdhms.format(new Date());
			String filePath = Constant.FROM_ZIP_PATH + fileKey;
			String zipPath = Constant.TARGET_ZIP_PATH;
			fileToZip.zipFiles(filePath, zipPath, fileKey + "_" + dt + ".zip", logger);
		}
	}

	/**
	* 文件重命名
	* @param fileName
	* @param type
	* @return
	*/
	// public static String updateFileName(String fileName, String type, int year) {
	// switch (type) {
	// case "upper-air-chart500":
	// case "upper-air-chart700":
	// case "upper-air-chart850":
	// case "upper-air-chart925":
	// case "ground-actual-weather-map":
	// case "Automatic-station-ground-1-hour":
	// case "lightning-location":
	// return String.format("%02d%s0000.%s", year / 100, fileName.substring(0, 8),
	// fileName.substring(9));
	// case "precipitation":
	// case "fog-fall-area":
	// case "high-temperature-falling-zone":
	// case "dust-fall-area":
	// return String.format("%02d%s", year % 100, fileName.substring(2));
	// case "haze-area":
	// return String.format("%02d%s", year % 100, fileName.substring(4));
	// }
	// return fileName;
	// }
}