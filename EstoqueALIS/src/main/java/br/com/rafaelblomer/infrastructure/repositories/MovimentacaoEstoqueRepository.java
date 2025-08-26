package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
}
