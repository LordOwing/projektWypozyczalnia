public class Wypozyczenie {
    private int id = -1;
    private String imieNazwisko;
    private String nazwaSprzetu;
    private int liczbaDni;
    private double stawkaZaDzien;

    public Wypozyczenie(String imieNazwisko, String nazwaSprzetu, int liczbaDni, double stawkaZaDzien) {
        setImieNazwisko(imieNazwisko);
        setNazwaSprzetu(nazwaSprzetu);
        setLiczbaDni(liczbaDni);
        setStawkaZaDzien(stawkaZaDzien);
    }

    public Wypozyczenie(int id, String imieNazwisko, String nazwaSprzetu, int liczbaDni, double stawkaZaDzien) {
        this.id = id;
        setImieNazwisko(imieNazwisko);
        setNazwaSprzetu(nazwaSprzetu);
        setLiczbaDni(liczbaDni);
        setStawkaZaDzien(stawkaZaDzien);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImieNazwisko() {
        return imieNazwisko;
    }

    public void setImieNazwisko(String imieNazwisko) {
        if (imieNazwisko == null || imieNazwisko.trim().length() < 3) {
            throw new IllegalArgumentException("Imię i nazwisko musi zawierać co najmniej 3 znaki.");
        }
        this.imieNazwisko = imieNazwisko.trim();
    }

    public String getNazwaSprzetu() {
        return nazwaSprzetu;
    }

    public void setNazwaSprzetu(String nazwaSprzetu) {
        if (nazwaSprzetu == null || nazwaSprzetu.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa sprzętu nie może być pusta.");
        }
        this.nazwaSprzetu = nazwaSprzetu.trim();
    }

    public int getLiczbaDni() {
        return liczbaDni;
    }

    public void setLiczbaDni(int liczbaDni) {
        if (liczbaDni <= 0) {
            throw new IllegalArgumentException("Liczba dni musi być większa od 0.");
        }
        this.liczbaDni = liczbaDni;
    }

    public double getStawkaZaDzien() {
        return stawkaZaDzien;
    }

    public void setStawkaZaDzien(double stawkaZaDzien) {
        if (stawkaZaDzien < 1.0) {
            throw new IllegalArgumentException("Stawka za dzień musi wynosić co najmniej 1.0.");
        }
        this.stawkaZaDzien = stawkaZaDzien;
    }

    public double obliczKoszt() {
        return liczbaDni * stawkaZaDzien;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%d;%.1f", imieNazwisko, nazwaSprzetu, liczbaDni, stawkaZaDzien);
    }
}
