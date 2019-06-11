package police.db.box;

import android.content.Context;

import io.objectbox.BoxStore;
import police.db.entity.MyObjectBox;

public class ObjectBox {
    private static BoxStore boxStore;

    public static BoxStore get() {
        return boxStore;
    }

    public static void init(Context context){
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }
}
