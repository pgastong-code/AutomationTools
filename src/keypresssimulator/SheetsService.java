package keypresssimulator;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SheetsService {

    private static final String SPREADSHEET_ID = "1TLk87QGR8T_IYI4YnRNojKOtxT71vVVFleXgDHJfgqc";
    private static final String SHEET_NAME = "ar_customers";
    private static final String SHEET_NAME_BR = "customers";
    private static final String CREDENTIALS_FILE = "credentials.json";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String SCOPE = "https://www.googleapis.com/auth/spreadsheets";

    /**
     * Busca el valor en columna B de ar_customers y devuelve las credenciales formateadas.
     * Columna B = usuario (teléfono), columna G = contraseña temporal.
     */
    public static String getCredentials(String searchValue) throws Exception {
        String credJson = new String(Files.readAllBytes(Paths.get(CREDENTIALS_FILE)), StandardCharsets.UTF_8);
        String clientEmail = extractJsonString(credJson, "client_email");
        String privateKeyPem = extractJsonString(credJson, "private_key");

        String accessToken = getAccessToken(clientEmail, privateKeyPem);

        String range = SHEET_NAME + "!B:G";
        String url = "https://sheets.googleapis.com/v4/spreadsheets/" + SPREADSHEET_ID
                + "/values/" + range.replace(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            System.err.println("Error HTTP " + response.statusCode() + ": " + response.body());
            throw new IOException("Error al consultar Sheets: HTTP " + response.statusCode());
        }

        List<List<String>> rows = parseSheetValues(response.body());
        System.out.println("Filas encontradas: " + rows.size());

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.isEmpty()) continue;
            String columnB = row.get(0).trim();
            if (columnB.equals(searchValue.trim())) {
                String password = row.size() > 5 ? row.get(5).trim().toUpperCase() : "";
                if (password.matches("\\d{6}")) {
                    System.out.println("Contraseña de 6 dígitos detectada, usuario ya creado.");
                    return "ya tiene usuario creado";
                }
                String result = "https://bac-financial.com/\n"
                        + "Tu usuario: " + columnB + "\n"
                        + "Tu contraseña temporal:  " + password + " (Mayúscula)";
                System.out.println("Resultado:\n" + result);
                return result;
            }
        }

        System.err.println("No se encontró: " + searchValue);
        return null;
    }

    /**
     * Busca el valor ingresado como sufijo de la columna "id" de customers (BAC_DB) y
     * devuelve las credenciales formateadas. El usuario es el teléfono (últimos dígitos
     * del id) y la contraseña se lee de la columna "password".
     */
    public static String getCredentialsBR(String searchValue) throws Exception {
        String credJson = new String(Files.readAllBytes(Paths.get(CREDENTIALS_FILE)), StandardCharsets.UTF_8);
        String clientEmail = extractJsonString(credJson, "client_email");
        String privateKeyPem = extractJsonString(credJson, "private_key");

        String accessToken = getAccessToken(clientEmail, privateKeyPem);

        String url = "https://sheets.googleapis.com/v4/spreadsheets/" + SPREADSHEET_ID
                + "/values/" + SHEET_NAME_BR.replace(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            System.err.println("Error HTTP " + response.statusCode() + ": " + response.body());
            throw new IOException("Error al consultar Sheets: HTTP " + response.statusCode());
        }

        List<List<String>> rows = parseSheetValues(response.body());
        if (rows.isEmpty()) {
            System.err.println("La hoja " + SHEET_NAME_BR + " está vacía.");
            return null;
        }

        List<String> header = rows.get(0);
        int idCol = indexOfHeader(header, "id");
        int passwordCol = indexOfHeader(header, "password");
        if (idCol == -1 || passwordCol == -1) {
            throw new IOException("No se encontraron las columnas 'id' y/o 'password' en " + SHEET_NAME_BR);
        }

        String search = searchValue.trim();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() <= idCol) continue;
            String idValue = row.get(idCol).trim();
            if (idValue.endsWith(search)) {
                String password = row.size() > passwordCol ? row.get(passwordCol).trim().toUpperCase() : "";
                if (password.matches("\\d{6}")) {
                    System.out.println("Contraseña de 6 dígitos detectada, usuario ya creado.");
                    return "ya tiene usuario creado";
                }
                String phone = extractTrailingDigits(idValue);
                String result = "https://bac-financial.com/br/\n"
                        + "Seu usuario: " + phone + "\n"
                        + "Sua senha temporal: " + password + " (Maiúscula)";
                System.out.println("Resultado:\n" + result);
                return result;
            }
        }

        System.err.println("No se encontró: " + searchValue);
        return null;
    }

    private static int indexOfHeader(List<String> header, String name) {
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    private static String extractTrailingDigits(String value) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)$").matcher(value);
        return matcher.find() ? matcher.group(1) : value;
    }

    private static String getAccessToken(String clientEmail, String privateKeyPem) throws Exception {
        String jwt = buildJwt(clientEmail, loadPrivateKey(privateKeyPem));

        String body = "grant_type=" + URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", StandardCharsets.UTF_8)
                + "&assertion=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error al obtener token: " + response.body());
        }

        return extractJsonString(response.body(), "access_token");
    }

    private static String buildJwt(String clientEmail, PrivateKey privateKey) throws Exception {
        String header = base64url("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");

        long now = System.currentTimeMillis() / 1000;
        String payloadJson = "{\"iss\":\"" + clientEmail + "\","
                + "\"scope\":\"" + SCOPE + "\","
                + "\"aud\":\"" + TOKEN_URL + "\","
                + "\"exp\":" + (now + 3600) + ","
                + "\"iat\":" + now + "}";
        String payload = base64url(payloadJson);

        String signingInput = header + "." + payload;

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signingInput.getBytes(StandardCharsets.US_ASCII));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(sig.sign());

        return signingInput + "." + signature;
    }

    private static String base64url(String input) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private static PrivateKey loadPrivateKey(String pem) throws Exception {
        String cleaned = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\n", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    // Extrae el valor de un campo string en JSON (simple, para estructura conocida)
    private static String extractJsonString(String json, String key) {
        int keyIdx = json.indexOf("\"" + key + "\"");
        if (keyIdx == -1) return null;
        int quoteStart = json.indexOf('"', json.indexOf(':', keyIdx)) + 1;

        StringBuilder value = new StringBuilder();
        int pos = quoteStart;
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == '\\' && pos + 1 < json.length()) {
                char next = json.charAt(pos + 1);
                switch (next) {
                    case '"':  value.append('"');  break;
                    case 'n':  value.append('\n'); break;
                    case '\\': value.append('\\'); break;
                    default:   value.append(next);
                }
                pos += 2;
                continue;
            }
            if (c == '"') break;
            value.append(c);
            pos++;
        }
        return value.toString();
    }

    // --- Parser de JSON mínimo para la respuesta de Sheets API ---

    private static List<List<String>> parseSheetValues(String json) {
        List<List<String>> result = new ArrayList<>();

        int valuesIdx = json.indexOf("\"values\":");
        if (valuesIdx == -1) return result;

        int outerArrayStart = json.indexOf('[', valuesIdx) + 1;
        int pos = outerArrayStart;

        while (pos < json.length()) {
            pos = skipWhitespace(json, pos);
            if (pos >= json.length()) break;

            char c = json.charAt(pos);
            if (c == '[') {
                result.add(parseStringArray(json, pos));
                pos = findClosingBracket(json, pos) + 1;
            } else if (c == ']') {
                break;
            } else {
                pos++;
            }
        }

        return result;
    }

    private static List<String> parseStringArray(String json, int start) {
        List<String> values = new ArrayList<>();
        int pos = start + 1;

        while (pos < json.length()) {
            pos = skipWhitespace(json, pos);
            if (pos >= json.length()) break;

            char c = json.charAt(pos);
            if (c == '"') {
                pos++;
                StringBuilder val = new StringBuilder();
                while (pos < json.length()) {
                    char ch = json.charAt(pos);
                    if (ch == '\\' && pos + 1 < json.length()) {
                        pos++;
                        switch (json.charAt(pos)) {
                            case 'n':  val.append('\n'); break;
                            case 't':  val.append('\t'); break;
                            case '"':  val.append('"');  break;
                            case '\\': val.append('\\'); break;
                            default:   val.append(json.charAt(pos));
                        }
                    } else if (ch == '"') {
                        break;
                    } else {
                        val.append(ch);
                    }
                    pos++;
                }
                pos++;
                values.add(val.toString());
            } else if (c == ']') {
                break;
            } else {
                pos++;
            }
        }

        return values;
    }

    private static int skipWhitespace(String json, int pos) {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        return pos;
    }

    private static int findClosingBracket(String json, int start) {
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            if (json.charAt(i) == '[') depth++;
            else if (json.charAt(i) == ']' && --depth == 0) return i;
        }
        return json.length() - 1;
    }
}
