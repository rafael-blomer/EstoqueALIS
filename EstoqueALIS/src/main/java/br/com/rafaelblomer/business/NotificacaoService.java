package br.com.rafaelblomer.business;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Serviço responsável por enviar notificações de avisos relacionados a lotes de produtos.
 * Atualmente, a notificação é feita via Telegram, utilizando a API oficial de bots.
 * - Envia mensagens sobre lotes próximos do vencimento.
 * - Formata as mensagens com MarkdownV2 para melhor visualização.
 * - Trata caracteres especiais para evitar falhas no parse do Telegram.
 */
@Service
public class NotificacaoService {

    // Token do bot configurado no application-secrets.properties
    @Value("${TOKEN_BOT_TELEGRAM}")
    private String TELEGRAM_BOT_TOKEN;

    // ID do chat que receberá as notificações(Em produção, mudar para que cada usuário tenha seu TELEGRAM_CHAT_ID)
    private final String TELEGRAM_CHAT_ID = "2028213081";

    /**
     * Formata uma mensagem para o Telegram sobre lotes que estão próximos de vencer.
     * @param lotes Lista de lotes a serem notificados
     * @param dias Quantidade de dias para o vencimento usada como critério do aviso
     * Se a lista estiver vazia ou nula, nada será enviado.
     */
    public void enviarAvisoLotes(List<LoteProduto> lotes, int dias) {
        if (lotes == null || lotes.isEmpty())
            return;
        StringBuilder mensagem = new StringBuilder("*⚠️ Aviso: Lotes vencendo em " + dias + " dias:*\n\n");
        lotes.forEach(lote -> {
            String nomeProdutoFormatado = escaparMarkdownV2(lote.getProduto().getNome());
            String loteRealFormatado = escaparMarkdownV2(lote.getLoteFabricante());
            String quantidadeFormatado = escaparMarkdownV2(lote.getQuantidadeLote().toString());
            String marcaProdutoFormatado = escaparMarkdownV2(lote.getProduto().getMarca());
            String estoqueProdutoFormatado = escaparMarkdownV2(lote.getProduto().getEstoque().getId().toString());
            String estoqueNomeFormatado = escaparMarkdownV2(lote.getProduto().getEstoque().getNomeEstoque());
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
                    .append("Nome Estoque: *")
                    .append(estoqueNomeFormatado)
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

    /**
     * Envia uma mensagem formatada para o Telegram utilizando a API oficial do bot.
     * @param texto Mensagem em formato MarkdownV2
     * Em caso de falha no envio (ex: token inválido, chat_id incorreto, rede indisponível),
     * a exceção é capturada e um erro é impresso no console.
     */
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

    /**
     * Escapa caracteres especiais do MarkdownV2 para evitar erros de formatação no Telegram.
     * @param texto Texto a ser tratado
     * @return Texto com caracteres especiais devidamente escapados
     */
    public String escaparMarkdownV2(String texto) {
        if (texto == null) return "";
        String[] specialChars = {"_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!"};
        for (String specialChar : specialChars) {
            texto = texto.replace(specialChar, "\\" + specialChar);
        }
        return texto;
    }
}
