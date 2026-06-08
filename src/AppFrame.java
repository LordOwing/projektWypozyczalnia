import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AppFrame extends JFrame {
    private JTable table;
    private WypozyczenieTableModel tableModel;
    private DatabaseManager dbManager;
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private boolean dbConnected = false;

    private JButton btnDodaj;
    private JButton btnEdytuj;
    private JButton btnUsun;
    private JButton btnZapisz;
    private JButton btnWczytaj;

    public AppFrame(DatabaseManager dbManager) {
        super("Rejestr wypożyczeń");
        this.dbManager = dbManager;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 450));
        setPreferredSize(new Dimension(950, 500));

        initComponents();

        pack();
        setLocationRelativeTo(null);

        showConnectionStatus(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menuOpcje = new JMenu("Opcje");
        JMenuItem itemUstawienia = new JMenuItem("Ustawienia bazy");
        itemUstawienia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                otworzUstawieniaBazy();
            }
        });
        menuOpcje.add(itemUstawienia);
        menuBar.add(menuOpcje);
        setJMenuBar(menuBar);

        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);

        JPanel noConnectionPanel = new JPanel(new GridBagLayout());
        noConnectionPanel.setBackground(Color.WHITE);
        JLabel lblNoConnection = new JLabel("Brak połączenia z bazą danych");
        lblNoConnection.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNoConnection.setForeground(Color.GRAY);
        noConnectionPanel.add(lblNoConnection);

        tableModel = new WypozyczenieTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        centerPanel.add(noConnectionPanel, "NO_CONNECTION");
        centerPanel.add(scrollPane, "TABLE");

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(new Color(240, 240, 243));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));

        Dimension btnSize = new Dimension(130, 32);

        btnDodaj = createButton("Dodaj", btnSize);
        btnEdytuj = createButton("Edytuj", btnSize);
        btnUsun = createButton("Usuń", btnSize);
        btnZapisz = createButton("Zapisz do bazy", btnSize);
        btnWczytaj = createButton("Wczytaj z bazy", btnSize);
        JButton btnZakoncz = createButton("Zakończ", btnSize);
        btnZakoncz.setForeground(new Color(239, 68, 68));

        bottomPanel.add(btnDodaj);
        bottomPanel.add(btnEdytuj);
        bottomPanel.add(btnUsun);
        bottomPanel.add(btnZapisz);
        bottomPanel.add(btnWczytaj);
        bottomPanel.add(btnZakoncz);

        add(bottomPanel, BorderLayout.SOUTH);

        btnDodaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                otworzDodajWypozyczenie();
            }
        });

        btnEdytuj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                otworzEdytujWypozyczenie();
            }
        });

        btnUsun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunWypozyczenie();
            }
        });

        btnWczytaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wczytajDaneZBazy(true);
            }
        });

        btnZapisz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zapiszDaneDoBazy();
            }
        });

        btnZakoncz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private JButton createButton(String text, Dimension size) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(size);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    private void showConnectionStatus(boolean connected) {
        if (connected) {
            cardLayout.show(centerPanel, "TABLE");
        } else {
            cardLayout.show(centerPanel, "NO_CONNECTION");
        }
        btnDodaj.setEnabled(connected);
        btnEdytuj.setEnabled(connected);
        btnUsun.setEnabled(connected);
        btnZapisz.setEnabled(connected);
        btnWczytaj.setEnabled(connected);
    }

    private void otworzDodajWypozyczenie() {
        WypozyczenieDialog dialog = new WypozyczenieDialog(this, "Dodaj wypożyczenie", null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            Wypozyczenie nowy = dialog.getWypozyczenie();
            tableModel.addRental(nowy);
        }
    }

    private void otworzEdytujWypozyczenie() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Wybierz wiersz z tabeli, który chcesz edytować.",
                "Brak wyboru",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Wypozyczenie doEdycji = tableModel.getRentalAt(modelRow);

        WypozyczenieDialog dialog = new WypozyczenieDialog(this, "Edytuj wypożyczenie", doEdycji);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            tableModel.updateRental(modelRow, dialog.getWypozyczenie());
        }
    }

    private void usunWypozyczenie() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Wybierz wiersz z tabeli, który chcesz usunąć.",
                "Brak wyboru",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Wypozyczenie w = tableModel.getRentalAt(modelRow);

        int choice = JOptionPane.showConfirmDialog(this,
                String.format("Czy na pewno chcesz usunąć wypożyczenie dla użytkownika %s (%s)?", w.getImieNazwisko(), w.getNazwaSprzetu()),
                "Potwierdzenie usunięcia",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            tableModel.removeRental(modelRow);
        }
    }

    private void wczytajDaneZBazy(boolean pokazInfo) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<List<Wypozyczenie>, Void>() {
            private String errorMsg = "";

            @Override
            protected List<Wypozyczenie> doInBackground() throws Exception {
                try {
                    return dbManager.pobierzWszystkie();
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    throw e;
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Wypozyczenie> list = get();
                    tableModel.setRentals(list);
                    if (pokazInfo) {
                        JOptionPane.showMessageDialog(AppFrame.this,
                                "Wczytano pomyślnie " + list.size() + " rekordów z bazy danych.",
                                "Sukces",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AppFrame.this,
                            "Nie udało się wczytać danych z bazy:\n" + errorMsg,
                            "Błąd bazy danych",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void zapiszDaneDoBazy() {
        List<Wypozyczenie> lista = tableModel.getRentals();

        int choice = JOptionPane.showConfirmDialog(this,
                "Czy chcesz zapisać dane do bazy danych? Obecna zawartość tabeli zostanie nadpisana.",
                "Potwierdzenie zapisu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Void, Void>() {
            private String errorMsg = "";

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    dbManager.zapiszWszystkie(lista);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    throw e;
                }
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    get();
                    wczytajDaneZBazy(false);
                    JOptionPane.showMessageDialog(AppFrame.this,
                            "Dane zostały pomyślnie zapisane w bazie danych.",
                            "Zapisano w bazie",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AppFrame.this,
                            "Nie udało się zapisać danych w bazie:\n" + errorMsg,
                            "Błąd zapisu",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void otworzUstawieniaBazy() {
        DatabaseConfigDialog dialog = new DatabaseConfigDialog(this, dbManager);
        dialog.setVisible(true);
        if (dialog.isConnected()) {
            dbConnected = true;
            showConnectionStatus(true);
            wczytajDaneZBazy(true);
        }
    }
}
