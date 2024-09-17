public class Temperatura {
    int ano;
    int mes;
    double valor;

    public Temperatura(int ano, int mes, double valor) {
        this.ano = ano;
        this.mes = mes;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Temperatura{" +
                "ano=" + ano +
                ", mes=" + mes +
                ", valor=" + valor +
                "}\n";
    }
}
