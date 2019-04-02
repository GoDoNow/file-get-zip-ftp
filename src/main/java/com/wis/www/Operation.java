package com.wis.www;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class Operation {
	private static Logger logger = Logger.getLogger("runlog");

	public static void operation() {
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
		for (Object pro : Constant.JSON) {
			final JSONObject json = (JSONObject) pro;
			final String name = json.getString("name");
			final Integer timer = json.getInteger("timer");
			System.out.println("----产品名称：" + name + " ----定时器间隔--" + timer);
			Runnable run = null;
			run = new Runnable() {
				public void run() {
					logger.info(" get file thread  (run--" + name + ")  start");
					Handle.handerService(name, json);
				}
			};
			// 开发时放开
			// scheduledThreadPool.scheduleAtFixedRate(run, 0, 20, TimeUnit.SECONDS);
			// 生产时放开
			 scheduledThreadPool.scheduleAtFixedRate(run, 0, timer, TimeUnit.MINUTES);
		}

	}
}
