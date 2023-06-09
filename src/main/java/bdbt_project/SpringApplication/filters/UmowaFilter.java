package bdbt_project.SpringApplication.filters;

import java.util.ArrayList;
import java.util.List;

public class UmowaFilter implements ValueFilter<String> {
    private String id;

    public UmowaFilter(){}

    public UmowaFilter(String id) {
        this.id = id;
    }

    @Override
    public List<String> getSelected() {
        var selected = new ArrayList<String>();
        if(id != null && !id.equals("")) selected.add(id);
        return selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GatunekFilter{" +
                "pies='" + id + '\'' +
                '}';
    }
}
