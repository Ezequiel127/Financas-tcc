# Finanças TCC — Referências Visuais e Profissionais

## Objetivo

Este documento consolida as principais referências utilizadas para orientar o design visual do **Finanças TCC** durante a Fase 2 de UX/UI.

Ele reúne:

- as três referências de produto/interface adotadas pelo projeto;
- uma alternativa complementar já analisada;
- cinco referências profissionais usadas como padrão de qualidade e processo;
- a forma correta de combinar essas influências sem copiar interfaces prontas.

> Este arquivo é complementar ao documento oficial de decisões globais da Fase 2. Ele não substitui requisitos, fluxos, arquitetura de informação ou decisões funcionais já aprovadas.

---

# 1. Referências de produto e interface

## Visão geral

| Referência | Melhor uso no projeto | Stack / base | Licença | Papel no Finanças TCC |
|---|---|---|---|---|
| **shadcn/ui** | Base visual e componentes | React + Tailwind, excelente encaixe com Next.js | MIT | **Fundação visual principal** |
| **TailAdmin** | Layout, sidebar, cards e dashboards financeiros | Next.js + React + TypeScript + Tailwind | MIT na versão Free | **Referência de layout** |
| **Tremor** | Gráficos, métricas e visualização financeira | React + Tailwind + Radix | Apache 2.0 | **Referência especializada em dados** |
| **Flowbite** | Componentes e padrões generalistas | Tailwind + ecossistema React/Next.js | MIT | **Alternativa complementar, não principal** |

---

## 1.1 shadcn/ui — fundação visual principal

O **shadcn/ui** é a principal referência para a linguagem visual do Finanças TCC.

A ideia não é copiar uma aplicação pronta, mas aproveitar a lógica de componentes reutilizáveis e adaptáveis para construir uma identidade própria.

### Aplicações no projeto

Usar como referência para:

- inputs;
- selects;
- formulários;
- botões;
- badges;
- dialogs;
- dropdowns;
- sheets;
- sidebar;
- cards;
- estados de foco;
- radius;
- tokens;
- estrutura de componentes;
- dark mode futuramente.

### Por que é a principal referência

A linguagem do shadcn/ui combina com a proposta do Finanças TCC:

- interface limpa;
- baixa poluição visual;
- componentes fáceis de adaptar;
- bom encaixe com React, Next.js e Tailwind;
- facilidade para construir um design system próprio.

**Papel definido no projeto:**

> **shadcn/ui = fundação visual e de componentes.**

---

## 1.2 TailAdmin — referência principal de layout

O **TailAdmin** é usado principalmente como inspiração para a estrutura das telas autenticadas.

Não deve ser adotado integralmente como design system, pois tende a possuir maior densidade típica de sistemas administrativos e dashboards corporativos.

### Aplicações no projeto

Usar como referência para:

- sidebar;
- header;
- grid;
- distribuição das áreas da página;
- espaçamento entre seções;
- cards financeiros;
- listas;
- dashboards;
- comportamento responsivo;
- organização desktop.

### Limite de uso

O Finanças TCC não deve parecer um ERP ou painel administrativo pesado.

Devemos aproveitar a organização estrutural do TailAdmin e reduzir sua densidade para manter uma experiência de finanças pessoais simples e orientada à decisão.

**Papel definido no projeto:**

> **TailAdmin = inspiração de layout e dashboard financeiro.**

---

## 1.3 Tremor — referência para dados e gráficos

O **Tremor** é utilizado de maneira especializada.

Sua função é orientar como apresentar informações financeiras e temporais sem transformar o produto em uma ferramenta de Business Intelligence.

### Aplicações no projeto

Usar como referência para:

- gráfico de **Receitas x Despesas**;
- projeções financeiras;
- indicadores positivos;
- indicadores de atenção;
- indicadores negativos;
- tooltips;
- legendas;
- visualização temporal;
- métricas compactas.

### Limite de uso

O Tremor não deve definir toda a identidade da aplicação.

A visualização de dados deve servir às decisões financeiras do usuário, e não dominar a experiência.

**Papel definido no projeto:**

> **Tremor = inspiração especializada para visualização de dados.**

---

## 1.4 Flowbite — alternativa complementar

O **Flowbite** também foi analisado.

Ele possui uma base ampla de componentes e bom ecossistema para Tailwind e Figma, mas sua linguagem tende a ser mais generalista e corporativa.

Por isso, fica como uma **quarta referência opcional**, útil apenas quando houver um padrão específico que não esteja bem resolvido pelas três referências principais.

Não deve competir com:

1. shadcn/ui;
2. TailAdmin;
3. Tremor.

---

# 2. Referências profissionais de design

Essas pessoas não são referências para copiar telas ou reproduzir estilos pessoais.

Elas funcionam como **padrões de qualidade, processo, refinamento e pensamento de produto**.

---

## 2.1 Jesse Showalter

### Principal força

**Workflow avançado de UI/UX, Figma, componentes, Auto Layout, design systems e prototipação.**

### O que aproveitar no Finanças TCC

- organização profissional do arquivo Figma;
- uso correto de Auto Layout;
- componentes reutilizáveis;
- variantes;
- protótipos bem estruturados;
- fluxo de trabalho próximo de equipes reais de produto;
- construção de interfaces que possam evoluir sem virar um arquivo desorganizado.

### Aplicação prática

Quando uma tela for criada, ela não deve ser apenas visualmente bonita.

Ela deve ser:

- editável;
- estruturada;
- reutilizável;
- compreensível;
- pronta para servir como referência na implementação.

---

## 2.2 Adham Dannaway

### Principal força

**Clareza visual, consistência de interface e construção de sistemas de design robustos e escaláveis.**

### O que aproveitar no Finanças TCC

