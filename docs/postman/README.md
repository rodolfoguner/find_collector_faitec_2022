# Testes da API com Postman

Importe [Find-Collectors.postman_collection.json](Find-Collectors.postman_collection.json)
no Postman. A collection usa o formato v2.1 e documenta os 17 endpoints do
backend atual, além de saúde, informações do Actuator e OpenAPI. Cada request
contém descrição, parâmetros, validações, status HTTP, erros esperados, exemplo
de resposta e teste do status de sucesso.

## Configuração

Com a API em execução, abra **Find Collectors → Variables**:

| Variável | Preenchimento |
| --- | --- |
| `baseUrl` | Padrão `http://localhost:8080`; ajuste a porta/servidor se necessário |
| `loginEmail` | E-mail de uma conta existente, preenchido manualmente |
| `loginPassword` | Senha da conta, preenchida manualmente |
| `signupName`, `signupPersonType` | Nome e tipo usados somente ao cadastrar uma conta |
| `personId` | Pessoa consultada, editada ou excluída; o cadastro salva este ID |
| `authenticatedPersonId` | ID do usuário autenticado; salvo por **Minha conta** |
| `recyclerId` | Pessoa `RECYCLER` usada ao criar/consultar coletas |
| `collectorId` | Pessoa `COLLECTOR` usada ao consultar coletas aceitas |
| `collectId` | Coleta consultada, editada ou excluída; salvo ao criar coleta |

Não precisa criar environment. Configuração de URL, credenciais e tokens é
lida das variáveis da collection; os scripts colocam os valores necessários
no escopo local da request para evitar sobreposição por um environment.
IDs podem ser preenchidos manualmente ou sobrepostos pelo environment.
**Cadastrar conta** e **Minha conta** preenchem `recyclerId` ou `collectorId`
conforme o tipo retornado, somente se a variável ainda estiver vazia.

O arquivo versionado deixa credenciais e tokens vazios. Preencha os valores
localmente no Postman e não compartilhe nem versione exports com dados reais.

## Autenticação automática

A autenticação **Bearer Token** pertence à collection e os endpoints protegidos
usam **Inherit auth from parent**. Os endpoints públicos usam **No Auth**.
O script **Pre-request** da collection verifica a autenticação antes de cada
request:

1. Sem tokens utilizáveis, faz login com `loginEmail`/`loginPassword`.
2. Com access válido por mais de 30 segundos, reutiliza o token.
3. Perto da expiração, consome o refresh e salva o novo par de tokens.
4. Se o refresh retorna `401`, limpa os tokens e tenta um novo login uma vez.
5. Se a autenticação falha, interrompe o request principal com
   `pm.execution.skipRequest()` e informa a falha sem registrar os tokens.

As requests explícitas **Login** e **Renovar tokens** são opcionais. O script
não consome o refresh antes da request explícita de renovação; se não houver
refresh utilizável, faz login para obtê-lo. O script **Post-response** da
collection também salva tokens de login/refresh manuais e limpa tokens após
`401` ou logout bem-sucedido. Um `401` do request principal não provoca reenvio;
o próximo envio tentará autenticar novamente.

Alterar `baseUrl` ou `loginEmail` invalida os tokens locais automaticamente.
Ao trocar de usuário, ajuste também os IDs. Se alterar apenas a senha e quiser
forçar login, limpe `accessToken`, `accessTokenExpiresAt`, `refreshToken` e
`refreshTokenExpiresAt` nas variáveis da collection.

O backend mantém sessões no PostgreSQL. Access tem duração padrão de 15
minutos; refresh/sessão têm prazo absoluto padrão de 7 dias. Renovação não
estende esse prazo. Refresh é de uso único: reutilização revoga a sessão
inteira. Execute os requests sequencialmente e evite concorrência entre abas
ou runners que compartilhem tokens. Uma resposta de refresh perdida seguida
de repetição pode exigir novo login.

O fluxo usa as APIs oficiais do sandbox:
[pm.sendRequest](https://learning.postman.com/v11/docs/tests-and-scripts/write-scripts/postman-sandbox-reference/pm-send-request)
e [pm.execution.skipRequest](https://learning.postman.com/latest-v-12/docs/tests-and-scripts/write-scripts/postman-sandbox-reference/pm-execution).
Use uma versão atual do Postman com suporte a `await pm.sendRequest` e
`pm.execution.skipRequest`.

## Sequência sugerida

1. Execute **Saúde** para verificar a API.
2. Se não tiver conta, preencha as credenciais e execute **Cadastrar conta**.
   A senha deve ter de 8 a 100 caracteres, pelo menos um número e uma maiúscula.
3. Execute **Minha conta**. O login ocorrerá automaticamente.
4. Preencha `personId` para consultar/editar uma pessoa.
5. Para criar coleta, informe `recyclerId` de uma pessoa `RECYCLER` existente.
   **Criar coleta** salva `collectId` para as operações seguintes.
6. Execute consultas, atualizações e exclusões conforme o teste desejado.

No Collection Runner, selecione as operações desejadas. A collection inteira
contém cadastro, atualizações, exclusões e logout, que alteram dados e podem
não fazer sentido em uma única execução. Os exemplos usam IDs fictícios e não
criam dados automaticamente. Login automático não preenche todos os IDs;
execute **Minha conta** ou configure-os antes de requests que precisam deles.

## Contratos e limites atuais

As descrições seguem os controllers, DTOs, mappers, use cases e consultas do
backend em `find-collectors/`, incluindo alterações atuais de sessões e logout.
Criação de coleta retorna `200`, cadastro retorna `201` e exclusões/logout
retornam `204`. Listas não têm paginação ou filtros gerais. `collectPoint` é
aceito na atualização de pessoa, mas não está em `PersonResponse`.

`free-collects/{id}` lista `PENDING` de outros recicladores;
`my-collects/{id}` lista coletas do reciclador exceto `COMPLETED`, inclusive
`CANCELLED`; `accepted-collects/{id}` lista `ACCEPTED` do coletor informado.
Não existem endpoints de aceitação, conclusão ou cancelamento. Excluir coleta
remove o registro. O backend ainda não restringe operações por proprietário
ou tipo do usuário autenticado.

Para testar autenticação ausente, tokens expirados/revogados, reutilização de
refresh ou o `401` após logout, duplique a collection e desabilite os scripts
de autenticação nessa cópia. Configure o Bearer/No Auth e o body manualmente.
Na collection original, o próximo request protegido após logout faz novo login.

Consulte também o [ambiente local](../development/local-setup.md), a
[arquitetura](../architecture/overview.md) e o OpenAPI servido pela aplicação
em `/v3/api-docs/find-collectors-api`.
