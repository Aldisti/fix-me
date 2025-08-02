package net.aldisti.router;

public class IdProvider {
    private static int id = 100000;

    private IdProvider() {}

    public static Integer next() {
        return id++;
    }

    public static Integer peek() {
        return IdProvider.id;
    }
}
