package net.aldisti.common.providers;

public class IdProvider {
    private static int id = 100000;

    private IdProvider() {}

    public static Integer next() {
        int id = IdProvider.id;
        IdProvider.id++;
        return id;
    }

    public static Integer peek() {
        return IdProvider.id;
    }
}
