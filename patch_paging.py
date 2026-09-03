filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

deps_to_add = """    implementation("androidx.paging:paging-compose:3.3.0")
    implementation("androidx.paging:paging-runtime-ktx:3.3.0")
"""

content = content.replace('implementation("androidx.compose.foundation:foundation")',
                          'implementation("androidx.compose.foundation:foundation")\n' + deps_to_add)

with open(filepath, 'w') as f:
    f.write(content)
