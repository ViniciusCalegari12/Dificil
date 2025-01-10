import java.util.Scanner;
import java.util.Stack;

public class CalculadoraDeExpressoesAvancadas {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo à Calculadora de Expressões Avançadas!");
        System.out.println(
                "Digite uma expressão matemática (ex: {[2 + (5 + 4) : 3 – √4 + 9] : 4}²) ou 'sair' para encerrar:");

        while (true) {
            System.out.print("Expressão: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando a calculadora. Até mais!");
                break;
            }

            try {

                String normalizedExpression = normalizeExpression(input);

                double result = evaluateExpression(normalizedExpression);
                System.out.println("Resultado: " + result);
            } catch (Exception e) {
                System.out.println("Expressão inválida. Por favor, tente novamente.");
            }
        }

        scanner.close();
    }

    private static String normalizeExpression(String expression) {
        expression = expression.replaceAll("√", "sqrt"); // Substitui raiz quadrada
        expression = expression.replaceAll("²", "^2"); // Substitui potência
        expression = expression.replaceAll(":", "/"); // Substitui divisão
        expression = expression.replaceAll("[{}\\[\\]]", "()"); // Substitui chaves e colchetes por parênteses
        return expression;
    }

    private static double evaluateExpression(String expression) {
        return evaluatePostfix(convertToPostfix(expression));
    }

    private static String convertToPostfix(String expression) {
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();
        String operators = "+-*/^";

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                postfix.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(' ').append(stack.pop());
                }
                stack.pop();
            } else if (operators.indexOf(c) != -1) {
                postfix.append(' ');
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                    postfix.append(stack.pop()).append(' ');
                }
                stack.push(c);
            } else if (expression.startsWith("sqrt", i)) {
                stack.push('√');
                i += 3;
            } else {
                postfix.append(' ');
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(' ').append(stack.pop());
        }

        return postfix.toString();
    }

    private static double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split(" ");

        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("√")) {
                stack.push(Math.sqrt(stack.pop()));
            } else {
                double b = stack.pop();
                double a = stack.isEmpty() ? 0 : stack.pop();
                switch (token) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> stack.push(a / b);
                    case "^" -> stack.push(Math.pow(a, b));
                }
            }
        }

        return stack.pop();
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^', '√' -> 3;
            default -> -1;
        };
    }
}
