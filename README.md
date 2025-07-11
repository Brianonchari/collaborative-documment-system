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

## comment-to-document location mapping
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
- `PARAGRAPH`: Refers to a specific paragraph (e.g. `"para-1"`)
- `SENTENCE`: Refers to a specific sentence (e.g. `"sent-3"`)
- `MARKER`: Refers to a predefined marker or anchor in the content (e.g. `"section-intro"`)

#### ➤ `contextReference`
Specifies the **exact target** within the document.  
Its format depends on the chosen `contextType`.

> ✅ This combination allows the system to anchor a comment to any logical unit in the document.

---

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

#### Comment on Marker
```json
{
  "text": "Revise introduction.",
  "contextType": "MARKER",
  "contextReference": "section-intro"
}

```
➡️ Targets a custom marker or anchor (e.g. "section-intro").
