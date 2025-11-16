public class DoorLock extends Device {
    private boolean isLocked;
    private String location;
    
    public DoorLock(String name, String location) {
        super(name);
        this.isLocked = true;
        this.location = location;
        this.isOn = true; // Door lock is always "on" as a system
    }
    
    @Override
    public String getStatus() {
        return String.format("%s (%s): %s", name, location, isLocked ? "LOCKED" : "UNLOCKED");
    }
    
    @Override
    public void toggle() {
        isLocked = !isLocked;
        notifyObservers();
    }
    
    @Override
    public void turnOn() {
        isLocked = true;
        notifyObservers();
    }
    
    @Override
    public void turnOff() {
        isLocked = false;
        notifyObservers();
    }
    
    public boolean isLocked() { return isLocked; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}