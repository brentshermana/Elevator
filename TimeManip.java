import java.sql.Time;
import java.util.Random;
import static java.lang.Math.toIntExact;

/*
utility methods which make sqlTime bearable,
plus a method needed to generate a sequence of times
spanning a certain interval
*/
public class TimeManip {
	@Deprecated
	public static Time makeTimeAfter (Time relativeTime, int s) throws RuntimeException {
		int s2 = s + timeToSeconds(relativeTime);

		if (s2 >= 24 * 60 * 60) {
			throw new RuntimeException();
		}
		return secondsToTime(s2);
	}

	public static Time[] generateTimes (Time intervalStart, int howManySecondsAfter, int howMany, int seed) {
		Random r = new Random(seed);
		Time[] values = new Time[howMany];
		for (int i = 0; i < howMany; i++) {
			int s = r.nextInt(howManySecondsAfter);
			values[i] = makeTimeAfter(intervalStart, s);
		}
		return values;
	}
	@Deprecated
	public static int timeToSeconds (Time t) {
		return t.getHours() * 3600 + t.getMinutes() * 60 + t.getSeconds();
	}
	@Deprecated
	public static Time secondsToTime (int s) {
		int seconds = s%60;
		s -= seconds;
		s /= 60;
		int minutes = s%60;
		s-=minutes;
		s/=60;
		int hours = s%60;
		return new Time(hours, minutes, seconds);
	}
	public static int difference (Time before, Time after) {
		return TimeManip.timeToSeconds(after) - TimeManip.timeToSeconds(before);
	}

    /**
    *solves the problem of getting the 'current' sqlTime when elevator is running
	*
    *@param systemStartTimeMillis: the System.currentTimeMillis when elevator
    *    begins running ('systemStartingTime' of ElevatorInfo)
    *@param sqlStartTime: the corresponding sql time when  elevator begins
    *    running ('sqlStartingTime' of ElevatorInfo)
    *@param systemCurrentTimeMillis: as you would expect, just toss
    *   System.currentTimeMillis in here
    */
    public static Time systemTimeToSqlTime (long systemStartTimeMillis, Time sqlStartTime, long systemCurrentTimeMillis) {
        int secondsAfter = toIntExact((systemCurrentTimeMillis - systemStartTimeMillis) / 1000l);
        return makeTimeAfter(sqlStartTime, secondsAfter);
    }
}
