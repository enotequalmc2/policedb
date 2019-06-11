package police.db.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Police {
    @Id long id;

    public String pid;
    public String name;
    public String position;
    public boolean auxiliary;
    public String source;
}
