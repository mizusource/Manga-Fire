with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchViewModel.kt", "r") as f:
    content = f.read()

# find the last closing brace
last_brace_idx = content.rfind('}')
before_brace = content[:last_brace_idx]
after_brace = content[last_brace_idx+1:]

new_content = before_brace[:before_brace.rfind('}')] + """
    fun onYearRangeChange(range: ClosedFloatingPointRange<Float>) {
        _filter.value = _filter.value.copy(yearRange = range)
    }

    fun onChapterRangeChange(range: ClosedFloatingPointRange<Float>) {
        _filter.value = _filter.value.copy(chapterRange = range)
    }
}
"""

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchViewModel.kt", "w") as f:
    f.write(new_content)
