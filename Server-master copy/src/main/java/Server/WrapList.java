package Server;

import Model.Dot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WrapList implements Serializable {

    public List<Dot> dots = new ArrayList<>();

    public void clear(){
        this.dots.clear();
    }

    public void ddd(Dot dot){
        this.dots.add(dot);
    }

    public void addAll(List<Dot> dots) {
        this.dots.addAll(dots);
    }
}
