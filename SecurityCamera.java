public class SecurityCamera extends Device {
    private boolean isRecording;
    private String location;
    private int resolution; // 720, 1080, 4K
    
    public SecurityCamera(String name, String location) {
        super(name);
        this.isRecording = false;
        this.location = location;
        this.resolution = 1080;
    }
    
    @Override
    public String getStatus() {
        return String.format("%s (%s): %s, Recording: %s, Resolution: %dp", 
                           name, location, isOn ? "ON" : "OFF", 
                           isRecording ? "YES" : "NO", resolution);
    }
    
    @Override
    public void toggle() {
        isOn = !isOn;
        if (!isOn) isRecording = false;
        notifyObservers();
    }
    
    @Override
    public void turnOn() {
        isOn = true;
        notifyObservers();
    }
    
    @Override
    public void turnOff() {
        isOn = false;
        isRecording = false;
        notifyObservers();
    }
    
    public void toggleRecording() {
        if (isOn) {
            isRecording = !isRecording;
            notifyObservers();
        }
    }
    
    public void setResolution(int resolution) {
        if (resolution == 720 || resolution == 1080 || resolution == 2160) {
            this.resolution = resolution;
            notifyObservers();
        }
    }
    
    public boolean isRecording() { return isRecording; }
    public String getLocation() { return location; }
    public int getResolution() { return resolution; }
}