package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Lớp tiện ích hỗ trợ làm việc với CSDL quan hệ
 * @author 
 */
public class XJdbc {

    private static Connection connection;

    // ===============================
    // 1. KẾT NỐI DATABASE
    // ===============================
    public static Connection openConnection() {
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dburl = "jdbc:sqlserver://localhost:1433;databaseName=BShoes;encrypt=true;trustServerCertificate=true;";
        String username = "sa";
        String password = "123";   // đổi nếu pass SQL khác

        try {
            if (!isReady()) {
                Class.forName(driver);
                connection = DriverManager.getConnection(dburl, username, password);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kết nối CSDL: " + e.getMessage(), e);
        }

        return connection;
    }

    /**
     * Hàm kết nối được sử dụng nội bộ
     */
    public static Connection getConnection() {
        return openConnection();
    }

    // ===============================
    // 2. HÀM INSERT TRẢ VỀ IDENTITY
    // ===============================
    public static int insertAndReturnId(String sql, Object... args) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs != null && rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new RuntimeException("Không lấy được ID sau INSERT!");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi insertAndReturnId: " + e.getMessage(), e);
        }
    }

    // ===============================
    // 3. EXECUTE UPDATE (INSERT/UPDATE/DELETE)
    // ===============================
    public static int executeUpdate(String sql, Object... values) {
        try {
            PreparedStatement stmt = getStmt(sql, values);
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // ===============================
    // 4. EXECUTE QUERY (SELECT)
    // ===============================
    public static ResultSet executeQuery(String sql, Object... values) {
        try {
            PreparedStatement stmt = getStmt(sql, values);
            return stmt.executeQuery();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // ===============================
    // 5. LẤY 1 GIÁ TRỊ
    // ===============================
    @SuppressWarnings("unchecked")
    public static <T> T getValue(String sql, Object... values) {
        try {
            ResultSet rs = executeQuery(sql, values);
            if (rs.next()) {
                return (T) rs.getObject(1);
            }
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // ===============================
    // 6. TẠO PREPARED STATEMENT
    // ===============================
    private static PreparedStatement getStmt(String sql, Object... values) throws SQLException {
        Connection conn = openConnection();
        PreparedStatement stmt;

        if (sql.trim().startsWith("{")) {
            stmt = conn.prepareCall(sql);
        } else {
            stmt = conn.prepareStatement(sql);
        }

        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }

        return stmt;
    }

    // ===============================
    // 7. KIỂM TRA TRẠNG THÁI KẾT NỐI
    // ===============================
    public static boolean isReady() {
        try {
            return (connection != null && !connection.isClosed());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // ===============================
    // 8. ĐÓNG KẾT NỐI
    // ===============================
    public static void closeConnection() {
        try {
            if (isReady()) {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
