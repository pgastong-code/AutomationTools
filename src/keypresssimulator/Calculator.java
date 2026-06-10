package keypresssimulator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Stack;

public class Calculator {

	public static Double evaluarExpresion(String expresion) {
		String expresionPostfix = convertirAPostfix(expresion);
		return evaluarPostfix(expresionPostfix);
	}

	private static String convertirAPostfix(String expresion) {
		StringBuilder postfix = new StringBuilder();
		Stack<Character> operators = new Stack<>();

		for (int i = 0; i < expresion.length(); i++) {
			char c = expresion.charAt(i);
			if (Character.isDigit(c)) {
				StringBuilder number = new StringBuilder();
				while (i < expresion.length()
						&& (Character.isDigit(expresion.charAt(i)) || expresion.charAt(i) == '.')) {
					number.append(expresion.charAt(i));
					i++;
				}
				i--; // Retroceder el índice para considerar el último dígito
				postfix.append(number).append(" ");
			} else if (c == '(') {
				operators.push(c);
			} else if (c == ')') {
				while (!operators.isEmpty() && operators.peek() != '(') {
					postfix.append(operators.pop()).append(" ");
				}
				operators.pop(); // Sacar el '(' de la pila
			} else if (isOperator(c)) {
				while (!operators.isEmpty() && precedency(operators.peek()) >= precedency(c)) {
					postfix.append(operators.pop()).append(" ");
				}
				operators.push(c);
			}
		}

		while (!operators.isEmpty()) {
			postfix.append(operators.pop()).append(" ");
		}

		return postfix.toString();
	}

	private static boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}

	private static int precedency(char operador) {
		if (operador == '+' || operador == '-') {
			return 1;
		} else if (operador == '*' || operador == '/') {
			return 2;
		}
		return 0;
	}

	private static Double evaluarPostfix(String expresion) {
		String[] tokens = expresion.split(" ");
		Stack<Double> stack = new Stack<>();

		for (String token : tokens) {
			if (token.matches("[0-9]+(\\.[0-9]+)?")) {
				stack.push(Double.parseDouble(token));
			} else if (isOperator(token.charAt(0))) {
				Double operand2 = stack.pop();
				Double operand1 = stack.pop();
				Double result = applyOperation(operand1, operand2, token.charAt(0));
				stack.push(result);
			}
		}

		return stack.pop();
	}

	private static Double applyOperation(Double operand1, Double operand2, char operador) {
		switch (operador) {
		case '+':
			return operand1 + operand2;
		case '-':
			return operand1 - operand2;
		case '*':
			return operand1 * operand2;
		case '/':
			if (operand2 == 0) {
				throw new ArithmeticException("División por cero");
			}
			return operand1 / operand2;
		default:
			throw new IllegalArgumentException("Operador inválido");
		}
	}

	public static String calculate(String expression) {
		expression = expression.replaceAll("[$ A-Za-z]+","").replaceAll("\\.","").replaceAll(",", ".");
		Double result = evaluarExpresion(expression);
        DecimalFormatSymbols simbols = new DecimalFormatSymbols(Locale.getDefault());
        simbols.setDecimalSeparator(',');
        simbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat("#,##0.##");
        String formattedResult = format.format(result);
		return formattedResult;
	}
}
