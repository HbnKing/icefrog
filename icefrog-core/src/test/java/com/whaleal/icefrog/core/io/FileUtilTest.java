package com.whaleal.icefrog.core.io;

import com.whaleal.icefrog.core.io.file.LineSeparator;
import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * {@link FileUtil} 单元测试类
 *
 * @author Looly
 * @author wh
 */
public class FileUtilTest {

	@Test(expected = IllegalArgumentException.class)
	public void fileTest() {
		File file = FileUtil.file("d:/aaa", "bbb");
		Assert.assertNotNull(file);

		// 构建目录中出现非子目录抛出异常
		FileUtil.file(file, "../ccc");

		FileUtil.file("E:/");
	}

	@Test
	public void getAbsolutePathTest() {
		String absolutePath = FileUtil.getAbsolutePath("LICENSE-junit.txt");
		Assert.assertNotNull(absolutePath);
		String absolutePath2 = FileUtil.getAbsolutePath(absolutePath);
		Assert.assertNotNull(absolutePath2);
		Assert.assertEquals(absolutePath, absolutePath2);

		String path = FileUtil.getAbsolutePath("中文.xml");
		Assert.assertTrue(path.contains("中文.xml"));

		path = FileUtil.getAbsolutePath("d:");
		Assert.assertEquals("d:", path);
	}

	@Test
	@Ignore
	public void touchTest() {
		FileUtil.touch("d:\\tea\\a.jpg");
	}

	@Test
	@Ignore
	public void delTest() {
		// 删除一个不存在的文件，应返回true
		boolean result = FileUtil.del("e:/icefrog_test_3434543533409843.txt");
		Assert.assertTrue(result);
	}

	@Test
	@Ignore
	public void delTest2() {
		// 删除一个不存在的文件，应返回true
		boolean result = FileUtil.del(Paths.get("e:/icefrog_test_3434543533409843.txt"));
		Assert.assertTrue(result);
	}

	@Test
	@Ignore
	public void renameTest() {
		FileUtil.rename(FileUtil.file("d:/test/3.jpg"), "2.jpg", false);
	}

	@Test
	@Ignore
	public void renameTest2() {
		FileUtil.move(FileUtil.file("d:/test/a"), FileUtil.file("d:/test/b"), false);
	}

	@Test
	public void copyTest() {
		File srcFile = FileUtil.file("icefrog.jpg");
		File destFile = FileUtil.file("icefrog.copy.jpg");

		FileUtil.copy(srcFile, destFile, true);

		Assert.assertTrue(destFile.exists());
		Assert.assertEquals(srcFile.length(), destFile.length());
	}

	@Test
	@Ignore
	public void copyFilesFromDirTest() {
		File srcFile = FileUtil.file("D:\\驱动");
		File destFile = FileUtil.file("d:\\驱动备份");

		FileUtil.copyFilesFromDir(srcFile, destFile, true);
	}

	@Test
	@Ignore
	public void copyDirTest() {
		File srcFile = FileUtil.file("D:\\test");
		File destFile = FileUtil.file("E:\\");

		FileUtil.copy(srcFile, destFile, true);
	}

	@Test
	@Ignore
	public void moveDirTest() {
		File srcFile = FileUtil.file("E:\\test2");
		File destFile = FileUtil.file("D:\\");

		FileUtil.move(srcFile, destFile, true);
	}

	@Test
	public void equalsTest() {
		// 源文件和目标文件都不存在
		File srcFile = FileUtil.file("d:/icefrog.jpg");
		File destFile = FileUtil.file("d:/icefrog.jpg");

		boolean equals = FileUtil.equals(srcFile, destFile);
		Assert.assertTrue(equals);

		// 源文件存在，目标文件不存在
		File srcFile1 = FileUtil.file("icefrog.jpg");
		File destFile1 = FileUtil.file("d:/icefrog.jpg");

		boolean notEquals = FileUtil.equals(srcFile1, destFile1);
		Assert.assertFalse(notEquals);
	}

	@Test
	@Ignore
	public void convertLineSeparatorTest() {
		FileUtil.convertLineSeparator(FileUtil.file("d:/aaa.txt"), CharsetUtil.CHARSET_UTF_8, LineSeparator.WINDOWS);
	}

