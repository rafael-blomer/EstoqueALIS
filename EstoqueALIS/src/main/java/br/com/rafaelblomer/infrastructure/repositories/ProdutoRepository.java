package br.com.rafaelblomer.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rafaelblomer.infrastructure.entities.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByEstoqueUsuarioId(Long usuarioId);

    List<Produto> findByEstoqueId(Long estoqueId);
}
