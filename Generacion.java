import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Generacion {

        public static void GenerarCodigoIntermedio(List<Simbolo> tablaSimbolos) {
                String contenido = "";// Cadena final que se guadara en el archivo txt.

                for (Simbolo simbolo : tablaSimbolos) {

                        if (simbolo.tipo == "Real" || simbolo.tipo == "Entero") {
                                contenido += simbolo.tipo + " " + simbolo.token + "\n";
                        }

                        if (simbolo.tipo == "Leer" || simbolo.tipo == "Escribir") {
                                contenido += simbolo.tipo + " " + simbolo.token + "\n";
                        }

                }

                File carpetaFile = new File(
                                System.getProperty("user.dir") + "//Codigos intermedios generados");

                int numeroDeArchivos = 0;
                // Verifica si la ruta es una carpeta y existe
                if (carpetaFile.isDirectory() && carpetaFile.exists()) {
                        // Lista de archivos en la carpeta
                        String[] archivos = carpetaFile.list();
                        if (archivos != null) {
                                numeroDeArchivos = archivos.length + 1;
                        }
                }

                // Ruta del archivo de texto que deseas crear
                String rutaArchivo = System.getProperty("user.dir") + "//Codigos intermedios generados//CódigoP"
                                + numeroDeArchivos + ".txt";

                try {
                        // Crear un objeto FileWriter para escribir en el archivo
                        FileWriter fileWriter = new FileWriter(rutaArchivo);

                        // Crear un objeto BufferedWriter para escribir de manera eficiente
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        // Escribir el contenido en el archivo
                        bufferedWriter.write(contenido);

                        // Cerrar el BufferedWriter (esto también cerrará el FileWriter)
                        bufferedWriter.close();

                        System.out.println("Se ha creado el archivo exitosamente.");
                } catch (IOException e) {
                        System.err.println("Error al crear el archivo: " + e.getMessage());
                }
        }
}
