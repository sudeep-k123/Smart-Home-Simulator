import java.util.*;
import java.time.LocalTime;

public class SmartHomeController implements Observer {
    private static SmartHomeController instance;
    private List<Device> devices;
    private List<ScheduledTask> scheduledTasks;
    private Timer schedulerTimer;
    private List<Observer> systemObservers;
    
    private SmartHomeController() {
        devices = new ArrayList<>();
        scheduledTasks = new ArrayList<>();
        systemObservers = new ArrayList<>();
        initializeScheduler();
    }
    
    public static synchronized SmartHomeController getInstance() {
        if (instance == null) {
            instance = new SmartHomeController();
        }
        return instance;
    }
    
    public void addDevice(Device device) {
        devices.add(device);
        device.addObserver(this);
        notifySystemObservers("Device added: " + device.getName());
    }
    
    public void removeDevice(String deviceId) {
        Device deviceToRemove = null;
        for (Device device : devices) {
            if (device.getId().equals(deviceId)) {
                deviceToRemove = device;
                break;
            }
        }
        
        if (deviceToRemove != null) {
            devices.remove(deviceToRemove);
            notifySystemObservers("Device removed: " + deviceToRemove.getName());
        }
    }
    
    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }
    
    public Device getDeviceById(String deviceId) {
        for (Device device : devices) {
            if (device.getId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }
    
    public List<Device> getDevicesByType(Class<?> type) {
        List<Device> filteredDevices = new ArrayList<>();
        for (Device device : devices) {
            if (type.isInstance(device)) {
                filteredDevices.add(device);
            }
        }
        return filteredDevices;
    }
    
    public void addScheduledTask(ScheduledTask task) {
        scheduledTasks.add(task);
        notifySystemObservers("Scheduled task added: " + task.toString());
    }
    
    public void removeScheduledTask(String taskId) {
        ScheduledTask taskToRemove = null;
        for (ScheduledTask task : scheduledTasks) {
            if (task.getId().equals(taskId)) {
                taskToRemove = task;
                break;
            }
        }
        
        if (taskToRemove != null) {
            scheduledTasks.remove(taskToRemove);
            notifySystemObservers("Scheduled task removed: " + taskToRemove.toString());
        }
    }
    
    public List<ScheduledTask> getScheduledTasks() {
        return new ArrayList<>(scheduledTasks);
    }
    
    private void initializeScheduler() {
        schedulerTimer = new Timer();
        schedulerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndExecuteScheduledTasks();
            }
        }, 0, 60000); // Check every minute
    }
    
    private void checkAndExecuteScheduledTasks() {
        LocalTime now = LocalTime.now();
        List<ScheduledTask> tasksToRemove = new ArrayList<>();
        
        for (ScheduledTask task : scheduledTasks) {
            if (!task.isEnabled()) continue;
            
            // Check if it's time to execute
            if (now.getHour() == task.getExecutionTime().getHour() && 
                now.getMinute() == task.getExecutionTime().getMinute()) {
                
                executeScheduledTask(task);
                
                // If it's not recurring, mark for removal
                if (!task.isRecurring()) {
                    tasksToRemove.add(task);
                }
            }
        }
        
        // Remove completed one-time tasks
        for (ScheduledTask task : tasksToRemove) {
            scheduledTasks.remove(task);
        }
    }
    
    private void executeScheduledTask(ScheduledTask task) {
        for (Device device : devices) {
            if (device.getName().equals(task.getDeviceName())) {
                executeActionOnDevice(device, task.getAction());
                notifySystemObservers("Executed scheduled task: " + task.toString());
                break;
            }
        }
    }
    
    private void executeActionOnDevice(Device device, String action) {
        switch (action.toLowerCase()) {
            case "turn on":
            case "on":
                device.turnOn();
                break;
            case "turn off":
            case "off":
                device.turnOff();
                break;
            case "toggle":
                device.toggle();
                break;
            default:
                // Handle specific actions for specific device types
                handleSpecificAction(device, action);
                break;
        }
    }
    
    private void handleSpecificAction(Device device, String action) {
        // Parse actions like "set brightness 75" or "set temperature 22"
        String[] parts = action.split(" ", 3);
        if (parts.length < 2) return;
        
        if (device instanceof Light && "set".equals(parts[0]) && "brightness".equals(parts[1]) && parts.length == 3) {
            try {
                int brightness = Integer.parseInt(parts[2]);
                ((Light) device).setBrightness(brightness);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        } else if (device instanceof AirConditioner && "set".equals(parts[0]) && "temperature".equals(parts[1]) && parts.length == 3) {
            try {
                int temp = Integer.parseInt(parts[2]);
                ((AirConditioner) device).setTemperature(temp);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        } else if (device instanceof Fan && "set".equals(parts[0]) && "speed".equals(parts[1]) && parts.length == 3) {
            try {
                int speed = Integer.parseInt(parts[2]);
                ((Fan) device).setSpeed(speed);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
        // Add more specific actions as needed
    }
    
    // Observer implementation
    @Override
    public void update(String message) {
        // Device status changed, notify system observers
        notifySystemObservers("Device Update: " + message);
    }
    
    // System observer methods
    public void addSystemObserver(Observer observer) {
        systemObservers.add(observer);
    }
    
    public void removeSystemObserver(Observer observer) {
        systemObservers.remove(observer);
    }
    
    private void notifySystemObservers(String message) {
        for (Observer observer : systemObservers) {
            observer.update(message);
        }
    }
    
    public void shutdown() {
        if (schedulerTimer != null) {
            schedulerTimer.cancel();
        }
    }
}