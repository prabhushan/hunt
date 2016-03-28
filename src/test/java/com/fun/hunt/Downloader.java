package com.fun.hunt;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;

import com.fun.hunt.utils.DBUtil;

public class Downloader {

	private static Connection conn = DBUtil.conn;
	private static final String SELECT_LINKS = "SELECT links,id from links where isactive = 'n'";
	private static final String SELECT_FILE_LINKS = "SELECT links,id from links where isactive = 'error'";

	public static void main(String... strings) {
		Downloader downloader = new Downloader();
	}

	private void loadImages() {

	}

	private void loadFiles() {

	}

	private static void getImages(String src) throws IOException {

		String folder = null;
		// Exctract the name of the image from the src attribute
		int indexname = src.lastIndexOf("/");
		if (indexname == src.length()) {
			src = src.substring(1, indexname);
		}
		indexname = src.lastIndexOf("/");
		String name = src.substring(indexname, src.length());
		System.out.println(name);
		// Open a URL Stream
		URL url = new URL(src);
		InputStream in = url.openStream();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				folderPath + name));
		for (int b; (b = in.read()) != -1;) {
			out.write(b);
		}
		out.close();
		in.close();
	}
}
