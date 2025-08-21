package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UsuarioConverter {
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
        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getCnpj(),
                entity.getEstoques());
    }
}
