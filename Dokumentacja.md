# Dokumentacja Projektu - Rejestr Wypożyczeń z integracją baz danych

Aplikacja desktopowa w języku Java (Swing) służąca do zarządzania wypożyczeniami sprzętu sportowego. Aplikacja oferuje integrację z relacyjnymi bazami danych (PostgreSQL, MySQL) za pomocą sterowników JDBC.

---

## 1. Opis działania aplikacji

1. **Konfiguracja połączenia**:
   - Dane połączeń są konfigurowalne w oknie ustawień bazy danych. Dostęp do tego okna realizowany jest poprzez menu górne: **Opcje -> Ustawienia bazy**.
   - Okno konfiguracji umożliwia wybór silnika bazodanowego (PostgreSQL lub MySQL), wpisanie danych serwera (Host, Port, Nazwa bazy, Użytkownik, Hasło) oraz przywrócenie wartości domyślnych (np. dla chmurowego poolera Supabase).
   - Aplikacja automatycznie testuje poprawność połączenia przed zapisaniem nowej konfiguracji.

2. **Główny pulpit (Dashboard)**:
   - Na starcie programu, dopóki połączenie z bazą danych nie zostanie skonfigurowane i pomyślnie uwierzytelnione (np. hasłem), w obszarze głównym wyświetlany jest komunikat **"Brak połączenia z bazą danych"**, a przyciski akcji na danej są zablokowane.
   - Po poprawnym połączeniu widok przełącza się na tabelę (`JTable`) prezentującą aktualną listę wypożyczeń. Kolumny to: *Imię i nazwisko*, *Sprzęt*, *Dni*, *Stawka* oraz wyliczony *Koszt*.
   - Układ graficzny jest zoptymalizowany pod kątem nowoczesnego wyglądu (motyw `FlatLightLaf`) z tabelą przylegającą do krawędzi okna.
   - Pod tabelą umieszczono poziomy, dolny panel zawierający 6 przycisków akcji:
     - **Dodaj**: Otwiera formularz dodawania nowego rekordu.
     - **Edytuj**: Otwiera formularz edycji dla zaznaczonego wiersza.
     - **Usuń**: Usuwa zaznaczone wypożyczenie z lokalnej listy po potwierdzeniu przez użytkownika.
     - **Zapisz do bazy**: Wykonuje transakcję czyszczącą tabelę w bazie i zapisuje bieżący stan tabeli (SQL DELETE + PreparedStatement batch INSERT).
     - **Wczytaj z bazy**: Pobiera wszystkie wpisy z bazy danych przez SQL SELECT i odświeża tabelę w programie.
     - **Zakończ**: Zamyka program.

3. **Dodawanie i Edycja**:
   - Pola formularza w oknie dialogowym podlegają walidacji. Puste wartości, błędne formaty liczb lub naruszenia reguł biznesowych (np. za krótkie imię, liczba dni <= 0) są przechwytywane jako wyjątki i wyświetlane użytkownikowi w czytelnym oknie ostrzegawczym.

---

## 2. Opis klas

### `Main`
Główna klasa wejściowa programu. Inicjalizuje wygląd `FlatLightLaf`, tworzy instancję `DatabaseManager` i uruchamia główne okno `AppFrame` w bezpiecznym wątku Swing (EDT).

### `Wypozyczenie`
Klasa modelu danych (POJO) reprezentująca pojedynczy rekord wypożyczenia. Zawiera hermetyzowane pola, konstruktory (z ID i bez), gettery, settery z walidacją biznesową oraz metodę wyliczania kosztu.

### `DatabaseManager`
Klasa obsługująca warstwę dostępu do danych (DAO) za pośrednictwem połączeń SQL JDBC:
- Przechowuje aktualnie wybraną konfigurację połączenia oraz typ bazy (`connectionType`).
- Obsługuje bezpośrednią komunikację ze sterownikami PostgreSQL i MySQL.
- Odpowiada za testowanie połączenia, pobieranie danych (`pobierzWszystkie()`) oraz zapis danych (`zapiszWszystkie()`) z automatycznym tworzeniem tabel w bazie JDBC, jeśli jeszcze nie istnieją.

### `WypozyczenieTableModel`
Model tabeli rozszerzający `AbstractTableModel`. Przechowuje listę obiektów `Wypozyczenie` i mapuje je na kolumny tabeli `JTable`. Umożliwia dynamiczne modyfikacje wierszy wraz z automatycznym powiadamianiem widoku o zmianach.

### `AppFrame`
Główne okno aplikacji (`JFrame`). Tworzy układ graficzny (tabela, górne menu, dolny panel przycisków). Obsługuje operacje sieciowe w tle przy użyciu `SwingWorker` dla komunikacji z bazami danych, co zapobiega zamrażaniu interfejsu użytkownika podczas operacji I/O.

### `WypozyczenieDialog`
Okno dialogowe (`JDialog`) obsługujące formularz dodawania oraz edycji wypożyczenia. Przeprowadza walidację wprowadzonych ciągów tekstowych oraz rzuca i obsługuje wyjątki w przypadku niepoprawnych danych.

---

## 3. Opis metod kluczowych

### W klasie `Wypozyczenie`:
- `public double obliczKoszt()`: Zwraca koszt wypożyczenia wyliczony jako `liczbaDni * stawkaZaDzien`.
- `public void setImieNazwisko(String imieNazwisko)`: Waliduje i ustawia imię i nazwisko (wyjątek `IllegalArgumentException`, jeśli długość < 3).
- `public void setNazwaSprzetu(String nazwaSprzetu)`: Waliduje, czy nazwa nie jest pusta.
- `public void setLiczbaDni(int liczbaDni)`: Waliduje, czy liczba dni > 0.
- `public void setStawkaZaDzien(double stawkaZaDzien)`: Waliduje, czy stawka >= 1.0.

### W klasie `DatabaseManager`:
- `public List<Wypozyczenie> pobierzWszystkie()`: Pobiera listę obiektów z bazy danych poprzez zapytanie SQL `SELECT`.
- `public void zapiszWszystkie(List<Wypozyczenie> lista)`: Czyści starą zawartość tabeli (`DELETE FROM wypozyczenia`) i zapisuje bieżącą serią zapytań `INSERT` (jako batch w transakcji).
- `private void initializeTableJdbc(Connection conn, String driverClass)`: Automatycznie tworzy tabelę `wypozyczenia` w bazie danych JDBC wraz z odpowiednią strukturą typów kolumn (np. `SERIAL` / `AUTO_INCREMENT`), jeżeli taka tabela jeszcze nie istnieje.
- `private void testConnectionJdbc(String driverClass, String url, String user, String password)`: Otwiera tymczasowe połączenie, weryfikuje strukturę tabeli i sprawdza poprawność zapytań SQL.
