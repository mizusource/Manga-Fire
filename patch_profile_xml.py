import re

with open("app/src/main/res/layout/fragment_profile.xml", "r") as f:
    text = f.read()

# Replace DonutChartView with PieChart
text = text.replace("com.fire.mangareader.util.DonutChartView", "com.github.mikephil.charting.charts.PieChart")

with open("app/src/main/res/layout/fragment_profile.xml", "w") as f:
    f.write(text)
