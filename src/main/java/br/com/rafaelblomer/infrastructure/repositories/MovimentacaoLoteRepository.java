package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.model.MovimentacaoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoLoteRepository extends JpaRepository<MovimentacaoLote, Long> {
}
