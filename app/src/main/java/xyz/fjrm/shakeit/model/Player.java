package xyz.fjrm.shakeit.model;

/**
 * Clase modelo de un jugador cualquiera.
 * @author Francisco Javier Reyes Mangas
 */
public class Player{
    private String name;
    private String id;
    private int score;

    public Player(String name, String id, int score){
        this.name = name;
        this.id = id;
        this.score = score;
    }

    public Player(String name, String id){
        this.name = name;
        this.id = id;
    }

    public Player(String id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player))
            return false;
        return ((Player) o).getId().toLowerCase().equals(this.id.toLowerCase());
    }

    @Override
    public int hashCode() {
        return name.hashCode()*97+id.hashCode();
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
