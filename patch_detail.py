import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

cloudflare_hack_old = '''                                    webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT, 
                                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT)); 
                                    webView.setAlpha(1.0f); 
                                    Toast.makeText(MangaDetailActivity.this, "يرجى حل اختبار التحقق (Cloudflare) للمتابعة", Toast.LENGTH_LONG).show(); 
                                });'''

cloudflare_hack_new = '''                                    webView.setAlpha(0.0f);
                                    Intent cfIntent = new Intent(MangaDetailActivity.this, CloudflareBypassActivity.class);
                                    cfIntent.putExtra("url", mangaUrl);
                                    startActivity(cfIntent);
                                });'''

content = content.replace(cloudflare_hack_old, cloudflare_hack_new)

comments_intent_old = '''                    Intent intent = new Intent(MangaDetailActivity.this, com.fire.mangareader.presentation.ui.comments.MangaCommentsActivity.class);
                    intent.putExtra("mangaUrl", mangaUrl);
                    startActivity(intent);'''

comments_intent_new = '''                    if (!com.fire.mangareader.util.AppAdminSettings.commentsEnabled) {
                        Toast.makeText(MangaDetailActivity.this, "التعليقات معطلة من قبل الإدارة", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MangaDetailActivity.this, com.fire.mangareader.presentation.ui.comments.MangaCommentsActivity.class);
                        intent.putExtra("mangaUrl", mangaUrl);
                        startActivity(intent);
                    }'''

content = content.replace(comments_intent_old, comments_intent_new)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

