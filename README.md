# Finanças TCC

Plataforma web de organização e planejamento financeiro pessoal desenvolvida como projeto acadêmico de **Análise e Desenvolvimento de Sistemas (ADS)**, com objetivo de evoluir também como produto e projeto de portfólio.

> **“Organize o que entrou, entenda o que saiu e saiba o que realmente pode fazer com o seu dinheiro.”**

## Sobre o projeto

O **Finanças TCC** busca ajudar pessoas a compreender melhor sua situação financeira real.

A proposta não é apenas registrar receitas e despesas, mas transformar os dados financeiros em informações úteis para responder perguntas como:

* Quanto dinheiro tenho agora?
* Quanto ainda preciso pagar?
* Quanto posso gastar sem comprometer minhas obrigações?
* Quanto das próximas rendas já está comprometido?
* Como minha situação financeira deve evoluir nos próximos meses?

Um dos conceitos centrais do projeto é o **“Quanto posso gastar agora?”**, calculado considerando dinheiro real disponível, reservas protegidas, obrigações conhecidas e despesas essenciais previstas.

## Status

🚧 **Em desenvolvimento**

A base técnica inicial do projeto já foi configurada:

* frontend com Next.js, React, TypeScript e Tailwind CSS;
* backend com Java 21 e Spring Boot;
* PostgreSQL como banco de dados;
* Flyway para migrations;
* estrutura de monorepo;
* configuração inicial de ambiente de desenvolvimento;
* Git e GitHub configurados.

As funcionalidades do produto ainda estão em fase de implementação.

## MVP

O MVP contempla, entre outras funcionalidades:

* cadastro e autenticação;
* contas e carteiras;
* receitas e despesas;
* recorrências;
* categorias;
* cartões de crédito e faturas;
* compras e parcelamentos;
* dívidas;
* projeção financeira;
* dashboard consolidado;
* cálculo de **“Quanto posso gastar agora?”**;
* histórico e gráficos básicos;
* metas, reservas e planejamento financeiro.

Integração bancária, Open Finance, IA financeira, aplicativo mobile nativo e modo família estão fora do MVP inicial.

## Tecnologias

### Frontend

* Next.js
* React
* TypeScript
* Tailwind CSS
* ESLint

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Hibernate
* Jakarta Bean Validation
* Maven

### Banco de dados

* PostgreSQL
* Flyway

### Testes

* JUnit 5
* Spring Boot Test
* Testcontainers

## Arquitetura

O backend será desenvolvido como um **monólito modular**, organizado por domínio de negócio.

Fluxo geral:

```text
Frontend
Next.js + React
        │
        │ REST / JSON
        ▼
Backend
Java + Spring Boot
        │
        │ JPA / Hibernate
        ▼
PostgreSQL
```

Os principais domínios previstos são:

```text
Identity
Accounts
Income
Expenses
Budgeting
Credit Cards
Debts
Planning
Projection
Insights
```

As regras financeiras oficiais permanecem no backend.

## Estrutura do repositório

```text
Financas-tcc/
├── frontend/   # Aplicação web
├── backend/    # API e regras de negócio
├── docs/       # Documentação técnica e do projeto
└── README.md
```

Frontend e backend são aplicações separadas tecnicamente, mas compartilham o mesmo histórico Git e o mesmo repositório.

## Segurança

O projeto trata dados financeiros como informações sensíveis.

Entre os princípios adotados estão:

* credenciais e secrets fora do Git;
* senhas nunca armazenadas em texto simples;
* autenticação obrigatória para dados financeiros;
* isolamento dos dados por usuário;
* validação no backend;
* tratamento seguro de erros;
* operações financeiras consistentes e transacionais quando necessário.

## Desenvolvimento local

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Aplicação disponível durante o desenvolvimento em:

```text
http://localhost:3000
```

### Backend

O projeto utiliza Maven Wrapper.

No Windows:

```bash
cd backend
mvnw.cmd spring-boot:run
```

O ambiente de desenvolvimento utiliza PostgreSQL e espera a variável:

```text
DB_PASSWORD
```

Credenciais reais não devem ser adicionadas ao repositório.

> As instruções completas de configuração do ambiente serão ampliadas conforme a implementação avançar.

## Documentação

A documentação do projeto será mantida em `docs/` e incluirá progressivamente:

* requisitos;
* arquitetura;
* modelo de banco de dados;
* decisões arquiteturais;
* documentação da API;
* roadmap;
* materiais relevantes ao TCC.

## Objetivo acadêmico

O projeto também será utilizado como base para o Trabalho de Conclusão de Curso de ADS, explorando o desenvolvimento de uma plataforma web voltada ao apoio à organização e ao planejamento financeiro pessoal.

Além do resultado acadêmico, o repositório tem como objetivo demonstrar evolução técnica em:

* desenvolvimento frontend;
* desenvolvimento backend com Java;
* modelagem de banco de dados;
* arquitetura de software;
* testes;
* Git e GitHub;
* documentação;
* evolução de produto.

## Roadmap

### Fase atual

* [x] Definição de requisitos
* [x] Definição da arquitetura técnica
* [x] Modelagem do banco de dados
* [x] Configuração inicial de frontend e backend
* [x] Criação do repositório GitHub
* [ ] Implementação do MVP
* [ ] Testes e validações
* [ ] Deploy do MVP
* [ ] Apresentação e documentação final do TCC

### Evoluções futuras

Possibilidades posteriores ao MVP incluem:

* PWA;
* aplicativo mobile;
* integração bancária/Open Finance;
* importação automática de transações;
* assistente financeiro com IA;
* modo família;
* recursos avançados de planejamento e análise financeira.

---

**Finanças TCC** — Projeto acadêmico e de portfólio em desenvolvimento.
