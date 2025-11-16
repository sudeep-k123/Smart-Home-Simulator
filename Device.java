import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Device implements Observable, Serializable {
    protected String id;
    protected String name;
    protected boolean isOn;
    protected List<Observer> observers;
    
    public Device(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isOn = false;
        this.observers = new ArrayList<>();
    }
    
    public abstract String getStatus();
    public abstract void toggle();
    public abstract void turnOn();
    public abstract void turnOff();
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isOn() { return isOn; }
    
    // Observable implementation
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(getStatus());
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s): %s", name, getClass().getSimpleName(), isOn ? "ON" : "OFF");
    }
}