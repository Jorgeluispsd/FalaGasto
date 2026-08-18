# FalaGasto - Controle de Gastos com IA

Sistema de controle de gastos financeiros que utiliza Inteligência Artificial para processar áudios de voz, extrair informações de transações e fornecer respostas em áudio.

## 🎯 Ideia do Projeto

O projeto permite que usuários enviem arquivos de áudio descrevendo seus gastos (ex: "gastei 25 reais no ônibus"). O sistema utiliza IA para:

1. **Transcrever o áudio** - Converte fala em texto usando OpenAI Whisper
2. **Extrair informações** - Identifica valor, descrição e categoria usando GPT-4o-mini
3. **Persistir no banco** - Salva as transações no PostgreSQL
4. **Retornar em áudio** - Fornece confirmação e consultas em formato de áudio usando OpenAI TTS

## 🚀 Como Funciona

### Fluxo Principal

1. **Upload de Áudio**: O usuário envia um arquivo de áudio (.m4a, .mp3, .wav)
2. **Transcrição**: O áudio é transcrito para texto usando OpenAI Whisper
3. **Extração**: A IA analisa o texto e extrai:
   - Descrição do gasto
   - Valor monetário (BigDecimal com 2 casas decimais)
   - Categoria (FOOD, TRANSPORT, GROCERIES, etc.)
4. **Validação**: Se não houver informações de gasto, o sistema não salva no banco
5. **Persistência**: As informações são salvas no banco de dados PostgreSQL
6. **Confirmação**: O usuário recebe um áudio confirmando o gasto salvo

### Consultas em Áudio

O sistema também permite consultar as transações salvas e receber a resposta em formato de áudio, facilitando o acesso para usuários que preferem interação por voz.

## 🛠 Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 4.1.0**
- **Spring AI 2.0.0** (OpenAI Integration)
  - Whisper para transcrição de áudio
  - GPT-4o-mini para extração de informações
  - TTS para síntese de voz
- **PostgreSQL 16** (via Docker Compose)
- **Spring Data JPA** (Hibernate)
- **Lombok** (para reduzir código boilerplate)
- **Jackson** (para processamento JSON)

## 📁 Estrutura do Projeto

```
src/main/java/budgeting/
├── Application.java                 # Classe principal da aplicação
├── component/
│   └── AudioFileProcessor.java     # Processa arquivos ao startup
├── controller/
│   └── TransactionController.java # Endpoints REST
├── dto/
│   ├── request/
│   │   └── TransactionRequestDTO.java
│   └── response/
│       └── TransactionResponseDTO.java
├── enums/
│   └── Category.java               # Enum de categorias
├── model/
│   └── Transaction.java           # Entidade JPA
├── repository/
│   └── TransactionRepository.java # Repository JPA
└── service/
    ├── AudioTranscriptionService.java  # Transcrição de áudio
    ├── TransactionExtractionService.java # Extração com IA
    ├── TextToSpeechService.java        # Síntese de voz
    └── TransactionService.java         # Lógica de negócio
```

## 🗄️ Banco de Dados

O projeto utiliza PostgreSQL configurado via Docker Compose:

- **Porta**: 5433 (para evitar conflitos com outros projetos)
- **Database**: budgetingdb
- **Tabela**: transactions
  - `id` (UUID, Primary Key)
  - `description` (VARCHAR)
  - `amount` (NUMERIC(10,2))
  - `category` (VARCHAR com check constraint)

### Categorias Disponíveis

- GROCERIES
- PHARMA
- TRANSPORT
- ENTERTAINMENT
- FOOD
- SHOPPING
- OTHER

## 🔧 Configuração

### Variáveis de Ambiente (.env)

```env
Usar a API_KEY da OPENAI
E configurar o banco de dados com:
- Seu usuário e sua senha

### Docker Compose

O arquivo `compose.yml` configura o container PostgreSQL com:
- Healthcheck usando `pg_isready`
- Volume persistente para dados
- Port mapping 5433:5432

## 📡 Endpoints da API

### Criação de Transações

#### Criar via JSON
```http
POST /transactions
Content-Type: application/json

