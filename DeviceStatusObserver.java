public class DeviceStatusObserver implements Observer {
    private String observerName;
    
    public DeviceStatusObserver(String name) {
        this.observerName = name;
    }
    
    @Override
    public void update(String message) {
        System.out.println("[" + observerName + "] " + message);
    }
}