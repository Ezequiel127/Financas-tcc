# Finanças TCC — UX/UI Fase 1

**Versão:** 1.0  
**Status:** Aprovado  
**Revisão editorial:** 15/08/2026 — alinhamento documental ao Documento de Requisitos v1.2, ao Modelo de Banco de Dados v1.1 e à revisão editorial da Arquitetura Técnica v1.0; nenhuma decisão de UX/UI foi redesenhada.  
**Data:** Agosto de 2026  
**Projeto:** Finanças TCC  
**Etapa:** Fase 1 — Fluxos e Arquitetura de Informação  

---

## 1. Finalidade

Este documento consolida as decisões aprovadas para a **Fase 1 de UX/UI** do projeto Finanças TCC.

Seu objetivo é servir como referência oficial para:

- criação dos wireframes;
- construção das telas no Figma;
- definição da navegação;
- organização dos fluxos;
- desenvolvimento frontend;
- integração frontend/backend;
- testes de usabilidade;
- documentação do TCC;
- criação e organização de tasks relacionadas à interface.

Este documento **não substitui** os documentos oficiais de requisitos, arquitetura ou banco de dados. Ele traduz essas decisões para a experiência do usuário e para a organização da interface.

---


## 1.1 Revisão de consistência

Antes da oficialização como fonte do projeto, esta consolidação passou por revisão detalhada.

Foram incorporadas as seguintes correções:

- fluxo de categorias;
- alteração de senha;
- exclusão da conta;
- ajuste manual do limite do cartão;
- informações secundárias compactas no Dashboard;
- correção semântica de `Total em dívidas`;
- correção de `Saldo final do mês` para `Variação líquida de caixa` quando o valor representar apenas entradas menos saídas;
- definição da semântica do gráfico `Receitas x Despesas`;
- remoção de `Pausar receita recorrente` por ausência de requisito funcional aprovado;
- política transversal de arquivamento e ações destrutivas;
- incorporação documental das duas decisões funcionais posteriormente aprovadas no Documento de Requisitos v1.2: fallback de 30 dias sem receita principal ativa e saldo inicial não detalhado de fatura já existente.

Nenhuma nova regra financeira ou funcionalidade de UX foi criada nesta revisão editorial.

---

## 2. Fontes oficiais consideradas

A Fase 1 foi definida com base em:

1. **Finanças TCC — Documento de Requisitos v1.2**;
2. **Finanças TCC — Documento de Arquitetura Técnica v1.0 — revisão editorial de 15/08/2026**;
3. **Finanças TCC — Modelo de Banco de Dados v1.1**;
4. **Documento-base do Projeto Finanças TCC**.

Para funcionalidades, regras de negócio e escopo aprovado, o **Documento de Requisitos v1.2** permanece como fonte funcional principal. A revisão editorial da Arquitetura Técnica v1.0 não altera decisões arquiteturais.

---

# 3. Princípios gerais de UX aprovados

A interface deve seguir os seguintes princípios:

- linguagem simples em português brasileiro;
- evitar termos financeiros desnecessariamente técnicos;
- orientar sem julgar;
- diferenciar claramente realidade atual de previsão;
- diferenciar consumo, fluxo de caixa, obrigação e quitação;
- não apresentar limite de cartão como dinheiro;
- não duplicar a mesma obrigação em áreas diferentes de forma que gere dupla interpretação financeira;
- reduzir quantidade de opções visíveis simultaneamente;
- priorizar tomada de decisão;
- manter formulários objetivos;
- usar divulgação progressiva: mostrar campos adicionais somente quando necessários;
- permitir que o usuário compreenda os cálculos relevantes;
- não depender somente de cor para comunicar estados;
- funcionar adequadamente em desktop, tablet e celular;
- evitar transformar o Dashboard em um painel excessivamente analítico.

---

# 4. Arquitetura de informação principal

A estrutura principal aprovada é:

