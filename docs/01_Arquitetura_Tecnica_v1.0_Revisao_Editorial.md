# Finanças TCC — Documento de Arquitetura Técnica

**Versão:** 1.0  
**Status:** Aprovado  
**Revisão editorial:** 15/08/2026 — referências documentais atualizadas para o Documento de Requisitos v1.2; nenhuma decisão arquitetural ou tecnologia foi alterada.  
**Data:** Agosto de 2026  
**Projeto:** Finanças TCC  

---

## 1. Finalidade

Este documento consolida as decisões de arquitetura e stack aprovadas para o MVP do projeto Finanças TCC.

Sua finalidade é servir como referência técnica para as próximas etapas de:

- modelagem do banco de dados;
- desenvolvimento backend;
- desenvolvimento frontend;
- testes;
- documentação;
- integração;
- deploy e evolução futura.

O documento complementa o **Documento de Requisitos v1.2**, que permanece como fonte oficial das funcionalidades, regras de negócio e requisitos não funcionais do produto.

Quando existir conflito entre uma decisão técnica e um requisito funcional aprovado, o requisito deve ser respeitado e a decisão arquitetural deve ser reavaliada.

---

## 2. Objetivos da arquitetura

A arquitetura deve permitir que o projeto:

- seja viável para desenvolvimento por um estudante de ADS;
- possua valor acadêmico e de portfólio;
- permita aprendizado relevante de backend com Java;
- mantenha o MVP tecnicamente simples;
- preserve integridade de dados financeiros;
- mantenha regras financeiras claras e determinísticas;
- isole os dados de cada usuário;
- permita evolução futura sem exigir reescrita integral;
- evite infraestrutura e padrões desnecessariamente complexos;
- permaneça compreensível e explicável no TCC.

---

## 3. Princípios arquiteturais

A implementação seguirá os seguintes princípios:

1. **Simplicidade antes de sofisticação.**
2. **Monólito modular antes de microserviços.**
3. **Separação por domínio de negócio.**
4. **Regras financeiras concentradas no backend.**
5. **Frontend não é fonte confiável para autorização ou validação.**
6. **Integridade financeira possui prioridade sobre conveniência de implementação.**
7. **Operações relacionadas devem ser atômicas quando necessário.**
8. **Entidades de persistência não devem definir o contrato público da API.**
9. **Dependências externas devem ser adicionadas apenas quando houver benefício concreto.**
10. **Complexidade futura não deve ser antecipada no MVP sem necessidade real.**

---

## 4. Stack tecnológica oficial

### 4.1 Frontend

- **Next.js**
- **React**
- **TypeScript**
- **Tailwind CSS**

O frontend será responsável principalmente por:

- interface;
- navegação;
- formulários;
- apresentação dos dados;
- experiência responsiva;
- comunicação com a API.

Cálculos financeiros oficiais e regras críticas de negócio não devem depender exclusivamente do frontend.

### 4.2 Backend

- **Java**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **Hibernate**
- **Jakarta Bean Validation**

O backend será responsável por:

- autenticação;
- autorização;
- regras de negócio;
- cálculos financeiros;
- consistência;
- validações;
- transações;
- persistência;
- exposição da API REST.

### 4.3 Banco de dados

**PostgreSQL**

O PostgreSQL será o banco de dados relacional principal da aplicação.

### 4.4 Comunicação

A comunicação entre frontend e backend será realizada por:

- API REST;
- JSON;
- HTTPS;
- endpoints versionados.

Prefixo inicial:

```text
/api/v1
```

---

## 5. Visão macro da arquitetura

```text
Usuário
   │
   │ HTTPS
   ▼
Next.js + React + TypeScript
   │
   │ REST / JSON / HTTPS
   │ /api/v1
   ▼
Java + Spring Boot
Monólito Modular
   │
   │ Spring Data JPA / Hibernate
   ▼
PostgreSQL
```

Frontend e backend serão aplicações tecnicamente separadas, porém pertencentes ao mesmo projeto.

---

## 6. Estilo arquitetural do backend

O backend será desenvolvido como um:

> **Monólito modular**

Isso significa que haverá uma única aplicação Spring Boot, porém seu código será dividido internamente por domínios de negócio com responsabilidades bem delimitadas.

