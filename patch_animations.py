import re

with open('app/src/main/res/values/styles.xml', 'r') as f:
    content = f.read()

anim_style = """
    <style name="WindowAnimationTransition">
        <item name="android:activityOpenEnterAnimation">@anim/ios_enter</item>
        <item name="android:activityOpenExitAnimation">@anim/ios_exit</item>
        <item name="android:activityCloseEnterAnimation">@anim/ios_pop_enter</item>
        <item name="android:activityCloseExitAnimation">@anim/ios_pop_exit</item>
    </style>
"""

content = re.sub(r'<style name="WindowAnimationTransition">.*?</style>', anim_style, content, flags=re.DOTALL)

with open('app/src/main/res/values/styles.xml', 'w') as f:
    f.write(content.strip())
