package police.db.network;

import okhttp3.OkHttpClient;

public class Client {
    private static OkHttpClient client;

    public static OkHttpClient get() {
        if(client == null){
            client = new OkHttpClient();
        }
        return client;
    }
}