	@Test
	public void normalizeTest() {
		Assert.assertEquals("/foo/", FileUtil.normalize("/foo//"));
		Assert.assertEquals("/foo/", FileUtil.normalize("/foo/./"));
		Assert.assertEquals("/bar", FileUtil.normalize("/foo/../bar"));
		Assert.assertEquals("/bar/", FileUtil.normalize("/foo/../bar/"));
		Assert.assertEquals("/baz", FileUtil.normalize("/foo/../bar/../baz"));
		Assert.assertEquals("/", FileUtil.normalize("/../"));
		Assert.assertEquals("foo", FileUtil.normalize("foo/bar/.."));
		Assert.assertEquals("../bar", FileUtil.normalize("foo/../../bar"));
		Assert.assertEquals("bar", FileUtil.normalize("foo/../bar"));
		Assert.assertEquals("/server/bar", FileUtil.normalize("//server/foo/../bar"));
		Assert.assertEquals("/bar", FileUtil.normalize("//server/../bar"));
		Assert.assertEquals("C:/bar", FileUtil.normalize("C:\\foo\\..\\bar"));
		//
		Assert.assertEquals("C:/bar", FileUtil.normalize("C:\\..\\bar"));
		Assert.assertEquals("../../bar", FileUtil.normalize("../../bar"));
		Assert.assertEquals("C:/bar", FileUtil.normalize("/C:/bar"));
		Assert.assertEquals("C:", FileUtil.normalize("C:"));
		Assert.assertEquals("\\/192.168.1.1/Share/", FileUtil.normalize("\\\\192.168.1.1\\Share\\"));
	}

	@Test
	public void normalizeBlankTest() {
		Assert.assertEquals("C:/aaa ", FileUtil.normalize("C:\\aaa "));
	}

	@Test
	public void normalizeHomePathTest() {
		String home = FileUtil.getUserHomePath().replace('\\', '/');
		Assert.assertEquals(home + "/bar/", FileUtil.normalize("~/foo/../bar/"));
	}

	@Test
	public void normalizeHomePathTest2() {
		String home = FileUtil.getUserHomePath().replace('\\', '/');
		// 多个~应该只替换开头的
		Assert.assertEquals(home + "/~bar/", FileUtil.normalize("~/foo/../~bar/"));
	}

	@Test
	public void normalizeClassPathTest() {
		Assert.assertEquals("", FileUtil.normalize("classpath:"));
	}

	@Test
	public void normalizeClassPathTest2() {
		Assert.assertEquals("../a/b.csv", FileUtil.normalize("../a/b.csv"));
		Assert.assertEquals("../../../a/b.csv", FileUtil.normalize("../../../a/b.csv"));
	}

	@Test
	public void doubleNormalizeTest() {
		String normalize = FileUtil.normalize("/aa/b:/c");
		String normalize2 = FileUtil.normalize(normalize);
		Assert.assertEquals("/aa/b:/c", normalize);
		Assert.assertEquals(normalize, normalize2);
	}

	@Test
	public void subPathTest() {
		Path path = Paths.get("/aaa/bbb/ccc/ddd/eee/fff");

		Path subPath = FileUtil.subPath(path, 5, 4);
		Assert.assertEquals("eee", subPath.toString());
		subPath = FileUtil.subPath(path, 0, 1);
		Assert.assertEquals("aaa", subPath.toString());
		subPath = FileUtil.subPath(path, 1, 0);
		Assert.assertEquals("aaa", subPath.toString());

		// 负数
		subPath = FileUtil.subPath(path, -1, 0);
		Assert.assertEquals("aaa/bbb/ccc/ddd/eee", subPath.toString().replace('\\', '/'));
		subPath = FileUtil.subPath(path, -1, Integer.MAX_VALUE);
		Assert.assertEquals("fff", subPath.toString());
		subPath = FileUtil.subPath(path, -1, path.getNameCount());
		Assert.assertEquals("fff", subPath.toString());
		subPath = FileUtil.subPath(path, -2, -3);
		Assert.assertEquals("ddd", subPath.toString());
	}