Não serão utilizados microserviços no MVP.

O objetivo é obter:

- simplicidade operacional;
- facilidade de desenvolvimento;
- transações mais simples;
- menor custo de infraestrutura;
- separação clara de responsabilidades;
- possibilidade de evolução futura.

Caso algum módulo precise futuramente se tornar um serviço independente, essa decisão deverá ser motivada por uma necessidade concreta.

---

## 7. Organização por domínio

O backend utilizará organização **package by feature/domain**, evitando uma divisão global onde todos os controllers, services e repositories da aplicação fiquem agrupados apenas pelo tipo técnico.

Os módulos conceituais aprovados são:

### 7.1 Identity

Responsável por:

- usuários;
- autenticação;
- credenciais;
- sessão;
- recuperação e alteração de senha.

### 7.2 Accounts

Responsável por:

- contas;
- carteiras;
- saldos;
- transferências internas;
- ajustes de reconciliação.

### 7.3 Income

Responsável por:

- receitas;
- receitas previstas e recebidas;
- recorrências de receita;
- ocorrências de recorrências.

### 7.4 Expenses

Responsável por:

- despesas;
- despesas planejadas e realizadas;
- situação de pagamento;
- recorrências;
- parcelamentos fora do cartão.

### 7.5 Budgeting

Responsável por:

- categorias;
- classificação essencial/opcional;
- orçamento por categoria;
- planejamento mensal;
- comparação planejado versus realizado.

### 7.6 Credit Cards

Responsável por:

- cartões;
- compras;
- compras parceladas;
- parcelas;
- faturas;
- pagamentos de fatura;
- limite comprometido;
- reconciliação de limite.

### 7.7 Debts

Responsável por:

- dívidas;
- saldo devedor;
- parcelas;
- pagamentos;
- pagamentos parciais e extras;
- estados da dívida.

### 7.8 Planning

Responsável por:

- metas financeiras;
- reservas;
- investimentos simplificados.

### 7.9 Projection

Responsável por:

- projeção financeira;
- janela padrão de doze meses;
- propagação de saldo projetado;
- indicadores de projeção;
- cálculo de dinheiro livre;
- cálculo de “Quanto posso gastar agora?”.

### 7.10 Insights

Responsável por:

- dashboard consolidado;
- alertas;
- informações agregadas provenientes dos demais módulos.

---

## 8. Responsabilidade única das regras

Uma regra de negócio deve possuir um responsável claro.

Exemplos:

- cálculo e composição de fatura pertencem ao módulo `Credit Cards`;
- saldo de contas pertence ao módulo `Accounts`;
- saldo devedor pertence ao módulo `Debts`;
- cálculo de projeções pertence ao módulo `Projection`.

Um módulo pode utilizar informações de outro, mas não deve recriar silenciosamente a mesma regra.

Essa política é especialmente importante para evitar dupla contagem financeira.

---

## 9. Estrutura interna dos módulos

Cada módulo seguirá, de forma geral, esta separação:

```text
module/
├── api/
│   ├── controllers
│   └── dtos
│
├── application/
│   └── services / use cases
│
├── domain/
│   ├── entidades
│   ├── objetos de valor
│   └── regras de negócio
│
└── infrastructure/
    └── persistência / repositories
```

Fluxo conceitual:

```text
API / Controller
       ↓
Application / Service
       ↓
Domain
       ↓
Infrastructure / Repository
       ↓
PostgreSQL
```

---

## 10. Responsabilidades das camadas

### API

Responsável por:

- receber requisições HTTP;
- converter entrada para DTOs;
- iniciar validações estruturais;
- devolver respostas HTTP;
- não concentrar regras financeiras.

### Application

Responsável por:

- coordenar casos de uso;
- controlar fluxo das operações;
- coordenar transações;
- integrar regras e módulos quando necessário.

### Domain

Responsável pelas regras reais do negócio.

Exemplos:

- cálculo financeiro;
- transições de estado;
- limites de operação;
- consistência conceitual;
- prevenção de situações inválidas.

### Infrastructure

Responsável por:

- persistência;
- integração com banco;
- implementações técnicas dos repositories;
- detalhes externos necessários aos módulos.

---

## 11. API REST

A API seguirá REST e utilizará JSON.

