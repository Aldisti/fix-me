package net.aldisti.common.providers;

public class IdProvider {
    private static int id = 1;

    private IdProvider() {}

    public static Integer generate() {
        int id = IdProvider.id;
        IdProvider.id++;
        return id;
    }
}
