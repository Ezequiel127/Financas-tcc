# Finanças TCC — Modelo de Banco de Dados

**Versão:** 1.1  
**Status:** Aprovado  
**Data:** Agosto de 2026  
**Projeto:** Finanças TCC  
**Banco de dados:** PostgreSQL  
**Persistência:** Spring Data JPA / Hibernate  
**Migrations:** Flyway  
**Arquitetura relacionada:** Monólito modular organizado por domínio  

---

## 1. Finalidade

Este documento consolida a modelagem conceitual e lógica/física aprovada para o banco de dados do MVP do projeto **Finanças TCC**.

Sua finalidade é servir como referência oficial para:

- criação das migrations Flyway;
- implementação das entidades e repositories no backend;
- definição de constraints e índices;
- validação de integridade financeira;
- implementação de consultas;
- testes de persistência e integração;
- evolução futura do schema.

Este documento deve ser utilizado em conjunto com:

1. **Documento-base do projeto Finanças TCC**;
2. **Documento de Requisitos v1.2**;
3. **Documento de Arquitetura Técnica v1.0**.

O Documento de Requisitos v1.2 continua sendo a fonte oficial das funcionalidades e regras de negócio. A Arquitetura Técnica v1.0 continua sendo a fonte oficial das decisões arquiteturais e tecnológicas.

A modelagem descrita aqui não altera o escopo do MVP. Ela transforma os conceitos e regras já aprovados em uma estrutura relacional consistente.


### 1.1 Alterações e revisões consolidadas da versão 1.1

Esta versão consolida os pontos do modelo afetados pelas decisões funcionais aprovadas no Documento de Requisitos v1.2:

1. quando não existir receita principal ativa, o horizonte de **“Quanto posso gastar agora?”** passa a ser de **30 dias corridos a partir da data do cálculo**;
2. o módulo de cartões passa a aceitar **saldo inicial não detalhado de fatura**, representado por `invoice_adjustments` com `kind = 'OPENING_BALANCE'`, sem reconstrução de compras históricas;
3. `income_recurrences.status` passa a aceitar somente `ACTIVE` e `ENDED`, pois pausa de receita recorrente não faz parte do RF-008;
4. o conjunto inicial de categorias padrão passa a ser o definido pela **RN-048**, com três categorias de receita e seis categorias de despesa.

Essas alterações não criam novas tabelas nem modificam as cardinalidades principais do modelo.

---

## 2. Princípios da modelagem

A modelagem segue os seguintes princípios:

1. **Uma única fonte de verdade por conceito financeiro.**
2. **Consumo, fluxo de caixa, obrigação e quitação permanecem distintos.**
3. **Uma mesma obrigação não pode ser representada de forma duplicada.**
4. **Saldo atual não será mantido como valor redundante quando puder ser calculado com segurança.**
5. **Informações deriváveis não devem competir com seus registros de origem.**
6. **Histórico financeiro real deve ser preservado.**
7. **Contas e cartões com histórico devem ser arquivados em vez de excluídos diretamente.**
8. **Dados financeiros devem permanecer isolados por usuário.**
9. **Constraints do PostgreSQL devem proteger invariantes simples e determinísticas.**
10. **Regras financeiras complexas permanecem no domínio/backend.**
11. **Operações que alterem vários registros relacionados devem ser atômicas.**
12. **Complexidade futura não deve ser antecipada sem necessidade real.**

---

## 3. Organização por domínio

O banco acompanha os módulos conceituais definidos na Arquitetura Técnica.

### 3.1 Identity

- `users`
- `password_reset_tokens`

### 3.2 Accounts

- `financial_accounts`
- `account_movements`
- `reconciliation_adjustments`
- `internal_transfers`

### 3.3 Income

- `income_recurrences`
- `income_occurrences`

### 3.4 Expenses

- `expense_recurrences`
- `expense_installment_plans`
- `expense_occurrences`

### 3.5 Budgeting

- `categories`
- `monthly_category_budgets`

### 3.6 Credit Cards

- `credit_cards`
- `card_purchases`
- `card_installments`
- `credit_card_invoices`
- `invoice_payments`
- `invoice_adjustments`
- `credit_limit_adjustments`

### 3.7 Debts

- `debts`
- `debt_installments`
- `debt_payments`

### 3.8 Planning

- `reserves`
- `financial_goals`
- `goal_contributions`
- `investments`
- `investment_transactions`
- `investment_adjustments`

### 3.9 Projection

Não possui tabela própria inicialmente.

### 3.10 Insights

Não possui tabela própria inicialmente.

