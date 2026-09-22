# Finanças TCC — UX/UI Fase 2 — Decisões Globais de Design

**Versão:** 1.0  
**Status:** Aprovado como base visual global da Fase 2  
**Data:** Agosto de 2026  
**Projeto:** Finanças TCC  

---

## 1. Finalidade

Este documento registra somente as **decisões visuais globais já aprovadas** para a Fase 2 de UX/UI do projeto Finanças TCC.

Ele não substitui a **UX/UI Fase 1**, que continua sendo a fonte oficial para fluxos, arquitetura de informação, navegação e comportamentos já aprovados.

As decisões específicas de cada tela serão tomadas e revisadas **uma tela por vez**, durante a execução no Figma.

---

## 2. Método de trabalho aprovado

A Fase 2 seguirá o fluxo:

1. escolher uma tela;
2. preparar um pedido objetivo para o ChatGPT Work;
3. executar a tela no arquivo Figma existente;
4. trazer o resultado/print para este chat;
5. revisar visualmente;
6. corrigir, se necessário;
7. aprovar a tela;
8. seguir para a próxima.

Não será feito planejamento visual detalhado de todas as telas antes da execução.

O **Work executa**; este chat **decide e revisa**.

---

## 3. Referências visuais aprovadas

As seguintes referências foram aprovadas:

### shadcn/ui
Referência principal para:
- linguagem visual;
- componentes;
- formulários;
- estados;
- estrutura de design system.

### TailAdmin
Referência para:
- layout de dashboard;
- sidebar;
- cards;
- organização de conteúdo;
- padrões responsivos.

### Tremor
Referência para:
- gráficos;
- métricas;
- visualização de dados financeiros.

Essas referências devem ser **adaptadas à identidade própria do Finanças TCC**, sem cópia cega.

---

## 4. Direção visual

A identidade visual deve ser:

- clean;
- moderna;
- financeira;
- visualmente calma;
- organizada;
- clara;
- sem aparência pesada de ERP;
- sem aparência de ferramenta de BI;
- orientada à tomada de decisão do usuário.

A interface deve priorizar valores financeiros e ações principais, deixando informações secundárias visualmente mais discretas.

---

## 5. Tipografia

### Fonte principal
**Geist Sans**

### Pesos recomendados

| Uso | Peso |
|---|---|
| Título de página | 600–700 |
| Título de seção/card | 600 |
| Valores financeiros principais | 600–700 |
| Texto normal | 400 |
| Labels e informações secundárias | 400–500 |
| Botões | 500–600 |

Para valores financeiros e métricas, utilizar **algarismos tabulares (`tabular-nums`)** quando apropriado.

---

## 6. Paleta global

### Cores principais

| Token | Cor |
|---|---|
| Primary | `#2563EB` |
| Primary Hover | `#1D4ED8` |
| Background | `#F8FAFC` |
| Surface Primary | `#FFFFFF` |
| Surface Subtle | `#F1F5F9` |
| Text Primary | `#0F172A` |
| Text Secondary | `#64748B` |
| Border | `#E2E8F0` |

### Cores semânticas

| Estado | Cor base |
|---|---|
| Positive | `#16A34A` |
| Warning | `#D97706` |
| Negative | `#DC2626` |
| Informational | `#0284C7` |

O branco permanece como **superfície principal**.  
`#F1F5F9` é utilizado como **superfície secundária/subtle**, e não como substituto universal do branco.

---

## 7. Geometria e profundidade

### Radius
- cards: `8px`;
- inputs: `8px`;
- selects: `8px`;
- botões: `8px`;
- badges pill: `999px`.

### Bordas
`1px solid #E2E8F0`

### Sombra de cards
Sombra suave e discreta, combinada com borda.

Referência:

```css
0 1px 3px rgba(15, 23, 42, 0.06)
```

Não utilizar sombras pesadas.

### Espaçamento
Sistema baseado em múltiplos de `4px`.

Escala recorrente:

```text
8 / 12 / 16 / 24 / 32px
```

Cards:
- desktop: aproximadamente `20–24px`;
- mobile: aproximadamente `16px`.

---

## 8. Controles e responsividade

### Altura de Button / Input / Select

| Contexto | Altura |
|---|---:|
| Desktop / Tablet | `40px` |
| Mobile | `48px` |

Radius permanece em `8px`.

A adaptação mobile deve priorizar conforto ao toque sem aumentar desnecessariamente a densidade vertical no desktop.

---

## 9. Componentes-base aprovados

### 9.1 Button

Variantes:

- Primary;
- Secondary;
- Ghost;
- Destructive;
- Disabled;
- Loading.

Regras:
- Primary usa `#2563EB`;
- hover usa `#1D4ED8`;
- Secondary usa superfície clara + borda;
- Destructive utiliza semântica negativa;
- estado de loading bloqueia repetição da ação.

---

### 9.2 Input / Select / Textarea

Regras:
- label visível;
- placeholder apenas como apoio;
- background principal `#FFFFFF`;
- borda `#E2E8F0`;
- foco visível em azul;
- erro deve usar cor + mensagem textual;
- estados disabled e error devem ser distinguíveis.

---

