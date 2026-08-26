with open("app/src/main/res/layout/activity_chapter_reader.xml", "r") as f:
    content = f.read()

timer_xml = """
        <!-- عداد وقت القراءة -->
        <TextView
            android:id="@+id/tvReadingTimer"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="top|center_horizontal"
            android:layout_marginTop="24dp"
            android:background="#A6000000"
            android:paddingStart="12dp"
            android:paddingEnd="12dp"
            android:paddingTop="6dp"
            android:paddingBottom="6dp"
            android:text="00:00"
            android:textColor="#FFFFFF"
            android:textSize="14sp"
            android:textStyle="bold"
            android:elevation="4dp"
            android:visibility="visible" />
"""

content = content.replace('android:visibility="gone" />', 'android:visibility="gone" />\n' + timer_xml, 1)

with open("app/src/main/res/layout/activity_chapter_reader.xml", "w") as f:
    f.write(content)


with open("app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

timer_vars = """
    private android.widget.TextView tvReadingTimer;
    private long readingStartTime;
    private android.os.Handler timerHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - readingStartTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            if (tvReadingTimer != null) {
                tvReadingTimer.setText(String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };
"""

content = content.replace("public class ChapterReaderActivity extends AppCompatActivity {", "public class ChapterReaderActivity extends AppCompatActivity {" + timer_vars)

init_timer = """
        tvReadingTimer = findViewById(R.id.tvReadingTimer);
        readingStartTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 1000);
"""

content = content.replace("setContentView(R.layout.activity_chapter_reader);", "setContentView(R.layout.activity_chapter_reader);\n" + init_timer)

destroy_timer = """
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
"""

content = content.replace("super.onDestroy();", destroy_timer + "\n        super.onDestroy();")

with open("app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)

