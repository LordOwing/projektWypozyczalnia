import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class WypozyczenieTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "Imię i nazwisko", "Sprzęt", "Dni", "Stawka", "Koszt"
    };
    private List<Wypozyczenie> rentals = new ArrayList<>();

    public void setRentals(List<Wypozyczenie> rentals) {
        this.rentals = new ArrayList<>(rentals);
        fireTableDataChanged();
    }

    public List<Wypozyczenie> getRentals() {
        return rentals;
    }

    public void addRental(Wypozyczenie w) {
        rentals.add(w);
        fireTableRowsInserted(rentals.size() - 1, rentals.size() - 1);
    }

    public void removeRental(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rentals.size()) {
            rentals.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public Wypozyczenie getRentalAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rentals.size()) {
            return rentals.get(rowIndex);
        }
        return null;
    }

    public void updateRental(int rowIndex, Wypozyczenie w) {
        if (rowIndex >= 0 && rowIndex < rentals.size()) {
            rentals.set(rowIndex, w);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    @Override
    public int getRowCount() {
        return rentals.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Wypozyczenie w = rentals.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return w.getImieNazwisko();
            case 1:
                return w.getNazwaSprzetu();
            case 2:
                return w.getLiczbaDni();
            case 3:
                return String.format("%.2f zł", w.getStawkaZaDzien());
            case 4:
                return String.format("%.2f zł", w.obliczKoszt());
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
            case 4:
                return String.class;
            default:
                return Object.class;
        }
    }
}
