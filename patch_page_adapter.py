import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/adapter/PageAdapter.java'
with open(filepath, 'r') as f:
    content = f.read()

import_statements = """import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.fire.mangareader.util.SystemUtils;"""

content = content.replace('import java.util.List;', 'import java.util.List;\n' + import_statements)

bind_logic = """        int maxTexture = SystemUtils.getMaxTextureSize();
        Glide.with(context)
             .load(imageUrls.get(position))
             .downsample(DownsampleStrategy.AT_MOST)
             .override(maxTexture, maxTexture)
             .into(holder.pageImageView);"""

content = content.replace('Glide.with(context).load(imageUrls.get(position)).into(holder.pageImageView);', bind_logic)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched PageAdapter.java")
