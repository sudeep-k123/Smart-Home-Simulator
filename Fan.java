public class Fan extends Device {
    private int speed; // 1-5
    private boolean oscillating;
    
    public Fan(String name) {
        super(name);
        this.speed = 1;
        this.oscillating = false;
    }
    
    @Override
    public String getStatus() {
        if (!isOn) return String.format("%s: OFF", name);
        return String.format("%s: ON, Speed: %d, Oscillating: %s", name, speed, oscillating ? "YES" : "NO");
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
    
    public void setSpeed(int speed) {
        if (speed >= 1 && speed <= 5) {
            this.speed = speed;
            if (!isOn) turnOn();
            notifyObservers();
        }
    }
    
    public void toggleOscillation() {
        oscillating = !oscillating;
        notifyObservers();
    }
    
    public int getSpeed() { return speed; }
    public boolean isOscillating() { return oscillating; }
}