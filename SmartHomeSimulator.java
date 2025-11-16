import javax.swing.SwingUtilities;

public class SmartHomeSimulator {
    public static void main(String[] args) {
        // Load saved data
        DevicePersistenceManager.loadDevices();
        
        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            SmartHomeGUI gui = new SmartHomeGUI();
            gui.setVisible(true);
        });
        
        // Add shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DevicePersistenceManager.saveDevices();
            SmartHomeController.getInstance().shutdown();
            System.out.println("Smart Home Simulator shut down gracefully.");
        }));
    }
}