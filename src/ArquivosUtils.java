import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class ArquivosUtils {

    public static final Path DIRETORIO_CIDADES = Paths.get("src" + File.separator + "resources" + File.separator + "temperaturas_cidades" + File.separator);

    public static List<String> listaNomesArquivosCSV() throws IOException {
        List<String> arquivos = new ArrayList<>();
        try(DirectoryStream<Path> pathsArquivo = Files.newDirectoryStream(DIRETORIO_CIDADES)){
            for (Path arquivo : pathsArquivo) {
                arquivos.add(arquivo.getFileName().toString());
            }
        }
        return arquivos;
    }

    public static List<List<String>> separaListaArquivosPorThread(List<String> arquivos, int threads) {
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

    public static List<List<String>> separaConteudoArquivoEm25Partes(String arquivo) throws IOException {
        Path path = Paths.get(DIRETORIO_CIDADES + "/" + arquivo);
        List<String> lines = Files.lines(path).toList();

        List<List<String>> arquivosPorThread = new ArrayList<>();

        int sublistQtd = (lines.size()) / 25;

        for(int i = 0; i < lines.size() - 100;){
            int inicioSubList = i;
            i += sublistQtd;
            arquivosPorThread.add(lines.subList(inicioSubList, i));
        }

        return arquivosPorThread;
    }

    public static void salvarResultado(String nomeArquivo, ArrayList<Long> temposDeExecucao) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");
            writer.write("Executado em: " + LocalDateTime.now().format(dtf) + "\n\n");
            for (int i = 1; i <= temposDeExecucao.size(); i++) {
                writer.write("Rodada " + i + "\n");
                writer.write("Tempo de Execução: " + temposDeExecucao.get(i - 1) + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void salvarEstatisticas(String nomeArquivo, List<Long> temposDeExecucao, LongStream statistics) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            writer.write("Tempo Médio: " + temposDeExecucao.stream().mapToLong(num -> num).average().orElse(0) + "\n\n");
            writer.write("------------------------------------\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
