package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteProdutoRepository extends JpaRepository<LoteProduto, Long> {
}
