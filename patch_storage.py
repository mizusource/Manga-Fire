with open('app/src/main/res/layout/activity_storage_manager.xml', 'r') as f:
    content = f.read()

content = content.replace('androidx.cardview.widget.CardView', 'com.google.android.material.card.MaterialCardView')
content = content.replace('app:cardCornerRadius="12dp"\n        app:cardElevation="4dp"\n        app:cardBackgroundColor="?attr/colorSurfaceVariant"', 'app:cardCornerRadius="16dp"\n        app:cardElevation="0dp"\n        app:strokeWidth="1dp"\n        app:strokeColor="?attr/colorOutline"\n        app:cardBackgroundColor="?attr/colorSurfaceVariant"')
content = content.replace('Storage Manager', 'إدارة التخزين')
content = content.replace('Total Space Used', 'إجمالي المساحة المستخدمة')
content = content.replace('Cache: 0 MB', 'المؤقتة: 0 MB')
content = content.replace('Downloads: 0 MB', 'التنزيلات: 0 MB')
content = content.replace('Clear Cache', 'مسح الذاكرة المؤقتة')
content = content.replace('Bulk Delete Read Chapters', 'حذف الفصول المقروءة')
content = content.replace('Clear All Downloads', 'مسح كل التنزيلات')
content = content.replace('Auto-delete after reading', 'حذف تلقائي بعد القراءة')

with open('app/src/main/res/layout/activity_storage_manager.xml', 'w') as f:
    f.write(content)
