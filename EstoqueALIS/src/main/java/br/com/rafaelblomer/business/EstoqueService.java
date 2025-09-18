package br.com.rafaelblomer.business;

import java.util.List;

import br.com.rafaelblomer.business.dtos.EstoqueCadastroDTO;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.EstoqueConverter;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.EstoqueRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository repository;

    @Autowired
    private EstoqueConverter converter;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Cria um novo estoque e o associa ao usuário identificado pelo token da sessão.
     * @param token Token JWT que identifica o usuário logado
     * @param cadastro DTO contendo os dados necessários para cadastro do estoque (nome do estoque)
     * @return DTO de resposta representando o estoque criado e persistido na base
     */
    @Transactional
    public EstoqueResponseDTO criarNovoEstoque(String token, EstoqueCadastroDTO cadastro) {
        Estoque estoque = new Estoque(cadastro.nomeEstoque(), usuarioService.findByToken(token));
        repository.save(estoque);
        return converter.entityParaResponseDTO(estoque);
    }

    /**
     * Desativa um estoque existente, marcando-o como inativo.
     * A operação só pode ser realizada pelo usuário proprietário do estoque.
     *
     * @param token Token JWT que identifica o usuário logado
     * @param id ID do estoque a ser desativado
     * @throws ObjetoNaoEncontradoException se o estoque não for encontrado
     * @throws AcaoNaoPermitidaException se o estoque não pertencer ao usuário
     */
    @Transactional
    public void desativarEstoque(String token, Long id) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = buscarEstoqueEntityId(id);
        verificarEstoqueUsuario(estoque, usuario);
        estoque.setAtivo(false);
        repository.save(estoque);
    }

    //ÚTEIS

    /**
     * Busca um estoque pelo ID.
     *
     * @param id ID do estoque
     * @return Estoque encontrado
     * @throws ObjetoNaoEncontradoException se nenhum estoque for encontrado
     */
    public Estoque buscarEstoqueEntityId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Estoque não foi encontrado."));
    }

    /**
     * Verifica se o estoque está ativo.
     *
     * @param estoque Estoque a ser verificado
     * @throws ObjetoInativoException se o estoque estiver inativo
     */
    public void verificarEstoqueAtivo(Estoque estoque) {
        if (!estoque.getAtivo())
            throw new ObjetoInativoException("Estoque não está ativo");
    }

    /**
     * Verifica se o estoque pertence ao usuário informado.
     *
     * @param estoque Estoque a ser verificado
     * @param usuario Usuário que está tentando realizar a ação
     * @throws AcaoNaoPermitidaException se o estoque não pertencer ao usuário
     */
    public void verificarEstoqueUsuario(Estoque estoque, Usuario usuario) {
        if(!estoque.getUsuario().equals(usuario))
            throw new AcaoNaoPermitidaException("O usuário não tem permissão para fazer essa ação.");
    }

    /**
     * Verifica se o produto pertence ao estoque informado.
     *
     * @param estoque Estoque esperado
     * @param produto Produto a ser verificado
     * @throws AcaoNaoPermitidaException se o produto não fizer parte do estoque
     */
    public void verificarEstoqueProduto(Estoque estoque, Produto produto) {
        if (!produto.getEstoque().equals(estoque))
            throw new AcaoNaoPermitidaException("O produto não faz parte desse estoque.");
    }
}
