import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class SubThreads {
	
	private static final List<String> NOMES_TODOS_ARQUIVOS = ArquivosUtils.listaNomesArquivosCSV();

	public static void experimentos11A20(int rodadas) {
		final List<Integer> numeroThreadsExperimentos = List.of(1, 2, 4, 8, 16, 32, 64, 80, 160, 320);

		for (int i = 0; i < numeroThreadsExperimentos.size(); i++) {
			realizaExperimento(numeroThreadsExperimentos.get(i), (i + 1) + 10, rodadas);
		}
	}

	private static void realizaExperimento(int THREADS, int numeroExperimento, int qtdRodadas){

		String nomeArquivoExperimento = "versao_" + numeroExperimento + ".txt";

		List<List<String>> listaArquivosPorThread = ArquivosUtils.separaListaArquivosPorThread(NOMES_TODOS_ARQUIVOS, THREADS);

		List<Long> temposDeExecucao = new ArrayList<>();
		for (int i = 0; i < qtdRodadas; i++) {

			List<Thread> threadsComSuasListas = listaArquivosPorThread.stream().map(subLista -> {
				return new Thread(() -> {
					try {
						SubThreads.lerArquivoComSubThreads(subLista);
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
			temposDeExecucao.add(fim - inicio);
			System.out.println("TEMPO DE EXECUÇÃO: " + (fim - inicio) + " ms");

		}
		ArquivosUtils.salvarResultado(nomeArquivoExperimento, temposDeExecucao);

		LongStream statistics = temposDeExecucao.stream().mapToLong(num -> num);

		System.out.println("Total: " + statistics.sum()
				+ "; minima: " + temposDeExecucao.stream().mapToLong(num -> num).min().orElse(0)
				+ "; maxima: " + temposDeExecucao.stream().mapToLong(num -> num).max().orElse(0)
				+ "; media: " + temposDeExecucao.stream().mapToLong(num -> num).average().orElse(0));
		ArquivosUtils.salvarEstatisticas(nomeArquivoExperimento, temposDeExecucao, statistics);

	}

    public static void lerArquivoComSubThreads(List<String> listaNomeArquivos) throws IOException {

    	for(String arquivo : listaNomeArquivos) {
    		List<List<String>> subListasPorThread = ArquivosUtils.separaConteudoArquivoEm25Partes(arquivo);
    		for(List<String> subLista : subListasPorThread) {
				SubThreads.lerListaDaSubThread(subLista, arquivo);
    		}
    	}

    }
    
    public static void lerListaDaSubThread(List<String> parteConteudoArquivo, String nomeCidade) {
    	Cidade cidade = new Cidade(nomeCidade);
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

		cidade.Min_Max();

    }



}