	@Test
	public void subPathTest2() {
		String subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc/");
		Assert.assertEquals("ccc/", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/");
		Assert.assertEquals("ccc/", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/test.txt");
		Assert.assertEquals("ccc/test.txt", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc");
		Assert.assertEquals("ccc", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc");
		Assert.assertEquals("ccc", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb");
		Assert.assertEquals("", subPath);

		subPath = FileUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb");
		Assert.assertEquals("", subPath);
	}

	@Test
	public void getPathEle() {
		Path path = Paths.get("/aaa/bbb/ccc/ddd/eee/fff");

		Path ele = FileUtil.getPathEle(path, -1);
		Assert.assertEquals("fff", ele.toString());
		ele = FileUtil.getPathEle(path, 0);
		Assert.assertEquals("aaa", ele.toString());
		ele = FileUtil.getPathEle(path, -5);
		Assert.assertEquals("bbb", ele.toString());
		ele = FileUtil.getPathEle(path, -6);
		Assert.assertEquals("aaa", ele.toString());
	}

	@Test
	public void listFileNamesTest() {
		List<String> names = FileUtil.listFileNames("classpath:");
		Assert.assertTrue(names.contains("icefrog.jpg"));

		names = FileUtil.listFileNames("");
		Assert.assertTrue(names.contains("icefrog.jpg"));

		names = FileUtil.listFileNames(".");
		Assert.assertTrue(names.contains("icefrog.jpg"));
	}

	@Test
	@Ignore
	public void listFileNamesInJarTest() {
		List<String> names = FileUtil.listFileNames("d:/test/icefrog-core-5.1.0.jar!/cn/icefrog/core/util ");
		for (String name : names) {
			Console.log(name);
		}
	}

	@Test
	@Ignore
	public void listFileNamesTest2() {
		List<String> names = FileUtil.listFileNames("D:\\m2_repo\\icefrogs-cli\\icefrogs-cli\\1.0\\icefrogs-cli-1.0.jar!org/apache/icefrogs/cli/");
		for (String string : names) {
			Console.log(string);
		}
	}

	@Test
	@Ignore
	public void loopFilesTest() {
		List<File> files = FileUtil.loopFiles("d:/");
		for (File file : files) {
			Console.log(file.getPath());
		}
	}

	@Test
	@Ignore
	public void loopFilesWithDepthTest() {
		List<File> files = FileUtil.loopFiles(FileUtil.file("d:/m2_repo"), 2, null);
		for (File file : files) {
			Console.log(file.getPath());
		}
	}

	@Test
	public void getParentTest() {
		// 只在Windows下测试
		if (FileUtil.isWindows()) {
			File parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 0);
			Assert.assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc\\ddd"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 1);
			Assert.assertEquals(FileUtil.file("d:\\aaa\\bbb\\cc"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 2);
			Assert.assertEquals(FileUtil.file("d:\\aaa\\bbb"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 4);
			Assert.assertEquals(FileUtil.file("d:\\"), parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 5);
			Assert.assertNull(parent);

			parent = FileUtil.getParent(FileUtil.file("d:/aaa/bbb/cc/ddd"), 10);
			Assert.assertNull(parent);
		}
	}

	@Test
	public void lastIndexOfSeparatorTest() {
		String dir = "d:\\aaa\\bbb\\cc\\ddd";
		int index = FileUtil.lastIndexOfSeparator(dir);
		Assert.assertEquals(13, index);

		String file = "ddd.jpg";
		int index2 = FileUtil.lastIndexOfSeparator(file);
		Assert.assertEquals(-1, index2);
	}

	@Test
	public void getNameTest() {
		String path = "d:\\aaa\\bbb\\cc\\ddd\\";
		String name = FileUtil.getName(path);
		Assert.assertEquals("ddd", name);

		path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
		name = FileUtil.getName(path);
		Assert.assertEquals("ddd.jpg", name);
	}

	@Test
	public void mainNameTest() {
		String path = "d:\\aaa\\bbb\\cc\\ddd\\";
		String mainName = FileUtil.mainName(path);
		Assert.assertEquals("ddd", mainName);

		path = "d:\\aaa\\bbb\\cc\\ddd";
		mainName = FileUtil.mainName(path);
		Assert.assertEquals("ddd", mainName);

		path = "d:\\aaa\\bbb\\cc\\ddd.jpg";
		mainName = FileUtil.mainName(path);
		Assert.assertEquals("ddd", mainName);
	}

	@Test
	public void extNameTest() {
		String path =  FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd\\" : "~/Desktop/icefrog/ddd/";
		String mainName = FileUtil.extName(path);
		Assert.assertEquals("", mainName);

		path =  FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd" : "~/Desktop/icefrog/ddd";
		mainName = FileUtil.extName(path);
		Assert.assertEquals("", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\ddd.jpg" : "~/Desktop/icefrog/ddd.jpg";
		mainName = FileUtil.extName(path);
		Assert.assertEquals("jpg", mainName);

		path = FileUtil.isWindows() ? "d:\\aaa\\bbb\\cc\\fff.xlsx" : "~/Desktop/icefrog/fff.xlsx";
		mainName = FileUtil.extName(path);
		Assert.assertEquals("xlsx", mainName);
	}

	@Test
	public void getWebRootTest() {
		File webRoot = FileUtil.getWebRoot();
		Assert.assertNotNull(webRoot);
		Assert.assertEquals("icefrog-core", webRoot.getName());
	}

	@Test
	public void getMimeTypeTest() {
		String mimeType = FileUtil.getMimeType("test2Write.jpg");
		Assert.assertEquals("image/jpeg", mimeType);

		mimeType = FileUtil.getMimeType("test2Write.html");
		Assert.assertEquals("text/html", mimeType);

		mimeType = FileUtil.getMimeType("main.css");
		Assert.assertEquals("text/css", mimeType);

		mimeType = FileUtil.getMimeType("test.js");
		Assert.assertEquals("application/x-javascript", mimeType);
	}

	@Test
	public void isSubTest() {
		File file = new File("d:/test");
		File file2 = new File("d:/test2/aaa");
		Assert.assertFalse(FileUtil.isSub(file, file2));
	}

	@Test
	public void isSubRelativeTest() {
		File file = new File("..");
		File file2 = new File(".");
		Assert.assertTrue(FileUtil.isSub(file, file2));
	}
}
