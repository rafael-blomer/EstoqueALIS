package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificacaoTokenUsuarioRepository extends JpaRepository<VerificacaoTokenUsuario, Long> {

    VerificacaoTokenUsuario findByToken(String token);
}
