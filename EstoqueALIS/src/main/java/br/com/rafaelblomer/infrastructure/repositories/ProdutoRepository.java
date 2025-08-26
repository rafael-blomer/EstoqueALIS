package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
