package br.com.alura;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.List;

public class CategorizadorDeProdutos {
    public static void main(String[] args) {
        final var produto = "Escova de dentes";
        final var system = """
                    Você é um categorizador de produtos e deve responder apenas o nome da categoria do produto informado
                
                    Escolha uma categoria dentra a lista abaixo:
                
                    1. Higiene pessoal
                    2. Eletronicos
                    3. Esportes
                    4. Outros
                
                    ######Exemplo de uso:
                
                    Pergunta: Bola de futebol
                    Resposta: Esportes
                """;

        final var apiKey = System.getenv("token-api");

        //Aumentar o tempo de timeout
        var service = new OpenAiService(apiKey, Duration.ofSeconds(30));

        final var completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4")
                .messages(List.of(
                        new ChatMessage(ChatMessageRole.USER.value(), produto),
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), system)
                ))
                .n(5) // Número de respostas
                .build();

        service
                .createChatCompletion(completionRequest)
                .getChoices()
                .forEach(c -> {
                    System.out.print(c.getMessage().getContent());
                    System.out.print("------------------------" + System.lineSeparator());
                });
    }
}