- consistência entre telas;
- padrões repetíveis;
- hierarquia visual clara;
- simplicidade;
- coerência entre componentes;
- redução de decisões visuais arbitrárias.

### Aplicação prática

Uma tela nova não deve parecer criada isoladamente.

Ela deve parecer parte do mesmo produto, utilizando:

- os mesmos padrões;
- a mesma lógica de espaçamento;
- a mesma geometria;
- a mesma linguagem de componentes.

---

## 2.3 Ioana Teleanu — UX Goodies

### Principal força

**Product thinking, UX contemporâneo, processo de design e uso estratégico de IA no trabalho de produto.**

### O que aproveitar no Finanças TCC

- pensar primeiro no problema do usuário;
- evitar elementos sem função;
- utilizar IA como apoio e não como substituta do processo de design;
- conectar UX, produto e objetivos reais;
- projetar experiências modernas sem adicionar complexidade desnecessária.

### Aplicação prática

Antes de decidir um componente ou interação, perguntar:

> **Isso ajuda o usuário a entender sua situação financeira ou tomar uma decisão melhor?**

Se a resposta for não, provavelmente não precisa estar na tela.

---

## 2.4 Zander Whitehurst

### Principal força

**Processo claro de design, execução profissional e criação de trabalhos compatíveis com cenários reais de mercado e portfólio.**

### O que aproveitar no Finanças TCC

- trabalhar com processo;
- justificar decisões;
- organizar entregáveis;
- manter qualidade de apresentação;
- construir um projeto que também possa ser apresentado profissionalmente;
- aproximar o TCC de um produto real.

### Aplicação prática

O Figma do Finanças TCC deve ser compreensível não apenas para quem criou, mas também para:

- avaliadores do TCC;
- desenvolvedores;
- recrutadores;
- outras pessoas analisando o portfólio.

---

## 2.5 Steve Schoger

### Principal força

**Refinamento visual de interfaces: hierarquia, espaçamento, tipografia, contraste e acabamento.**

### O que aproveitar no Finanças TCC

- hierarquia tipográfica;
- espaçamentos precisos;
- agrupamento visual;
- contraste;
- alinhamento;
- proporção;
- uso moderado de bordas e sombras;
- percepção de acabamento profissional.

### Aplicação prática

Quando uma tela estiver funcional, ainda devemos revisar:

- o que chama atenção primeiro;
- se elementos secundários estão realmente secundários;
- se textos estão legíveis;
- se existem espaçamentos inconsistentes;
- se os cards têm peso visual correto;
- se a tela parece refinada ou apenas “montada”.

---

# 3. Como combinar todas as referências

A combinação oficial de referências deve ser entendida desta forma:

> **shadcn/ui = fundação visual e componentes**  
> **TailAdmin = estrutura e layout financeiro**  
> **Tremor = dados e gráficos**  
> **Jesse Showalter = qualidade de execução no Figma**  
> **Adham Dannaway = consistência e design system**  
> **Ioana Teleanu = pensamento de produto e UX**  
> **Zander Whitehurst = processo e padrão profissional**  
> **Steve Schoger = refinamento visual**

Nenhuma dessas referências deve ser seguida isoladamente.

O objetivo é construir uma identidade própria para o **Finanças TCC**.

---

# 4. Critérios de qualidade resultantes

Toda tela do projeto deve buscar os seguintes critérios.

## Estrutura

- grid claro;
- alinhamentos consistentes;
- proporções equilibradas;
- uso correto do espaço em branco;
- Auto Layout sempre que fizer sentido.

## Hierarquia

- ação ou informação principal identificável rapidamente;
- títulos claramente diferenciados;
- valores financeiros prioritários visualmente;
- informações secundárias discretas, mas legíveis.

## Componentes

- reutilizáveis;
- consistentes;
- simples;
- editáveis;
- preparados para implementação futura.

## Tipografia

- legível em uso real;
- hierarquia explícita;
- sem textos excessivamente pequenos para economizar espaço;
- pesos usados com intenção.

## Visual financeiro

- transmitir confiança;
- transmitir organização;
- evitar aparência de banco tradicional excessivamente institucional;
- evitar aparência genérica de painel administrativo;
- evitar estética de Business Intelligence.

## Produto

Cada elemento deve responder a pelo menos uma destas funções:

- informar;
- orientar;
- permitir uma ação;
- mostrar estado;
- ajudar uma decisão financeira.

Elementos puramente decorativos devem ser utilizados com moderação.

---

# 5. Regra de aplicação nas telas do Finanças TCC

Ao criar ou revisar uma tela:

1. respeitar primeiro os requisitos e fluxos aprovados;
2. utilizar o design system global já definido;
3. usar shadcn/ui como base de componentes;
4. recorrer ao TailAdmin para decisões de layout;
5. recorrer ao Tremor somente quando houver visualização de dados;
6. revisar organização e reutilização com a mentalidade de Jesse Showalter;
7. verificar consistência como um design system com a referência de Adham Dannaway;
8. revisar a utilidade da experiência com pensamento de produto inspirado em Ioana Teleanu;
9. avaliar se a entrega possui padrão profissional e de portfólio, como referência de Zander Whitehurst;
10. fazer uma última revisão de hierarquia, espaçamento, tipografia e acabamento inspirada no trabalho de Steve Schoger.

---

# 6. Princípio final

O Finanças TCC não deve parecer:

- uma cópia do shadcn/ui;
- um template TailAdmin recolorido;
- um dashboard Tremor;
- uma reprodução do estilo de um designer específico.

O resultado esperado é:

> **um produto financeiro próprio, limpo, moderno, compreensível e consistente, construído sobre boas referências de mercado e boas práticas profissionais de design.**
