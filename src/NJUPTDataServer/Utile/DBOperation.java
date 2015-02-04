package NJUPTDataServer.Utile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBOperation implements IDBOperation {
	protected Connection conn = null;
	protected   String URL = "jdbc:postgresql://202.119.236.102:5432/proto";
	protected   String USER = "postgres"; // 根据你自己设置的数据库连接用户进行设置
	protected   String PASSWORD = "admin"; // 根据你自己设置的数据库连接密码进行设置

	public DBOperation() {
	    try {
            //1-21修改-建立的连接什么时候关闭
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
  
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	public boolean ini(String ip,String port,String sid, String user, String pw) {
		return false;
	}


	public void closed() {
		try {
			if(!conn.isClosed()) {conn.close();}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void excuteNoResult(String sql) {
		try {
			Statement stmt = conn.createStatement();// 执行SQL语句
			
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("SQL语句执行异常:"+sql);
		}
	}
	
	public String excuteWithResult(String sql) {
		String result = "";
		try {
			Statement stmt = conn.createStatement();// 执行SQL语句
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			int fieldcount=rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for(int i=1;i<fieldcount+1;i++){
					String temp=rs.getString(i);
					if(temp==null){
						result=result+",";
					}
					else{
					result=result+temp.trim()+",";}
				}
				//去掉尾部的逗号
				if (result.length()>0){result=result.substring(0, result.length()-1);}
				result=result+";";
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("SQL语句执行异常:"+sql);
		} 
		//去掉尾部的分号
		if (result.length()>0){result=result.substring(0, result.length()-1);}
		return result;
	}

	public ResultSet excuteWithSetResult(String sql) {
		// TODO Auto-generated method stub
	    ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return rs;
	}

}
