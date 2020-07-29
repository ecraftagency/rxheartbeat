package eventLoop;

public class ScoreObject {
  public int    id;
  public long   score;
  public String name;
  public static ScoreObject of(int id, long score, String name) {
    ScoreObject scoreObj = new ScoreObject();
    scoreObj.id = id;
    scoreObj.score = score;
    scoreObj.name = name;
    return scoreObj;
  }

  @Override
  public String toString() {
    return "ScoreObject{" +
            "id=" + id +
            ", score=" + score +
            ", name='" + name + '\'' +
            '}';
  }
}
