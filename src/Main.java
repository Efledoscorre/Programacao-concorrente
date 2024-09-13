import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
<<<<<<< HEAD
   public static void main(String[] args) {

       Guardador guardador = new Guardador();
       String filePath;
       filePath = "C:\\Users\\lucas\\IdeaProjects\\Concorrente\\resources\\temperaturas_cidades\\temperaturas_cidades";
       String line;
       while ((line = br.readLine()) != null) {

           String[] values = line.split(",");


           if (values.length == 6) {
               int ano = Integer.parseInt(values[2]);
               int mes = Integer.parseInt(values[3]);
               double temperatura = Double.parseDouble(values[5]);


               cidade.adicionarTemperatura(ano, mes, temperatura);
           }
       }
   }
    catch(IOException e) {
        e.printStackTrace();
=======
    public static void main(String[] args) {
        String filePath;
        filePath = "src/resources/temperaturas_cidades/Albania__Tirana.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
>>>>>>> 9f8758517e4d030081dd463d5e81ecd844a00a25
    }
}



