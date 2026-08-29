import re

with open('app/src/main/java/com/fire/mangareader/utils/AppAdminSettings.java', 'r') as f:
    content = f.read()

# Let's fix the initialize method
init_method_pattern = re.compile(r'public static void initialize\(Context context\) \{.*?\}\n    public static String filterProfanity', re.DOTALL)

fixed_init = """public static void initialize(Context context) {
        if (isInitialized) return;
        isInitialized = true;
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromLocal();
        // Since we moved from Firebase to Supabase, we skip Firebase DB init for now
    }
    public static String filterProfanity"""

content = init_method_pattern.sub(fixed_init, content)

with open('app/src/main/java/com/fire/mangareader/utils/AppAdminSettings.java', 'w') as f:
    f.write(content)
