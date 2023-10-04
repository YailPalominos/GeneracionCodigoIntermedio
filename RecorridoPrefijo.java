import java.util.Stack;

public class RecorridoPrefijo {
    public static String realizarPrefijo(String operacion) {

        Stack<Character> operadores = new Stack<>();
        Stack<String> operandos = new Stack<>();
        StringBuilder expresionPrefija = new StringBuilder();

        for (int i = 0; i < operacion.length(); i++) {
            char caracter = operacion.charAt(i);

            if (esOperador(caracter)) {
                // Si el caracter es un operador, se compara su jerarquía con los operadores en
                // la pila.
                while (!operadores.isEmpty() && getJerarquia(caracter) <= getJerarquia(operadores.peek())) {
                    // Mientras el operador en la pila tenga mayor o igual jerarquía, se desapilan
                    // operandos y se forma una nueva expresión.
                    String operando1 = operandos.pop();
                    String operando2 = operandos.pop();
                    char currentOperator = operadores.pop();
                    String newOperand = currentOperator + " " + operando2 + " " + operando1;
                    operandos.push(newOperand);
                }
                // Se apila el operador actual en la pila de operadores.
                operadores.push(caracter);
            } else if (caracter == '(') {
                // Si el caracter es un paréntesis izquierdo, se apila directamente en la pila
                // de operadores.
                operadores.push(caracter);
            } else if (caracter == ')') {
                // Si el caracter es un paréntesis derecho, se desapilan operadores y operandos
                // hasta encontrar el paréntesis izquierdo correspondiente.
                while (!operadores.isEmpty() && operadores.peek() != '(') {
                    String operando1 = operandos.pop();
                    String operando2 = operandos.pop();
                    char currentOperator = operadores.pop();
                    String newOperand = currentOperator + " " + operando2 + " " + operando1;
                    operandos.push(newOperand);
                }
                operadores.pop(); // Se retira el paréntesis izquierdo de la pila de operadores.
            } else if (!Character.isWhitespace(caracter)) { // Ignoramos los espacios en blanco.
                // Si el caracter es un número o parte de un número, lo añadimos a la pila de
                // operandos.
                StringBuilder token = new StringBuilder();
                token.append(caracter);
                while (i + 1 < operacion.length() && !esOperador(operacion.charAt(i + 1))
                        && operacion.charAt(i + 1) != '(' && operacion.charAt(i + 1) != ')') {
                    i++;
                    token.append(operacion.charAt(i));
                }
                operandos.push(token.toString());
            }
        }

        // Después de procesar toda la expresión, desapilamos cualquier operador que
        // quede en la pila de operadores y formamos la expresión prefija.
        while (!operadores.isEmpty()) {
            String operando1 = operandos.pop();
            String operando2 = operandos.pop();
            char operator = operadores.pop();
            String newOperand = operator + " " + operando2 + " " + operando1;
            operandos.push(newOperand);
        }

        // La expresión prefija resultante se encuentra en la pila de operandos (debe
        // haber solo un elemento en la pila).
        if (!operandos.isEmpty()) {
            expresionPrefija.append(operandos.pop());
        }

        // Devolvemos la expresión prefija como una cadena.
        return expresionPrefija.toString();
    }

    // Verifica si el caracter es un operador.
    private static boolean esOperador(char caracter) {
        return "+-*/&|!<>=".indexOf(caracter) != -1;
    }

    // Obtiene la jerarquía del operador.
    private static int getJerarquia(char operador) {
        if (operador == '+' || operador == '-') {
            return 1;
        } else if (operador == '*' || operador == '/') {
            return 2;
        } else if (operador == '&' || operador == '|') {
            return 3;
        } else if (operador == '!') {
            return 4;
        }
        return 0;
    }
}