```text
FINANÇAS TCC
│
├── Autenticação
│   ├── Criar conta
│   ├── Entrar
│   └── Recuperar senha
│
├── Configuração inicial
│   ├── Conta e saldo
│   ├── Receita principal (se houver)
│   ├── Obrigações existentes
│   └── Resumo inicial
│
├── Início
│   └── Dashboard
│
├── Movimentações
│   ├── Receitas
│   ├── Despesas
│   ├── Histórico
│   └── Categorias
│
├── Contas
│   ├── Contas e carteiras
│   ├── Transferências
│   └── Ajuste de saldo
│
├── Cartões
│   ├── Cartões
│   ├── Compras
│   ├── Parcelamentos
│   ├── Fatura atual
│   └── Faturas futuras
│
├── Dívidas
│   ├── Dívidas ativas
│   ├── Parcelas
│   └── Pagamentos
│
├── Planejamento
│   ├── Orçamento
│   ├── Metas
│   ├── Reservas
│   ├── Investimentos
│   └── Patrimônio
│
└── Projeção
    ├── Próximos 12 meses
    └── Compromissos futuros
```

### Justificativa da estrutura

A navegação foi organizada segundo o **modelo mental do usuário**, e não como cópia direta dos módulos técnicos do backend.

A interface agrupa informações por intenção prática: o que tenho, o que entrou e saiu, o que devo, o que planejei e o que provavelmente acontecerá nos próximos meses.

---

# 5. Navegação principal — Desktop

No desktop, a navegação principal utiliza sidebar lateral:

```text
Início
Movimentações
Contas
Cartões
Dívidas
Planejamento
Projeção
```

Configurações e perfil permanecem em área secundária.

Não serão criados itens principais separados para cada funcionalidade menor.

---

# 6. “Quanto posso gastar agora?”

O recurso **“Quanto posso gastar agora?”** não será um item independente da navegação principal.

Ele será uma das informações mais importantes do **Dashboard**, com possibilidade de abrir explicação ou detalhamento.

Conceitualmente:

```text
Saldo real disponível
- Reservas protegidas
- Obrigações dentro do horizonte do ciclo financeiro
- Despesas essenciais previstas no mesmo intervalo
= Dinheiro livre
```

O horizonte do ciclo financeiro é:

```text
Se existir receita principal ativa
→ até a próxima ocorrência dessa receita

Se não existir receita principal ativa
→ 30 dias corridos a partir da data do cálculo
```

Receitas avulsas ou entradas extras não alteram automaticamente esse horizonte.

O valor exibido é sempre não negativo.

Quando o resultado matemático for negativo:

```text
Quanto posso gastar agora?
R$ 0,00

Faltam R$ X para cobrir
os compromissos do ciclo.
```

O déficit deve ser mostrado separadamente.

---

# 7. Fluxo de autenticação

## 7.1 Entrada

A tela inicial de autenticação possui apenas dois caminhos principais:

```text
Já tenho conta → Entrar
Ainda não tenho conta → Criar conta
```

Não serão adicionados login social, CPF, telefone ou outros mecanismos sem novo requisito.

---

## 7.2 Criar conta

Tela:

```text
Criar sua conta

E-mail
Senha
Confirmar senha

[ Criar conta ]

Já possui uma conta? Entrar
```

O cadastro deve ser realizado em uma única tela.

Comportamentos:

- validar formato do e-mail;
- informar quando as senhas não coincidirem;
- apresentar requisitos de senha de forma compreensível;
- permitir mostrar/ocultar senha;
- impedir envio repetido durante processamento;
- exibir feedback de erro próximo ao campo;
- após sucesso, iniciar a configuração financeira inicial.

Não serão solicitados nome, CPF, telefone ou data de nascimento no cadastro do MVP.

---

## 7.3 Login

Tela:

```text
Entrar

E-mail
Senha

[ Entrar ]

Esqueci minha senha

Não possui conta? Criar conta
```

Fluxo:

```text
Entrar
  ↓
Credenciais válidas?
  ├── Não → mensagem genérica de erro
  └── Sim
       ↓
Configuração inicial concluída?
  ├── Sim → Dashboard
  └── Não → continuar configuração inicial
```

Mensagens não devem revelar se determinado e-mail existe ou não.

---

## 7.4 Recuperação de senha

Fluxo:

