package Model;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    public long Id;
    public List <Dot> dots;
    public Snake(){
        this.dots = new ArrayList<Dot>();
    }

    public Snake(List<Dot> dots) {
        this.dots = dots;
    }
    public void updateState(List<Dot> dots){
        this.dots = dots;
    }

    public List<Dot> update(List<Snake> fullCharacters){
        List<Dot> dots = new ArrayList<>();
        for(Snake character : fullCharacters){
            if(character.Id == this.Id){
                continue;
            }

            dots.addAll(character.dots);
        }
        return dots;
    }
}
