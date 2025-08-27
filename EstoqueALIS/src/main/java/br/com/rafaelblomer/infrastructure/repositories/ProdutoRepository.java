package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByEstoqueUsuarioId(Long usuarioId);

    List<Produto> findByEstoqueId(Long estoqueId);
}
