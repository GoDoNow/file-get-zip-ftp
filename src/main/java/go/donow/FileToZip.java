package go.donow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * 压缩算法类 实现文件压缩，文件夹压缩，以及文件和文件夹的混合压缩
 * @author lihy
 */
public class FileToZip {
	public FileToZip() {
	}

	public void zipFiles(String getPath, String zipPath, String fileName, Logger logger) {
		logger.info("----zip file----:source: " + getPath + "  target: " + zipPath + fileName);
		File srcfile = new File(getPath);
		File targetFile = new File(zipPath + fileName);
		if (!srcfile.exists()) {// 压缩源文件目录不存在返回
			logger.error("----压缩数据源文件不存在----");
			return;
		}
		if (targetFile.exists()) {// 压缩目的文件存在删除
			targetFile.delete();
		} else {
			File targetParent = targetFile.getParentFile();
			if (!targetParent.exists()) {// 压缩目的文件目录不存在创建
				targetParent.mkdirs();
			}
		}
		ZipOutputStream out = null;
		boolean flag = false;// 文件压缩成功标志
		try {
			out = new ZipOutputStream(new FileOutputStream(targetFile));
			if (srcfile.isFile()) {
				zipFile(srcfile, out, "");
			} else {
				File[] list = srcfile.listFiles();
				for (int i = 0; i < list.length; i++) {
					compress(list[i], out, "");
				}
			}
			flag = true;
			logger.info("----file zip end--success----");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (flag) {// 防止读写流冲突
			// 压缩结束后删除源文件
			deleteDir(srcfile);
			String re = SendFile.upLoadFromProduction(fileName, zipPath);
			if ("fail".equals(re)) {
				re = SendFile.upLoadFromProduction(fileName, zipPath);
			}
			logger.info("---- send " + zipPath + fileName + " zip " + re + "----");
		}
	}

	/**
	 * 压缩文件夹里的文件 起初不知道是文件还是文件夹--- 统一调用该方法
	 * 
	 * @param file
	 * @param out
	 * @param basedir
	 */
	private void compress(File file, ZipOutputStream out, String basedir) {
		/* 判断是目录还是文件 */
		if (file.isDirectory()) {
			this.zipDirectory(file, out, basedir);
		} else {
			this.zipFile(file, out, basedir);
		}
	}

	/**
	 * 压缩单个文件
	 * @param srcfile
	 */
	public void zipFile(File srcfile, ZipOutputStream out, String basedir) {
		if (!srcfile.exists())
			return;
		byte[] buf = new byte[1024];
		FileInputStream in = null;
		try {
			int len;
			in = new FileInputStream(srcfile);
			out.putNextEntry(new ZipEntry(basedir + srcfile.getName()));

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.closeEntry();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 压缩文件夹
	 * 
	 * @param dir
	 * @param out
	 * @param basedir
	 */
	public void zipDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists())
			return;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* 递归 */
			compress(files[i], out, basedir + dir.getName() + "/");
		}
	}

	/**
	 * 压缩结束后删除源文件
	 * 
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
}