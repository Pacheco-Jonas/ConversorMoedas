import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ConversorMoedas {

    private static final String URL_API = "https://v6.exchangerate-api.com/v6/09b5a69c8ecd005b5d10bcce/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Conversor de Moedas ===");
        System.out.print("Digite a moeda de origem (ex: USD): ");
        String moedaOrigem = scanner.nextLine().toUpperCase();

        System.out.print("Digite a moeda de destino (ex: BRL): ");
        String moedaDestino = scanner.nextLine().toUpperCase();

        System.out.print("Digite o valor a ser convertido: ");
        double valor = scanner.nextDouble();

        try {
            double taxa = obterTaxaCambio(moedaOrigem, moedaDestino);
            double valorConvertido = valor * taxa;

            System.out.printf("%f %s equivale a %f %s\n", valor, moedaOrigem, valorConvertido, moedaDestino);
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao converter as moedas: " + e.getMessage());
        }

        scanner.close();
    }

    private static double obterTaxaCambio(String moedaOrigem, String moedaDestino) throws Exception {
        String url = URL_API + moedaOrigem;

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

        if (resposta.statusCode() != 200) {
            throw new Exception("Erro ao acessar a API. Código de status: " + resposta.statusCode());
        }

        // Analisa a resposta JSON
        String corpoResposta = resposta.body();
        int indiceInicio = corpoResposta.indexOf(moedaDestino);
        if (indiceInicio == -1) {
            throw new Exception("Moeda de destino não encontrada.");
        }

        String taxaString = corpoResposta.substring(indiceInicio + 5, corpoResposta.indexOf(",", indiceInicio));
        return Double.parseDouble(taxaString);
    }
}
