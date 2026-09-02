import os
import re
import shutil

BASE_DIR = 'app/src/main'
SRC_DIR = os.path.join(BASE_DIR, 'java/com/fire/mangareader')

# Mappings of old directory name to new parent directory
MAPPINGS = {
    'activity': 'presentation/activity',
    'fragment': 'presentation/fragment',
    'adapter': 'presentation/adapter',
    'ui': 'presentation/ui',
    'reader': 'presentation/reader',
    'model': 'domain/model',
    'database': 'data/database',
    'network': 'data/network',
    'utils': 'util',
    'service': 'data/service'
}

PKG_MAPPINGS = {
    'com.fire.mangareader.activity': 'com.fire.mangareader.presentation.activity',
    'com.fire.mangareader.fragment': 'com.fire.mangareader.presentation.fragment',
    'com.fire.mangareader.adapter': 'com.fire.mangareader.presentation.adapter',
    'com.fire.mangareader.ui': 'com.fire.mangareader.presentation.ui',
    'com.fire.mangareader.reader': 'com.fire.mangareader.presentation.reader',
    'com.fire.mangareader.model': 'com.fire.mangareader.domain.model',
    'com.fire.mangareader.database': 'com.fire.mangareader.data.database',
    'com.fire.mangareader.network': 'com.fire.mangareader.data.network',
    'com.fire.mangareader.utils': 'com.fire.mangareader.util',
    'com.fire.mangareader.service': 'com.fire.mangareader.data.service'
}

def process_file_content(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content
    
    if filepath.endswith('.java') or filepath.endswith('.kt'):
        for old_pkg, new_pkg in PKG_MAPPINGS.items():
            # Update package declarations
            new_content = re.sub(rf'^package\s+{old_pkg}', f'package {new_pkg}', new_content, flags=re.MULTILINE)
            # Update imports
            new_content = re.sub(rf'import\s+{old_pkg}', f'import {new_pkg}', new_content)
            # Update fully qualified names in code
            new_content = new_content.replace(old_pkg + '.', new_pkg + '.')

    elif filepath.endswith('.xml'):
        for old_pkg, new_pkg in PKG_MAPPINGS.items():
            new_content = new_content.replace(old_pkg + '.', new_pkg + '.')
            
            if filepath.endswith('AndroidManifest.xml'):
                # Handle relative paths in manifest like ".activity.SplashActivity"
                short_old = old_pkg.replace('com.fire.mangareader', '')
                short_new = new_pkg.replace('com.fire.mangareader', '')
                new_content = new_content.replace(f'android:name="{short_old}.', f'android:name="{short_new}.')
                
    if content != new_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)

def main():
    # 1. Update contents
    for root, dirs, files in os.walk(BASE_DIR):
        for file in files:
            if file.endswith('.java') or file.endswith('.kt') or file.endswith('.xml'):
                filepath = os.path.join(root, file)
                process_file_content(filepath)
                
    # 2. Move directories
    for old_dir_name, new_dir_rel in MAPPINGS.items():
        old_path = os.path.join(SRC_DIR, old_dir_name)
        if not os.path.exists(old_path):
            continue
            
        new_path = os.path.join(SRC_DIR, new_dir_rel)
        new_parent = os.path.dirname(new_path)
        
        if not os.path.exists(new_parent):
            os.makedirs(new_parent, exist_ok=True)
            
        print(f"Moving {old_path} to {new_path}")
        os.rename(old_path, new_path)

if __name__ == '__main__':
    main()
