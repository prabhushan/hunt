package com.fun.hunt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fun.hunt.utils.DBUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

public class Downloader {

	private static Connection conn = DBUtil.conn;
	private static final String SELECT_LINKS = "SELECT links from links where isactive = 'n'";
	private static final String SELECT_FILE_LINKS = "SELECT links from links where isactive = 'error'";
	private static final String folderPath = "D:\\cleanup\\huntedfiles\\";
	private static final String INSERT_FILE_SQL = "insert into IMAGES values (?,?)";
	static Logger log = Logger.getLogger("Downloader");

	public static void main(String... strings) throws Exception {
		Downloader downloader = new Downloader();
		//downloader.loadImages();
		//downloader.loadFiles();
		downloader.test();
		log.info("Files Downloaded");
	}

	private void test() throws IOException, InterruptedException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		int i = webClient.waitForBackgroundJavaScript(1000);
		//Page page = webClient.getPage("http://prod-intranet/portal/whos-who/new-employee/");
	
		HtmlPage page = null;
		try {
		    page = webClient.getPage("http://prod-intranet/portal/drfirst-events/");
		} catch (Exception e) {
		    System.out.println("Get page error");
		}
		JavaScriptJobManager manager = page.getEnclosingWindow().getJobManager();
		while (manager.getJobCount() > 0) {
		    Thread.sleep(1000);
		}
//		File file = new File("D:\\cleanup\\sample1.html");
//		FileOutputStream stream = new FileOutputStream(file);
//		stream.write(page.asXml().getBytes());
//		stream.close();
		//System.out.println(page.asXml());
		//return page;
        log.info("Load Content ==>");
        Document doc = Jsoup.parse(page.getWebResponse().getContentAsString());
		//Document doc = Jsoup.connect("http://prod-intranet/portal/whos-who/new-employee/#asf_animated_modal").timeout(20000).get();
		//Elements ele = doc.select("div.scroll-top-wrap");
		//Elements ele =doc.getElementsByTag("div");
		Elements ele = doc.select("div[style^=background-image:]");
		for (Element element : ele) {
			element.attr("style");
			element.cssSelector();
			System.out.println(element.html());
		}
		
	}

	private void loadImages() throws Exception {
		List<String> sourceURL = DBUtil.selectLinks(conn, SELECT_LINKS);
		log.info("Total Count "+sourceURL.size());
		int iLoop =0;
		for (String webSiteURL : sourceURL) {
			log.info("URL Count "+ ++iLoop);
			try {

				// Connect to the website and get the html
				Document doc = Jsoup.connect(webSiteURL).timeout(20000).get();

				// Get all elements with img tag ,
				Elements img = doc.getElementsByTag("img");
				
				for (Element el : img) {

					// for each element get the srs url
					String src = el.absUrl("src");
					if (StringUtils.isNotBlank(src) && !filterFiles(src)) {
						getFiles(webSiteURL,src);
						insertFiles(webSiteURL, src);

					}

				}

			} catch (Exception ex) {
				log.severe("Error Occurred ==> " + webSiteURL + "==="
						+ ExceptionUtils.getStackTrace(ex));
			}
		}

	}

	private void loadFiles() throws Exception {
		List<String> sourceURL = DBUtil.selectLinks(conn, SELECT_FILE_LINKS);
		log.info("Total Count " + sourceURL.size());
		for (String fileURL : sourceURL) {
			try {
				getFiles("FILE URL", fileURL);
				insertFiles("FILE URL", fileURL);
			} catch (Exception ex) {
				log.severe("Error Occurred ==> " + fileURL + "===" + ExceptionUtils.getStackTrace(ex));
			}
		}
	}

	private void insertFiles(String sourcelink, String fileName)
			throws Exception {
		DBUtil.insertImagesLinks(conn, INSERT_FILE_SQL, new String[] {
				sourcelink, fileName });
	}

	private static void getFiles(String webSiteUrl,String src) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {

			String folder = null;
			// Exctract the name of the image from the src attribute
			int indexname = src.lastIndexOf("/");
			if (indexname == src.length()) {
				src = src.substring(1, indexname);
			}
			indexname = src.lastIndexOf("/");
			String name = src.substring(indexname, src.length());
			// Open a URL Stream
			URL url = new URL(src);
			in = url.openStream();
			out = new BufferedOutputStream(new FileOutputStream(folderPath
					+ name));
			for (int b; (b = in.read()) != -1;) {
				out.write(b);
			}

		} catch (Exception ex) {
			log.severe("Error Occurred ==> " +webSiteUrl+"^^^^^"+ src + "==="
					+ ExceptionUtils.getStackTrace(ex));
		} finally {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		}
	}
	
	private boolean filterFiles(String src){
		if(StringUtils.contains(src, ".php")){
			return true;
		}
		return false;
	}
}
