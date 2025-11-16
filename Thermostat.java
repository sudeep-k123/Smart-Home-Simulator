import java.io.Serializable;

public class Thermostat extends Device implements Serializable {
    private static final long serialVersionUID = 1L;
    private int targetTemperature;
    private int currentTemperature;
    private String mode; // "heat", "cool", "off"
    
    public Thermostat(String name) {
        super(name);
        this.targetTemperature = 22;
        this.currentTemperature = 22;
        this.mode = "off";
    }
    
    @Override
    public String getStatus() {
        return String.format("%s: Mode: %s, Target: %d°C, Current: %d°C", 
                           name, mode, targetTemperature, currentTemperature);
    }
    
    @Override
    public void toggle() {
        if (mode.equals("off")) {
            mode = "heat";
        } else if (mode.equals("heat")) {
            mode = "cool";
        } else {
            mode = "off";
        }
        notifyObservers();
    }
    
    @Override
    public void turnOn() {
        if (mode.equals("off")) {
            mode = "heat";
        }
        notifyObservers();
    }
    
    @Override
    public void turnOff() {
        mode = "off";
        notifyObservers();
    }
    
    public void setTargetTemperature(int temperature) {
        if (temperature >= 10 && temperature <= 35) {
            this.targetTemperature = temperature;
            if (mode.equals("off")) {
                mode = "heat";
            }
            notifyObservers();
        }
    }
    
    public void setCurrentTemperature(int temperature) {
        if (temperature >= -10 && temperature <= 50) {
            this.currentTemperature = temperature;
            notifyObservers();
        }
    }
    
    // THIS WAS MISSING - ADD THIS METHOD
    public void setMode(String mode) {
        this.mode = mode;
        notifyObservers();
    }
    
    public int getTargetTemperature() { return targetTemperature; }
    public int getCurrentTemperature() { return currentTemperature; }
    public String getMode() { return mode; }
}