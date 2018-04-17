package sakura.common.reactor.event;

public interface MyEventListener {
    void onNewEvent(MyEvent event);

    void onEventStopped();
}