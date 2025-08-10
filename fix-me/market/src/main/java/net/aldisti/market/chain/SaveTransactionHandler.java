package net.aldisti.market.chain;

import net.aldisti.market.db.Database;

public class SaveTransactionHandler implements Handler {

    private static final SaveTransactionHandler INSTANCE = new SaveTransactionHandler();

    private SaveTransactionHandler() {}

    public static SaveTransactionHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handle(Request request, Response response) {
        Database.save(response.getTransaction());
    }
}
