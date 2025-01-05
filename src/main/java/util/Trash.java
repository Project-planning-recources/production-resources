package util;


import algorithm.NelderMeadAlgorithm.NMVATransaction;

/**
 * Класс для любых тестов и проверок
 */
public class Trash {
    public static void main(String[] args) throws Exception {
        testTransaction();
    }


    public static void testTransaction() throws Exception {
        NMVATransaction transaction = new NMVATransaction();
        transaction.StartTransaction();
    }
}
