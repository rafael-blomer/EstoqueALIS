package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByEstoqueId(Long estoqueId);

    @Query("SELECT m FROM MovimentacaoEstoque m " +
            "JOIN m.itensMovimentacao i " +
            "WHERE i.loteProduto.produto.id = :produtoId " +
            "AND m.estoque.id = :estoqueId")
    List<MovimentacaoEstoque> listarHistoricoMovimentacoesProduto(@Param("produtoId") Long produtoId,
                                                                  @Param("estoqueId") Long estoqueId);

    List<MovimentacaoEstoque> findByEstoqueIdAndDataHoraBetween(Long estoqueId,
                                                                LocalDateTime dataInicio,
                                                                LocalDateTime dataFinal);

    @Query("SELECT m FROM MovimentacaoEstoque m " +
            "JOIN m.itensMovimentacao i " +
            "WHERE i.loteProduto.produto.id = :produtoId " +
            "AND m.estoque.id = :estoqueId " +
            "AND m.dataHora BETWEEN :dataInicio AND :dataFinal")
    List<MovimentacaoEstoque> listarHistoricoMovimentacoesProdutoEData(@Param("produtoId") Long produtoId,
                                                                       @Param("estoqueId") Long estoqueId,
                                                                       @Param("dataInicio") LocalDateTime dataInicio,
                                                                       @Param("dataFinal") LocalDateTime dataFinal);

}