Prefixo inicial:

```text
/api/v1
```

Exemplo conceitual:

```text
POST /api/v1/expenses
GET  /api/v1/accounts
GET  /api/v1/credit-cards
```

A definição exata dos endpoints ocorrerá durante a implementação de cada funcionalidade.

---

## 12. DTOs e entidades

Entidades JPA não serão expostas diretamente pela API.

Fluxo de entrada:

```text
Request
   ↓
Request DTO
   ↓
Application / Domain
   ↓
Entity
   ↓
Database
```

Fluxo de saída:

```text
Entity / Domain
   ↓
Response DTO
   ↓
JSON
```

Essa separação reduz acoplamento entre:

- banco;
- domínio;
- contrato da API;
- frontend.

DTOs simples poderão utilizar Java `record` quando apropriado.

---

## 13. Autenticação

No MVP web será utilizada:

> **Spring Security + sessão server-side**

O navegador utilizará cookie de sessão configurado com proteções adequadas, incluindo:

- `HttpOnly`;
- `Secure`;
- `SameSite`.

Também será utilizada proteção contra CSRF.

JWT não será utilizado na autenticação principal do MVP web.

A necessidade de OAuth2/JWT poderá ser reavaliada futuramente caso sejam adicionados:

- aplicativos mobile;
- clientes externos;
- integrações;
- APIs públicas.

---

## 14. Credenciais

Senhas nunca devem ser armazenadas em texto simples.

O armazenamento e validação de senhas devem utilizar os mecanismos seguros fornecidos pelo Spring Security.

O algoritmo específico e seus parâmetros serão definidos na implementação seguindo as práticas atuais da tecnologia.

---

## 15. Autorização e isolamento de dados

O backend deve determinar o usuário responsável pela operação através da sessão autenticada.

O frontend não será considerado fonte confiável do identificador do proprietário.

Exemplo conceitual:

```text
GET /api/v1/accounts/123

Spring Security
      ↓
usuário autenticado
      ↓
busca conta 123 pertencente ao usuário autenticado
```

Consultas não devem utilizar somente o ID global do registro quando houver necessidade de verificar propriedade.

Conceitualmente:

```text
findByIdAndOwner(...)
```

em vez de depender apenas de:

```text
findById(...)
```

---

## 16. Modelo de autorização do MVP

Não será criado RBAC complexo no MVP.

O modelo inicial é:

```text
Usuário autenticado
        ↓
pode acessar somente os próprios dados
```

Papéis administrativos ou permissões mais avançadas somente serão adicionados caso um requisito futuro os justifique.

---

## 17. PostgreSQL Row Level Security

PostgreSQL RLS não será utilizado no MVP.

Como o banco será acessado pelo backend Spring Boot e não diretamente pelo navegador, a autorização permanecerá centralizada no backend.

A utilização futura de RLS poderá ser reavaliada como camada adicional de defesa caso a arquitetura evolua e exista necessidade concreta.

---

## 18. Transações e consistência

Operações que modifiquem múltiplos registros relacionados devem ser executadas atomicamente.

A camada `Application/Service` utilizará transações gerenciadas pelo Spring, normalmente com:

```java
@Transactional
```

Exemplos de operações que podem exigir transação:

- transferência entre contas;
- pagamento de fatura;
- criação e atualização de parcelamentos;
- pagamentos de dívida;
- reconciliações;
- operações que alterem simultaneamente saldo e registros relacionados.

A regra principal é:

> uma operação não pode deixar os dados financeiros parcialmente persistidos.

---

## 19. Validação

A aplicação terá múltiplas barreiras complementares de validação.

### 19.1 DTO

Jakarta Bean Validation será utilizada para dados estruturais.

Exemplos:

- campos obrigatórios;
- formato;
- tamanho;
- valores inválidos.

### 19.2 Application e Domain

Regras de negócio serão validadas nas camadas apropriadas.

Exemplos:

- transições permitidas;
- valores compatíveis com a obrigação;
- estados financeiros;
- consistência entre registros relacionados.

### 19.3 Banco

Constraints do PostgreSQL serão utilizadas como barreira final para invariantes adequados ao banco.

A validação do frontend serve à experiência do usuário, mas não substitui a validação do backend.

---

## 20. Tratamento de erros

