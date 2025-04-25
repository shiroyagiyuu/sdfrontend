package pureplus;

public class SDQueEvent {
    int  type;
    final static int  ADD = 0, REMOVE = 1;

    public SDQueEvent(int evtype) {
        this.type = evtype;
    }
}