```text
Login
  ↓
Esqueci minha senha
  ↓
Informar e-mail
  ↓
Enviar instruções
  ↓
Tela de confirmação
  ↓
Link recebido
  ↓
Nova senha
  ↓
Login
```

Mensagem neutra recomendada:

> Se existir uma conta vinculada a esse e-mail, você receberá as instruções para redefinir sua senha.

Não serão usadas perguntas de segurança no MVP.

---

## 7.5 Logout

Fluxo:

```text
Perfil / Configurações
  ↓
Sair
  ↓
Sessão encerrada
  ↓
Login
```

Não haverá confirmação extra de logout por padrão, pois a ação é reversível.

---


## 7.6 Alterar senha

Dentro da área autenticada:

```text
Configurações
  ↓
Segurança
  ↓
Alterar senha
```

A tela deve permitir a alteração de senha do usuário autenticado, com validações e feedback adequados.

Os detalhes técnicos de autenticação, sessão e validação permanecem responsabilidade da implementação.

**Recomendação registrada:** manter alteração de senha dentro de `Configurações → Segurança`.  
É uma função obrigatória de credenciais, mas não pertence ao fluxo principal de uso financeiro. Isso mantém a navegação limpa e agrupa ações de segurança em um único lugar.

---

## 7.7 Exclusão da conta

Fluxo conceitual:

```text
Configurações
  ↓
Conta
  ↓
Excluir conta
  ↓
Explicação das consequências
  ↓
Confirmação explícita
  ↓
Solicitação / conclusão
  ↓
Sessão encerrada
```

A interface deve:

- exigir confirmação explícita;
- deixar claro que a ação é sensível;
- não prometer eliminação imediata e irrestrita de todos os dados em qualquer circunstância;
- direcionar o usuário de volta ao estado não autenticado após a conclusão aplicável.

O mecanismo visual exato de confirmação será definido na Fase 2.

**Recomendação registrada:** incluir o fluxo agora e definir apenas o comportamento conceitual.  
A exclusão da conta é requisito obrigatório e precisa existir na arquitetura de UX, mas detalhes jurídicos e técnicos não devem ser inventados nesta etapa.

---

# 8. Configuração financeira inicial

O usuário não deve ser lançado diretamente em um Dashboard vazio.

Fluxo aprovado:

```text
Criar conta
    ↓
Entrar
    ↓
Configuração inicial
    │
    ├── 1. Onde está seu dinheiro?
    │
    ├── 2. Receita principal (se houver)
    │
    ├── 3. O que você já tem para pagar?
    │
    └── 4. Resumo da situação
             ↓
          Dashboard
```

---

## 8.1 Onde está seu dinheiro?

Pergunta:

> Quanto dinheiro você tem hoje e onde ele está?

O usuário cadastra pelo menos uma conta/carteira, mesmo que seu saldo seja R$ 0,00.

Exemplo:

```text
Conta Nubank
Saldo atual: R$ 2.000,00
```

---

## 8.2 Receita principal

Pergunta:

> Qual renda normalmente inicia seu ciclo financeiro?

O usuário pode informar que **não possui receita principal** e continuar a configuração normalmente.

Exemplo:

```text
Salário
R$ 2.000,00
Recebo todo dia 5
```

Essa receita serve como referência para o horizonte do cálculo de dinheiro livre quando existir uma receita principal ativa.

A configuração inicial deve permitir continuar mesmo quando o usuário não possuir receita principal ativa. Nesse caso, o horizonte de “Quanto posso gastar agora?” e de “a pagar no ciclo atual” será de **30 dias corridos a partir da data do cálculo**.

---

## 8.3 Obrigações já existentes

O onboarding deve permitir que o usuário entre na plataforma **já endividado**.

Opções:

```text
O que você já possui hoje?

[ ] Fatura ou compras no cartão
[ ] Dívidas ou empréstimos
[ ] Contas e despesas recorrentes
[ ] Parcelamentos fora do cartão
[ ] Nenhum desses
```

As opções escolhidas conduzem aos respectivos cadastros.

Quando o usuário já possuir uma fatura existente, a opção de cartão deve permitir iniciar o controle por meio de **saldo inicial não detalhado da fatura**, sem exigir reconstrução das compras históricas que originaram esse valor.

