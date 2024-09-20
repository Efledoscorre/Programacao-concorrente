import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SubThread {

    public static void experimentoComSubThread(List<List<String>> listaArquivosPorThread ) throws IOException {
        String primeiraLista = listaArquivosPorThread.getFirst().getFirst();
        List<List<String>> lists = lerArquivo(primeiraLista);

        List<Thread> threadsAExecutar = lists.stream().map(lista -> {
            return new Thread(() -> {
                lista.forEach(arquivo -> {
                    try {
                        lerArquivo(arquivo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
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

        for (int i = 0; i < listaArquivosPorThread.size(); i++){

        }


    }

    public static List<List<String>>  lerArquivo(String arquivo) throws IOException {
        Path path = Paths.get(Main.DIRETORIOCIDADES + "/" + arquivo);
        List<String> lines = Files.lines(path).toList();

        List<List<String>> arquivosPorThread = new ArrayList<>();


        int sublistQtd = (lines.size()) / 25;

        for(int i = 0; i < lines.size() - 100;){
            int inicioSubList = i;
            i += sublistQtd;
            arquivosPorThread.add(lines.subList(inicioSubList, i));
        }

        return arquivosPorThread;

        /*System.out.println(lines);
        System.out.println(lines.size());
        System.out.println(lines.size() / 25);
        System.out.println(lines.size() % 25);*/
    }






}
