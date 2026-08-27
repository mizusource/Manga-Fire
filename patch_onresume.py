import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

onresume = '''
    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
    }
'''

if 'protected void onResume()' not in content:
    content = content.replace('protected void onCreate(Bundle savedInstanceState) {', onresume + '    protected void onCreate(Bundle savedInstanceState) {')

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