O backend utilizará tratamento centralizado de erros por meio de:

```java
@RestControllerAdvice
```

Serão utilizadas exceções próprias para situações de domínio quando apropriado.

As respostas de erro devem possuir formato JSON consistente.

Códigos HTTP deverão representar corretamente o resultado da operação.

Exemplos:

- `400` — requisição inválida;
- `401` — autenticação necessária;
- `403` — operação não autorizada;
- `404` — recurso inexistente;
- `409` — conflito;
- `500` — erro interno.

Stack traces, detalhes internos e informações sensíveis não devem ser retornados ao cliente.

---

## 21. Valores monetários

Valores financeiros utilizarão:

No Java:

```java
BigDecimal
```

No PostgreSQL:

```sql
NUMERIC
```

ou:

```sql
DECIMAL
```

Não serão utilizados `float` ou `double` para representar dinheiro.

As regras adicionais são:

- escala monetária definida explicitamente;
- arredondamento explícito;
- nenhuma dependência de arredondamento implícito;
- cálculos oficiais realizados no backend;
- frontend responsável principalmente por apresentação e formatação.

---

## 22. Datas e horários

Datas financeiras sem necessidade de horário utilizarão:

```java
LocalDate
```

Exemplos:

- vencimento;
- competência;
- data de compra;
- data prevista;
- data de pagamento quando somente o dia importar.

Timestamps técnicos utilizarão UTC.

Exemplos:

- criação;
- atualização;
- login;
- eventos técnicos.

A interface será responsável por apresentar timestamps no contexto adequado ao usuário.

A apresentação de datas seguirá o padrão brasileiro.

---

## 23. Concorrência

Será utilizado controle otimista de concorrência de forma seletiva.

Quando uma entidade ou estado financeiro possuir risco relevante de atualização simultânea, poderá utilizar:

```java
@Version
```

Objetivo:

> impedir que uma atualização baseada em uma versão antiga sobrescreva silenciosamente uma alteração mais recente.

A estratégia padrão não utilizará pessimistic locking.

Locks mais restritivos somente serão adicionados se um caso concreto demonstrar necessidade.

---

## 24. Migrations do banco

Será utilizado:

> **Flyway**

Alterações estruturais do banco deverão ser representadas por migrations versionadas.

Convenção de nomes:

```text
V1__initial_schema.sql
V2__add_credit_cards.sql
V3__add_invoice_fields.sql
```

A equipe deve preservar o padrão de nomenclatura do Flyway para garantir ordenação, rastreabilidade e execução previsível das migrations.

Mudanças manuais não rastreadas em produção devem ser evitadas.

A modelagem detalhada das tabelas e migrations pertence à etapa de Banco de Dados.

---

## 25. Ambientes

Serão utilizados três ambientes conceituais:

- `dev`;
- `test`;
- `prod`.

O Spring Boot utilizará profiles para diferenças legítimas de configuração entre ambientes.

Exemplos:

```text
application-dev
application-test
application-prod
```

Profiles não devem alterar regras financeiras ou regras de negócio.

---

## 26. Segredos e configurações

Credenciais e segredos deverão ser armazenados em variáveis de ambiente ou mecanismo seguro equivalente.

Não devem ser versionados no Git:

- senha do banco;
- credenciais;
- tokens;
- secrets;
- chaves privadas.

Arquivos de exemplo poderão documentar quais variáveis são necessárias sem conter os valores reais.

---

## 27. CORS e credenciais

Como frontend e backend são aplicações separadas, a política de CORS deverá ser configurada explicitamente no Spring Boot.

Para autenticação baseada em sessão, as requisições legítimas do frontend deverão permitir envio de credenciais.

Diretrizes:

- permitir somente origens conhecidas e configuradas por ambiente;
- habilitar credenciais quando necessário para o cookie de sessão;
- não utilizar origem curinga (`*`) em conjunto com credenciais;
- manter configuração distinta entre `dev` e `prod`;
- alinhar CORS, cookies e CSRF para que a proteção de sessão permaneça consistente.

A configuração específica será definida durante a implementação e deploy.

---

## 28. Logging e observabilidade

O MVP utilizará logging padrão do ecossistema Spring Boot através de SLF4J e implementação correspondente fornecida pela stack.

