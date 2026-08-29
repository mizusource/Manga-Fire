import re

with open('app/src/main/java/com/fire/mangareader/utils/AppAdminSettings.java', 'r') as f:
    content = f.read()

# Let's just find the initialize method and replace it up to filterProfanity
start_idx = content.find('public static void initialize(Context context) {')
end_idx = content.find('public static String filterProfanity(String text) {')

fixed_init = """public static void initialize(Context context) {
        if (isInitialized) return;
        isInitialized = true;
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromLocal();
    }
    """

content = content[:start_idx] + fixed_init + content[end_idx:]

with open('app/src/main/java/com/fire/mangareader/utils/AppAdminSettings.java', 'w') as f:
    f.write(content)
