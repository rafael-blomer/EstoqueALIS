# EstoqueALIS - Sistema de Gestão de Estoque com Spring Boot

Este projeto é um **backend Java (Spring Boot)** para um sistema simples de **gestão de estoque com lotes, movimentações e notificações**.  

Ele foi desenvolvido como demonstração das minhas habilidades em **Java 21**, **arquitetura REST**, **segurança JWT** e **integrações externas (E-mail / Telegram)**.  
O foco principal é o **backend** — modelagem do domínio, regras de negócio, segurança, testes e integrações.

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3** (Web, Data JPA, Validation, Mail)
- **Spring Security + JWT (jjwt)**
- **BCrypt** para hashing de senhas
- **PostgreSQL**
- **Maven**
- **JUnit 5 + Mockito**
- **Spring Scheduling (`@Scheduled`)**
- **RestTemplate** para integrações externas (ex.: Telegram)

---

## ⚙️ Principais Funcionalidades

1. Cadastro e autenticação de usuários (login via **JWT**).  
2. Verificação de e-mail com token de validação e fluxo de recuperação de senha.  
3. **CRUD** de Produtos, Estoques e Lotes de Produto.  
4. Movimentações de estoque (entrada/saída), incluindo controle por lote.  
5. Ao realizar uma retirada, o sistema indica os **lotes mais próximos do vencimento**.  
6. Cálculo automático da **quantidade total por produto** (somando os lotes).  
7. Relatórios e verificações de **lotes próximos ao vencimento**.  
8. Notificações automáticas via **Telegram** (bot) sobre lotes prestes a vencer.  
9. Código documentado com **JavaDoc** e testes unitários cobrindo a camada de negócio.

---

## Integrações

- **E-mail:** envio configurado com `spring-boot-starter-mail`.  
- **Telegram:** notificações via API de bot (implementado em `NotificacaoService`).  

---

## O que aprendi desenvolvendo este projeto

1. Organizar regras de negócio em serviços e manter controllers enxutos.  
2. Modelar lotes de produtos e contabilizar quantidades por lote.  
3. Implementar autenticação **JWT** com **Spring Security**.  
4. Aplicar boas práticas com **DTOs** e conversores para isolar entidades da API.  
5. Escrever testes unitários com **JUnit + Mockito**, mockando repositórios.  
6. Integrar o sistema com serviços externos (**SMTP** e **Telegram**) com segurança.
