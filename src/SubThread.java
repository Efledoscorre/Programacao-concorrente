import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SubThread {
	
	static Integer contador = 0;
	
	public static void start() throws Exception {
		List<String> arquivos = Main.lerNomeArquivosCSV();

    	int threads = 320;
        List<List<String>> listaArquivosPorThread = Main.separarArquivosPorThread(arquivos, threads);

        List<Thread> threadsComSuasListas = listaArquivosPorThread.stream().map(subLista -> {
        	return new Thread(() -> {
        		try {
					SubThread.lerArquivoComSubThreads(subLista);
				} catch (IOException e) {
					e.printStackTrace();
				}
        	});
        }).toList();
        
        long inicio = System.currentTimeMillis();
        threadsComSuasListas.forEach(thread -> thread.start());
        
        threadsComSuasListas.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
        long fim = System.currentTimeMillis();
        
        System.out.println(fim - inicio);
        System.out.println("NÚMERO DE THREADS CRIADAS: " + SubThread.contador);
	}

    private static List<List<String>> separaConteudoArquivoEm25Partes(String arquivo) throws IOException {
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
    }

    public static void lerArquivoComSubThreads(List<String> listaNomeArquivos) throws IOException {
    	Cidade cidade = new Cidade("teste");

    	List<Thread> subThreads = new ArrayList<>();
    	
    	for(String arquivo : listaNomeArquivos) {
    		List<List<String>> subListasPorThread = separaConteudoArquivoEm25Partes(arquivo);
    		for(List<String> subLista : subListasPorThread) {
    			Thread thread = new Thread(() -> {
    				SubThread.lerListaDaSubThread(subLista);
    			});
    			subThreads.add(thread);
    		}
    	}
    	
    	subThreads.forEach(thread -> thread.start());
    	
    	subThreads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
    }
    
    private static void lerListaDaSubThread(List<String> parteConteudoArquivo) {
    	Cidade cidade = new Cidade("teste");
    	for(String linha : parteConteudoArquivo) {
			if(linha.contains("Year")) {
				continue;
			}
			String[] values = linha.split(",");
	        if (values.length == 6) {
	            int ano = Integer.parseInt(values[4]);
	            int mes = Integer.parseInt(values[2]);
	            double temperatura = Double.parseDouble(values[5]);
	
	            cidade.adicionarTemperatura(ano, mes, temperatura);
	        }    		
    	}
    	synchronized (contador) {
			contador++;
		}
	    System.out.println("Lista executada pela thread: " + Thread.currentThread().getName());
//	    cidade.Min_Max();
    }





}
