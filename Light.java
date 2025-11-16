import java.io.Serializable;

public class Light extends Device implements Serializable {
    private static final long serialVersionUID = 1L;
    private int brightness; // 0-100%
    private String color;   // "warm", "cool", "daylight"
    
    public Light(String name) {
        super(name);
        this.brightness = 50;
        this.color = "warm";
    }
    
    @Override
    public String getStatus() {
        if (!isOn) return String.format("%s: OFF", name);
        return String.format("%s: ON, Brightness: %d%%, Color: %s", name, brightness, color);
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
    
    public void setBrightness(int brightness) {
        if (brightness >= 0 && brightness <= 100) {
            this.brightness = brightness;
            if (!isOn) turnOn();
            notifyObservers();
        }
    }
    
    public void setColor(String color) {
        this.color = color;
        notifyObservers();
    }
    
    public int getBrightness() { return brightness; }
    public String getColor() { return color; }
}