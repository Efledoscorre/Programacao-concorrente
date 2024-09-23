import java.util.ArrayList;
import java.util.List;

public class Cidade {
    private String nome;
    private List<Temperatura> temperaturas;

    public Cidade(String nome) {
        this.nome = nome;
        this.temperaturas = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<Temperatura> getTemperaturas() {
        return temperaturas;
    }

    public void adicionarTemperatura(int ano, int mes, double valor) {
        this.temperaturas.add(new Temperatura(ano, mes, valor));
    }


    public void  Min_Max() {
        System.out.println("cidade: " + nome);
        int anoInicial = 1995;
        int anoFinal = 2020;
        boolean printou = false;

        for (int ano = anoInicial; ano <= anoFinal; ano++) {

            for (int mes = 1; mes <= 12; mes++) {
                List<Double> temperaturasMes = new ArrayList<>();
                for (Temperatura t : temperaturas) {
                    if (t.ano == ano && t.mes == mes) {
                        temperaturasMes.add(t.valor);
                    }
                }

                if (!temperaturasMes.isEmpty()) {
                    double min = temperaturasMes.get(0);
                    double max = temperaturasMes.get(0);
                    double soma = 0.0;

                    for (double temp : temperaturasMes) {
                        if (temp < min) min = temp;
                        if (temp > max) max = temp;
                        soma += temp;
                    }

                    if(!printou){
                        System.out.println();
                        System.out.println("ano: " + ano);
                        printou = true;
                    }

                    double media = soma / temperaturasMes.size();
                    System.out.printf("mes: %d  minima: %.2f  maxima: %.2f  media: %.2f%n", mes, min, max, media);
                }
            }
        }
    }
}
