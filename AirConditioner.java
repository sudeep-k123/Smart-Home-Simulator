public class AirConditioner extends Device {
    private int temperature; // in Celsius
    private String mode;     // "cool", "heat", "fan", "dry"
    
    public AirConditioner(String name) {
        super(name);
        this.temperature = 22;
        this.mode = "cool";
    }
    
    @Override
    public String getStatus() {
        if (!isOn) return String.format("%s: OFF", name);
        return String.format("%s: ON, Temperature: %d°C, Mode: %s", name, temperature, mode);
    }
    
    @Override
    public void toggle() {
        isOn = !isOn;
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
        notifyObservers();
    }
    
    public void setTemperature(int temperature) {
        if (temperature >= 16 && temperature <= 30) {
            this.temperature = temperature;
            if (!isOn) turnOn();
            notifyObservers();
        }
    }
    
    public void setMode(String mode) {
        this.mode = mode;
        if (!isOn) turnOn();
        notifyObservers();
    }
    
    public int getTemperature() { return temperature; }
    public String getMode() { return mode; }
}