**Total do modelo completo do MVP: 29 tabelas.**

A existência das 29 tabelas no modelo não exige que todas sejam criadas na primeira migration. O MVP possui funcionalidades essenciais e complementares, e as migrations podem ser organizadas por domínio e dependência.

---

# 4. Identity

## 4.1 `users`

Representa o usuário proprietário dos dados financeiros.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador interno |
| `email` | Identificador de login |
| `password_hash` | Hash seguro da senha |
| `created_at` | Data/hora de criação |
| `updated_at` | Data/hora da última alteração |

### Regras

- `email` é obrigatório;
- o e-mail deve ser normalizado para minúsculas;
- deve existir proteção de unicidade case-insensitive;
- senha nunca deve ser armazenada em texto simples;
- CPF, telefone, endereço e outros dados não necessários ao MVP não serão adicionados sem novo requisito.

### Identificador

A estratégia padrão aprovada para PKs internas é:

```sql
BIGINT GENERATED BY DEFAULT AS IDENTITY
```

---

## 4.2 `password_reset_tokens`

Representa solicitações temporárias de redefinição de senha.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Usuário relacionado |
| `token_hash` | Hash do token |
| `expires_at` | Expiração |
| `used_at` | Momento de utilização, quando ocorrer |
| `created_at` | Criação |

### Regras

- o token real não deve ser armazenado em texto puro;
- tokens expirados não devem permitir redefinição;
- tokens já utilizados não devem ser reutilizados.

Não será criada inicialmente uma tabela própria de sessões. A persistência de sessão será definida durante a implementação do Spring Security caso exista necessidade concreta.

---

# 5. Accounts

## 5.1 `financial_accounts`

Representa contas bancárias, carteiras, dinheiro em espécie, poupança ou outras localizações de dinheiro real.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `name` | Nome da conta/carteira |
| `type` | Tipo |
| `initial_balance` | Saldo inicial |
| `initial_balance_date` | Data de referência do saldo inicial |
| `archived_at` | Arquivamento |
| `created_at` | Criação |
| `updated_at` | Alteração |
| `version` | Controle otimista de concorrência |

### Tipos conceituais iniciais

```text
CHECKING
SAVINGS
CASH
OTHER
```

### Saldo

Não existe campo `current_balance`.

O saldo é derivado por:

```text
saldo inicial
+ créditos em account_movements
- débitos em account_movements
+ reconciliation_adjustments
```

Essa escolha evita duas fontes concorrentes para o saldo da conta.

---

## 5.2 `account_movements`

Representa o efeito real de entrada ou saída de dinheiro em uma conta ou carteira.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `account_id` | Conta afetada |
| `amount` | Valor absoluto |
| `direction` | Crédito ou débito |
| `movement_date` | Data financeira |
| `description` | Descrição opcional |
| `created_at` | Criação |

### Direções

```text
CREDIT
DEBIT
```

### Regra de origem

Não serão utilizados:

```text
source_type
source_id
```

polimórficos.

A relação com a origem será feita por **FKs reais nas entidades que causaram a movimentação**, por exemplo:

- `income_occurrences.account_movement_id`;
- `expense_occurrences.account_movement_id`;
- `invoice_payments.account_movement_id`;
- `debt_payments.account_movement_id`;
- `investment_transactions.account_movement_id`;
- `internal_transfers.debit_movement_id`;
- `internal_transfers.credit_movement_id`.

Isso permite integridade referencial nativa do PostgreSQL.

### Limite conceitual

`account_movements` registra **fluxo de caixa**, não o significado econômico da operação.

Ele não substitui:

- receita;
- despesa;
- pagamento de fatura;
- pagamento de dívida;
- transferência;
- investimento.

---

## 5.3 `reconciliation_adjustments`

Representa ajustes manuais de reconciliação de saldo.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `account_id` | Conta reconciliada |
| `amount` | Ajuste positivo ou negativo |
| `adjustment_date` | Data |
| `reason` | Motivo opcional |
| `affected` | Indica reconciliação afetada por edição retroativa |
| `created_at` | Criação |

### Regras

- reconciliação altera o saldo calculado;
- não é receita;
- não é despesa;
- não é um `account_movement` econômico comum;
- edição de movimentação anterior à reconciliação não apaga a reconciliação;
- quando necessário, a reconciliação é marcada como afetada e deve ser revisada.

---

## 5.4 `internal_transfers`

Representa transferência entre contas do próprio usuário.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `source_account_id` | Conta de origem |
| `destination_account_id` | Conta de destino |
| `amount` | Valor |
| `transfer_date` | Data |
| `description` | Descrição opcional |
| `debit_movement_id` | Movimento de saída |
| `credit_movement_id` | Movimento de entrada |
| `created_at` | Criação |

