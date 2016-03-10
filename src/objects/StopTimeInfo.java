package objects;


public class StopTimeInfo {
    public static final int DEFAULT_VALUES = 0;

    private int hours;
    private int minute;
    private int second;

    public StopTimeInfo() {
        hours = DEFAULT_VALUES;
        minute = DEFAULT_VALUES;
        second = DEFAULT_VALUES;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public boolean isChanged() {
        return hours != 0 || minute != 0 || second != 0;
    }

    public int getAllInSecond() {
        return ((hours * 3600) + (minute * 60) + second);
    }

    @Override
    public String toString() {
        return "hours: " + hours + ";\tminute: " + minute + ";\tsecond: " + second;
    }
}
