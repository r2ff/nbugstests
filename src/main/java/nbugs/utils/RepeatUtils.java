package nbugs.utils;

public class RepeatUtils {

    public static <T> void repeat(int iterations, Runnable action) {
        for (int i = 0; i < iterations; i++) {
            action.run();
        }
    }
}
