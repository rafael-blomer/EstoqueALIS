package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteProdutoRepository extends JpaRepository<LoteProduto, Long> {

    List<LoteProduto> findByProdutoId(Long produtoId);
}
