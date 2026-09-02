package com.fire.mangareader.presentation.reader.viewer;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import java.io.File;
import java.io.FileOutputStream;

import okhttp3.Request;
import okhttp3.Response;

public class WebtoonPageHolder extends RecyclerView.ViewHolder {

    private FrameLayout container;
    private ProgressBar progressBar;
    private SubsamplingScaleImageView subsamplingImageView;
    private ImageView gifImageView;
    private LinearLayout errorLayout;
    private Button btnRetry;
    private String currentUrl;
    private String currentCookies;
    private String refererUrl;

    public WebtoonPageHolder(@NonNull View itemView) {
        super(itemView);
        container = (FrameLayout) itemView;
        
        progressBar = new ProgressBar(itemView.getContext());
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(120, 120, Gravity.CENTER);
        progressBar.setLayoutParams(pbParams);
        container.addView(progressBar);

        errorLayout = new LinearLayout(itemView.getContext());
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        errorLayout.setGravity(Gravity.CENTER);
        TextView errorText = new TextView(itemView.getContext());
        errorText.setText("فشل تحميل الصفحة");
        errorText.setTextColor(android.graphics.Color.WHITE);
        btnRetry = new Button(itemView.getContext());
        btnRetry.setText("إعادة المحاولة");
        errorLayout.addView(errorText);
        errorLayout.addView(btnRetry);
        FrameLayout.LayoutParams errParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        errorLayout.setLayoutParams(errParams);
        errorLayout.setVisibility(View.GONE);
        container.addView(errorLayout);

        btnRetry.setOnClickListener(v -> {
            if (currentUrl != null) bind(currentUrl, currentCookies, refererUrl);
        });
    }

    public void bind(String imageUrl, String cookies, String referer) {
        // 🚀 الإصلاح السحري: الروابط الناقصة
        if (imageUrl != null && imageUrl.startsWith("//")) {
            imageUrl = "https:" + imageUrl;
        }

        this.currentUrl = imageUrl;
        this.currentCookies = cookies;
        this.refererUrl = referer;
        
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        if (subsamplingImageView != null) {
            container.removeView(subsamplingImageView);
            subsamplingImageView.recycle();
            subsamplingImageView = null;
        }
        if (gifImageView != null) {
            container.removeView(gifImageView);
            gifImageView = null;
        }

        boolean isLocalFile = currentUrl != null && !currentUrl.startsWith("http");

        if (isLocalFile) {
            File localImage = new File(currentUrl);
            
            if (currentUrl.toLowerCase().endsWith(".gif")) {
                gifImageView = new ImageView(itemView.getContext());
                gifImageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                gifImageView.setAdjustViewBounds(true);
                container.addView(gifImageView, 0);
                
                Glide.with(itemView.getContext())
                     .asGif()
                     .load(localImage)
                     .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                     .into(gifImageView);
                progressBar.setVisibility(View.GONE);
            } else {
                subsamplingImageView = new SubsamplingScaleImageView(itemView.getContext());
                subsamplingImageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE);
                subsamplingImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                subsamplingImageView.setMinimumTileDpi(180);
                container.addView(subsamplingImageView, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                
                subsamplingImageView.setImage(ImageSource.uri(Uri.fromFile(localImage)));
                
                subsamplingImageView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {
                    @Override
                    public void onReady() {
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onImageLoadError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

        } else {
            // 🌐 وضع الأونلاين (التحقق من الكاش أولاً ثم التحميل السريع)
            new Thread(() -> {
                try {
                    String finalUrl = currentUrl;
                    if (finalUrl.startsWith("//")) finalUrl = "https:" + finalUrl;

                    File tempCacheFile = new File(itemView.getContext().getCacheDir(), "img_" + Math.abs(finalUrl.hashCode()) + ".jpg");

                    if (!tempCacheFile.exists() || tempCacheFile.length() == 0) {
                        Request request = new Request.Builder()
                                .url(finalUrl)
                                .header("Referer", refererUrl != null ? refererUrl : finalUrl)
                                .build();

                        Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();

                        if (!response.isSuccessful() || response.body() == null) {
                            throw new Exception("HTTP " + response.code());
                        }

                        byte[] bytes = response.body().bytes();

                        // 🗜️ فحص إعداد جودة الصور (توفير البيانات)
                        android.content.SharedPreferences prefs = itemView.getContext().getSharedPreferences("MangaFirePrefs", android.content.Context.MODE_PRIVATE);
                        int quality = prefs.getInt("image_quality_value", 100);

                        if (quality < 100 && !currentUrl.toLowerCase().endsWith(".gif")) {
                            try {
                                android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                if (bmp != null) {
                                    FileOutputStream fos = new FileOutputStream(tempCacheFile);
                                    bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, fos);
                                    fos.flush();
                                    fos.close();
                                    bmp.recycle();
                                } else {
                                    FileOutputStream fos = new FileOutputStream(tempCacheFile);
                                    fos.write(bytes);
                                    fos.flush();
                                    fos.close();
                                }
                            } catch (Exception e) {
                                FileOutputStream fos = new FileOutputStream(tempCacheFile);
                                fos.write(bytes);
                                fos.flush();
                                fos.close();
                            }
                        } else {
                            FileOutputStream fos = new FileOutputStream(tempCacheFile);
                            fos.write(bytes);
                            fos.flush();
                            fos.close();
                        }
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (currentUrl.toLowerCase().endsWith(".gif")) {
                            gifImageView = new ImageView(itemView.getContext());
                            gifImageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            gifImageView.setAdjustViewBounds(true);
                            container.addView(gifImageView, 0);
                            
                            Glide.with(itemView.getContext())
                                .asGif()
                                .load(tempCacheFile)
                                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                                .into(gifImageView);
                        } else {
                            subsamplingImageView = new SubsamplingScaleImageView(itemView.getContext());
                            subsamplingImageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE);
                            subsamplingImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                            subsamplingImageView.setMinimumTileDpi(180);
                            container.addView(subsamplingImageView, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            
                            subsamplingImageView.setImage(ImageSource.uri(Uri.fromFile(tempCacheFile)));
                            subsamplingImageView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {
                                @Override
                                public void onReady() {
                                    progressBar.setVisibility(View.GONE);
                                }
                                @Override
                                public void onImageLoadError(Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    });
                }
            }).start();
        }
    }
}
