
//Biblotecas importadas
import java.io.IOException; //Bibloteca Control de excepción para localizar el archivo
import java.io.UnsupportedEncodingException; //Bibloteca Control de excepción para convertir bytes a string
import java.nio.file.Files; //Bibloteca verificacion de existencia el archivo
import java.nio.file.Path; //Bibloteca de la ruta
import java.nio.file.Paths; //Bibloteca del instanciador de la ruta

public class Ejecucion {

    /**
     * @author Braulio Yail Palominos Patiño
     */

    public static void main(String[] args) throws IOException {
        // Envia el codigo fuente obtenido del archivo Ejemplo.txt
        Analisis analisis = new Analisis(ObtenerCodigoFuente(System.getProperty("user.dir") + "\\CodigoFuente.txt"));
        analisis.Generar();
    }

    public static String ObtenerCodigoFuente(String rutaEstatica) throws IOException {
        Path ruta = Paths.get(rutaEstatica);
        // Finaliza el programa y lanza un mensage, en caso de no existir el directorio
        if (!Files.exists(ruta)) {
            System.err.println("No existe el directorio");
            return "";
        }
        // Almacena los bytes encontrados en la ruta del archivo
        byte[] bytes = Files.readAllBytes(ruta);
        // Regresa una cadena tipo String de los bytes del archivo ingresando a una
        // función retorno
        return ConvertirBytesAString(bytes);
    }

    private static String ConvertirBytesAString(byte[] byteValue) throws UnsupportedEncodingException {
        String stringValue = (new String(byteValue, "US-ASCII"));
        return (stringValue);
    }

}
