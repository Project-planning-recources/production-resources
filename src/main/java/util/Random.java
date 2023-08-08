package util;

public class Random {
    private static final java.util.Random random = new java.util.Random();

    private Random() {}

    public static int randomInt(int max) {
        return random.nextInt(max);
    }

}
