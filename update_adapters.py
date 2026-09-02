import os

def apply_crossfade(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if '.transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())' not in content:
        # For HeroBannerAdapter
        content = content.replace('.diskCacheStrategy(DiskCacheStrategy.ALL)\n                .into(holder.ivHeroCover);', 
                                  '.diskCacheStrategy(DiskCacheStrategy.ALL)\n                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())\n                .into(holder.ivHeroCover);')
        
        # For MangaAdapter
        content = content.replace('.format(com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565) // تقليل الدقة قليلاً لتسريع التحميل وتوفير الرام\n             .into(holder.mangaCover);', 
                                  '.format(com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565)\n             .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())\n             .into(holder.mangaCover);')

    with open(filepath, 'w') as f:
        f.write(content)

apply_crossfade('app/src/main/java/com/fire/mangareader/presentation/adapter/HeroBannerAdapter.java')
apply_crossfade('app/src/main/java/com/fire/mangareader/presentation/adapter/MangaAdapter.java')
print("Applied CrossFade to adapters")
