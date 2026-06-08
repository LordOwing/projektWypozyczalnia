import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WypozyczenieDialog extends JDialog {
    private JTextField txtImieNazwisko;
    private JTextField txtNazwaSprzetu;
    private JTextField txtLiczbaDni;
    private JTextField txtStawkaZaDzien;
    private boolean saved = false;
    private Wypozyczenie resultWypozyczenie = null;
    private Wypozyczenie existing = null;

    public WypozyczenieDialog(Frame parent, String title, Wypozyczenie existing) {
        super(parent, title, true);
        this.existing = existing;
        initComponents();
        if (existing != null) {
            populateFields(existing);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Imię i nazwisko:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtImieNazwisko = new JTextField(20);
        formPanel.add(txtImieNazwisko, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Nazwa sprzętu:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNazwaSprzetu = new JTextField(20);
        formPanel.add(txtNazwaSprzetu, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Liczba dni:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtLiczbaDni = new JTextField(20);
        formPanel.add(txtLiczbaDni, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Stawka za dzień (zł):"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtStawkaZaDzien = new JTextField(20);
        formPanel.add(txtStawkaZaDzien, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        buttonsPanel.setBackground(new Color(245, 245, 245));

        JButton btnZapisz = new JButton("Zapisz");
        btnZapisz.putClientProperty("JButton.buttonType", "filled");
        btnZapisz.setBackground(new Color(36, 180, 126));
        btnZapisz.setForeground(Color.WHITE);

        JButton btnAnuluj = new JButton("Anuluj");

        btnZapisz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        btnAnuluj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonsPanel.add(btnZapisz);
        buttonsPanel.add(btnAnuluj);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void populateFields(Wypozyczenie w) {
        txtImieNazwisko.setText(w.getImieNazwisko());
        txtNazwaSprzetu.setText(w.getNazwaSprzetu());
        txtLiczbaDni.setText(String.valueOf(w.getLiczbaDni()));
        txtStawkaZaDzien.setText(String.valueOf(w.getStawkaZaDzien()));
    }

    private void onSave() {
        String imie = txtImieNazwisko.getText().trim();
        String sprzet = txtNazwaSprzetu.getText().trim();
        String dniStr = txtLiczbaDni.getText().trim();
        String stawkaStr = txtStawkaZaDzien.getText().trim();

        if (imie.isEmpty() || sprzet.isEmpty() || dniStr.isEmpty() || stawkaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Wszystkie pola formularza muszą być wypełnione!",
                "Błąd walidacji",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dni;
        double stawka;
        try {
            dni = Integer.parseInt(dniStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Liczba dni musi być poprawną liczbą całkowitą!",
                "Błąd formatu",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            stawka = Double.parseDouble(stawkaStr.replace(',', '.'));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Stawka za dzień musi być poprawną liczbą (np. 45.0)!",
                "Błąd formatu",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Wypozyczenie validated = new Wypozyczenie(imie, sprzet, dni, stawka);
            if (existing != null) {
                validated.setId(existing.getId());
            }
            resultWypozyczenie = validated;
            saved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Błąd walidacji danych",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCancel() {
        saved = false;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Wypozyczenie getWypozyczenie() {
        return resultWypozyczenie;
    }
}
