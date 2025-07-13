## 🛠 Setup Instructions

### 1. Clone the Repository

```bash
git git@github.com:Brianonchari/collaborative-documment-system.git
cd collaborative-documment-system

```

### 2. Start progress with docker compose 
```bash
cd local-env
docker-compose up -d

```

### 3. Run the App
```declarative
 ./gradlew bootRun
```

## 🧠 Comment-to-Document Location Mapping

Each comment is tied to a specific location within a document using two fields:  
`contextType` and `contextReference`.

### 🔑 Mapping Structure

| Field              | Type                  | Description                                                         |
|--------------------|-----------------------|---------------------------------------------------------------------|
| `contextType`      | `PARAGRAPH` \| `SENTENCE` \| `MARKER` | Specifies the kind of location the comment refers to                |
| `contextReference` | `String`              | Holds the exact reference (e.g. paragraph ID or marker)             |

---

### 🧩 How It Works

#### ➤ `contextType`
Defines **how** to interpret the location of the comment:
- `PARAGRAPH`: Refers to a specific paragraph (e.g. `"paragrah-1"`)
- `SENTENCE`: Refers to a specific sentence (e.g. `"sentence-3"`)
- `MARKER`: Refers to a predefined marker or anchor in the content (e.g. `"section-intro"`)

#### ➤ `contextReference`
Specifies the **exact target** within the document.  
Its format depends on the chosen `contextType`.

> ✅ This combination allows the system to anchor a comment to any logical unit in the document.

---
if you want to add a comment to a specific sentence inside a specific paragraph, like "paragraph 30, sentence 5",
then you need to model `hierarchical context references`.

For SENTENCE type, the contextReference uses format:
``paragraph-{paragraphNumber}.sentence-{sentenceNumber}``
Example: "paragraph-30.sent-5"

### 📝 Example Use Cases

#### Comment on Paragraph

```json
{
  "documentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "text": "Can you edit this section?",
  "contextType": "PARAGRAPH",
  "contextReference": "para-2"
}
```
➡️ Targets paragraph 2 of the document.

#### Comment on Sentence
```json
{
  "documentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "text": "Consider rephrasing this.",
  "contextType": "SENTENCE",
  "contextReference": "sent-5"
}

```
➡️️ Targets the fifth sentence in the document.

```json
{
  "text": "Consider revising this sentence for clarity.",
  "contextType": "SENTENCE",
  "contextReference": "paragraph-30.sentence-5"
}

```
➡️️ Targets the fifth sentence in the document on paragraph 30.
#### Comment on Marker
```json
{
  "text": "Revise introduction.",
  "contextType": "MARKER",
  "contextReference": "section-intro"
}

```
➡️ Targets a custom marker or anchor (e.g. "section-intro").


## Sample API Usage - curl
create document
```bash 
curl -X POST http://localhost:8080/v1/documents \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Sample Document",
        "content": "This is the full content of the document."
      }'
```

add comment 
```bash
curl -X POST http://localhost:8080/v1/documents/{documentId}/comments \
  -H "Content-Type: application/json" \
  -d '{
        "documentId": "{documentId}",
        "text": "This is a comment on sentence 5 of paragraph 30.",
        "contextType": "SENTENCE",
        "contextReference": "para-30.sent-5"
      }'

```
