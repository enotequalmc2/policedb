package police.db.event;

import java.util.ArrayList;
import java.util.List;

import police.db.entity.Police;

public class PoliceListEvent {
    private List<Police> polices = new ArrayList<>();

    public PoliceListEvent(List<Police> polices){
        this.polices = polices;
    }

    public List<Police> getPolices() {
        return polices;
    }
}
