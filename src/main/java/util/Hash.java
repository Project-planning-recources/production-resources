package util;

public class Hash {
    private Hash() {}

    public static Long hashForTechProcess(Long orderId, Long productId, Long techProcessId) {
        return orderId + 11 * productId + 31 * techProcessId;
    }
}
