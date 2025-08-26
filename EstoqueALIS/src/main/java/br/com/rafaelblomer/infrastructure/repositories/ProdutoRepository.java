package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("""
        SELECT DISTINCT p
        FROM Produto p
        JOIN p.lotes l
        JOIN l.estoque e
        WHERE e.usuario.id = :usuarioId
    """)
    List<Produto> buscarProdutosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
        SELECT DISTINCT p
        FROM Produto p
        JOIN p.lotes l
        WHERE l.estoque.id = :estoqueId
    """)
    List<Produto> buscarProdutosPorEstoque(@Param("estoqueId") Long estoqueId);
}
