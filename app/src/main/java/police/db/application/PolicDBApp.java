package police.db.application;

import android.app.Application;

import police.db.box.ObjectBox;

public class PolicDBApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ObjectBox.init(this);
    }
}