### Regras

- origem e destino pertencem ao mesmo usuário;
- origem e destino não podem ser iguais;
- `amount > 0`;
- uma transferência gera exatamente dois movimentos:
  - `DEBIT` na origem;
  - `CREDIT` no destino;
- transferência não é receita nem despesa;
- transferência não altera o patrimônio total.

---

# 6. Income

## 6.1 `income_recurrences`

Representa a regra de uma receita recorrente.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `category_id` | Categoria opcional |
| `description` | Descrição/origem |
| `expected_amount` | Valor previsto padrão |
| `frequency` | Frequência |
| `start_date` | Início |
| `end_date` | Término opcional |
| `is_primary_cycle_income` | Receita principal do ciclo |
| `status` | Estado da recorrência |
| `created_at` | Criação |
| `updated_at` | Alteração |

### Estados conceituais

```text
ACTIVE
ENDED
```

`PAUSED` não é um estado válido para receitas recorrentes no MVP. O RF-008 permite editar ocorrências, editar esta e as seguintes ou encerrar a recorrência, mas não prevê pausa.

### Receita principal do ciclo

Deve existir no máximo uma recorrência ativa marcada como principal para cada usuário.

Essa regra será protegida por **índice único parcial** no PostgreSQL.

Entradas extras e receitas avulsas não alteram automaticamente o horizonte do ciclo financeiro.

---

## 6.2 `income_occurrences`

Representa uma receita concreta prevista ou recebida.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `recurrence_id` | Recorrência opcional |
| `category_id` | Categoria |
| `description` | Descrição/origem |
| `expected_amount` | Valor previsto |
| `received_amount` | Valor efetivamente recebido |
| `expected_date` | Data prevista |
| `received_date` | Data de recebimento |
| `status` | Prevista ou recebida |
| `account_movement_id` | Movimento real gerado |
| `created_at` | Criação |
| `updated_at` | Alteração |

### Estados

```text
EXPECTED
RECEIVED
```

### Regras

- receita avulsa possui `recurrence_id = NULL`;
- receita prevista não possui movimento real;
- somente uma receita recebida aumenta o saldo;
- `expected_amount` e `received_amount` permanecem separados;
- o valor recebido pode ser diferente do previsto sem alterar automaticamente as futuras ocorrências da recorrência.

---

# 7. Budgeting

## 7.1 `categories`

Representa categorias padrão e personalizadas.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário quando personalizada |
| `name` | Nome |
| `kind` | Receita ou despesa |
| `is_default` | Categoria padrão |
| `archived_at` | Arquivamento |
| `created_at` | Criação |

### Tipos

```text
INCOME
EXPENSE
```

### Categorias padrão

```text
user_id = NULL
is_default = true
```

O conjunto inicial oficial definido pela **RN-048** é:

**Receitas (`kind = 'INCOME'`):**

- Salário;
- Renda extra;
- Outras receitas.

**Despesas (`kind = 'EXPENSE'`):**

- Moradia;
- Alimentação;
- Transporte;
- Saúde;
- Lazer;
- Outros.

Essas nove categorias devem ser inseridas pela migration de seed `V3.1__seed_default_categories.sql`.

### Categorias personalizadas

```text
user_id = usuário
is_default = false
```

Categorias não representam dinheiro nem obrigação financeira.

---

## 7.2 `monthly_category_budgets`

Representa orçamento mensal opcional por categoria.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `category_id` | Categoria |
| `reference_month` | Competência |
| `planned_amount` | Valor planejado |
| `created_at` | Criação |
| `updated_at` | Alteração |

### Regras

- apenas um orçamento por usuário + categoria + mês;
- orçamento não é despesa;
- orçamento não é obrigação financeira.

### Mês de referência

`reference_month` será armazenado como `DATE` fixado no primeiro dia do mês.

Exemplo:

```text
Agosto/2026 → 2026-08-01
```

---

# 8. Expenses

## 8.1 `expense_recurrences`

Representa regra de despesa recorrente.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `category_id` | Categoria |
| `description` | Descrição |
| `expected_amount` | Valor previsto padrão |
| `frequency` | Frequência |
| `start_date` | Início |
| `end_date` | Término opcional |
| `essentiality` | Essencial ou opcional |
| `status` | Estado |
| `created_at` | Criação |
| `updated_at` | Alteração |

---

## 8.2 `expense_installment_plans`

