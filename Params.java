import java.sql.Time;
public class Params {
    public static final int moveTime = 500; //milliseconds
    public static final int doorTime = 500; //milliseconds
    public static final int floors = 20;
    public static final int testDuration = 3*60; //seconds
    public static final int peopleInTest = 30;
    public static final int minWeight = 80; //pounds
    public static final int maxWeight = 250; //pounds
    public static final int capacity = maxWeight * 4;

    public static final int sampleSeed = 1001;

    //the following values only exist for the PatientSmartBrain
    public static final double serveCondition = 0.4;
    public static final int timeToWait = 60;
    public static final int maxDirectionalChanges = 4;
    public static final Time testStartTime = new Time(8,0,0);

    //DumbBrain, SmartBrain, or PatientSmartBrain
    public static final String brainType = "PatientSmartBrain";
}
