import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Main {
    private static int contador = 0;

    public static void main(String[] args) throws Exception {
    	
    	//Descomente a linha abaixo para testar os experimentos com subthreads
    	//SubThread.start();
    	
    	int contVersoes = 0;
        String nomeArquivo = "versao_" + contVersoes + ".txt";

        int THREADS = 2;
        List<String> arquivos = ArquivosUtils.listaNomesArquivosCSV();
        List<List<String>> listaArquivosPorThread = ArquivosUtils.separaListaArquivosPorThread(arquivos, THREADS);

        ArrayList<Long> temposDeExecucao = new ArrayList<>();
        long inicioTotal = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {
            long inicio = System.currentTimeMillis();
            List<Thread> threadsAExecutar = listaArquivosPorThread.stream().map(lista -> {
                return new Thread(() -> {
                    lista.forEach(arquivo -> lerArquivo(arquivo));
                });
            }).toList();

            threadsAExecutar.forEach(thread -> thread.start());

            threadsAExecutar.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            long fim = System.currentTimeMillis();
            temposDeExecucao.add(fim - inicio);

            System.out.println("ACABOU");
            System.out.println("CONTAGEM: " + contador);
            System.out.println("TEMPO DE EXECUÇÃO: " + (fim - inicio));
        }
        ArquivosUtils.salvarResultado(nomeArquivo, temposDeExecucao);

        long finalTotal = System.currentTimeMillis();
        long tempoTotal = finalTotal - inicioTotal;
        LongStream statistics = temposDeExecucao.stream().mapToLong(num -> num);

        System.out.println("Total: " + statistics.sum()
        + "; minima: " + temposDeExecucao.stream().mapToLong(num -> num).min().orElse(0)
        + "; maxima: " + temposDeExecucao.stream().mapToLong(num -> num).max().orElse(0)
        + "; media: " + temposDeExecucao.stream().mapToLong(num -> num).average().orElse(0));
        ArquivosUtils.salvarEstatisticas(nomeArquivo, temposDeExecucao, statistics);
    }

    private static void lerArquivo(String caminho) {
        contador = contador + 1;
        Cidade cidade = new Cidade(caminho);
        try (BufferedReader br = new BufferedReader(new FileReader("src/resources/temperaturas_cidades/" + caminho))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 6) {
                    int ano = Integer.parseInt(values[4]);
                    int mes = Integer.parseInt(values[2]);
                    double temperatura = Double.parseDouble(values[5]);
                    cidade.adicionarTemperatura(ano, mes, temperatura);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Houve um erro", ex);
        }
        System.out.println(cidade.getNome());
        cidade.Min_Max();
    }
}