Representa parcelamentos fora do cartão.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `category_id` | Categoria |
| `description` | Descrição |
| `total_amount` | Compromisso total |
| `installment_count` | Quantidade de parcelas |
| `start_date` | Início |
| `essentiality` | Essencial ou opcional |
| `created_at` | Criação |
| `updated_at` | Alteração |

Essa tabela não deve ser utilizada para compras parceladas em cartão.

---

## 8.3 `expense_occurrences`

Representa o gasto de um período.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `recurrence_id` | Recorrência opcional |
| `installment_plan_id` | Parcelamento opcional |
| `category_id` | Categoria |
| `description` | Descrição |
| `expected_amount` | Valor previsto |
| `realized_amount` | Valor realizado |
| `due_date` | Vencimento |
| `payment_date` | Data de pagamento |
| `economic_status` | Situação econômica |
| `payment_status` | Situação do pagamento |
| `essentiality` | Essencial ou opcional |
| `account_movement_id` | Movimento real quando pago |
| `created_at` | Criação |
| `updated_at` | Alteração |

### Situação econômica

```text
PLANNED
REALIZED
```

### Situação de pagamento

```text
PENDING
PAID
```

### Atraso

`OVERDUE` não será armazenado como estado independente.

Será derivado por:

```text
payment_status = PENDING
AND due_date < data atual
```

### Exclusividade de origem

Uma ocorrência pode vir:

- de uma recorrência;
- de um parcelamento fora do cartão;
- de nenhuma origem, quando avulsa.

Não pode vir de recorrência e parcelamento simultaneamente.

Essa regra será protegida por `CHECK`.

---

# 9. Credit Cards

## 9.1 `credit_cards`

Representa cartões de crédito do usuário.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `name` | Nome |
| `total_limit` | Limite total |
| `closing_day` | Dia de fechamento |
| `due_day` | Dia de vencimento |
| `archived_at` | Arquivamento |
| `created_at` | Criação |
| `updated_at` | Alteração |
| `version` | Concorrência otimista |

### Limite disponível

Não existe `available_limit` como fonte principal persistida.

O limite disponível é derivado de:

```text
limite total
- principal ainda comprometido
+ ajustes manuais aplicáveis
```

Limite de cartão nunca é renda, saldo, patrimônio ou dinheiro livre.

---

## 9.2 `card_purchases`

Representa o compromisso original assumido em uma compra no cartão.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `card_id` | Cartão |
| `category_id` | Categoria |
| `description` | Descrição |
| `purchase_date` | Data da compra |
| `total_amount` | Valor total |
| `installment_count` | Quantidade de parcelas |
| `created_at` | Criação |
| `updated_at` | Alteração |

Compra no cartão não reduz conta bancária no momento da compra.

---

## 9.3 `card_installments`

Representa o impacto mensal da compra.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `purchase_id` | Compra |
| `invoice_id` | Fatura |
| `installment_number` | Número |
| `amount` | Valor |
| `reference_date` | Competência |
| `created_at` | Criação |

### Regras

- compra em 1x também gera uma `card_installment`;
- cada parcela pertence a uma compra;
- cada parcela pertence a uma fatura;
- `(purchase_id, installment_number)` deve ser único.

---

## 9.4 `credit_card_invoices`

Representa a obrigação do cartão em um ciclo.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `card_id` | Cartão |
| `reference_month` | Competência |
| `closing_date` | Fechamento real do ciclo |
| `due_date` | Vencimento |
| `created_at` | Criação |
| `updated_at` | Alteração |
| `version` | Concorrência otimista |

### Regras

- `(card_id, reference_month)` deve ser único;
- `reference_month` utiliza `DATE` no primeiro dia do mês;
- `invoice_total` não é armazenado inicialmente como fonte oficial;
- `outstanding_balance` não é armazenado inicialmente como fonte oficial.

A composição da fatura deriva de parcelas, ajustes e pagamentos.

A fatura não constitui um segundo passivo independente das compras.

---

## 9.5 `invoice_payments`

Representa pagamento integral ou parcial de uma fatura.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `invoice_id` | Fatura |
| `account_id` | Conta de origem |
| `amount` | Total pago |
| `principal_amount` | Principal quitado |
| `charges_amount` | Juros, multas ou encargos pagos |
| `payment_date` | Data |
| `account_movement_id` | Movimento de saída |
| `created_at` | Criação |

### Regra

```text
amount = principal_amount + charges_amount
```

O pagamento:

- reduz o saldo pendente da fatura;
- produz saída real da conta;
- não é nova despesa de consumo;
- apenas principal libera limite comprometido.

---

## 9.6 `invoice_adjustments`

