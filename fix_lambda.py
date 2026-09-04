import re

with open("app/src/main/java/com/fire/mangareader/data/network/DilarScraper.java", "r") as f:
    text = f.read()

pattern = r'(String description = extractJsonField\(jsonResponse, "summary"\);\n\s+if \(description.isEmpty\(\)\) description = "لا يوجد وصف متاح\.";)'
replacement = r'String tempDesc = extractJsonField(jsonResponse, "summary");\n                final String description = tempDesc.isEmpty() ? "لا يوجد وصف متاح." : tempDesc;'

text = re.sub(pattern, replacement, text)

with open("app/src/main/java/com/fire/mangareader/data/network/DilarScraper.java", "w") as f:
    f.write(text)
