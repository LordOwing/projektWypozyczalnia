import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseConfigDialog extends JDialog {
    private JComboBox<String> cbDbType;
    private JPanel cardPanel;


    private JTextField txtPostgresHost;
    private JTextField txtPostgresPort;
    private JTextField txtPostgresDb;
    private JTextField txtPostgresUser;
    private JPasswordField txtPostgresPassword;


    private JTextField txtMysqlHost;
    private JTextField txtMysqlPort;
    private JTextField txtMysqlDb;
    private JTextField txtMysqlUser;
    private JPasswordField txtMysqlPassword;

    private JButton btnPolacz;
    private JButton btnDomyslne;
    private JButton btnAnuluj;

    private DatabaseManager dbManager;
    private boolean connected = false;

    public DatabaseConfigDialog(Frame parent, DatabaseManager dbManager) {
        super(parent, "Konfiguracja połączenia z bazą danych", true);
        this.dbManager = dbManager;
        initComponents();
        loadCurrentConfig();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setResizable(false);

        // Panel wyboru typu (North)
        JPanel typeSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        typeSelectPanel.add(new JLabel("Typ bazy danych:"));
        cbDbType = new JComboBox<>(new String[]{
                "PostgreSQL (JDBC)",
                "MySQL (JDBC)"
        });
        typeSelectPanel.add(cbDbType);
        add(typeSelectPanel, BorderLayout.NORTH);


        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));


        JPanel postgresPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.insets = new Insets(6, 8, 6, 8);
        gbcP.fill = GridBagConstraints.HORIZONTAL;

        gbcP.gridx = 0; gbcP.gridy = 0; gbcP.weightx = 0.3;
        postgresPanel.add(new JLabel("Host / Serwer:"), gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.7;
        txtPostgresHost = new JTextField(30);
        postgresPanel.add(txtPostgresHost, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 1; gbcP.weightx = 0.3;
        postgresPanel.add(new JLabel("Port:"), gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.7;
        txtPostgresPort = new JTextField(30);
        postgresPanel.add(txtPostgresPort, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 2; gbcP.weightx = 0.3;
        postgresPanel.add(new JLabel("Nazwa bazy danych:"), gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.7;
        txtPostgresDb = new JTextField(30);
        postgresPanel.add(txtPostgresDb, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 3; gbcP.weightx = 0.3;
        postgresPanel.add(new JLabel("Użytkownik:"), gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.7;
        txtPostgresUser = new JTextField(30);
        postgresPanel.add(txtPostgresUser, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 4; gbcP.weightx = 0.3;
        postgresPanel.add(new JLabel("Hasło:"), gbcP);
        gbcP.gridx = 1; gbcP.weightx = 0.7;
        txtPostgresPassword = new JPasswordField(30);
        postgresPanel.add(txtPostgresPassword, gbcP);

        cardPanel.add(postgresPanel, "POSTGRESQL_JDBC");


        JPanel mysqlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcM = new GridBagConstraints();
        gbcM.insets = new Insets(6, 8, 6, 8);
        gbcM.fill = GridBagConstraints.HORIZONTAL;

        gbcM.gridx = 0; gbcM.gridy = 0; gbcM.weightx = 0.3;
        mysqlPanel.add(new JLabel("Host / Serwer:"), gbcM);
        gbcM.gridx = 1; gbcM.weightx = 0.7;
        txtMysqlHost = new JTextField(30);
        mysqlPanel.add(txtMysqlHost, gbcM);

        gbcM.gridx = 0; gbcM.gridy = 1; gbcM.weightx = 0.3;
        mysqlPanel.add(new JLabel("Port:"), gbcM);
        gbcM.gridx = 1; gbcM.weightx = 0.7;
        txtMysqlPort = new JTextField(30);
        mysqlPanel.add(txtMysqlPort, gbcM);

        gbcM.gridx = 0; gbcM.gridy = 2; gbcM.weightx = 0.3;
        mysqlPanel.add(new JLabel("Nazwa bazy danych:"), gbcM);
        gbcM.gridx = 1; gbcM.weightx = 0.7;
        txtMysqlDb = new JTextField(30);
        mysqlPanel.add(txtMysqlDb, gbcM);

        gbcM.gridx = 0; gbcM.gridy = 3; gbcM.weightx = 0.3;
        mysqlPanel.add(new JLabel("Użytkownik:"), gbcM);
        gbcM.gridx = 1; gbcM.weightx = 0.7;
        txtMysqlUser = new JTextField(30);
        mysqlPanel.add(txtMysqlUser, gbcM);

        gbcM.gridx = 0; gbcM.gridy = 4; gbcM.weightx = 0.3;
        mysqlPanel.add(new JLabel("Hasło:"), gbcM);
        gbcM.gridx = 1; gbcM.weightx = 0.7;
        txtMysqlPassword = new JPasswordField(30);
        mysqlPanel.add(txtMysqlPassword, gbcM);

        cardPanel.add(mysqlPanel, "MYSQL_JDBC");

        add(cardPanel, BorderLayout.CENTER);


        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        buttonsPanel.setBackground(new Color(245, 245, 245));

        btnDomyslne = new JButton("Domyślne");
        btnDomyslne.putClientProperty("JButton.buttonType", "border");
        buttonsPanel.add(btnDomyslne, BorderLayout.WEST);

        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtonsPanel.setOpaque(false);

        btnPolacz = new JButton("Połącz");
        btnPolacz.putClientProperty("JButton.buttonType", "filled");
        btnPolacz.setBackground(new Color(36, 180, 126));
        btnPolacz.setForeground(Color.WHITE);

        btnAnuluj = new JButton("Anuluj");

        rightButtonsPanel.add(btnPolacz);
        rightButtonsPanel.add(btnAnuluj);
        buttonsPanel.add(rightButtonsPanel, BorderLayout.EAST);

        add(buttonsPanel, BorderLayout.SOUTH);


        cbDbType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                String selected = (String) cbDbType.getSelectedItem();
                if ("PostgreSQL (JDBC)".equals(selected)) {
                    cl.show(cardPanel, "POSTGRESQL_JDBC");
                } else {
                    cl.show(cardPanel, "MYSQL_JDBC");
                }
                pack();
            }
        });


        btnDomyslne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cbDbType.getSelectedItem();
                if ("PostgreSQL (JDBC)".equals(selected)) {
                    txtPostgresHost.setText("aws-0-eu-west-1.pooler.supabase.com");
                    txtPostgresPort.setText("5432");
                    txtPostgresDb.setText("postgres");
                    txtPostgresUser.setText("postgres.xkomyamqrwdavtsgevpo");
                    txtPostgresPassword.setText("");
                } else {
                    txtMysqlHost.setText("localhost");
                    txtMysqlPort.setText("3306");
                    txtMysqlDb.setText("wypozyczalnia");
                    txtMysqlUser.setText("root");
                    txtMysqlPassword.setText("");
                }
            }
        });

        btnAnuluj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnPolacz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onConnect();
            }
        });
    }

    private void loadCurrentConfig() {
        String type = dbManager.getConnectionType();
        CardLayout cl = (CardLayout) (cardPanel.getLayout());


        String currentPostgresUrl = dbManager.getPostgresUrl();
        String[] postgresParams = parseJdbcUrl(currentPostgresUrl, "aws-0-eu-west-1.pooler.supabase.com", "5432", "postgres");
        txtPostgresHost.setText(postgresParams[0]);
        txtPostgresPort.setText(postgresParams[1]);
        txtPostgresDb.setText(postgresParams[2]);
        txtPostgresUser.setText(dbManager.getPostgresUser());
        txtPostgresPassword.setText(dbManager.getPostgresPassword());


        String currentMysqlUrl = dbManager.getMysqlUrl();
        String[] mysqlParams = parseJdbcUrl(currentMysqlUrl, "localhost", "3306", "wypozyczalnia");
        txtMysqlHost.setText(mysqlParams[0]);
        txtMysqlPort.setText(mysqlParams[1]);
        txtMysqlDb.setText(mysqlParams[2]);
        txtMysqlUser.setText(dbManager.getMysqlUser());
        txtMysqlPassword.setText(dbManager.getMysqlPassword());

        if ("MYSQL_JDBC".equals(type)) {
            cbDbType.setSelectedItem("MySQL (JDBC)");
            cl.show(cardPanel, "MYSQL_JDBC");
        } else {
            cbDbType.setSelectedItem("PostgreSQL (JDBC)");
            cl.show(cardPanel, "POSTGRESQL_JDBC");
        }
    }

    private String[] parseJdbcUrl(String url, String defaultHost, String defaultPort, String defaultDb) {
        String host = defaultHost;
        String port = defaultPort;
        String db = defaultDb;
        try {
            if (url != null && url.startsWith("jdbc:")) {
                String clean = url.substring(url.indexOf("//") + 2);
                int paramIdx = clean.indexOf("?");
                if (paramIdx != -1) {
                    clean = clean.substring(0, paramIdx);
                }
                int slashIdx = clean.indexOf("/");
                if (slashIdx != -1) {
                    db = clean.substring(slashIdx + 1);
                    String hostPort = clean.substring(0, slashIdx);
                    int colonIdx = hostPort.indexOf(":");
                    if (colonIdx != -1) {
                        host = hostPort.substring(0, colonIdx);
                        port = hostPort.substring(colonIdx + 1);
                    } else {
                        host = hostPort;
                    }
                }
            }
        } catch (Exception e) {

        }
        return new String[]{host, port, db};
    }

    private void onConnect() {
        final String selected = (String) cbDbType.getSelectedItem();
        final String type;
        final String url;
        final String user;
        final String keyOrPassword;

        if ("PostgreSQL (JDBC)".equals(selected)) {
            type = "POSTGRESQL_JDBC";
            String host = txtPostgresHost.getText().trim();
            String port = txtPostgresPort.getText().trim();
            String db = txtPostgresDb.getText().trim();
            if (host.isEmpty() || port.isEmpty() || db.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Host, Port oraz Nazwa bazy danych nie mogą być puste!",
                        "Błąd walidacji",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            url = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=require";
            user = txtPostgresUser.getText().trim();
            keyOrPassword = new String(txtPostgresPassword.getPassword()).trim();
        } else {
            type = "MYSQL_JDBC";
            String host = txtMysqlHost.getText().trim();
            String port = txtMysqlPort.getText().trim();
            String db = txtMysqlDb.getText().trim();
            if (host.isEmpty() || port.isEmpty() || db.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Host, Port oraz Nazwa bazy danych nie mogą być puste!",
                        "Błąd walidacji",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC";
            user = txtMysqlUser.getText().trim();
            keyOrPassword = new String(txtMysqlPassword.getPassword()).trim();
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        enableUI(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                dbManager.testConnection(type, url, keyOrPassword, user);
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                enableUI(true);

                try {
                    get();
                    dbManager.setConnectionType(type);
                    if ("POSTGRESQL_JDBC".equals(type)) {
                        dbManager.setPostgresUrl(url);
                        dbManager.setPostgresUser(user);
                        dbManager.setPostgresPassword(keyOrPassword);
                    } else if ("MYSQL_JDBC".equals(type)) {
                        dbManager.setMysqlUrl(url);
                        dbManager.setMysqlUser(user);
                        dbManager.setMysqlPassword(keyOrPassword);
                    }
                    connected = true;

                    JOptionPane.showMessageDialog(DatabaseConfigDialog.this,
                            "Połączenie z bazą danych zostało pomyślnie zweryfikowane i zapisane.",
                            "Sukces",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    Throwable root = e.getCause() != null ? e.getCause() : e;
                    String msg = root.getClass().getSimpleName() + ": " + root.getMessage();
                    JOptionPane.showMessageDialog(DatabaseConfigDialog.this,
                            "Nie udało się połączyć z bazą danych:\n" + msg,
                            "Błąd połączenia",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void enableUI(boolean enabled) {
        cbDbType.setEnabled(enabled);
        btnPolacz.setEnabled(enabled);
        btnDomyslne.setEnabled(enabled);
        btnAnuluj.setEnabled(enabled);

        txtPostgresHost.setEnabled(enabled);
        txtPostgresPort.setEnabled(enabled);
        txtPostgresDb.setEnabled(enabled);
        txtPostgresUser.setEnabled(enabled);
        txtPostgresPassword.setEnabled(enabled);

        txtMysqlHost.setEnabled(enabled);
        txtMysqlPort.setEnabled(enabled);
        txtMysqlDb.setEnabled(enabled);
        txtMysqlUser.setEnabled(enabled);
        txtMysqlPassword.setEnabled(enabled);
    }

    public boolean isConnected() {
        return connected;
    }
}
