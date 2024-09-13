import java.util.ArrayList;
import java.util.List;

public class Guardador {
    List<Pais> paises;

    public Guardador() {
        this.paises = new ArrayList<>();
    }

    public void adicionarPais(Pais pais) {
        this.paises.add(pais);
    }


    public Pais encontrarPais(String nomePais) {
        for (Pais pais : paises) {
            if (pais.nome.equals(nomePais)) {
                return pais;
            }
        }
        return null;
    }


    public void exibirDados() {
        for (Pais pais : paises) {
            pais.exibirDados();
        }
    }
}
