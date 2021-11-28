import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map.Entry;

public class wordDatabase {

	public static Connection getConnection() throws Exception {

		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/wordOccurences";
			String username = "root";
			String password = "cop2805";
			Class.forName(driver);

			Connection conn = DriverManager.getConnection(url, username, password);

			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;

	}

	public static void createTable() {

		try {

			Connection conn = getConnection();
			PreparedStatement create = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS word(freq int NOT NULL, word varchar(30) NOT NULL, PRIMARY KEY(word))");
			create.executeUpdate();
			conn.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		finally {
			System.out.println("Connection established.");
		}

	}

	public static void insertToTable(List<Entry<String, Integer>> arrlist) {

		try {

			for (Entry<String, Integer> word : arrlist) {

				Connection conn = getConnection();
				PreparedStatement posted = conn.prepareStatement("INSERT IGNORE INTO word (freq, word) VALUES('"
						+ word.getValue() + "', '" + word.getKey() + "')");
				posted.executeUpdate();
				posted.close();

			}

		} catch (Exception e) {
			System.out.println(e);
		}

		finally {
			System.out.println("Insertion complete.");
		}

	}

	public static String queryTable() {

		String query = "select * from word ORDER BY freq DESC";

		try {
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(query);

			String wordData = "";

			int count = 0;
			String topTwenty = "";

			while (result.next()) {

				wordData = result.getInt(1) + ": " + result.getString(2);
				topTwenty += result.getString(2) + ", ";
				System.out.println(wordData);
				count++;

				if (count >= 20)
					break;

			}			
						
			statement.close();
			
			return topTwenty;

		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;

	}

}