Representa fatos reais adicionais informados pelo usuário sobre a fatura.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `invoice_id` | Fatura |
| `amount` | Valor |
| `kind` | Tipo |
| `description` | Motivo |
| `adjustment_date` | Data |
| `created_at` | Criação |

### Tipos conceituais iniciais

```text
CHARGE
CREDIT
CORRECTION
OPENING_BALANCE
```

Pode representar, por exemplo:

- juros;
- multa;
- crédito;
- correção real da fatura;
- saldo inicial não detalhado de uma fatura já existente no momento em que o usuário começa a utilizar o sistema.

### Regra de `OPENING_BALANCE`

`OPENING_BALANCE` representa uma obrigação real já existente na fatura cuja composição histórica por compras não será reconstruída no MVP.

Regras:

- `amount > 0`;
- compõe o saldo e a obrigação da fatura;
- participa de compromissos futuros, projeção e patrimônio líquido;
- pode ser reduzido por pagamentos registrados da própria fatura;
- não cria `card_purchase`;
- não cria `card_installment`;
- não gera consumo retroativo por categoria;
- não deve duplicar a mesma obrigação em `debts`, `expense_installment_plans` ou qualquer outro módulo.

O sistema não inventará automaticamente regras do emissor.

---

## 9.7 `credit_limit_adjustments`

Representa reconciliação manual do limite do cartão.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `card_id` | Cartão |
| `amount` | Ajuste positivo ou negativo |
| `adjustment_date` | Data |
| `reason` | Motivo opcional |
| `created_at` | Criação |

Ajustes não sobrescrevem silenciosamente o cálculo automático.

---

## 9.8 Fluxo oficial do cartão

Fluxo normal de compras registradas no sistema:

```text
CardPurchase
    ↓
CardInstallment
    ↓
CreditCardInvoice
    ↓
InvoicePayment
    ↓
AccountMovement DEBIT
```

Para uma fatura que já existia antes do início do uso do sistema, existe uma segunda origem válida de obrigação:

```text
CardPurchase → CardInstallment ─┐
                               ├─→ CreditCardInvoice
OPENING_BALANCE ────────────────┘
```

Cada nível possui responsabilidade distinta:

| Registro | Responsabilidade |
|---|---|
| `card_purchases` | Compromisso original de compras registradas no sistema |
| `card_installments` | Consumo/impacto mensal das compras registradas |
| `invoice_adjustments (OPENING_BALANCE)` | Obrigação inicial já existente, sem consumo histórico reconstruído |
| `credit_card_invoices` | Obrigação do ciclo |
| `invoice_payments` | Quitação |
| `account_movements` | Saída real do dinheiro |

`OPENING_BALANCE` compõe a obrigação da fatura, mas não representa consumo retroativo por categoria.

Compra, parcela, saldo inicial, fatura e pagamento nunca devem ser somados como passivos independentes da mesma obrigação.

---

# 10. Debts

## 10.1 `debts`

Representa a obrigação principal da dívida.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `user_id` | Proprietário |
| `creditor` | Credor/instituição |
| `description` | Descrição opcional |
| `original_amount` | Valor original |
| `current_balance` | Saldo devedor real atual |
| `start_date` | Início/contratação |
| `next_due_date` | Próximo vencimento |
| `end_date` | Término opcional |
| `installment_count` | Quantidade opcional |
| `installment_amount` | Valor padrão opcional |
| `interest_rate` | Juros conhecidos, opcional |
| `status` | Estado |
| `created_at` | Criação |
| `updated_at` | Alteração |
| `version` | Concorrência otimista |

### Estados

```text
ACTIVE
PAID_OFF
RENEGOTIATED
```

### Saldo devedor

`current_balance` é deliberadamente armazenado.

Essa é uma exceção à regra geral de evitar valores deriváveis, porque a dívida representa uma realidade contratual externa que pode sofrer:

- juros desconhecidos;
- encargos;
- renegociação;
- correções informadas pelo credor.

O histórico interno nem sempre é suficiente para reproduzir o saldo contratual real.

---

## 10.2 `debt_installments`

Representa o cronograma previsto quando houver parcelamento.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
| `debt_id` | Dívida |
| `installment_number` | Número |
| `due_date` | Vencimento |
| `expected_amount` | Valor esperado |
| `created_at` | Criação |

### Regra

```text
UNIQUE(debt_id, installment_number)
```

A parcela não é um passivo adicional além da dívida.

---

## 10.3 `debt_payments`

Representa pagamentos realizados da dívida.

### Campos principais

| Campo | Finalidade |
|---|---|
| `id` | Identificador |
