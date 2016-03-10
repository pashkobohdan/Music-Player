package objects;


public class StopCount {
    public static final int DEFAULT_COUNT = 0;

    private boolean isRePlay = false;
    private int count;

    public StopCount() {
        this.setCount(DEFAULT_COUNT);
    }

    public StopCount(int count) {
        this.setCount(count);
    }

    public boolean isRePlay() {
        return isRePlay;
    }

    public int getCount() {
        return count;
    }

    public boolean isChanged() {
        return count != DEFAULT_COUNT;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
