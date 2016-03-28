package com.fun.hunt.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionUtil {
	private Connection conn = null;
	private static ConnectionUtil instance = new ConnectionUtil();
	
	private ConnectionUtil() {
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection("jdbc:h2:" + "D:\\cleanup\\indexcreater\\" + "reference");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final ConnectionUtil getInstance() {
		return instance;
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.conn = null;
		// To make the instance not usable anymore
		instance = null;
	}
}

