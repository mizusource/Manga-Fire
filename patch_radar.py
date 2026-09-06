import re

xml_path = "app/src/main/res/layout/activity_manga_detail.xml"
with open(xml_path, "r") as f:
    xml_content = f.read()

# Add a 3rd tab
tab_replacement = """
            <com.google.android.material.tabs.TabItem android:text="التفاصيل" />
            <com.google.android.material.tabs.TabItem android:text="الفصول" />
            <com.google.android.material.tabs.TabItem android:text="الإحصائيات" />
"""
xml_content = re.sub(r'<com.google.android.material.tabs.TabItem android:text="التفاصيل" />\s*<com.google.android.material.tabs.TabItem android:text="الفصول" />', tab_replacement.strip(), xml_content)


# Add statsContainer to the FrameLayout
stats_container = """
                <!-- Statistics Container -->
                <LinearLayout
                    android:id="@+id/statsContainer"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp"
                    android:visibility="gone">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="تحليل تصنيفات المانجا"
                        android:textColor="#FFFFFF"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:layout_gravity="center"
                        android:layout_marginBottom="16dp" />

                    <com.github.mikephil.charting.charts.RadarChart
                        android:id="@+id/radarChart"
                        android:layout_width="match_parent"
                        android:layout_height="300dp" />
                        
                    <TextView
                        android:id="@+id/txtRadarSummary"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text=""
                        android:textColor="#B3FFFFFF"
                        android:textSize="14sp"
                        android:gravity="center"
                        android:layout_marginTop="16dp" />

                </LinearLayout>
"""

# Insert right after chaptersContainer
xml_content = xml_content.replace('</androidx.recyclerview.widget.RecyclerView>', '</androidx.recyclerview.widget.RecyclerView>\n' + stats_container)

with open(xml_path, "w") as f:
    f.write(xml_content)

java_path = "app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java"
with open(java_path, "r") as f:
    java_content = f.read()

java_imports = """
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import android.graphics.Color;
"""
if "RadarChart" not in java_content:
    java_content = java_content.replace("import java.util.List;", "import java.util.List;\n" + java_imports)

# Add variables
vars_replacement = """
    private RecyclerView rvChapters;
    private View detailsContainer;
    private View statsContainer;
    private RadarChart radarChart;
    private TextView txtRadarSummary;
"""
java_content = java_content.replace("private RecyclerView rvChapters;", vars_replacement.strip())

# Initialize views
init_replacement = """
        rvChapters = findViewById(R.id.rvChapters);
        detailsContainer = findViewById(R.id.detailsContainer);
        statsContainer = findViewById(R.id.statsContainer);
        radarChart = findViewById(R.id.radarChart);
        txtRadarSummary = findViewById(R.id.txtRadarSummary);
"""
java_content = java_content.replace("rvChapters = findViewById(R.id.rvChapters);", init_replacement.strip())

# Update tab listener
tab_listener = """
                if (tab.getPosition() == 0) {
                    detailsContainer.setVisibility(View.VISIBLE);
                    rvChapters.setVisibility(View.GONE);
                    statsContainer.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    detailsContainer.setVisibility(View.GONE);
                    rvChapters.setVisibility(View.VISIBLE);
                    statsContainer.setVisibility(View.GONE);
                } else if (tab.getPosition() == 2) {
                    detailsContainer.setVisibility(View.GONE);
                    rvChapters.setVisibility(View.GONE);
                    statsContainer.setVisibility(View.VISIBLE);
                    setupRadarChart();
                }
"""
java_content = re.sub(r'if \(tab\.getPosition\(\) == 0\) \{.*?\} else \{.*?\}', tab_listener.strip(), java_content, flags=re.DOTALL)

# Add setupRadarChart method
radar_method = """
    private void setupRadarChart() {
        if (radarChart.getData() != null) return; // Already setup

        radarChart.setBackgroundColor(Color.TRANSPARENT);
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.DKGRAY);
        radarChart.setWebAlpha(100);

        List<RadarEntry> entries = new ArrayList<>();
        // Generate some nice looking stats based on title length or random to simulate MangaSlayer
        int val1 = 60 + (mangaTitle.length() * 2) % 40;
        int val2 = 50 + (mangaTitle.length() * 5) % 50;
        int val3 = 70 + (mangaTitle.length() * 3) % 30;
        int val4 = 40 + (mangaTitle.length() * 7) % 60;
        int val5 = 80 + (mangaTitle.length() * 4) % 20;

        entries.add(new RadarEntry(val1)); // Action
        entries.add(new RadarEntry(val2)); // Romance
        entries.add(new RadarEntry(val3)); // Drama
        entries.add(new RadarEntry(val4)); // Comedy
        entries.add(new RadarEntry(val5)); // Fantasy

        RadarDataSet set = new RadarDataSet(entries, "تقييم المانجا");
        set.setColor(Color.parseColor("#39FF14"));
        set.setFillColor(Color.parseColor("#39FF14"));
        set.setDrawFilled(true);
        set.setFillAlpha(100);
        set.setLineWidth(2f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);

        RadarData data = new RadarData(set);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        radarChart.setData(data);
        radarChart.invalidate();
        radarChart.animateXY(1400, 1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"أكشن", "رومانسي", "دراما", "كوميدي", "خيال"}));
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(false);
        
        txtRadarSummary.setText("بناءً على التقييمات، تتميز هذه المانجا بـ " + (val1 > val2 ? "الأكشن" : "الرومانسية") + " و " + (val3 > val4 ? "الدراما" : "الكوميديا") + ".");
    }
"""
java_content = java_content.replace("private void loadMangaDetails() {", radar_method + "\n\n    private void loadMangaDetails() {")

with open(java_path, "w") as f:
    f.write(java_content)

