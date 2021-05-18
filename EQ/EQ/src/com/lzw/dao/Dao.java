package com.lzw.dao;
import java.awt.Rectangle;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import com.lzw.userList.User;
public class Dao {
	// 数据库驱动
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String url = "jdbc:derby:db_EQ";// 数据库URL
	private static Connection conn = null;// 数据库连接
	private static Dao dao = null;
	private Dao() {
		try {
			Class.forName(driver);
			if (!dbExists()) {
				conn = DriverManager.getConnection(url + ";create=true");
				createTable();
			} else
				conn = DriverManager.getConnection(url);
			addDefUser();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "数据库连接异常，或者本软件已经运行。");
			System.exit(0);
		}
	}
	private boolean dbExists() {// 测试数据库是否存在
		boolean bExists = false;
		File dbFileDir = new File("db_EQ");
		if (dbFileDir.exists()) {
			bExists = true;
		}
		return bExists;
	}
	public static Dao getDao() {// 获取DAO实例
		if (dao == null)
			dao = new Dao();
		return dao;
	}
	public List<User> getUsers() {// 获取所有用户
		List<User> users = new ArrayList<User>();
		try {
			String sql = "select * from tb_users";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				User user = new User();
				user.setIp(rs.getString(1));
				user.setHost(rs.getString(2));
				user.setName(rs.getString(3));
				user.setTipText(rs.getString(4));
				user.setIcon(rs.getString(5));
				users.add(user);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public User getUser(String ip) {// 获取指定IP的用户
		String sql = "select * from tb_users where ip=?";
		User user = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, ip);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setIp(rs.getString(1));
				user.setHost(rs.getString(2));
				user.setName(rs.getString(3));
				user.setTipText(rs.getString(4));
				user.setIcon(rs.getString(5));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}
	public void addUser(User user) {// 添加用户
		try {
			String sql = "insert into tb_users values(?,?,?,?,?)";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getIp());
			ps.setString(2, user.getHost());
			ps.setString(3, user.getName());
			ps.setString(4, user.getTipText());
			ps.setString(5, user.getIcon());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateUser(User user) {// 修改用户
		try {
			String sql = "update tb_users set host=?,name=?,tooltip=?,icon=? where ip='"
					+ user.getIp() + "'";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getHost());
			ps.setString(2, user.getName());
			ps.setString(3, user.getTipText());
			ps.setString(4, user.getIcon());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void delUser(User user) {// 删除用户
		try {
			String sql = "delete from tb_users where ip=?";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getIp());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateLocation(Rectangle location) {// 更新窗体位置
		String sql = "update tb_location set xLocation=?,yLocation=?,width=?,height=?";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, location.x);
			pst.setInt(2, location.y);
			pst.setInt(3, location.width);
			pst.setInt(4, location.height);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Rectangle getLocation() { // 获取窗体位置
		Rectangle rec = new Rectangle(100, 0, 240, 500);
		String sql = "select * from tb_location";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rec.x = rs.getInt(1);
				rec.y = rs.getInt(2);
				rec.width = rs.getInt(3);
				rec.height = rs.getInt(4);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rec;
	}
	public void createTable() { // 创建数据表格
		String createUserSql = "CREATE TABLE tb_users ("
				+ "ip varchar(16) primary key," + "host varchar(30),"
				+ "name varchar(20)," + "tooltip varchar(50),"
				+ "icon varchar(50))";
		String createLocationSql = "CREATE TABLE tb_location ("
				+ "xLocation int," + "yLocation int," + "width int,"
				+ "height int)";
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(createUserSql);
			stmt.execute(createLocationSql);
			addDefLocation();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void addDefUser() {// 创建本机用户
		try {
			InetAddress local = InetAddress.getLocalHost();
			User user = new User();
			user.setIp(local.getHostAddress());
			user.setHost(local.getHostName());
			user.setName(local.getHostName());
			user.setTipText(local.getHostAddress());
			user.setIcon("1.gif");
			if (getUser(user.getIp()) == null) {
				addUser(user);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	public void addDefLocation() {// 添加默认窗体位置
		String sql = "insert into tb_location values(?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, 100);
			pst.setInt(2, 0);
			pst.setInt(3, 240);
			pst.setInt(4, 500);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Dao dao = new Dao();
		List<User> users = dao.getUsers();
		for (User user : users) {
			System.out.println(user.getIp() + "\t" + user.getName());
		}
	}
}
