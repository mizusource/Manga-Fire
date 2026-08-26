package com.fire.mangareader.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import okhttp3.Request;
import okhttp3.Response;

public final class ChapterExportManager {
    private static final String TAG = "ChapterExportManager";
    private static final String ROOT_FOLDER_NAME = "SpeedManga";

    public interface ExportCallback {
        void onProgress(int current, int total, String status);
        void onSuccess(File exportedFile, ExportFormat format);
        void onError(String error);
    }

    public static String sanitizeFileName(String name) {
        if (name == null || name.trim().isEmpty()) return "Chapter";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    public static File getMangaDirectory(String mangaTitle, boolean createSubfolder) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File root = new File(downloadsDir, ROOT_FOLDER_NAME);
        if (!root.exists()) root.mkdirs();
        if (createSubfolder && mangaTitle != null && !mangaTitle.trim().isEmpty()) {
            root = new File(root, sanitizeFileName(mangaTitle));
        }
        if (!root.exists()) root.mkdirs();
        return root;
    }

    public static void exportChapter(Context context, String mangaTitle, String chapterTitle, String chapterUrl, List<String> pageUrls, ExportFormat format, String qualitySetting, ExportCallback callback) {
        new Thread(() -> {
            try {
                if (pageUrls == null || pageUrls.isEmpty()) {
                    callback.onError("لا توجد صفحات لتصديرها");
                    return;
                }

                File targetDir = getMangaDirectory(mangaTitle, true);
                String safeTitle = sanitizeFileName(mangaTitle + "_" + chapterTitle);
                File outputFile = new File(targetDir, safeTitle + "." + format.getExtension());

                int total = pageUrls.size();
                callback.onProgress(0, total, "جاري تحضير الصفحات للتصدير...");

                // Download pages in parallel
                ConcurrentHashMap<Integer, byte[]> pagesMap = new ConcurrentHashMap<>();
                ConcurrentHashMap<Integer, int[]> dimensionsMap = new ConcurrentHashMap<>();
                ExecutorService executor = Executors.newFixedThreadPool(4);
                AtomicInteger completed = new AtomicInteger(0);

                for (int i = 0; i < total; i++) {
                    final int index = i;
                    final String url = pageUrls.get(i);
                    executor.execute(() -> {
                        try {
                            byte[] rawBytes = loadPageBytes(url);
                            if (rawBytes != null) {
                                byte[] optimized = optimizeImageBytes(rawBytes, qualitySetting);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeByteArray(optimized, 0, optimized.length, options);
                                if (options.outWidth > 0 && options.outHeight > 0) {
                                    pagesMap.put(index, optimized);
                                    dimensionsMap.put(index, new int[]{options.outWidth, options.outHeight});
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error fetching page " + index, e);
                        } finally {
                            int count = completed.incrementAndGet();
                            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                callback.onProgress(count, total, "جاري تجهيز الصفحة " + count + " من " + total + "...");
                            });
                        }
                    });
                }

                executor.shutdown();
                executor.awaitTermination(5, java.util.concurrent.TimeUnit.MINUTES);

                if (pagesMap.isEmpty()) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        callback.onError("فشل تحميل أي صفحة لتصدير الفصل");
                    });
                    return;
                }

                if (format == ExportFormat.PDF) {
                    writePdf(pagesMap, dimensionsMap, total, outputFile);
                } else {
                    writeCbz(pagesMap, total, outputFile);
                }

                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(outputFile, format);
                });

            } catch (Exception e) {
                Log.e(TAG, "Export failed", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError("فشل التصدير: " + e.getMessage());
                });
            }
        }).start();
    }

    private static void writePdf(ConcurrentHashMap<Integer, byte[]> pagesMap, ConcurrentHashMap<Integer, int[]> dimensionsMap, int total, File outputFile) throws IOException {
        List<Integer> validIndices = new ArrayList<>(pagesMap.keySet());
        Collections.sort(validIndices);

        FileOutputStream fos = new FileOutputStream(outputFile);
        long[] offsets = new long[validIndices.size() * 3 + 3];
        long currentOffset = 0;

        byte[] header = "%PDF-1.4\n%âãÏÓ\n".getBytes(StandardCharsets.US_ASCII);
        fos.write(header);
        currentOffset += header.length;

        offsets[1] = currentOffset;
        byte[] catalog = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(StandardCharsets.US_ASCII);
        fos.write(catalog);
        currentOffset += catalog.length;

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < validIndices.size(); i++) {
            kids.append((i * 3 + 3)).append(" 0 R ");
        }

        offsets[2] = currentOffset;
        byte[] pagesObj = ("2 0 obj\n<< /Type /Pages /Count " + validIndices.size() + " /Kids [ " + kids.toString() + "] >>\nendobj\n").getBytes(StandardCharsets.US_ASCII);
        fos.write(pagesObj);
        currentOffset += pagesObj.length;

        for (int i = 0; i < validIndices.size(); i++) {
            int pageIdx = validIndices.get(i);
            byte[] imgBytes = pagesMap.get(pageIdx);
            int[] dims = dimensionsMap.get(pageIdx);
            int w = dims != null ? dims[0] : 800;
            int h = dims != null ? dims[1] : 1200;

            int pageObjNum = i * 3 + 3;
            int imgObjNum = i * 3 + 4;
            int contentsObjNum = i * 3 + 5;

            // Page obj
            offsets[pageObjNum] = currentOffset;
            byte[] pageData = (pageObjNum + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [ 0 0 " + w + " " + h + " ] /Resources << /XObject << /Img" + i + " " + imgObjNum + " 0 R >> >> /Contents " + contentsObjNum + " 0 R >>\nendobj\n").getBytes(StandardCharsets.US_ASCII);
            fos.write(pageData);
            currentOffset += pageData.length;

            // Image obj
            offsets[imgObjNum] = currentOffset;
            byte[] imgHeader = (imgObjNum + " 0 obj\n<< /Type /XObject /Subtype /Image /Width " + w + " /Height " + h + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + imgBytes.length + " >>\nstream\n").getBytes(StandardCharsets.US_ASCII);
            fos.write(imgHeader);
            currentOffset += imgHeader.length;

            fos.write(imgBytes);
            currentOffset += imgBytes.length;

            byte[] imgFooter = "\nendstream\nendobj\n".getBytes(StandardCharsets.US_ASCII);
            fos.write(imgFooter);
            currentOffset += imgFooter.length;

            // Content obj
            byte[] contentStream = ("q " + w + " 0 0 " + h + " 0 0 cm /Img" + i + " Do Q\n").getBytes(StandardCharsets.US_ASCII);
            offsets[contentsObjNum] = currentOffset;
            byte[] contentsData = (contentsObjNum + " 0 obj\n<< /Length " + contentStream.length + " >>\nstream\n").getBytes(StandardCharsets.US_ASCII);
            fos.write(contentsData);
            currentOffset += contentsData.length;

            fos.write(contentStream);
            currentOffset += contentStream.length;

            byte[] contentsFooter = "endstream\nendobj\n".getBytes(StandardCharsets.US_ASCII);
            fos.write(contentsFooter);
            currentOffset += contentsFooter.length;
        }

        long startXref = currentOffset;
        int totalObjs = validIndices.size() * 3 + 3;
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(totalObjs).append("\n");
        xref.append("0000000000 65535 f \n");
        for (int i = 1; i < totalObjs; i++) {
            xref.append(String.format(Locale.US, "%010d 00000 n \n", offsets[i]));
        }
        xref.append("trailer\n<< /Size ").append(totalObjs).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(startXref).append("\n%%EOF\n");

        fos.write(xref.toString().getBytes(StandardCharsets.US_ASCII));
        fos.flush();
        fos.close();
    }

    private static void writeCbz(ConcurrentHashMap<Integer, byte[]> pagesMap, int total, File outputFile) throws IOException {
        List<Integer> validIndices = new ArrayList<>(pagesMap.keySet());
        Collections.sort(validIndices);

        FileOutputStream fos = new FileOutputStream(outputFile);
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos, 8192));

        for (int i = 0; i < validIndices.size(); i++) {
            int pageIdx = validIndices.get(i);
            byte[] imgBytes = pagesMap.get(pageIdx);
            String entryName = String.format(Locale.US, "page_%03d.jpg", i + 1);
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(imgBytes);
            zos.closeEntry();
        }

        zos.flush();
        zos.close();
    }

    public static byte[] optimizeImageBytes(byte[] inputBytes, String qualitySetting) {
        try {
            int maxW = 1080;
            int quality = 75;
            if ("HIGH".equalsIgnoreCase(qualitySetting)) {
                maxW = 1400;
                quality = 90;
            } else if ("LOW".equalsIgnoreCase(qualitySetting)) {
                maxW = 720;
                quality = 50;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length, options);
            int w = options.outWidth;
            int h = options.outHeight;
            if (w <= 0 || h <= 0) return inputBytes;

            int sampleSize = 1;
            while (w / sampleSize > maxW * 2) {
                sampleSize *= 2;
            }

            BitmapFactory.Options decodeOpts = new BitmapFactory.Options();
            decodeOpts.inSampleSize = sampleSize;
            decodeOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length, decodeOpts);
            if (bitmap == null) return inputBytes;

            if (bitmap.getWidth() > maxW) {
                int targetH = (int) (((float) bitmap.getHeight() / bitmap.getWidth()) * maxW);
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, maxW, targetH, true);
                if (scaled != bitmap) {
                    bitmap.recycle();
                    bitmap = scaled;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            bitmap.recycle();
            return baos.toByteArray();
        } catch (Exception e) {
            return inputBytes;
        }
    }

    public static byte[] loadPageBytes(String pageSource) {
        if (pageSource == null || pageSource.trim().isEmpty()) return null;
        try {
            if (pageSource.startsWith("file://") || pageSource.startsWith("/")) {
                File file = new File(pageSource.replace("file://", ""));
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = fis.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    fis.close();
                    return baos.toByteArray();
                }
            }

            Request req = new Request.Builder()
                    .url(pageSource)
                    .header("User-Agent", UserAgentGenerator.getRandomUserAgent())
                    .build();
            Response response = MangaOkHttp.getClient().newCall(req).execute();
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                response.close();
                return bytes;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading bytes: " + pageSource, e);
        }
        return null;
    }

    public static void openFile(Context context, File file, ExportFormat format) {
        try {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, format.getMimeType());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "فتح بواسطة"));
        } catch (Exception e) {
            Toast.makeText(context, "لا يوجد تطبيق مثبت لفتح هذا الملف (" + format.getExtension() + ")", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareFile(Context context, File file, String title) {
        try {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(file.getName().endsWith(".pdf") ? "application/pdf" : "application/zip");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "مشاركة " + title));
        } catch (Exception e) {
            Toast.makeText(context, "فشلت مشاركة الملف", Toast.LENGTH_SHORT).show();
        }
    }
}
