filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

deps_to_add = """    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
"""

content = content.replace('implementation("androidx.navigation:navigation-compose:2.7.7")',
                          'implementation("androidx.navigation:navigation-compose:2.7.7")\n' + deps_to_add)

with open(filepath, 'w') as f:
    f.write(content)
