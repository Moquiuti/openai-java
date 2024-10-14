package br.com.alura;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class CategorizadorDeProdutos {
    public static void main(String[] args) {
        final var produto = "Escova de dentes";
        final var system = "Você é um categorizador de produtos.";

        final var apiKey = System.getenv("token-api");
        final var service = new OpenAiService(apiKey);

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
