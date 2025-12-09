# Legal Judgment Analysis Pipeline

## 1. Project Overview
The **Legal Judgment Analysis Pipeline** is a Proof of Concept (POC) designed to demonstrate the capabilities of Retrieval-Augmented Generation (RAG) in the legal technology domain.

This system ingests PDF legal judgment documents, extracts and analyzes text using Google's Gemini AI, and creates a searchable knowledge base. It allows legal professionals to perform semantic searches over processed judgments and receive AI-generated answers grounded in the source text, significantly reducing the time required for case law research.

## 2. Architecture & Workflow
The pipeline follows a structured data flow:

1.  **Ingestion & Extraction:**
    *   PDF documents are uploaded via the REST API or UI.
    *   Text is extracted using **Apache PDFBox**.
2.  **AI Metadata Extraction:**
    *   Raw text is sent to **Google Gemini Pro** to identify key metadata (Case Title, Court, Date, Legal Principles).
    *   Metadata is stored in **MongoDB** for structured querying.
3.  **Vector Embedding:**
    *   The judgment text is chunked and converted into vector embeddings.
    *   Embeddings are stored in **Qdrant** (Vector Database) for similarity search.
4.  **Retrieval (RAG):**
    *   User queries are converted to vectors.
    *   The system performs a semantic search in Qdrant to find relevant text chunks.
5.  **Generation:**
    *   Relevant chunks + User Query are sent to **Gemini Pro**.
    *   The AI generates a context-aware answer.

## 3. Key Features
*   **Automated Document Analysis:** Instantly processes complex legal PDFs to extract structured data.
*   **Semantic Search:** Finds relevant information by meaning, not just keyword matching.
*   **AI-Powered Q&A:** Generates human-readable answers to legal questions based on uploaded case files.
*   **Hybrid Storage:** Utilizes MongoDB for document metadata and Qdrant for vector embeddings.
*   **Interactive UI:** Simple, chat-based web interface for uploading files and asking questions.

## 4. Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.4.0 | Core application framework. |
| **AI / LLM** | LangChain4j, Google Gemini Pro | Orchestration and Large Language Model. |
| **Vector DB** | Qdrant | High-performance vector similarity search engine. |
| **Database** | MongoDB | Document store for structured metadata. |
| **PDF Processing** | Apache PDFBox | Library for parsing PDF documents. |
| **Frontend** | HTML5, JavaScript, CSS3 | Lightweight web interface. |
| **Infrastructure** | Docker, Docker Compose | Containerization of databases. |

## 5. Prerequisites
Ensure the following are installed on your system:
*   **Java 17 Development Kit (JDK)**
*   **Docker & Docker Compose** (for running databases)
*   **Maven** (for building the project)

## 6. Configuration
**CRITICAL STEP:** You must configure your Google Gemini API key before running the application.

1.  Navigate to `src/main/resources/`.
2.  Create a new file named `secret.properties`.
3.  Add your API key in the following format:
    ```properties
    gemini.api.key=YOUR_ACTUAL_API_KEY_HERE
    ```
    > **Note:** The `secret.properties` file is included in `.gitignore` to prevent accidental exposure of credentials.

## 7. Installation & Running

### Step 1: Clone the Repository
```bash
git clone <repository_url>
cd assesment-pipeline
```

### Step 2: Start Infrastructure
Start the MongoDB and Qdrant containers using Docker Compose:
```bash
docker compose up -d
```
*Verify containers are running:* `docker ps`

### Step 3: Run the Application
Use the Maven wrapper to start the Spring Boot application:
```bash
./mvnw spring-boot:run
```
The application will start on port **8087**.

### Step 4: Access the Application
*   **Web UI:** Open [http://localhost:8087/index.html](http://localhost:8087/index.html) in your browser.

## 8. API Documentation

### Extract & Index Document
Uploads a PDF for analysis, metadata extraction, and vector indexing.

*   **Endpoint:** `POST /api/v1/judgment/extract`
*   **Consumes:** `multipart/form-data`
*   **Parameters:**
    *   `file` (Required): The PDF file to be processed.

### Semantic Search
Performs a RAG-based search on the indexed documents.

*   **Endpoint:** `GET /api/v1/judgment/search`
*   **Parameters:**
    *   `query` (Required): The natural language legal question (e.g., "What was the final verdict?").
*   **Response:** JSON object containing the AI-generated answer.

## 9. Project Structure
```text
src/main/java/com/example/urooz
├── controller
│   └── JudgmentController.java  // REST API endpoints
├── model
│   └── JudgmentMetadata.java    // Data entity
├── repository
│   └── JudgmentRepository.java  // MongoDB repository
└── service
    ├── PdfExtractionService.java // PDF parsing
    ├── LlmExtractionService.java // Gemini metadata extraction
    ├── VectorStoreService.java   // Qdrant embedding storage
    ├── SearchService.java        // RAG retrieval logic
    └── AnswerGeneratorService.java // Final answer generation
```

## 10. Troubleshooting

### Connection Refused (MongoDB/Qdrant)
*   **Error:** `com.mongodb.MongoSocketOpenException` or similar connection errors.
*   **Solution:** Ensure Docker containers are running (`docker ps`). If not, run `docker compose up -d` again.

### API Key Missing
*   **Error:** `java.lang.IllegalArgumentException: key must not be null`
*   **Solution:** Verify that `src/main/resources/secret.properties` exists and contains the `gemini.api.key` property. Ensure the filename is exact.
