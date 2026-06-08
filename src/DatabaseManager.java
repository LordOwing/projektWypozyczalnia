import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static final String DEFAULT_JDBC_POSTGRES_URL = "jdbc:postgresql://aws-0-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require";
    public static final String DEFAULT_JDBC_POSTGRES_USER = "postgres.xkomyamqrwdavtsgevpo";
    public static final String DEFAULT_JDBC_MYSQL_URL = "jdbc:mysql://localhost:3306/wypozyczalnia?useSSL=false&serverTimezone=UTC";
    public static final String DEFAULT_JDBC_MYSQL_USER = "root";

    private String connectionType = "POSTGRESQL_JDBC";

    private String postgresUrl = DEFAULT_JDBC_POSTGRES_URL;
    private String postgresUser = DEFAULT_JDBC_POSTGRES_USER;
    private String postgresPassword = "";

    private String mysqlUrl = DEFAULT_JDBC_MYSQL_URL;
    private String mysqlUser = DEFAULT_JDBC_MYSQL_USER;
    private String mysqlPassword = "";

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getPostgresUrl() { return postgresUrl; }
    public void setPostgresUrl(String postgresUrl) { this.postgresUrl = postgresUrl; }

    public String getPostgresUser() { return postgresUser; }
    public void setPostgresUser(String postgresUser) { this.postgresUser = postgresUser; }

    public String getPostgresPassword() { return postgresPassword; }
    public void setPostgresPassword(String postgresPassword) { this.postgresPassword = postgresPassword; }

    public String getMysqlUrl() { return mysqlUrl; }
    public void setMysqlUrl(String mysqlUrl) { this.mysqlUrl = mysqlUrl; }

    public String getMysqlUser() { return mysqlUser; }
    public void setMysqlUser(String mysqlUser) { this.mysqlUser = mysqlUser; }

    public String getMysqlPassword() { return mysqlPassword; }
    public void setMysqlPassword(String mysqlPassword) { this.mysqlPassword = mysqlPassword; }

    public String getJdbcUrl() {
        if ("MYSQL_JDBC".equals(connectionType)) {
            return mysqlUrl;
        }
        return postgresUrl;
    }

    public void setJdbcUrl(String url) {
        if ("MYSQL_JDBC".equals(connectionType)) {
            this.mysqlUrl = url;
        } else {
            this.postgresUrl = url;
        }
    }

    public String getJdbcUser() {
        if ("MYSQL_JDBC".equals(connectionType)) {
            return mysqlUser;
        }
        return postgresUser;
    }

    public void setJdbcUser(String user) {
        if ("MYSQL_JDBC".equals(connectionType)) {
            this.mysqlUser = user;
        } else {
            this.postgresUser = user;
        }
    }

    public String getJdbcPassword() {
        if ("MYSQL_JDBC".equals(connectionType)) {
            return mysqlPassword;
        }
        return postgresPassword;
    }

    public void setJdbcPassword(String password) {
        if ("MYSQL_JDBC".equals(connectionType)) {
            this.mysqlPassword = password;
        } else {
            this.postgresPassword = password;
        }
    }

    public boolean hasConfig() {
        return getJdbcUrl() != null && !getJdbcUrl().trim().isEmpty() && getJdbcUser() != null && !getJdbcUser().trim().isEmpty();
    }

    public String getHost() {
        try {
            int index = getJdbcUrl().indexOf("//");
            if (index != -1) {
                return "JDBC (" + getJdbcUrl().substring(index + 2) + ")";
            }
            return "JDBC Database";
        } catch (Exception e) {
            return "JDBC Database";
        }
    }

    public String getDbTypeName() {
        if ("POSTGRESQL_JDBC".equals(connectionType)) {
            return "PostgreSQL";
        } else if ("MYSQL_JDBC".equals(connectionType)) {
            return "MySQL";
        }
        return "Unknown";
    }

    public void testConnection(String type, String url, String keyOrPassword, String user) throws Exception {
        if ("POSTGRESQL_JDBC".equals(type)) {
            testConnectionJdbc("org.postgresql.Driver", url, user, keyOrPassword);
        } else if ("MYSQL_JDBC".equals(type)) {
            testConnectionJdbc("com.mysql.cj.jdbc.Driver", url, user, keyOrPassword);
        }
    }

    private void initializeTableJdbc(java.sql.Connection conn, String driverClass) throws Exception {
        String sql;
        if ("com.mysql.cj.jdbc.Driver".equals(driverClass)) {
            sql = "CREATE TABLE IF NOT EXISTS wypozyczenia (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "imie_nazwisko VARCHAR(255) NOT NULL, " +
                  "nazwa_sprzetu VARCHAR(255) NOT NULL, " +
                  "liczba_dni INT NOT NULL, " +
                  "stawka_za_dzien DOUBLE PRECISION NOT NULL" +
                  ")";
        } else {
            sql = "CREATE TABLE IF NOT EXISTS wypozyczenia (" +
                  "id SERIAL PRIMARY KEY, " +
                  "imie_nazwisko VARCHAR(255) NOT NULL, " +
                  "nazwa_sprzetu VARCHAR(255) NOT NULL, " +
                  "liczba_dni INT NOT NULL, " +
                  "stawka_za_dzien DOUBLE PRECISION NOT NULL" +
                  ")";
        }
        try (java.sql.Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private void testConnectionJdbc(String driverClass, String url, String user, String password) throws Exception {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL bazy danych nie może być pusty.");
        }
        Class.forName(driverClass);
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, password)) {
            initializeTableJdbc(conn, driverClass);
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT 1");
            }
        }
    }

    public List<Wypozyczenie> pobierzWszystkie() throws Exception {
        if ("POSTGRESQL_JDBC".equals(connectionType)) {
            return pobierzWszystkieJdbc("org.postgresql.Driver");
        } else if ("MYSQL_JDBC".equals(connectionType)) {
            return pobierzWszystkieJdbc("com.mysql.cj.jdbc.Driver");
        }
        return new ArrayList<>();
    }

    private List<Wypozyczenie> pobierzWszystkieJdbc(String driverClass) throws Exception {
        List<Wypozyczenie> list = new ArrayList<>();
        Class.forName(driverClass);
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(getJdbcUrl(), getJdbcUser(), getJdbcPassword())) {
            initializeTableJdbc(conn, driverClass);
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery("SELECT id, imie_nazwisko, nazwa_sprzetu, liczba_dni, stawka_za_dzien FROM wypozyczenia ORDER BY id ASC")) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String imie = rs.getString("imie_nazwisko");
                    String sprzet = rs.getString("nazwa_sprzetu");
                    int dni = rs.getInt("liczba_dni");
                    double stawka = rs.getDouble("stawka_za_dzien");
                    list.add(new Wypozyczenie(id, imie, sprzet, dni, stawka));
                }
            }
        }
        return list;
    }

    public void zapiszWszystkie(List<Wypozyczenie> lista) throws Exception {
        if ("POSTGRESQL_JDBC".equals(connectionType)) {
            zapiszWszystkieJdbc("org.postgresql.Driver", lista);
        } else if ("MYSQL_JDBC".equals(connectionType)) {
            zapiszWszystkieJdbc("com.mysql.cj.jdbc.Driver", lista);
        }
    }

    private void zapiszWszystkieJdbc(String driverClass, List<Wypozyczenie> lista) throws Exception {
        Class.forName(driverClass);
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(getJdbcUrl(), getJdbcUser(), getJdbcPassword())) {
            initializeTableJdbc(conn, driverClass);
            conn.setAutoCommit(false);
            try {
                try (java.sql.Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM wypozyczenia");
                }

                if (!lista.isEmpty()) {
                    String sql = "INSERT INTO wypozyczenia (imie_nazwisko, nazwa_sprzetu, liczba_dni, stawka_za_dzien) VALUES (?, ?, ?, ?)";
                    try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        for (Wypozyczenie w : lista) {
                            pstmt.setString(1, w.getImieNazwisko());
                            pstmt.setString(2, w.getNazwaSprzetu());
                            pstmt.setInt(3, w.getLiczbaDni());
                            pstmt.setDouble(4, w.getStawkaZaDzien());
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
