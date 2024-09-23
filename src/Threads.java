import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.LongStream;

public class Threads {

    private static final List<String> NOMES_TODOS_ARQUIVOS = ArquivosUtils.listaNomesArquivosCSV();

    public static void experimentos1A10(int rodadas){
        final List<Integer> numeroThreadsExperimentos = List.of(1, 2, 4, 8, 16, 32, 64, 80, 160, 320);

        for (int i = 0; i < numeroThreadsExperimentos.size(); i++) {
            realizaExperimento(numeroThreadsExperimentos.get(i), i + 1, rodadas);
        }
    }
    
    private static void realizaExperimento(int THREADS, int numeroExperimento, int qtdRodadas){
        String nomeArquivo = "versao_" + numeroExperimento + ".txt";

        List<List<String>> listaArquivosPorThread = ArquivosUtils.separaListaArquivosPorThread(NOMES_TODOS_ARQUIVOS, THREADS);

        ArrayList<Long> temposDeExecucao = new ArrayList<>();

        for (int i = 0; i < qtdRodadas; i++) {
            List<Thread> threadsAExecutar = listaArquivosPorThread.stream().map(lista -> {
                return new Thread(() -> {
                    lista.forEach(arquivo -> lerArquivo(arquivo));
                });
            }).toList();

            long inicio = System.currentTimeMillis();
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

            System.out.println("TEMPO DE EXECUÇÃO: " + (fim - inicio));
        }
        ArquivosUtils.salvarResultado(nomeArquivo, temposDeExecucao);

        LongSummaryStatistics statistics = temposDeExecucao.stream().mapToLong(num -> num).summaryStatistics();

        System.out.println("Total: " + statistics.getSum()
                + "; minima: " + statistics.getMin()
                + "; maxima: " + statistics.getMax()
                + "; media: " + statistics.getAverage());
        ArquivosUtils.salvarEstatisticas(nomeArquivo, temposDeExecucao, statistics);
    }

    private static void lerArquivo(String caminho) {
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
