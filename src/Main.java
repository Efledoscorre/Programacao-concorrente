import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static int contador = 0;
    public static void main(String[] args) throws IOException {

        Guardador guardador = new Guardador();

        Path diretorioCidades = Paths.get("src/resources/temperaturas_cidades");

        ArrayList<Integer> listTempoDeExecucao = new ArrayList<>();
        List<String> arquivos = new ArrayList<>();

        if (Files.isDirectory(diretorioCidades)) {
            DirectoryStream<Path> pathsArquivo = Files.newDirectoryStream(diretorioCidades);
            int i = 0;
            for(Path arquivo : pathsArquivo){
                arquivos.add(arquivo.getFileName().toString());
//                lerArquivo(arquivo);
                i++;
            }
        }

        List<List<String>> listaArquivosPorThread = new ArrayList<>();
        int numArquivos = 320;
        int threads = 320;

        for(int i = 0; i < numArquivos;){
            int inicioSubList = i;
            int sublistQtd = numArquivos / threads;
            i += sublistQtd;
            listaArquivosPorThread.add(arquivos.subList(inicioSubList, i));
        }

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

        System.out.println("ACABOU");
        System.out.println("CONTAGEM: " + contador);
        System.out.println("TEMPO DE EXECUÇÃO: " + (fim - inicio));

    }

    private static void lerArquivo(String caminho){
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
        }catch (IOException ex){
            throw new RuntimeException("Houve um erro", ex);
        }
        System.out.println(cidade.nome);
        cidade.Min_Max();
    }
}