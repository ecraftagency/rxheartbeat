import com.common.Utilities;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Json101 {
  public static void main(String[] args) {
    int nLayer  = 3;
    int nRow    = 5;
    int nCol    = 5;
    String json = "[[0,1,1,1],[1,2,1,1],[0,2,2,1],[4,2,2,1],[4,2,6,6]]";



    Type listOfListOfInt  = new TypeToken<List<List<Integer>>>() {}.getType();
    List<List<Integer>> tiles = Utilities.gson.fromJson(json, listOfListOfInt);

    Map<Integer, List<Tile>> layerMap = tiles.stream()
      .filter(tile -> tile.get(0) < nLayer && tile.get(2) < nCol && tile.get(3) < nRow) // filter illegal tile
      .map(Tile::of) //transform list<int> -> Tile object
      .collect(Collectors.groupingBy(Tile::getType, Collectors.toList())); //construct map

    System.out.println(layerMap);
  }

  public static class Tile {
    public int layer;
    public int type;
    public int col;
    public int row;

    public static Tile of(List<Integer> n) {
      Tile m = new Tile();
      m.layer = n.get(0);
      m.type = n.get(1);
      m.col = n.get(2);
      m.row = n.get(3);
      return m;
    }

    public int getLayer() {
      return layer;
    }

    public int getType() {
      return type;
    }

    @Override
    public String toString() {
      return "Tile{" +
              "layer=" + layer +
              ", type=" + type +
              ", col=" + col +
              ", row=" + row +
              '}';
    }
  }
}
