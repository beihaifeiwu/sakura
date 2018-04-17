package sakura.common.reactor.event;


import java.util.ArrayList;
import java.util.List;

public class MyEventSource {

    private List<MyEventListener> listeners;

    public MyEventSource() {
        this.listeners = new ArrayList<>();
    }

    public void register(MyEventListener listener) {
        listeners.add(listener);
    }

    public void newEvent(MyEvent event) {
        for (MyEventListener listener : listeners) {
            listener.onNewEvent(event);
        }
    }

    public void eventStopped() {
        for (MyEventListener listener : listeners) {
            listener.onEventStopped();
        }
    }
}