{
  "description": "Lanche",
  "amount": 25.50,
  "category": "FOOD"
}
```

#### Criar via Áudio
```http
POST /transactions/audio
Content-Type: multipart/form-data

file: [arquivo de áudio]
```
**Resposta**: Arquivo de áudio MP3 com confirmação

### Consultas

#### Listar todas (JSON)
```http
GET /transactions
```
**Resposta**: JSON com todas as transações

#### Listar todas (Áudio)
```http
GET /transactions/audio
```
**Resposta**: Arquivo de áudio MP3 lendo todas as transações

#### Buscar por ID (JSON)
```http
GET /transactions/{id}
```
**Resposta**: JSON da transação específica

#### Buscar por ID (Áudio)
```http
GET /transactions/{id}/audio
```
**Resposta**: Arquivo de áudio MP3 lendo a transação

#### Filtrar por categoria (JSON)
```http
GET /transactions/category/{category}
```
**Resposta**: JSON com transações da categoria

#### Filtrar por categoria (Áudio)
```http
GET /transactions/category/{category}/audio
```
**Resposta**: Arquivo de áudio MP3 lendo as transações da categoria

## 🎵 Processamento de Áudios

O sistema aceita arquivos de áudio dos usuários e os processa para extrair informações financeiras:

- **Formatos suportados**: .m4a, .mp3, .wav
- **Idioma**: Português (configurado no Whisper)
- **Processo**:
  1. O áudio é transcrito para texto
  2. A IA analisa se há informações de gasto
  3. Se houver gasto, extrai os dados e salva no banco
  4. Se não houver gasto, retorna mensagem informando

As transações podem ser consultadas posteriormente, e o usuário pode optar por receber as informações em formato de áudio, facilitando o acesso e compreensão dos dados.

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- Java 25
- Maven
- Docker Desktop
- Chave da API OpenAI

### Passos

1. **Clonar o projeto**
   ```bash
   git clone [url-do-repositorio]
   cd ControleDeGastos
   ```

2. **Configurar variáveis de ambiente**
   - Crie arquivo `.env` na raiz
   - Adicione suas credenciais (veja seção Configuração)

3. **Iniciar Docker Desktop**
   - Abra o Docker Desktop
   - O container PostgreSQL será iniciado automaticamente

4. **Rodar a aplicação**
   ```bash
   ./mvnw spring-boot:run
   ```
   Ou execute a classe `Application.java` no IntelliJ IDEA

5. **Testar os endpoints**
   - Use IntelliJ HTTP Client, Postman, ou cURL
   - Envie arquivos de áudio para `POST /transactions/audio`
   - Consulte as transações salvas

## 📝 Exemplo de Uso

### Enviar Áudio de Gasto

```bash
curl -X POST http://localhost:8080/transactions/audio \
  -F "file=@audio-files/Onibus_Audio.m4a" \
  --output resposta.mp3
```

### Consultar Transações

```bash
# JSON
curl http://localhost:8080/transactions

# Áudio
curl http://localhost:8080/transactions/audio --output lista.mp3
```

## 🔒 Segurança

- Credenciais do banco e API OpenAI são gerenciadas via variáveis de ambiente
- Arquivo `.env` está no `.gitignore` para não expor segredos
- Validação de entrada para prevenir injeção de dados
- Validação de gastos para evitar salvar informações irrelevantes

## 📦 Dependências Principais

- `spring-boot-starter-webmvc` - API REST
- `spring-boot-starter-data-jpa` - Persistência
- `spring-ai-starter-model-openai` - Integração OpenAI
- `postgresql` - Driver PostgreSQL
- `spring-boot-docker-compose` - Integração Docker (dev)
- `lombok` - Redução de código boilerplate

## 🤝 Contribuindo

Este é um projeto pessoal para aprendizado e demonstração de integração de IA em aplicações Spring Boot.

## 📄 Licença

Projeto desenvolvido para fins educacionais.
