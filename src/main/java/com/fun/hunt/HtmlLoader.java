package com.fun.hunt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

public class HtmlLoader {
	static Logger log = Logger.getLogger("HtmlLoader");
	private static final String folderPath = "D:\\cleanup\\huntedfiles\\";

	public static String getHTMLSource(String url) throws Exception{
		try(WebClient webClient = new WebClient(BrowserVersion.CHROME)){
			webClient.waitForBackgroundJavaScript(1000);
			//Page page = webClient.getPage("http://prod-intranet/portal/whos-who/new-employee/");
		
			HtmlPage page = null;
			try {
			    page = webClient.getPage(url);
			} catch (Exception e) {
			    log.severe("Get page error ==>" + ExceptionUtils.getStackTrace(e));
			}
			JavaScriptJobManager manager = page.getEnclosingWindow().getJobManager();
			int iLoop = 0;
			while (manager.getJobCount() > 0) {
				if(iLoop > 50){
					break;
				}
				else{
					iLoop++;
				}
			    Thread.sleep(1000);
			}
			String source = page.asXml();
			createFile(url,source);
			
			
			//System.out.println(page.asXml());
			//return page;
	        log.info("Load Content ==>");
	        
			return source;
		}
		
		
	}
	
	private static void createFile(String src, String source) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			
			// Exctract the name of the image from the src attribute
			String name = src.replace("http://", "-").replace("/","-")+".txt";
			//File file = new File(folderPath + name);
			// FileOutputStream stream = new FileOutputStream(file);
			// stream.write(page.asXml().getBytes());
			// stream.close();
			// System.out.println(page.asXml());
			// Open a URL Stream
			File file = new File(folderPath+name);
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(source.getBytes());
			stream.close();
		} catch (Exception e) {
			log.severe("Error occured while download ==> "+src+"=="+ExceptionUtils.getStackTrace(e));
		}
		finally{
			if(in !=null)
			in.close();
			if(out !=null)
			out.close();
		}

	}
	
	public static void main(String...strings) throws Exception{
		getHTMLSource("http://prod-intranet/portal/tips-on-how-to-disagree-with-a-leader/");
	}

}
