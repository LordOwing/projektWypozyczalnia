import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Nie udało się zainicjalizować wyglądu FlatLaf.");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DatabaseManager dbManager = new DatabaseManager();
                AppFrame frame = new AppFrame(dbManager);
                frame.setVisible(true);
            }
        });
    }
}
