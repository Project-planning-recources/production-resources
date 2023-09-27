package util;

public class Random {
    private static final java.util.Random random = new java.util.Random();

    private Random() {}

    public static int randomInt(int max) {
        return random.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

}
