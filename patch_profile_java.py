import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ProfileActivity.java", "r") as f:
    text = f.read()

# Add imports for MPAndroidChart
imports = """
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import android.graphics.Color;
import java.util.ArrayList;
"""
text = text.replace("import com.fire.mangareader.util.DonutChartView;", imports)

# Replace DonutChartView declaration
text = text.replace("private DonutChartView donutChart;", "private PieChart donutChart;")

# Replace initialization
# donutChart = findViewById(R.id.donutChart); -> keep it.

# Replace setData
setData_pattern = r'donutChart\.setData\(favCount, \(readCount > 0 \? 1 : 0\), planCount, compCount, dropCount\);'
setData_code = """
                ArrayList<PieEntry> entries = new ArrayList<>();
                if (favCount > 0) entries.add(new PieEntry(favCount, "المفضلة"));
                int readMangaCount = readCount > 0 ? 1 : 0;
                if (readMangaCount > 0) entries.add(new PieEntry(readMangaCount, "أشاهدها"));
                if (planCount > 0) entries.add(new PieEntry(planCount, "أرغب"));
                if (compCount > 0) entries.add(new PieEntry(compCount, "مكتمل"));
                if (dropCount > 0) entries.add(new PieEntry(dropCount, "مسقط"));
                
                if (entries.isEmpty()) {
                    entries.add(new PieEntry(1, "لا يوجد بيانات"));
                }
                
                PieDataSet dataSet = new PieDataSet(entries, "الإحصائيات");
                dataSet.setColors(new int[]{Color.parseColor("#5A9CC4"), Color.parseColor("#44A85F"), Color.parseColor("#C33B32"), Color.parseColor("#6A3CC4"), Color.parseColor("#8E24AA")});
                PieData data = new PieData(dataSet);
                donutChart.setData(data);
                donutChart.getDescription().setEnabled(false);
                donutChart.setDrawHoleEnabled(true);
                donutChart.setHoleColor(Color.TRANSPARENT);
                donutChart.getLegend().setEnabled(false); // We have custom legend
                donutChart.invalidate(); // refresh
"""
text = re.sub(setData_pattern, setData_code, text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ProfileActivity.java", "w") as f:
    f.write(text)
