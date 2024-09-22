import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Main {
    public static final Path DIRETORIOCIDADES = Paths.get("src/resources/temperaturas_cidades/");
    private static int contador = 0;

    public static void main(String[] args) throws Exception {
    	
    	//Descomente a linha abaixo para testar os experimentos com subthreads
    	//SubThread.start();
    	
    	int contVersoes = 0;
        String nomeArquivo = "versao_" + contVersoes + ".txt";

        int THREADS = 2;
        List<String> arquivos = lerNomeArquivosCSV();
        List<List<String>> listaArquivosPorThread = separarArquivosPorThread(arquivos, THREADS);

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
            
            salvarResultado(nomeArquivo, i + 1, contador, fim - inicio);
            System.out.println("ACABOU");
            System.out.println("CONTAGEM: " + contador);
            System.out.println("TEMPO DE EXECUÇÃO: " + (fim - inicio));
        }

        long finalTotal = System.currentTimeMillis();
        long tempoTotal = finalTotal - inicioTotal;
        LongStream statistics = temposDeExecucao.stream().mapToLong(num -> num);

        System.out.println("Total: " + statistics.sum()
        + "; minima: " + temposDeExecucao.stream().mapToLong(num -> num).min().orElse(0)
        + "; maxima: " + temposDeExecucao.stream().mapToLong(num -> num).max().orElse(0)
        + "; media: " + temposDeExecucao.stream().mapToLong(num -> num).average().orElse(0));
        salvarEstatisticas(nomeArquivo, temposDeExecucao, statistics);
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
        System.out.println(cidade.nome);
        cidade.Min_Max();
    }

    public static List<String> lerNomeArquivosCSV() throws IOException {
        List<String> arquivos = new ArrayList<>();
        if (Files.isDirectory(DIRETORIOCIDADES)) {
            DirectoryStream<Path> pathsArquivo = Files.newDirectoryStream(DIRETORIOCIDADES);
            for (Path arquivo : pathsArquivo) {
                arquivos.add(arquivo.getFileName().toString());
            }
        }
        return arquivos;
    }

    public static List<List<String>> separarArquivosPorThread(List<String> arquivos, int threads) {
        List<List<String>> arquivosPorThread = new ArrayList<>();
        int numArquivos = arquivos.size();
        int sublistQtd = numArquivos / threads;

        for (int i = 0; i < numArquivos;) {
            int inicioSubList = i;
            i += sublistQtd;
            arquivosPorThread.add(arquivos.subList(inicioSubList, i));
        }
        return arquivosPorThread;
    }

    private static void salvarResultado(String nomeArquivo, int rodada, int contagem, long tempoExecucao) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            writer.write("Rodada " + rodada + "\n");
            writer.write("Tempo de Execução: " + tempoExecucao + "\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void salvarEstatisticas(String nomeArquivo, List<Long> temposDeExecucao, LongStream statistics) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            writer.write("Tempo Médio: " + temposDeExecucao.stream().mapToLong(num -> num).average().orElse(0) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
