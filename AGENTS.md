# Instruções para agentes — Finanças TCC

## Propósito

- Construir um MVP de finanças pessoais que mostre dinheiro realmente livre, obrigações,
  cartões, dívidas, planejamento, projeção e “Quanto posso gastar agora?”.
- Tratar o projeto também como TCC e portfólio em Java/Spring Boot.
- Priorizar utilidade prática, integridade financeira e regras explicáveis.

## Fontes de autoridade

- Fonte funcional oficial: `docs/Financas-TCC-Documento-de-Requisitos-v1.2.pdf`.
- Em seguida, consultar `docs/01_Arquitetura_Tecnica_v1.0_Revisao_Editorial.md`.
- Para banco de dados: `docs/02_Modelo_Banco_Dados_v1.1_Consolidado.md`.
- Para fluxos e interface: `docs/03_UX_UI_Fase1_v1.0_Revisao_Editorial_Atualizado.md`
  e `docs/04_UX_UI_Fase2_Decisoes_Globais_v1.0.md`.
- Para referências visuais complementares: `docs/05_Referencias_Visuais_e_Profissionais_v1.0.md`.
- Depois, considerar as migrations do pacote oficial e a evidência operacional atual do repositório.
- Consultar `docs/00_LEIA_PRIMEIRO.txt` e `docs/README_FONTES.txt` para contexto e precedência.
- Referenciar essas fontes; não reproduzir aqui requisitos, fluxos ou esquemas completos.
- Diante de conflito entre fontes, registrar a divergência e pedir decisão; não escolher
  silenciosamente nem reescrever requisitos aprovados.

## Arquitetura e stack aprovadas

- Organizar o backend como monólito modular por domínio, com API REST JSON em `/api/v1`.
- Usar Java e Spring Boot; PostgreSQL, JPA/Hibernate e Flyway para persistência.
- Usar Next.js, React, TypeScript e Tailwind no frontend.
- Autenticar o MVP web com sessão no servidor, cookie protegido e CSRF.
- Não adotar JWT como mecanismo principal de autenticação do MVP.
- Usar `BigDecimal` para dinheiro e `LocalDate` para datas financeiras/de negócio.
- Manter regras financeiras e autorização efetiva no backend.

## Disciplina de escopo e mudanças

- Entregar primeiro o MVP aprovado; funcionalidades de evolução futura exigem decisão explícita.
- Evitar dependências, frameworks e complexidade arquitetural sem benefício concreto.
- Trabalhar em passos pequenos, verificáveis e rastreáveis; evitar refatorações alheias à tarefa.
- Inspecionar código e documentação pertinentes antes de editar; preferir padrões existentes.
- Preservar o trabalho preexistente e não alterar silenciosamente requisitos ou arquitetura.
- Se faltar evidência necessária, marcar como pendente em vez de inventar uma conclusão.
- Não afirmar que implementação, serviço ou testes funcionam sem verificação atual.

## Banco de dados e Flyway

- Não reescrever silenciosamente migrations que possam já ter sido aplicadas.
- Evoluir o esquema com migrations incrementais e revisar impactos de checksum do Flyway.
- CONFIRMADO NO CHECKOUT: `backend/src/main/resources/db/migration/` contém somente
  `V1__create_identity_schema.sql`.
- O pacote oficial de fontes de 22/09/2026 contém V2–V8 e V3.1 como migrations de
  referência/vigentes, mas elas não estão implementadas neste checkout.
- A V1 do pacote difere da V1 do repositório pela coluna nullable `users.name VARCHAR(255)`
  e por comentários/quebra de linha final; as demais definições SQL coincidem.
- RECONCILIAÇÃO PENDENTE: não copiar, substituir nem modificar migrations com base no
  pacote sem autorização explícita e decisão sobre a divergência da V1.

## Segurança

- Nunca expor nem commitar senhas, tokens, cookies, chaves privadas, dados financeiros
  pessoais ou outros segredos.
- Preservar o uso de variáveis de ambiente ou mecanismo seguro equivalente para segredos.
- Não enfraquecer controles de autenticação, autorização, sessão, CSRF ou isolamento de
  dados sem aprovação explícita.

## Verificação e relato

- Executar os testes mais específicos que cubram a mudança e relatar os que não rodaram.
- Executar `git diff --check` e inspecionar o diff final e `git status --short --branch`.
- Distinguir claramente o que foi verificado agora do que permanece sem validação.
- Escrever relatórios ao usuário em português brasileiro.
- Quando pertinente, separar CONFIRMADO ATUAL, HISTÓRICO, HIPÓTESE e VALIDAÇÃO PENDENTE.
