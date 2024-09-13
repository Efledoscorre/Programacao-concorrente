import java.util.ArrayList;
import java.util.List;

public class Pais {
    String nome;
    List<Cidade> cidades;

    public Pais(String nome) {
        this.nome = nome;
        this.cidades = new ArrayList<>();
    }
}
