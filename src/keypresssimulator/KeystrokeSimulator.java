package keypresssimulator;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeystrokeSimulator {
	
	private static Robot robot;

	public static void main(String[] args) {
		try {
						//test
//			args = new String[2];
//			args[0] = "tot";
//			args[1] = "-15978,34	-10658,03	3831,67			1366,00	5000,00	-4536,00";

			robot = new Robot();
			
//			cliptboard:
//Banco Naranja X|Titular: Gabriel Meira de Lamanche Assis|Cuenta  |CBU: 4530000800017271360090|Alias: GMEIRADELAMAN.NX.ARS|CUIL: 20962047069	46200
//Banco Naranja X|Titular: Clayton Vitor Loiola de Oliveira|Cuenta  4092306831078722|CBU: 4530000800014710981698|Alias: CLOIOLADEOLIV.NX.ARS|CUIL: 4092306831078722	2052,53

			
			//pix x
			
//			String cuit = "20-29384 - 0";txtp2p
//			System.out.println(cuit.replaceAll(" ", "").replaceAll("\\-", ""));
			
			if (args[0].trim().equals("intra")) {
				String credentials = SheetsService.getCredentials(args[1].trim());
				if (credentials != null) typeString(credentials);
			} else if (args[0].trim().equals("intrabr")) {
				String credentials = SheetsService.getCredentialsBR(args[1].trim());
				if (credentials != null) typeString(credentials);
			} else if (args[0].trim().equals("cbu")) {
				//Preparo los datos de la cuenta
				prepareBankAccountData(args);
			} else if(args[0].equals("binance")) {
				fillBinanceForm(args);
			} else if(args[0].equals("mus")) {
				typeString(getCalculationText(null, Boolean.TRUE));
			} else if(args[0].equals("sum")) {
				typeString(getCalculationText(null, Boolean.FALSE));
			} else if(args[0].equals("col")) {
				typeString(columnize(args));
			} else if(args[0].equals("tot")) {
				typeString(totalize(args));
			} else if(args[0].equals("cal")) {
				typeString(calculate());
			} else if(args[0].equals("pix")) {
				prepareP2PTransferData(args);
			} else if(args[0].equals("post")) {
				sendMessageThroughWhatsappWeb(null, null);
			} else if(args[0].equals("spam")) {
				sendMessageThroughWhatsappWeb(null, args[1]);
			} else if(args[0].equals("inline")) {
				switchMode(true);
			} else if(args[0].equals("multiline")) {
				switchMode(false);
			} else if(args[0].equals("send")) {
				sendMultilineMessages(Boolean.FALSE);
			} else if(args[0].equals("fija")) {
				sendMultilineMessages(Boolean.TRUE);
			} else {
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				
				StringSelection str = new StringSelection(args[0].replaceAll("\\|", "\n"));
				clipboard.setContents(str, str);
				
				pasteClipboard();
				
				if(args.length > 1 && args[1].startsWith("pix")) {
					
					type(KeyEvent.VK_ENTER);//enter
					str = new StringSelection(args[1].substring(4));
					clipboard.setContents(str, str);

					pasteClipboard();
					
					type(KeyEvent.VK_ENTER);//enter
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pega el texto del clipboard, enviando enters cada vez que encuentra un pipe
	 */
	private static void sendMultilineMessages(Boolean isFromFile) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String data;

		if(isFromFile) {
			data = TxtFileReader.readFile("whatsappMultilineMessage.txt");
		} else {
			data = getClipboardData();
		}
		String [] messages = data.split("\\|");
		for (String message : messages) {
			StringSelection str = new StringSelection(message);
			clipboard.setContents(str, str);
			pasteClipboard();
			type(KeyEvent.VK_ENTER);
		}
		
	}
	
	private static void prepareBankAccountData(String[] args) {
		/* test */

		//String data = "Santander rio		|Melina bustos|cbu: 0720757288000005251186|cta $: 757-052511/8\"	100000,00";

		String data = getClipboardData();
		
		data = data.replaceAll("\"", "").replaceAll("[\r\n]+","\r\n").replaceAll("[ ]+"," "); 

		if(data.startsWith("\r\n")) {
			data = data.substring(2,data.length());
		}

				
		String value = null; 
		String account = null;
		
		if(!args[1].equals("x")) {
			value = formatValue(args[1]);
			sendPaymentMessage(data, value);
		} else {
			String dataArray[] = data.split("\\n");
			for (String line : dataArray) {
				// Obtengo los datos de la cuenta
				account = line.substring(0, line.lastIndexOf("\t"));
				
				// Obtengo el valor a transferir
				value = line.substring(line.lastIndexOf("\t")+1).replaceAll("\\.", "").replaceAll(",", ".");
			    String formattedValue = formatValue(value);
			    
			    formattedValue = formattedValue.replaceAll(",00","");
			    
			    //Si no tiene | entonces es un boleto
			    if(!account.contains("|")) {
			    	sendBillPaymentMessage(account.replaceAll("[\t]+", " "), formattedValue);
			    } else {
			    	sendPaymentMessage(account, formattedValue);
			    }
			    
			}
			
		}
	}
	
	private static void sendBillPaymentMessage(String account, String value) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		StringSelection newContent = new StringSelection("Factura de: *" + value.replace("%2C", ",") + "* pesos.\r\n\r\n*Para pagar por Mercado Pago*:\r\nCuentas y servicios -> Escanear una cuenta -> Ingresar codigo manualmente -> Continuar -> Pagar\r\n\r\n*Para pagar con UALA*:\r\nPagos -> Servicios -> Escanear Factura -> Ingresar código a mano -> Seguir");
		clipboard.setContents(newContent, newContent);
		
		pasteClipboard();
		type(KeyEvent.VK_ENTER);
		
		newContent = new StringSelection(account);
		clipboard.setContents(newContent, newContent);
		pasteClipboard();
		type(KeyEvent.VK_ENTER);
	}
	
	private static void sendPaymentMessage(String account, String value) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    StringSelection newContent = new StringSelection(account.replaceAll("\\|", "\n").replaceAll("\"", "").concat("\n*"+value.replace("%2C", ",") +" pesos en esta cuenta*\n\n- *No redondear*, enviar valor exacto. \n- Enviar captura del comprobante *(no PDF)*"));
	    clipboard.setContents(newContent, newContent);
		pasteClipboard();
		type(KeyEvent.VK_ENTER);
		
		String CBUOrAlias = parseCBUorAlias(account);
		newContent = new StringSelection(CBUOrAlias);
		clipboard.setContents(newContent, newContent);
		pasteClipboard();
		type(KeyEvent.VK_ENTER);	
	}
	
	private static void switchMode(Boolean inline) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String inlineAccount = new String();
		
		String origin = "";
		String replacement = "";
		
		if(inline) {
			origin = "\\n"; replacement = "|";
		} else {
			origin = "\\|"; replacement = "\n";
		}
		try {
			inlineAccount = ((String) clipboard.getData(DataFlavor.stringFlavor)).replaceAll(origin,replacement);
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		
		StringSelection inlineAccountSelection = new StringSelection(inlineAccount);
		clipboard.setContents(inlineAccountSelection, inlineAccountSelection);
		
		pasteClipboard();
	}
	
	private static void sendMessageThroughWhatsappWeb(String recipees, String message) { 
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	    // Si no tengo el mensaje, es porque hay que sacarlo del txt
	    if (message == null) {
	        message = TxtFileReader.readFile("spam.txt");
	    }

	    // Si no tengo los recepees, es porque hay que sacarlo del clipboard
	    if (recipees == null) {
	        try {
	            recipees = (String) clipboard.getData(DataFlavor.stringFlavor);
	        } catch (HeadlessException | UnsupportedFlavorException | IOException e) {
	            e.printStackTrace();
	        }
	    }

	    recipees = recipees.replaceAll("\t", "\n");
	    String[] recepeesArr = recipees.split("\n");

	    // Usar un LinkedHashSet para eliminar duplicados manteniendo el orden
	    Set<String> uniqueRecepees = new LinkedHashSet<>(Arrays.asList(recepeesArr));

	    for (String fullName : uniqueRecepees) {
	        // Extraer el primer nombre (primera palabra antes de un espacio)
	        String firstName = fullName.split(" ")[0]; // Tomamos la primera palabra como nombre

	        // Reemplazar %name% en el mensaje
	        String personalizedMessage = message.replace("%name%", firstName);

	        // CTRL+ALT+/ (para buscar en WhatsApp)
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_ALT);
	        robot.keyPress(KeyEvent.VK_SHIFT);
	        type(KeyEvent.VK_7);
	        robot.keyRelease(KeyEvent.VK_SHIFT);
	        robot.keyRelease(KeyEvent.VK_ALT);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        robot.delay(400);

	        StringSelection newContent = new StringSelection(fullName);
	        clipboard.setContents(newContent, newContent);

	        // Pegamos el nombre del grupo/persona
	        pasteClipboard();
	        robot.delay(200);
	        type(KeyEvent.VK_ENTER);
	        robot.delay(600);

	        // Copiamos el mensaje personalizado al portapapeles
	        StringSelection messageSelection = new StringSelection(personalizedMessage);
	        clipboard.setContents(messageSelection, messageSelection);

	        // Pegamos el mensaje y lo enviamos
	        pasteClipboard();
	        robot.delay(800);
	        type(KeyEvent.VK_ENTER);
	        robot.delay(200);
	    }
	}

	/**
	 * Gets the calculation text if
	 * If param "text" is null, it gets the string from clipboard
	 * @param the list of values to sumarize
	 * @param if true, returns the formula in google sheets
	 * @return
	 */
	private static String getCalculationText(String text, Boolean inverted) {
		String data = null;
		if(text == null) {
			data = getClipboardData();
		} else {
			data = text;
		}
//		String example = "		244180				131000		654800	319663	115368,00			237400												192377,51			600000,00	226556,00			285500,00												800000,00	800000,00	280000,00	150000,00			";
//		String example = "=43000+34200+18000+17500+14000	=62105+60000+27000				=53820+90000+64752+67044+54158";
		String textCalculation = getAdditionText(getPlusSeparatedValues(splitValues(data)), inverted);
		
		return textCalculation;
	}
	
	private static void typeString(String text) {
		StringSelection sumStringSel = new StringSelection(text);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(sumStringSel, sumStringSel);
		pasteClipboard();
	}
	
	
	/**
	 * 
	 * @param the list of values to totalize
	 */
	private static String totalize(String [] args) {
		String data = getClipboardData();

//		String example = "		244180				131000		654800	319663	115368,00			237400												192377,51			600000,00	226556,00			285500,00												800000,00	800000,00	280000,00	150000,00			";
//		String example = "=43000+34200+18000+17500+14000	=62105+60000+27000				=53820+90000+64752+67044+54158";
		String sumString = KeystrokeSimulator.getCalculationText(data, Boolean.FALSE);
		
		return formatearNumeros(sumString.replaceAll("=", "\r\n--------------\r\n").replaceAll("\\+", "\r\n"));
	}
	
	public static String formatearNumeros(String texto) {
	    String[] lineas = texto.split("\\n");
	    StringBuilder resultado = new StringBuilder();
	    
	    for (String linea : lineas) {
	        String lineaTrim = linea.trim();
	        
	        // Si la línea es vacía o son guiones, la dejamos igual
	        if (lineaTrim.isEmpty() || lineaTrim.matches("-+")) {
	            resultado.append(linea).append("\n");
	            continue;
	        }
	        
	        try {
	            // Limpiar el número: eliminar puntos (separadores de miles) y reemplazar coma por punto
	            String numeroLimpio = lineaTrim.replace(".", "").replace(",", ".");
	            
	            double numero = Double.parseDouble(numeroLimpio);
	            
	            // Separar parte entera y decimal
	            long parteEntera = (long) numero;
	            int parteDecimal = (int) Math.round((numero - parteEntera) * 100);
	            
	            // Formatear parte entera con separador de miles
	            String enteraFormateada = String.format("%,d", parteEntera).replace(",", ".");
	            
	            // Determinar si tiene decimales
	            if (parteDecimal > 0) {
	                // Tiene decimales, mostrar con 2 dígitos
	                resultado.append(enteraFormateada).append(",").append(String.format("%02d", parteDecimal)).append("\n");
	            } else {
	                // No tiene decimales
	                resultado.append(enteraFormateada).append("\n");
	            }
	            
	        } catch (NumberFormatException e) {
	            // Si no es número, lo dejamos igual
	            resultado.append(linea).append("\n");
	        }
	    }
	    
	    return resultado.toString().trim();
	}
	
	private static String columnize(String [] args) {
		String data = getClipboardData();

//		String example = "		244180				131000		654800	319663	115368,00			237400												192377,51			600000,00	226556,00			285500,00												800000,00	800000,00	280000,00	150000,00			";
//		String example = "=43000+34200+18000+17500+14000	=62105+60000+27000				=53820+90000+64752+67044+54158";
		String colString = getPlusSeparatedValues(splitValues(data)).replaceAll("\\+","\r\n").replaceAll("\\.", ",").replaceAll("\\,00", "");
		
		return colString;
	}
	
	private static String calculate() {
		String expression = getClipboardData();
		return Calculator.calculate(expression);
	}
	
	/**
	 * Agarra el texto sucio, y guarda los numeros en una Lista de Strings
	 * @param unorganizedSum
	 * @return
	 */
	private static List<String> splitValues(String unorganizedSum) {
        List<String> arrValues = new ArrayList<String>();
        
        unorganizedSum = unorganizedSum.replaceAll("\\.","");
        Pattern pattern = Pattern.compile("\\d+(\\,\\d+)?");
        Matcher matcher = pattern.matcher(unorganizedSum);
        
        while (matcher.find()) {
            String match = matcher.group();
            arrValues.add(match.replaceAll("\\.",","));
        }
        
        return arrValues;
	}
	
	private static String getAdditionText(String expression, Boolean inverted) {
		if(!inverted) {
			return expression+"="+Calculator.calculate(expression);
		} else {
			return "="+expression;
		}
	}
	
	private static String getPlusSeparatedValues(List<String> arrValues) {
		StringBuilder plusSeparatedValues = new StringBuilder();
		
		for (String value : arrValues) {
			plusSeparatedValues.append(value).append("+");
		}
        
		plusSeparatedValues.delete(plusSeparatedValues.length()-1, plusSeparatedValues.length());
		
        return plusSeparatedValues.toString();
	}
	
	
	/**
	 * Recibe en tres lineas (titular, pix, total). Y devuelve los datos formateados para que las haga otra persona
	 * @param args
	 */
	private static void prepareP2PTransferData(String[] args) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String data = getClipboardData();
		
//		Vinicius Alves dos Santos	Fernanda venancio	Rayane Micaelle Reis Costa
//		144.064.006.80	calcadosrofeis@hotmail.com	rayanemicaelle8@gmail.com
//		2.036,70	3.660,00	4.500,00
		
		/**
		 * Estrategia:
		 * Primero reemplazo los \t por \n
		 * Voy a tener (en este ejemplo), los datos de los 3, en 9 lineas.
		 */
		
		String accountFields[] = data.replaceAll("\t","\n").split("\n");
	
		int accounts = accountFields.length/3;
		
		for (int i = 0; i < accounts; i++) {
			String name = accountFields[i];
			String pix = accountFields[i+accounts];
			String cleanedPix = pix.substring(accountFields[i+accounts].indexOf(":")+1,accountFields[i+accounts].length()).trim();
			String value = formatValue(accountFields[i+accounts*2].replaceAll("\\.", "").replaceAll(",", "."));
			
			String accountData = name + "\r\n" + pix + "\r\n\r\n*" + value + " reais nesse pix*"
					+ "\r\n\r\n *- Por favor, enviar comprovante em imagem*";

			StringSelection accountDataSel = new StringSelection(accountData);
			StringSelection pixSel = new StringSelection(cleanedPix);
			
			clipboard.setContents(accountDataSel, accountDataSel);
			pasteClipboard();
			type(KeyEvent.VK_ENTER);

			clipboard.setContents(pixSel, pixSel);
			pasteClipboard();
			type(KeyEvent.VK_ENTER);
						
		}
		
	}
	
	private static void fillBinanceForm(String[] args) {
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String data = getClipboardData();
		
		//Sample string: Argen|Victoria Christie Reis Matos Dos Santos |801-352559/7|0720801888000035255972|vickchristie|95589835|23955898354|victoria@gmail.com
		String accountFields[] = data.toString().split("\\|");
		
		//Limpio el CUIT
		accountFields[6] = accountFields[6].replaceAll(" ", "").replaceAll("\\-", "");
		
		if(accountFields[0].toLowerCase().trim().equals("argen")) {
			StringSelection newContent = new StringSelection(accountFields[2].replaceAll(" ", "").replaceAll("-", "")); //Nro de cuenta
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[3]); //CBU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);

			newContent = new StringSelection(accountFields[6].replaceAll(" ", "").replaceAll("-", "")); //CUIT
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection("Titular: "+accountFields[1]); //Titular
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);

			newContent = new StringSelection(accountFields[5]); //DNI
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			type(KeyEvent.VK_TAB);type(KeyEvent.VK_SPACE); //Confirmar
		}
		
		if(accountFields[0].toLowerCase().trim().equals("brubank")) {
			StringSelection newContent = new StringSelection(accountFields[6].replaceAll(" ", "").replaceAll("-", "")); //CUIT
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[3]); //CBU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[2].replaceAll(" ", "").replaceAll("-", "")); //Nro de cuenta
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			type(KeyEvent.VK_TAB);type(KeyEvent.VK_SPACE); //Confirmar
		}
		
		if(accountFields[0].toLowerCase().trim().equals("mercadopago")) {
			StringSelection newContent = new StringSelection(accountFields[7]); //E-mail
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[5]); //DNI
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection("TITULAR: " + accountFields[1]); //Nombre y CVU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[3]); //Nombre y CVU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			type(KeyEvent.VK_TAB);type(KeyEvent.VK_SPACE); //Confirmar
		}
		
		if(accountFields[0].toLowerCase().trim().equals("reba")) {
			StringSelection newContent = new StringSelection(accountFields[3]); //CVU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			type(KeyEvent.VK_TAB);type(KeyEvent.VK_SPACE); //Confirmar
		}
		
		if(accountFields[0].toLowerCase().trim().equals("uala")) {
			StringSelection newContent = new StringSelection(accountFields[3]); //CBU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[6].replaceAll(" ", "").replaceAll("-", "")); //CUIT
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			type(KeyEvent.VK_TAB);type(KeyEvent.VK_SPACE); //Confirmar
		}
		
		if(accountFields[0].toLowerCase().trim().equals("prex")) {
			
			StringSelection newContent = new StringSelection("Titular: "+accountFields[1]); //Titular
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection(accountFields[2].replaceAll(" ", "").replaceAll("-", "")); //Nro de cuenta
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
			
			newContent = new StringSelection("CVU: "+ accountFields[3]); //CBU
			clipboard.setContents(newContent, newContent);
			pasteClipboard();
			type(KeyEvent.VK_TAB);
						
			type(KeyEvent.VK_TAB);type(KeyEvent.VK_SPACE); //Confirmar
		}
		
	}

	private static void type(int key) {
		robot.keyPress(key);
		robot.keyRelease(key);
		robot.delay(150);
	}

	private static void pasteClipboard() {
		robot.delay(700);
		robot.keyPress(KeyEvent.VK_CONTROL);
		type(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}
	
	private static String formatValue(String value) {
	    NumberFormat format = NumberFormat.getInstance();
	    format.setGroupingUsed(true);
	    format.setMinimumFractionDigits(2);
	    format.setMaximumFractionDigits(2);
	    return format.format(Double.parseDouble(value));
	}
	
	//ICBC|Titular: Walter David Lopez|Cuenta: 0520/01123408/77|CBU: |Alias: OSO.CONEJO.HUEVO|CUIL: 20-25369532-4
	private static String parseCBUorAlias(String data) {
		data = data.toLowerCase();
		String CBUorAlias = new String();
		
		Pattern myPattern = Pattern.compile("(?<!\\d)\\d{22}(?!\\d)");
		Matcher m = myPattern.matcher(data);
		while (m.find()) {
			CBUorAlias = m.group(0);
		}
		
		
		
		if(CBUorAlias.length() < 1) {
			myPattern = Pattern.compile("alias[\\: ]+([a-z0-9.-]+)");
			m = myPattern.matcher(data);
			while (m.find()) {
				CBUorAlias = m.group(1);
			}
		}
		System.out.println(CBUorAlias);
		return CBUorAlias;

	}

	private static String getClipboardData() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String data = new String();
		
		try {
			data = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		return data;
	}

}
