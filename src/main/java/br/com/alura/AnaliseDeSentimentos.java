package br.com.alura;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;

public class AnaliseDeSentimentos {

    private static final Logger logger = LoggerFactory.getLogger(AnaliseDeSentimentos.class);

    public static void main(String[] args) throws InterruptedException {
        try {
            var promptSistema = """
                    Você é um analisador de sentimentos de avaliações de produtos.
                    Escreva um parágrafo com até 50 palavras resumindo as avaliações e depois atribua qual o sentimento geral para o produto.
                    Identifique também 3 pontos fortes e 3 pontos fracos identificados a partir das avaliações.
                    
                    #### Formato de saída
                    Nome do produto:
                    Resumo das avaliações: [resuma em até 50 palavras]
                    Sentimento geral: [deve ser: POSITIVO, NEUTRO ou NEGATIVO]
                    Pontos fortes: [3 bullets points]
                    Pontos fracos: [3 bullets points]
                    """;

            final var diretorioAvaliacoes = Path.of("src/main/resources/avaliacoes");
            final var arquivosDeAvaliacoes = Files.walk(diretorioAvaliacoes, 1)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(nome -> nome.replace("avaliacoes-", "").replace(".txt", ""))
                    .toArray(String[]::new);

            for (String arquivo : arquivosDeAvaliacoes) {
                logger.info("Analisando o arquivo: {}", arquivo);
                var promptUsuario = carregarArquivo(arquivo);

                var request = ChatCompletionRequest
                        .builder()
                        .model("gpt-4-1106-preview")
                        .messages(Arrays.asList(
                                new ChatMessage(
                                        ChatMessageRole.SYSTEM.value(),
                                        promptSistema),
                                new ChatMessage(
                                        ChatMessageRole.USER.value(),
                                        promptUsuario)))
                        .build();

                var chave = System.getenv("token-api");
                var service = new OpenAiService(chave, Duration.ofSeconds(60));

                var resposta = service
                        .createChatCompletion(request)
                        .getChoices().get(0).getMessage().getContent();

                salvarAnalise(arquivo, resposta);
                logger.info("Analise salva com sucesso!");
            }
        } catch (OpenAiHttpException ex) {
            var errorCode = ex.statusCode;
            switch (errorCode) {
                case 401 -> throw new RuntimeException("Erro com a chave da API!", ex);
                case 429 -> {
                    logger.warn("Limite de requisições atingido! Aguarde 1 minuto para continuar.");
                    Thread.sleep(1000 * 5);
                }
                case 500, 503 -> {
                    logger.warn("API fora do Ar! Aguarde 1 minuto para continuar.");
                    Thread.sleep(1000 * 5);
                }
                default -> logger.error("Erro desconhecido: {}", ex.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao analisar os sentimentos!", e);
        }
    }

    private static String carregarArquivo(final String arquivo) {
        try {
            var path = Path.of("src/main/resources/avaliacoes/avaliacoes-" + arquivo + ".txt");
            return String.join("\n", Files.readAllLines(path));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar o arquivo!", e);
        }
    }

    private static void salvarAnalise(final String arquivo, final String analise) {
        try {
            var path = Path.of("src/main/resources/analises/analise-sentimentos-" + arquivo + ".txt");
            Files.writeString(path, analise, StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o arquivo!", e);
        }
    }
}