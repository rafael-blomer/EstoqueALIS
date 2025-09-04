package br.com.rafaelblomer.business;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class NotificacaoService {
    private final String TELEGRAM_BOT_TOKEN = "8265065180:AAFNFcul5WyAa3XDsh2Q8ImbHZOxrbuUgMQ";
    private final String TELEGRAM_CHAT_ID = "";

    public void enviarAvisoLotes(List<LoteProduto> lotes, int dias) {
        if (lotes == null || lotes.isEmpty()) {
            return;
        }

        StringBuilder mensagem = new StringBuilder("*⚠️ Aviso: Lotes vencendo em " + dias + " dias:*\n\n");

        lotes.forEach(lote -> {
            String nomeProdutoFormatado = escaparMarkdownV2(lote.getProduto().getNome());
            String loteRealFormatado = escaparMarkdownV2(lote.getLoteFabricante());
            String quantidadeFormatado = escaparMarkdownV2(lote.getQuantidadeLote().toString());
            String marcaProdutoFormatado = escaparMarkdownV2(lote.getProduto().getMarca());
            String estoqueProdutoFormatado = escaparMarkdownV2(lote.getProduto().getEstoque().getId().toString());
            String dataValidadeFormatada = escaparMarkdownV2(lote.getDataValidade().toString());

            mensagem.append("Produto: *")
                    .append(nomeProdutoFormatado)
                    .append("*\n")
                    .append("Marca: *")
                    .append(marcaProdutoFormatado)
                    .append("*\n")
                    .append("ID Estoque: *")
                    .append(estoqueProdutoFormatado)
                    .append("*\n")
                    .append("Quantidade: *")
                    .append(quantidadeFormatado)
                    .append("*\n")
                    .append("Lote do fabricante: *")
                    .append(loteRealFormatado)
                    .append("*\n")
                    .append("Validade: `")
                    .append(dataValidadeFormatada)
                    .append("`\n\n");
        });

        enviarTelegram(mensagem.toString());
    }

    private void enviarTelegram(String texto) {
        String baseUrl = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("chat_id", TELEGRAM_CHAT_ID)
                .queryParam("text", texto)
                .queryParam("parse_mode", "MarkdownV2")
                .build()
                .toUri();

        try {
            new RestTemplate().getForObject(uri, String.class);
        } catch (Exception e) {
            System.err.println("Falha ao enviar mensagem para o Telegram: " + e.getMessage());
        }
    }

    private String escaparMarkdownV2(String texto) {
        if (texto == null) return "";
        String[] specialChars = {"_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!"};
        for (String specialChar : specialChars) {
            texto = texto.replace(specialChar, "\\" + specialChar);
        }
        return texto;
    }
}
