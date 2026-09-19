const express = require('express');
const axios = require('axios');
const puppeteerExtra = require('puppeteer-extra');
const StealthPlugin = require('puppeteer-extra-plugin-stealth');
const cors = require('cors');

puppeteerExtra.use(StealthPlugin());

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());

app.get('/', (req, res) => {
    res.send('Manga API is running (DOM Parsing Mode)!');
});

// مسار البروكسي
app.get('/api/proxy-image', async (req, res) => {
    const imageUrl = req.query.url;
    const referer = req.query.referer || '';
    if (!imageUrl) return res.status(400).json({ error: 'Missing image URL' });

    try {
        const response = await axios({
            method: 'get',
            url: imageUrl,
            responseType: 'stream',
            headers: {
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                'Referer': referer,
                'Accept': 'image/webp,image/apng,image/*,*/*;q=0.8'
            },
            timeout: 15000
        });
        res.setHeader('Content-Type', response.headers['content-type'] || 'image/jpeg');
        res.setHeader('Cache-Control', 'public, max-age=604800, immutable');
        response.data.pipe(res);
    } catch (error) {
        res.status(500).json({ error: 'Failed to fetch image' });
    }
});

// مسار سحب الفصول (الاعتماد على HTML بدلاً من Network Interception)
app.get('/api/scrape-chapter', async (req, res) => {
    const chapterUrl = req.query.url;
    if (!chapterUrl) return res.status(400).json({ error: 'Missing chapter URL' });

    let browser;
    try {
        browser = await puppeteerExtra.launch({
            headless: "new",
            args: [
                '--no-sandbox', 
                '--disable-setuid-sandbox', 
                '--disable-dev-shm-usage',
                '--disable-blink-features=AutomationControlled'
            ]
        });
        
        const page = await browser.newPage();
        await page.evaluateOnNewDocument(() => { Object.defineProperty(navigator, 'webdriver', { get: () => undefined }); });
        await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36');
        
        // استخدام domcontentloaded للسرعة بدلاً من networkidle0 لأننا سنسحب من الـ HTML مباشرة
        await page.goto(chapterUrl, { waitUntil: 'domcontentloaded', timeout: 45000 });
        
        // محاكاة النزول لأسفل لتحفيز الـ Lazy Load إن وجد
        await page.evaluate(async () => {
            await new Promise((resolve) => {
                let totalHeight = 0;
                const distance = 250;
                const timer = setInterval(() => {
                    window.scrollBy(0, distance);
                    totalHeight += distance;
                    // لا داعي للنزول لآخر الصفحة تماماً، فقط تحفيز السكربتات
                    if (totalHeight >= 3000 || totalHeight >= document.body.scrollHeight) {
                        clearInterval(timer);
                        resolve();
                    }
                }, 100);
            });
        });

        // استخراج روابط الصور من عناصر HTML مباشرة
        const imageUrls = await page.evaluate(() => {
            const images = [];
            // معظم القوالب بما فيها قوالب Madara و Manga-Starz و SparkManga و Dilar
            const containers = document.querySelectorAll(
                '.reading-content img, ' + 
                '#readerarea img, ' + 
                '.page-break img, ' + 
                '.ts-main-image img, ' + 
                'div[class*="reading-content"] img, ' +
                '.chapter-image img, ' +
                '.blocks-gallery-item img, ' +
                '#image-container img' // إضافة شائعة أخرى
            );
            
            containers.forEach(img => {
                // جلب الرابط من الخصائص المختلفة
                let src = img.getAttribute('data-src') || 
                          img.getAttribute('data-lazy-src') || 
                          img.getAttribute('src');
                          
                if (src) {
                    src = src.trim();
                    // تجاهل صور الـ Base64 الصغيرة وأيقونات الموقع
                    if (!src.startsWith('data:image') && src.length > 50 && !src.includes('favicon') && !src.includes('logo')) {
                        images.push(src);
                    }
                }
            });
            return images;
        });

        await browser.close();

        if (imageUrls.length === 0) {
            return res.status(404).json({ error: 'لم يتم العثور على صور في كود الصفحة.' });
        }

        const proxyUrls = imageUrls.map(imgUrl => 
            `http://${req.headers.host}/api/proxy-image?url=${encodeURIComponent(imgUrl)}&referer=${encodeURIComponent(chapterUrl)}`
        );

        res.json({ chapter_url: chapterUrl, total_pages: proxyUrls.length, pages: proxyUrls });

    } catch (error) {
        if (browser) await browser.close();
        res.status(500).json({ error: 'فشل في سحب الفصل.', details: error.message });
    }
});

app.listen(PORT, () => { console.log(`Server is running on port ${PORT}`); });
