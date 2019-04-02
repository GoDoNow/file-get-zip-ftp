package go.donow;

import org.apache.log4j.PropertyConfigurator;

public class App 
{
	static {
		// 读取日志配置文件
		PropertyConfigurator.configure("log4j.properties");
	}
    public static void main(String[] args) {
		// 初始化参数
		Init.init();
		// 启动定时器
		Operation.operation();
	}
}
