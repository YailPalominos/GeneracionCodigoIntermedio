import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analisis {
    /**
     * @author Braulio Yail Palominos Patiño
     */
    private String codigoFuente = ""; // Codigo fuente resibido de la clase de ejecutador.
    private List<Simbolo> tablaSimbolos;// Lista de tablaSimbolos donde se guardara toda la información.
    private int posLectura = 0; // Posición de lectura con respecto al codigo fuente.
    private int linea = 1; // Linea en la que va la posicion de lectura con respecto al codigo fuente.
    private int Id = 0;// Identificardor auto incremental para las variables.
    private int IdTemporales = 1;// Identificador para las variables temporales.

    public Analisis(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    // Genera las clases con los simbolos
    public void Generar() {

        // Inicializamos la tabla de simbolos
        tablaSimbolos = new ArrayList<Simbolo>();
        // Codigo fuente a chart para leer parte por parte
        var letras = this.codigoFuente.toCharArray();
        String palabra = "";
        String palabras = "";

        // Expreciones regulares:
        Pattern cambioLinea = Pattern.compile("\n");
        Pattern iniciarVariables = Pattern
                .compile("((Real|Entero) (([a-zA-Z0-9]+)\\,)(([a-zA-Z0-9]+)\\,)*(([a-zA-Z0-9]+)\\;))");
        Pattern leerOEscribirVariables = Pattern
                .compile("\\b(?:Leer|Escribir)\\((?:[a-zA-Z0-9]+(?:,\\s*[a-zA-Z0-9]+)*)\\);");
        Pattern realizarOperacion = Pattern.compile(
                "(\\w+)\\s*=\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*((?:[-+*/]\\s*((?:\\d+(?:\\.\\d+)?)|\\w+)\\s*)+)(\\s*(\\([^()]+\\)|\\[[^\\[\\]]+\\]))?\\s*;");

        // Recorre palabra por palabra encontrada
        for (int y = 0; y < letras.length; y++) {

            palabra += letras[y];
            posLectura++;
            // Para detectar el cambio de linea
            Matcher matcherCambioLinea = cambioLinea.matcher(codigoFuente);
            matcherCambioLinea.region(0, y);
            int lineaV = 1;
            while (matcherCambioLinea.find()) {
                lineaV++;
            }
            if (lineaV > linea) {
                linea = lineaV;
                posLectura = 0;
            }

            if (palabra.split("\\s").length > 0) {
                palabra = palabra.trim();

                char letra = letras[y];
                var x = ((letra + "").replace("", " ").trim());

                if (x.length() == 0 | x.equals(";")) {

                    if (palabras.length() == 0) {
                        palabras += palabra;
                    } else {
                        palabras += " " + palabra;
                    }
                    palabra = "";

                    Matcher matcherIniciarVariables = iniciarVariables.matcher(palabras);
                    Matcher matcherLeerEscribirVariables = leerOEscribirVariables.matcher(palabras);
                    Matcher matcherRealizaroperaciones = realizarOperacion.matcher(palabras);

                    if (matcherIniciarVariables.find()) {
                        // Es una asignación de varias variables a la vez sin un valor inicial.
                        // Ejemplo: Real cuenta,numero,resultado;
                        String tipo = "";

                        if (palabras.contains("Real ")) {
                            palabras = palabras.replace("Real ", "");
                            tipo = "Real";
                        }
                        if (palabras.contains("Entero ")) {
                            palabras = palabras.replace("Entero ", "");
                            tipo = "Entero";
                        }
                        palabras = palabras.replace(";", "");

                        String[] arregloPalabras = palabras.split(",");
                        for (String variable : arregloPalabras) {
                            AgregarSimbolo(variable, tipo, Id, 1,
                                    "" + linea, "0");
                            Id += 1;
                        }
                        palabras = "";
                    }

                    if (matcherLeerEscribirVariables.find()) {
                        // Es para leer o escribir las variables
                        // Ejemplo: Leer(valor);
                        // Ejemplo: Escribir(valor);
                        // Ejemplo: Escribir(x,z);
                        // Ejemplo: Leer(A,B,C);

                        String tipo = "";

                        if (palabras.contains("Escribir")) {
                            palabras = palabras.replace("Escribir", "");
                            tipo = "Escribir";
                        }
                        if (palabras.contains("Leer")) {
                            palabras = palabras.replace("Leer", "");
                            tipo = "Leer";
                        }

                        palabras = palabras.replace("Leer", "");
                        palabras = palabras.replace("Escribir", "");
                        palabras = palabras.replace("(", "");
                        palabras = palabras.replace(")", "");
                        palabras = palabras.replace(";", "");

                        String[] arregloPalabras = palabras.split(",");
                        for (String variable : arregloPalabras) {
                            AgregarSimbolo(variable, tipo, Id, 1,
                                    "" + linea, "0");
                            Id += 1;
                        }

                        palabras = "";
                    }

                    if (matcherRealizaroperaciones.find()) {
                        // Es para validar operaciones
                        // cuenta=23+(numero-valor);
                        // numero=cuenta/123.99;
                        // resultado=numero+cuenta;

                        // Primero separamos la parte de la asignación
                        palabras = palabras.replace(";", "");
                        String[] asignacion = palabras.split("=");
                        String tipo = "";

                        if (ComprobarToken(asignacion[0]) == false) {
                            GenerarError("No se encontro el token al cual se le esta asignando", palabras);
                        } else {
                            Simbolo simbolo = VerSimbolo(asignacion[0]);
                            tipo = simbolo.tipo;
                        }

                        String operacionSeparada = "";
                        String parteOperacionSeparada = asignacion[1];

                        parteOperacionSeparada = parteOperacionSeparada.replace("+", " + ");
                        parteOperacionSeparada = parteOperacionSeparada.replace("-", " - ");
                        parteOperacionSeparada = parteOperacionSeparada.replace("/", " / ");
                        parteOperacionSeparada = parteOperacionSeparada.replace("*", " * ");
                        operacionSeparada = operacionSeparada + parteOperacionSeparada;

                        String operacionPrefija = RecorridoPrefijo.realizarPrefijo(operacionSeparada);
                        GenerarOperacion(asignacion[0], operacionSeparada);

                        palabras = "";
                    }

                }
            }
        }

        Generacion.GenerarCodigoIntermedio(tablaSimbolos);

    }

    public void GenerarOperacion(String variable, String valor) {

        // e=a+b*c/d;
        // g=f*2;
        String[] operandos = valor.split(" ");

        boolean guardado = false;
        boolean asignacion = true;
        String operador = "";

        AgregarSimbolo("temp" + IdTemporales, "=", IdTemporales, 1,
                "" + linea, operandos[0]);

        for (int x = 1; x < operandos.length; x++) {
            if (esOperador(operandos[x])) {
                operador = operandos[x];
            } else {

                if (guardado)
                    IdTemporales += 1;
                AgregarSimbolo("temp" + IdTemporales, asignacion == true ? operador : "=", IdTemporales, 1,
                        "" + linea, operandos[x]);
                if (guardado)
                    IdTemporales -= 1;

                if (operandos.length > 3) {
                    // = temp1 temp2
                    IdTemporales += 1;
                    AgregarSimbolo("temp" + IdTemporales, asignacion == false ? operador : "=",
                            IdTemporales, 1,
                            "" + linea, "temp" + (IdTemporales - 1));
                    guardado = true;

                }
                asignacion = false;
            }
        }

        // Agrega la asignación
        AgregarSimbolo(variable, "=", IdTemporales, 1, "" + linea, "temp" + IdTemporales);
    }

    private boolean esOperador(String caracteres) {
        if (caracteres.length() == 1) {
            if (caracteres.equals("+") || caracteres.equals("-") || caracteres.equals("*") || caracteres.equals("/")) {
                return true;
            } else {
                return false;
            }
        } else if (caracteres.length() > 1) {
            return false;
        } else {
            return false;
        }
    }

    public boolean ComprobarToken(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.token.equals(token)) {
                AñadirRepeticion(token);
                return true;
            }
        }
        return false;
    }

    public void AñadirRepeticion(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.token.equals(token)) {
                simbolo.repeticiones++;
                simbolo.linea = simbolo.linea + "," + linea;
            }
        }
    }

    public void GenerarError(String error, String palabra) {

        System.out.println();
        System.out.format("%10s %10s %10s %10s",
                " \033[31mError " + error + ": \033[0m" + palabra, "Linea " + linea,
                " Inicia " + (posLectura - 1), " Termina " + (posLectura - 1 + palabra.length()));
        System.out.println();
        return;
    }

    public boolean VerificarConstante(String constante) {
        Pattern constantes = Pattern
                .compile("[\\d.]+");
        Matcher matcherConstantes = constantes.matcher(constante);
        if (matcherConstantes.find()) {
            return true;
        } else {
            return false;
        }
    }

    public Simbolo VerSimbolo(String token) {
        for (Simbolo simbolo : tablaSimbolos) {
            if (simbolo.token.equals(token)) {
                return simbolo;
            }
        }
        return null;
    }

    public void EscribirSimbolo(Simbolo oSimbolo) {
        for (int x = 0; x < tablaSimbolos.size(); x++) {
            if (tablaSimbolos.get(x).token.equals(oSimbolo.token)) {
                tablaSimbolos.set(x, oSimbolo);
            }
        }
    }

    public void AgregarSimbolo(String token, String tipo, int idToken, int repeticiones, String linea,
            String valor) {
        Simbolo oSimbolo = new Simbolo();
        oSimbolo.token = token;
        oSimbolo.tipo = tipo;
        oSimbolo.idToken = idToken;
        oSimbolo.repeticiones = repeticiones;
        oSimbolo.linea = linea;
        oSimbolo.valor = valor;
        tablaSimbolos.add(oSimbolo);
    }
}
