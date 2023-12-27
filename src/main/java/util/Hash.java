package util;

public class Hash {
    private Hash() {}

    public static Long hash(Long first, Long second) {
        return first + 11 * second;
    }
    public static Long hash(Long first, Long second, Long third) {
        return first + 11 * second + 31 * third;
    }
}
