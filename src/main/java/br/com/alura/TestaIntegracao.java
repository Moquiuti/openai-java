package br.com.alura;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class TestaIntegracao {
    public static void main(String[] args) {
        final var user = "Gere 5 produtos";
        final var system = "Você é um gerador de produtos fictícios para um e-commerce e deve gerar apenas o nome doss produtos solicitados pelo usuário.";

        final var apiKey = System.getenv("token-api");
        final var service = new OpenAiService(apiKey);

        final var completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4")
                .messages(List.of(
                        new ChatMessage(ChatMessageRole.USER.value(), user),
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), system)
                ))
                .build();

        service.createChatCompletion(completionRequest)
                .getChoices()
                .forEach(System.out::println);
    }
}
