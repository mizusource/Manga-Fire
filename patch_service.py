import os

filepath = 'app/src/main/java/com/fire/mangareader/service/DownloadService.java'
with open(filepath, 'r') as f:
    content = f.read()

replacement = """        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(task.notifId, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(task.notifId, notification);
        }"""

content = content.replace("startForeground(task.notifId, notification);", replacement)

with open(filepath, 'w') as f:
    f.write(content)
