package com.tianliang.spider.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
	public Connection dbConn = null;

	public String driverName = SystemParas.jdbc_driver;
	public String connection_url = SystemParas.jdbc_url;
	public String userName = SystemParas.jdbc_userName;
	public String userPwd = SystemParas.jdbc_userPwd;
	private Statement stat;

	public Statement getStat() {
		return stat;
	}

	public void setStat(Statement stat) {
		this.stat = stat;
	}

	// 采用默认的jdbc参数
	public DBUtils() {
		// 打开链接
		this.openConnection();
		stat = this.createStatement();
	}

	// 非默认的jdbc参数
	public DBUtils(String driverName, String connection_url, String userName,
			String userPwd) {
		this.driverName = driverName;
		this.connection_url = connection_url;
		this.userName = userName;
		this.userPwd = userPwd;
		// 打开链接
		this.openConnection();
		stat = this.createStatement();
	}

	public Statement createStatement() {
		try {
			return this.dbConn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean openConnection() {
		try {
			Class.forName(driverName);
			dbConn = DriverManager.getConnection(connection_url, userName,
					userPwd);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean closeConnection(Connection conn) {
		try {
			if (conn.isClosed()) {
				return true;
			} else {
				conn.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void testCallPrecedure() {
		// openConnection(driverName, connection_url, userName, userPwd);
		openConnection();
		try {
			Statement stat = dbConn.createStatement();
			String sql = "call del_repeat_records_zel 'zel'";
			CallableStatement cmd = null;
			cmd = dbConn.prepareCall(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {
		DBUtils dBUtils = new DBUtils();

		ResultSet rs = dBUtils.getStat().executeQuery(
				"select * from brand_baseinfo");

		ResultSetMetaData rsMetaData = rs.getMetaData();
		int column_size = rsMetaData.getColumnCount();
		// System.out.println();
		for (int i = 1; i <= column_size; i++) {
			System.out.println(rsMetaData.getColumnName(i));
		}

		while (rs.next()) {
			for (int i = 1; i <= column_size; i++) {
				System.out.println(rs.getString(rsMetaData.getColumnName(i)));
			}
		}
		System.out.println("finish!");
	}

}