Logs devem registrar:

- erros técnicos;
- falhas relevantes;
- informações necessárias para depuração;
- contexto técnico seguro.

Logs não devem registrar desnecessariamente:

- senhas;
- tokens;
- credenciais;
- dados financeiros sensíveis.

O MVP não utilizará inicialmente infraestrutura pesada como:

- ELK;
- tracing distribuído;
- stack dedicada de observabilidade;
- sistemas complexos de monitoramento.

Essas ferramentas poderão ser adicionadas futuramente caso exista necessidade operacional.

---

## 29. Documentação da API

A API será documentada utilizando:

- OpenAPI;
- Swagger UI;
- `springdoc-openapi`.

A documentação deve permitir visualizar:

- endpoints;
- DTOs;
- parâmetros;
- respostas;
- códigos HTTP;
- estrutura do contrato da API.

Além da função técnica, essa documentação poderá ser utilizada na apresentação acadêmica e no portfólio.

---

## 30. Estratégia de testes

### 30.1 Testes unitários

Será utilizado:

> **JUnit 5**

Prioridade especial para regras do domínio financeiro.

Exemplo conceitual:

```text
entrada conhecida
      ↓
regra financeira
      ↓
resultado esperado
```

### 30.2 Testes de integração

Serão utilizados:

- Spring Boot Test;
- Testcontainers;
- PostgreSQL real executado em container para testes.

O objetivo é reduzir diferenças entre o banco utilizado nos testes e o PostgreSQL utilizado pela aplicação.

### 30.3 Testes da API e segurança

Os testes devem abranger casos relevantes envolvendo:

- autenticação;
- autorização;
- isolamento entre usuários;
- validação;
- erros;
- códigos HTTP;
- integração entre camadas.

### 30.4 H2

H2 não será utilizado como substituto principal do PostgreSQL nos testes de integração.

---

## 31. Política de dependências

O MVP deve manter quantidade reduzida de dependências externas.

Dependências devem possuir benefício técnico claro.

Base prevista:

- Spring Boot;
- Spring Web;
- Spring Security;
- Spring Data JPA;
- Jakarta Validation;
- PostgreSQL Driver;
- Flyway;
- springdoc-openapi;
- JUnit;
- Testcontainers.

Inicialmente não serão adicionados:

- Lombok;
- MapStruct.

A necessidade dessas ou de outras bibliotecas poderá ser reavaliada posteriormente.

---

## 32. Organização do repositório

O projeto utilizará um único repositório.

Estrutura conceitual:

```text
financas-tcc/
├── frontend/
├── backend/
└── docs/
```

O frontend e backend permanecerão separados internamente, mas compartilharão:

- histórico do Git;
- documentação;
- gestão do projeto;
- releases quando apropriado.

---

## 33. Documentação de arquitetura

As decisões arquiteturais serão consolidadas em:

```text
docs/
└── architecture/
    ├── architecture-v1.0.md
    └── adr/
```

Este documento representa a versão oficial `architecture-v1.0.md`.

---

## 34. Architecture Decision Records

Decisões arquiteturais relevantes futuras poderão utilizar ADRs.

Um ADR deve registrar principalmente:

- contexto;
- decisão;
- motivo;
- consequências.

Não será necessário criar ADR para cada pequena escolha técnica.

Também não é obrigatório recriar retroativamente um ADR individual para todas as decisões já consolidadas neste documento.

---

## 35. Padrões não adotados no MVP

A arquitetura deliberadamente não utilizará inicialmente:

- microserviços;
- Clean Architecture completa;
- arquitetura hexagonal rigorosa;
- CQRS;
- Event Sourcing;
- DDD tático completo;
- JWT como autenticação principal do cliente web;
- RBAC complexo;
- PostgreSQL RLS;
- pessimistic locking por padrão;
- H2 como banco principal de integração;
- Lombok;
- MapStruct;
- infraestrutura avançada de observabilidade.

Isso não significa que essas tecnologias ou padrões sejam inadequados.

Significa apenas que não existe atualmente uma necessidade aprovada que justifique sua complexidade no MVP.

---

## 36. Segurança como requisito transversal

Segurança não será tratada como um módulo isolado.

Ela deverá atravessar toda a arquitetura.

