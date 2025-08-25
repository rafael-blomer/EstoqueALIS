package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioConverter {

    @Autowired
    private EstoqueConverter estoqueConverter;

    public Usuario dtoCadastroParaEntity(UsuarioCadastroDTO entityCadastro) {
        return new Usuario(
                entityCadastro.nome(),
                entityCadastro.email(),
                entityCadastro.cnpj(),
                entityCadastro.telefone(),
                entityCadastro.senha(),
                new ArrayList<>());
    }

    public UsuarioResponseDTO entityParaResponseDTO(Usuario entity) {
        List<EstoqueResponseDTO> list = entity.getEstoques()
                .stream()
                .map(e -> estoqueConverter.entityParaResponseDTO(e))
                .toList();

        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getCnpj(),
                list);
    }
}
