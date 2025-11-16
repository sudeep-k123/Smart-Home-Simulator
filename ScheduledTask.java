import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ScheduledTask implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String deviceName;
    private String action;
    private LocalTime executionTime;
    private boolean enabled;
    private boolean recurring;
    
    public ScheduledTask(String deviceName, String action, String time, boolean recurring) {
        this.id = java.util.UUID.randomUUID().toString();
        this.deviceName = deviceName;
        this.action = action;
        this.enabled = true;
        this.recurring = recurring;
        
        try {
            this.executionTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException e) {
            this.executionTime = LocalTime.now();
        }
    }
    
    public String getId() { return id; }
    public String getDeviceName() { return deviceName; }
    public String getAction() { return action; }
    public LocalTime getExecutionTime() { return executionTime; }
    public boolean isEnabled() { return enabled; }
    public boolean isRecurring() { return recurring; }
    
    // THESE WERE MISSING - ADD THESE METHODS
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setExecutionTime(LocalTime time) { this.executionTime = time; }
    public void setAction(String action) { this.action = action; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    
    public String getTimeAsString() {
        return executionTime.format(DateTimeFormatter.ofPattern("H:mm"));
    }
    
    @Override
    public String toString() {
        return String.format("Task: %s %s at %s (%s)", 
                           deviceName, action, getTimeAsString(), 
                           recurring ? "Daily" : "One-time");
    }
}