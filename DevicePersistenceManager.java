import java.io.*;
import java.util.*;

public class DevicePersistenceManager {
    private static final String DEVICE_FILE = "smart_home_devices.dat";
    private static final String TASK_FILE = "smart_home_tasks.dat";
    
    public static void saveDevices() {
        SmartHomeController controller = SmartHomeController.getInstance();
        List<Device> devices = controller.getDevices();
        List<ScheduledTask> tasks = controller.getScheduledTasks();
        
        try {
            // Save devices
            FileOutputStream deviceFos = new FileOutputStream(DEVICE_FILE);
            ObjectOutputStream deviceOos = new ObjectOutputStream(deviceFos);
            deviceOos.writeObject(devices);
            deviceOos.close();
            
            // Save tasks
            FileOutputStream taskFos = new FileOutputStream(TASK_FILE);
            ObjectOutputStream taskOos = new ObjectOutputStream(taskFos);
            taskOos.writeObject(tasks);
            taskOos.close();
            
            System.out.println("Devices and tasks saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving devices: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void loadDevices() {
        SmartHomeController controller = SmartHomeController.getInstance();
        
        // Load devices
        try {
            FileInputStream deviceFis = new FileInputStream(DEVICE_FILE);
            ObjectInputStream deviceOis = new ObjectInputStream(deviceFis);
            List<Device> loadedDevices = (List<Device>) deviceOis.readObject();
            deviceOis.close();
            
            // Clear existing devices and add loaded ones
            for (Device device : loadedDevices) {
                controller.addDevice(device);
            }
            
            System.out.println("Devices loaded successfully: " + loadedDevices.size() + " devices.");
        } catch (FileNotFoundException e) {
            System.out.println("No saved devices found.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading devices: " + e.getMessage());
        }
        
        // Load tasks
        try {
            FileInputStream taskFis = new FileInputStream(TASK_FILE);
            ObjectInputStream taskOis = new ObjectInputStream(taskFis);
            List<ScheduledTask> loadedTasks = (List<ScheduledTask>) taskOis.readObject();
            taskOis.close();
            
            // Add loaded tasks
            for (ScheduledTask task : loadedTasks) {
                controller.addScheduledTask(task);
            }
            
            System.out.println("Tasks loaded successfully: " + loadedTasks.size() + " tasks.");
        } catch (FileNotFoundException e) {
            System.out.println("No saved tasks found.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }
}