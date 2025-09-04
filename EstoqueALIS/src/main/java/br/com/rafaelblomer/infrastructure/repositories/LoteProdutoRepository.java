package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteProdutoRepository extends JpaRepository<LoteProduto, Long> {

    List<LoteProduto> findByProdutoId(Long produtoId);

    @Query("SELECT l FROM LoteProduto l " +
            "WHERE l.produto.id = :produtoId AND l.quantidadeLote > 0 " +
            "ORDER BY l.dataValidade ASC")
    List<LoteProduto> findLotesDisponiveisOrdenadosPorValidade(@Param("produtoId") Long produtoId);

    @Query("SELECT l FROM LoteProduto l WHERE l.dataValidade = :dataAlvo")
    List<LoteProduto> findLotesQueVencemEm(@Param("dataAlvo") LocalDate dataAlvo);

}


