package com.fun.hunt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fun.hunt.utils.ConnectionUtil;
import com.fun.hunt.utils.DBUtil;

/**
 * Hello world! http://prod-intranet/portal/
 */
public class IndexBuilder {
	static boolean initialLoad = true;
	static Logger log = Logger.getLogger("IndexBuilder");
	private static final String prefix = "http://prod-intranet/portal/";
	private static final List<String> filterPrefix = new ArrayList<String>();
	private static final String INSERT_LINK_SQL = "insert into links values (?,?,?,?)";
	private static final String SELECT_ACTIVE_LINKS = "SELECT links,id from links where isactive = 'y'";
	private static final String SELECT_LINKS_ALREADY_EXISTS = "select count(1) from links where links = (?)";
	private static final String UPDATE_PROCESSED_LINKS = "update links set isactive ='n' where id = (?)";
	private static final String UPDATE_ERROR_LINKS = "update links set isactive ='error' where id = (?)";

	private static Connection conn = DBUtil.conn;
	static {

		try {
			loadFilterUrls();
			if (initialLoad) {
				DBUtil.createTables();
				DBUtil.insertLinks(conn, INSERT_LINK_SQL, new String[] { "1",
						prefix, prefix, "y" });
			}

		} catch (Exception e) {
			log.severe("Error occured ==>"+ExceptionUtils.getStackTrace(e));
		}
	}

	public static void main(String[] args) throws Exception {
		log.info("Initial Load");
		while (true) {
			List<String> resultList = DBUtil.selectActiveLink(conn,
					SELECT_ACTIVE_LINKS);
			if (resultList != null && resultList.isEmpty()) {
				break;
			}
			try{
				loadHtmlLinks(resultList.get(0), resultList.get(1));	
			}
			catch(Exception ex){
				log.severe("Error Occured ===>"+resultList.get(0)+"==="+ExceptionUtils.getStackTrace(ex));
				DBUtil.updateLink(conn, UPDATE_ERROR_LINKS, resultList.get(1));
			}
			

		}

		ConnectionUtil.getInstance().closeConnection();
		log.info("Completed");
		// loadHtmlLinks(prefix);
	}

	private static void loadHtmlLinks(String url, String linkId)
			throws Exception {
		if(StringUtils.isBlank(url)){
			return;
		}
		Document doc = Jsoup.connect(url).timeout(20000).get();
		Elements links = doc.select("a[href]");
		Set<String> uniqueSet = new HashSet<String>();
		for (Element link : links) {
			if (!skipAddition(link)) {
				uniqueSet.add(link.attr("abs:href"));
			}
		}
		for (String href : uniqueSet) {
			if (!DBUtil.linkAlreadyExists(conn, SELECT_LINKS_ALREADY_EXISTS,
					StringUtils.trim(href))) {
				DBUtil.insertLinks(conn, INSERT_LINK_SQL, new String[] {
						UUID.randomUUID().toString(), StringUtils.trim(url), StringUtils.trim(href), "y" });
			}

		}
		DBUtil.updateLink(conn, UPDATE_PROCESSED_LINKS, linkId);

	}

	private static boolean skipAddition(Element link) {
		String strLink = link.attr("abs:href");
		if (StringUtils.isBlank(strLink)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(strLink, prefix)) {
			return true;
		}
		for (Iterator iterator = filterPrefix.iterator(); iterator.hasNext();) {
			String strFilter = (String) iterator.next();
			if (StringUtils.startsWithIgnoreCase(strLink, strFilter)) {
				return true;
			}
		}
		
		if(!StringUtils.startsWithIgnoreCase(strLink, prefix)){
			return true;
		}

		return false;
	}

	private static void loadFilterUrls() {
		filterPrefix.add("http://prod-intranet/portal/is");
		filterPrefix.add("http://prod-intranet/portal/sysops");
		filterPrefix.add("mailto:");
		filterPrefix.add("http://prod-intranet/portal/profile/");
		filterPrefix.add("http://prod-intranet/portal/our-culture/#wplu");
	}
}
