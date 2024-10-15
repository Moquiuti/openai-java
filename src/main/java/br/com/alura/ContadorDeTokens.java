package br.com.alura;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.ModelType;

public class ContadorDeTokens {

    public static void main(String[] args) {
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
        var qtd = enc.countTokens("Identifique o perfil de compra de cada cliente");

        System.out.println("QTD de Tokens: " +qtd);
    }

}
