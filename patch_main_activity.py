import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt'
with open(filepath, 'r') as f:
    content = f.read()

# The error is because there are 3 closing brackets at the end of BottomNavigationBar, but it only needs 1 or 2 depending on blocks.
# Let's count the blocks in BottomNavigationBar.
# @Composable
# fun BottomNavigationBar(navController: NavHostController) { // 1
#    ...
#    NavigationBar(...) { // 2
#        ...
#        items.forEach { item -> // 3
#            NavigationBarItem(
#                ...
#                onClick = { // 4
#                    navController.navigate(...) { // 5
#                        ...
#                    } // 5
#                } // 4
#            )
#        } // 3
#    } // 2
# } // 1
# So it should end with `        }\n    }\n}`.

content = content.replace('        }\n    }\n}\n}\n\n@Composable', '        }\n    }\n}\n\n@Composable')

with open(filepath, 'w') as f:
    f.write(content)