Esse saldo inicial:

- compõe a obrigação da fatura;
- participa de compromissos, projeção e patrimônio;
- não gera consumo retroativo por categoria;
- não deve ser duplicado como dívida, parcelamento fora do cartão ou outra obrigação equivalente.

Não será criado um formulário único gigante para todas as obrigações.

---

## 8.4 Resumo inicial

Exemplo:

```text
Sua situação inicial

Dinheiro atual              R$ 2.000
Próxima renda principal     R$ 2.000
A pagar até lá              R$ 3.000

Quanto pode gastar agora    R$ 0

Faltam R$ 1.000 para cobrir
os compromissos cadastrados.
```

Ação:

```text
[ Ir para meu Dashboard ]
```

O exemplo acima representa um usuário com receita principal ativa. Quando ela não existir, a interface deve substituir a referência à próxima renda principal por uma indicação equivalente ao **horizonte dos próximos 30 dias**, sem bloquear o acesso ao Dashboard.

---

## 8.5 Itens que não bloqueiam o onboarding

Não serão obrigatórios durante a entrada inicial:

- orçamento;
- metas;
- reservas;
- categorias personalizadas;
- investimentos.

Esses itens podem ser configurados posteriormente.

---

# 9. Contas e Carteiras

## 9.1 Estrutura

```text
Contas
├── Visão geral
├── Detalhe da conta
├── Nova conta/carteira
├── Transferir entre contas
└── Ajustar saldo
```

---

## 9.2 Visão geral

Exemplo:

```text
Contas e carteiras

Saldo total atual
R$ 4.350,00

Nubank
Conta corrente
R$ 2.000,00

Dinheiro
Carteira
R$ 350,00

Poupança
R$ 2.000,00

[ + Adicionar conta ]
[ Transferir ]
```

O saldo consolidado aparece primeiro; a distribuição por conta vem em seguida.

---

## 9.3 Nova conta/carteira

Campos:

```text
Nome
Tipo
Saldo atual
Data desse saldo

[ Salvar conta ]
```

Tipos iniciais:

- conta corrente;
- poupança;
- dinheiro;
- outro.

Não serão pedidos agência, número de conta ou outros dados bancários desnecessários ao MVP.

---

## 9.4 Detalhe da conta

Exemplo:

```text
Nubank

Saldo atual
R$ 2.000,00

[ Transferir ]
[ Ajustar saldo ]

Movimentações recentes
────────────────────────
Salário        + R$ 2.000
Mercado        - R$   180
Internet       - R$    99
```

A tela representa fluxo de caixa real da conta.

Compras no cartão ainda não pagas não aparecem como saída da conta.

---

## 9.5 Transferência entre contas

Exemplo:

```text
Transferir dinheiro

De:
Nubank

Para:
Poupança

Valor:
R$ 500,00

Data:
15/08/2026

[ Transferir ]
```

Uma transferência interna:

- reduz uma conta;
- aumenta outra;
- não é receita;
- não é despesa;
- não altera patrimônio total.

---

## 9.6 Ajustar saldo

Na interface, será utilizado o termo simples **“Ajustar saldo”**.

Exemplo:

```text
O sistema calcula:
R$ 1.950,00

Seu banco mostra:
R$ 2.000,00

Diferença:
+ R$ 50,00
```

Tela:

```text
Saldo real informado
R$ 2.000,00

Motivo (opcional)
Ex.: lançamento não registrado

[ Confirmar ajuste ]
```

Tecnicamente, isso corresponde a uma reconciliação.

---


# 10. Categorias

## 10.1 Finalidade

Categorias organizam receitas e despesas para uso em cadastros, histórico, orçamento e análises.

A interface deve distinguir:

```text
Categorias padrão
Categorias personalizadas
```

Categorias não representam dinheiro, obrigação ou movimentação financeira.

---

## 10.2 Tela principal

Exemplo:

```text
Categorias

Despesas
Moradia
Alimentação
Transporte
Saúde
Lazer
Outros

Receitas
Salário
Renda extra
Outras receitas

[ + Nova categoria ]
```