### 9.3 Card

Regras:
- `8px` de radius;
- `#FFFFFF`;
- borda sutil;
- sombra suave;
- padding responsivo;
- card neutro por padrão.

Cores semânticas não devem dominar o fundo completo de cards financeiros sem necessidade.

---

## 10. Badge / Status

Variantes:

- Positive;
- Warning;
- Negative;
- Info;
- Neutral.

### Combinações acessíveis aprovadas

| Variante | Fundo | Texto |
|---|---|---|
| Positive | `#F0FDF4` | `#166534` |
| Warning | `#FFFBEB` | `#92400E` |
| Negative | `#FEF2F2` | `#991B1B` |
| Info | `#F0F9FF` | `#075985` |
| Neutral | `#F8FAFC` | `#475569` |

Estilo:
- altura aproximada: `24px`;
- padding horizontal: `8px`;
- radius: `999px`;
- fonte: aproximadamente `12px / 500`;
- ícone opcional.

Estados importantes nunca devem depender apenas da cor.

---

## 11. Alert

Variantes visuais:

- Info;
- Warning;
- Negative;
- Positive.

Estrutura:
- fundo semântico claro;
- borda sutil;
- ícone;
- título curto;
- descrição objetiva;
- ação opcional;
- radius `8px`;
- padding aproximadamente `16px`.

Um único componente será reutilizado para diferentes tipos de alerta.

---

## 12. Progress

Uso principal:
- metas;
- orçamento por categoria;
- reservas;
- outros progressos financeiros quando aplicável.

Regras:
- altura aproximada: `8px`;
- trilho `#E2E8F0`;
- radius `999px`;
- azul para progresso neutro;
- verde para conclusão/positivo;
- amarelo para atenção;
- vermelho para excesso/negativo;
- sempre acompanhado de texto, valor ou percentual.

Quando um orçamento ultrapassar 100%, mostrar explicitamente o excesso; não comunicar apenas pela barra.

---

## 13. Tabs

Regras:
- altura aproximada: `36–40px`;
- texto em torno de `14px / 500`;
- aba ativa com destaque azul e indicador inferior;
- abas inativas em tom secundário;
- evitar visual de grandes botões preenchidos;
- permitir scroll horizontal no mobile quando necessário.

---

## 14. Dropdown / Menu de ações

Uso para ações secundárias, por exemplo:
- editar;
- arquivar;
- ajustar;
- encerrar;
- excluir quando permitido.

Regras:
- acionador normalmente por menu de contexto;
- ação destrutiva visualmente separada;
- itens com altura adequada ao dispositivo;
- ações principais da tela não devem ser escondidas no menu.

---

## 15. Dialog de confirmação

Utilizado em ações destrutivas ou sensíveis.

Estrutura:
- título direto;
- explicação da consequência;
- ação de cancelar;
- ação destrutiva claramente identificada;
- radius `8px`;
- adaptação adequada ao mobile.

---

## 16. Tooltip

Regras:
- fundo `#0F172A`;
- texto branco;
- fonte aproximada `12–13px`;
- radius aproximado `6px`;
- padding aproximado `6px 8px`;
- largura curta;
- disponível em hover/focus.

Informação essencial não pode existir apenas dentro de tooltip.

---

## 17. Skeleton

Regras:
- utilizar tons neutros próximos de `#E2E8F0`;
- preservar aproximadamente a estrutura do conteúdo final;
- animação discreta;
- evitar spinners grandes no Dashboard quando skeleton for mais apropriado.

---

## 18. Empty State

Estrutura padrão:

1. título curto;
2. explicação objetiva;
3. ação principal quando existir.

Exemplo conceitual:

```text
Nenhum cartão cadastrado

Adicione seu primeiro cartão para acompanhar compras e faturas.

[ Adicionar cartão ]
```

Evitar ilustrações grandes ou excesso de conteúdo decorativo.

---

## 19. Acessibilidade global

A interface deve:

- manter contraste adequado;
- não depender apenas de cor para comunicar estados;
- utilizar texto e/ou ícone em estados relevantes;
- possuir foco visível;
- manter labels claras;
- permitir leitura confortável de valores financeiros;
- considerar navegação por teclado;
- manter áreas de toque adequadas no mobile.

---

## 20. O que este documento NÃO define

Este documento não define:

- layout final de todas as telas;
- conteúdo específico de cada tela;
- posição de cada botão em cada módulo;
- fluxos de navegação;
- regras financeiras;
- arquitetura de informação;
- formulários completos de todas as funcionalidades.

Esses pontos permanecem nas fontes oficiais correspondentes ou serão decididos **tela por tela**.

---

## 21. Próxima etapa

A partir desta base global, a Fase 2 passa a trabalhar por execução e revisão de telas individuais.

Primeira tela prevista:

> **Login**

Após aprovação da tela de Login, seguir para a próxima tela definida no fluxo oficial, sem antecipar decisões visuais desnecessárias de telas futuras.

---

## 22. Status

As decisões globais registradas neste documento estão **aprovadas**.

Mudanças relevantes nessa base devem ser registradas explicitamente em nova versão deste documento, evitando alterações silenciosas.
