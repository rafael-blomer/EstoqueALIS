# EstoqueALIS

**Backend Java (Spring Boot) — Sistema simples de gestão de estoque com lotes, movimentações e notificações**

Este repositório contém a API backend de um projeto de controle de estoque (EstoqueALIS) que eu desenvolvi como demonstração das minhas habilidades em Java e arquiteturas REST com Spring Boot. O foco aqui é o backend: modelagem do domínio, regras de negócio, segurança, testes e integrações (e-mail / Telegram).

---

## Tecnologias principais

* Java 21
* Spring Boot 3 (starter: web, data-jpa, validation, mail)
* Spring Data JPA (repositórios + entities)
* Spring Security + JWT (jjwt)
* BCrypt para hashing de senhas
* PostgreSQL (driver) — usado como banco de dados
* Maven
* JUnit 5 + Mockito (testes unitários)
* Agendamentos com `@Scheduled` (spring scheduling)
* `RestTemplate` para integrações externas (ex.: Telegram)

---

## Visão rápida do que o backend faz

* Cadastro e autenticação de usuários (login via JWT)
* Verificação de e-mail (token de verificação) e fluxo de recuperação de senha
* CRUD de Produtos, Estoques e Lotes de Produto
* Movimentações de estoque (entrada/saída), incluindo controle por lote
* Quando houver uma retirada, O sistema mostra os lotes mais próximos do vencimento que devem ser retirados
* Cálculo de quantidade total por produto (somando lotes)
* Relatórios / verificações de lotes próximos ao vencimento
* Notificações via Telegram (bot) sobre lotes próximos ao vencimento
* Serviços documentados com JavaDoc e testes unitários cobrindo a camada de negócio

---

## Organização do código

Pacotes principais:

* `br.com.rafaelblomer.controllers` — controllers REST (endpoints)
* `br.com.rafaelblomer.business` — services, regras de negócio, DTOs e conversores
* `br.com.rafaelblomer.infrastructure.entities` — entidades JPA (Produto, LoteProduto, Estoque, MovimentacaoEstoque, Usuario, etc.)
* `br.com.rafaelblomer.infrastructure.repositories` — interfaces Spring Data JPA
* `br.com.rafaelblomer.infrastructure.security` — configuração de segurança, JWT, filtros e UserDetails
* `br.com.rafaelblomer.infrastructure.scheduling` — jobs agendados (ex.: verificação de lotes)
* `src/test/java` — testes unitários por serviço (JUnit5 + Mockito)

---

## Endpoints principais (resumo)

* `POST /usuario/cadastro` — criar usuário (envia e-mail de verificação)
* `POST /usuario/login` — autenticar e receber token JWT
* `GET /usuario` — obter usuário a partir do token (endpoint protegido)
* `POST /produto` — cadastrar produto
* `GET /produto` — listar produtos
* `GET /produto/{id}` — buscar produto por id
* `POST /loteproduto` — cadastrar lote de produto (com validade e quantidade)
* `POST /estoques` — cadastrar estoque
* `POST /movimentacoes/saida` — registrar saída (usa `MovimentacaoSaidaDTO`)
* `GET /movimentacoes/estoque` — listar movimentações por estoque

(Existem endpoints adicionais para buscas filtradas por datas/produto/estoque e operações de usuário como `esquecisenha` / `alterarsenha`.)

---

## Segurança

* Autenticação por JWT (biblioteca `jjwt`) — `JwtUtil` gera/valida tokens
* `SecurityConfig` configura `SecurityFilterChain` e um filtro JWT que interceta requisições
* Senhas armazenadas usando `BCryptPasswordEncoder`
* Fluxo de verificação por e-mail com token persistido (`VerificacaoTokenUsuario`) para validação de contas

---

## Integrações

* E-mail: `spring-boot-starter-mail` (configurado via propriedades)
* Telegram: envio de notificações via API de bots (implementado em `NotificacaoService`)

---

## Testes

* Testes unitários com JUnit 5 + Mockito cobrindo a camada de negócio (services). Exemplos de classes de teste presentes:

  * `UsuarioServiceTest`
  * `ProdutoServiceTest`
  * `LoteProdutoServiceTest`
  * `EstoqueServiceTest`
  * `MovimentacaoEstoqueServiceTest`
  * `RelatorioServiceTest`
  * `EmailServiceTest`
  * `NotificacaoServiceTest`

Os testes usam mocks dos repositórios e verificam comportamentos (por exemplo: salvar entidades, lançar exceções esperadas, envio de notificações, etc.).

---

## Documentação do código

* As classes de serviço (`business/*.java`) possuem JavaDoc e comentários descrevendo responsabilidades e parâmetros principais.

---

## Frontend do sistema

Além da API backend, este projeto conta com um frontend simples em **HTML, CSS e JavaScript** que consome a API.

## Boas práticas e observações de segurança

* NÃO deixar credenciais (tokens, senhas) em arquivos versionados. Mover `application-secrets.properties` para um local seguro ou usar variáveis de ambiente / CI secrets.
* A chave JWT usada hoje é definida em `JwtUtil` como Base64 codificada — em produção, armazenar em um gerenciador de segredos / variável de ambiente com rotação adequada.
* Em ambiente de produção, ajustar `@Scheduled` para executações apropriadas (no código atual é 30s para demonstração).

---

## O que eu aprendi desenvolvendo este projeto

* Organizar regras de negócio em serviços e manter controllers finos
* Como modelar lotes de produtos e contabilizar quantidades por lote
* Implementar autenticação JWT com Spring Security (filtros, `UserDetailsService`)
* Boas práticas com DTOs e conversores para isolar entidade da API pública
* Testar serviços com JUnit + Mockito, mockando repositórios e validando interações
* Integração com serviços externos (SMTP e Telegram) e cuidados com segredos

---

## Próximos passos

* Tornas a aplicação que hoje é um Monólito em Microsserviços
* Implementar documentação OpenAPI/Swagger
* Refatorar leitura de segredos para `Spring Cloud Config` ou `Vault`
* Adicionar testes de integração (Spring Boot Test com banco em memória / Testcontainers)
* Cobertura de logs e monitoramento (Prometheus / Grafana)