Categorias padrão devem ser identificáveis como fornecidas pelo sistema e permanecer somente leitura no MVP.

Categorias personalizadas podem ser criadas e gerenciadas pelo usuário.

---

## 10.3 Criar categoria personalizada

Campos:

```text
Nome

Tipo
( ) Receita
( ) Despesa

[ Salvar categoria ]
```

Não devem ser adicionados campos extras sem necessidade funcional.

---

## 10.4 Editar e arquivar categoria personalizada

Ações possíveis:

```text
[ Editar categoria ]
[ Arquivar categoria ]
```

Quando uma categoria possuir histórico, a interface deve priorizar arquivamento em vez de exclusão destrutiva.

Categorias arquivadas deixam de aparecer como opção principal em novos cadastros, mas permanecem preservadas nos registros históricos.

---

## 10.5 Regra de UX

Categorias padrão não devem ser alteradas pelo usuário.

Categorias personalizadas podem ser editadas ou arquivadas, respeitando preservação histórica.

**Recomendação registrada:** manter o fluxo de categorias simples e administrativo.  
Categorias são uma infraestrutura de organização utilizada em vários módulos, não uma área financeira independente. Isso evita sobrecarregar a interface e reduz inconsistências entre despesas, receitas e orçamento.

---

# 11. Receitas

## 11.1 Estrutura conceitual

```text
Receita avulsa
Receita recorrente
Receita principal
```

Previsto e recebido permanecem distintos.

---

## 11.2 Tela principal

Exemplo:

```text
Receitas

Este mês
Recebidas        R$ 2.000,00
Previstas        R$   350,00

[ + Nova receita ]

Todas | Recebidas | Previstas

15 ago  Salário        R$ 2.000   Recebida
20 ago  Freelance      R$   250   Prevista
25 ago  Ajuda familiar R$   100   Prevista
```

---

## 11.3 Nova receita

Campos base:

```text
Descrição
Valor
Data

Tipo
( ) Avulsa
( ) Recorrente

Situação
( ) Prevista
( ) Já recebi
```

Se já recebida:

```text
Recebido em:
[ Conta/carteira ]
```

---

## 11.4 Receita recorrente

Campos adicionais:

```text
Frequência
Início
Término opcional

[ ] Esta é minha receita principal
```

Na interface, será usado o nome **“Receita principal”**, acompanhado de uma explicação curta.

---

## 11.5 Confirmar recebimento

Exemplo:

```text
Salário
R$ 2.000,00
Previsto para 15/08

[ Confirmar recebimento ]
```

Ao confirmar:

```text
Quanto você recebeu?
Quando?
Em qual conta entrou?

[ Confirmar ]
```

O valor recebido pode ser diferente do previsto sem alterar automaticamente os próximos lançamentos.

---

## 11.6 Editar recorrência

Opções:

```text
Alterar:

( ) Somente esta ocorrência
( ) Esta e as próximas
```

Além de:

```text
[ Encerrar recorrência ]
```

A opção de pausar receita recorrente não faz parte do fluxo oficial desta versão.

---

# 12. Despesas

## 12.1 Conceitos separados

```text
Tipo
├── Avulsa
├── Recorrente
└── Parcelada fora do cartão

Classificação
├── Essencial
└── Opcional

Situação econômica
├── Planejada
└── Realizada

Pagamento
├── Pendente
└── Pago
```

---

## 12.2 Tela principal

Exemplo:

```text
Despesas

Este mês

Realizadas        R$ 1.240,00
Ainda a pagar     R$   680,00

[ + Nova despesa ]

Todas | Pendentes | Pagas | Recorrentes
```

Valor e vencimento possuem maior prioridade visual.

---

## 12.3 Nova despesa

Campos:

```text
Descrição
Valor
Vencimento
Categoria

Classificação
( ) Essencial
( ) Opcional

Tipo
( ) Avulsa
( ) Recorrente
( ) Parcelada
```

Depois:

```text
Essa despesa já aconteceu?
( ) Ainda está planejada
( ) Já foi realizada
```

Separadamente:

```text
O pagamento já foi feito?
( ) Não
( ) Sim
```

Se pago:

