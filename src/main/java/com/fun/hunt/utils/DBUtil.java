package com.fun.hunt.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
	public static Connection conn = ConnectionUtil.getInstance()
			.getConnection();
	
	public static void insertLinks(Connection conn, String query, String[] args)
			throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			for (int i = 0; i < args.length; i++) {
				stmt.setString(i+1, args[i]);
			}
			stmt.executeUpdate();

		}
	}
	

	public static List<String> selectActiveLink(Connection conn, String query) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			ResultSet results = stmt.executeQuery();
			List<String> resultList = new ArrayList<String>();
			if(results!=null && results.next()){
				resultList.add(results.getString("links"));
				resultList.add(results.getString("id"));
			}
			return resultList;

		}
	}
	
	//Select links to crawl for images and files.
	public static List<String> selectLinks(Connection conn, String query) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			ResultSet results = stmt.executeQuery();
			List<String> resultList = new ArrayList<String>();
			while(results!=null && results.next()){
				resultList.add(results.getString("links"));
				//resultList.add(results.getString("id"));
			}
			return resultList;

		}
	}
	

	//insert images and links to the table for reference
	public static void insertImagesLinks(Connection conn, String query, String[] args)
			throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			for (int i = 0; i < args.length; i++) {
				stmt.setString(i+1, args[i]);
			}
			stmt.executeUpdate();

		}
	}

	public static boolean linkAlreadyExists(Connection conn, String query,String link) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setString(1, link);
			ResultSet results = stmt.executeQuery();
			if(results!=null && results.next()){
				if(results.getInt(1) > 0){
					return true;
				}
			}
			return false;

		}
	}
	public static void updateLink(Connection conn, String query,String linkId) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setString(1, linkId);
			stmt.executeUpdate();
		}
	}

	public static void createTables() throws SQLException {
		try (Statement stmt = conn.createStatement()) {
			StringBuilder builder = new StringBuilder();
			builder.append("create table links (");
			builder.append("id varchar2, source varchar2,links varchar2,isactive varchar2)");
			stmt.executeUpdate(builder.toString());

			StringBuilder builderImages = new StringBuilder();
			builderImages.append("create table images (");
			builderImages.append("sourceid varchar2,image varchar2)");
			stmt.executeUpdate(builderImages.toString());
		}

	}

}
