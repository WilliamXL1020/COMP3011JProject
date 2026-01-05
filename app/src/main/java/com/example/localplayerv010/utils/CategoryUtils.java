package com.example.localplayerv010.utils;

import android.util.Log;

import com.example.localplayerv010.model.PexelsVideo;
import com.example.localplayerv010.model.UserOnline;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频分类工具类
 * 用于根据Pexels视频数据智能判断视频分类
 */
public class CategoryUtils {

    private static boolean DEBUG_MODE = true;
    private static final String TAG = "CategoryUtils";

    // 分类关键词映射表（基于Pexels常见标签优化）
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();
    private static final Map<String, Integer> CATEGORY_WEIGHTS = new HashMap<>();

    static {
        // 初始化分类关键词
        initCategoryKeywords();
        initCategoryWeights();
    }

    /**
     * 根据Pexels视频数据智能判断分类
     * @param pexelsVideo Pexels视频数据对象
     * @return 分类标识符 (gaming, music, movie, education, lifestyle, funny, selected)
     */
    public static String determineCategoryFromPexelsData(PexelsVideo pexelsVideo) {
        if (pexelsVideo == null) {
            return "selected";
        }

        if (DEBUG_MODE) {
            Log.d(TAG, "=== 开始视频分类 ===");
            Log.d(TAG, "视频ID: " + pexelsVideo.getId());
            Log.d(TAG, "URL: " + pexelsVideo.getUrl());
            Log.d(TAG, "标签: " + formatTags(pexelsVideo.getTags()));
            Log.d(TAG, "时长: " + pexelsVideo.getDuration() + "秒");
            Log.d(TAG, "分辨率: " + pexelsVideo.getWidth() + "x" + pexelsVideo.getHeight());
        }

        // 使用评分系统进行多维度判断
        Map<String, Integer> categoryScores = new HashMap<>();
        Map<String, List<String>> scoreDetails = new HashMap<>();

        // 初始化各个维度的得分Map
        Map<String, Integer> tagScores = new HashMap<>();
        Map<String, Integer> urlScores = new HashMap<>();
        Map<String, Integer> userScores = new HashMap<>();
        Map<String, Integer> durationScores = new HashMap<>();
        Map<String, Integer> resolutionScores = new HashMap<>();

        // 初始化所有分数和详情
        for (String category : getAllCategories()) {
            categoryScores.put(category, 0);
            scoreDetails.put(category, new ArrayList<>());
            tagScores.put(category, 0);
            urlScores.put(category, 0);
            userScores.put(category, 0);
            durationScores.put(category, 0);
            resolutionScores.put(category, 0);
        }

        // selected有基础分
        categoryScores.put("selected", 1);
        scoreDetails.get("selected").add("基础分: +1分");

        // 1. 基于URL评分（新增维度）
        scoreByUrl(pexelsVideo.getUrl(), categoryScores, urlScores, scoreDetails);

        // 2. 基于标签评分
        scoreByTags(pexelsVideo.getTags(), categoryScores, tagScores, scoreDetails);

        // 3. 基于用户信息评分
        scoreByUser(pexelsVideo.getUser(), categoryScores, userScores, scoreDetails);

        // 4. 基于视频时长评分
        scoreByDuration(pexelsVideo.getDuration(), categoryScores, durationScores, scoreDetails);

        // 5. 基于视频分辨率/宽高比评分
        scoreByResolution(pexelsVideo.getWidth(), pexelsVideo.getHeight(),
                categoryScores, resolutionScores, scoreDetails);

        if (DEBUG_MODE) {
            printScoreDetails(pexelsVideo, categoryScores,
                    tagScores, urlScores, userScores, durationScores, resolutionScores, scoreDetails);
        }

        // 找出最高分的分类
        String result = getBestCategory(categoryScores);

        if (DEBUG_MODE) {
            Log.d(TAG, "最终分类结果: " + result + " (" + getCategoryDisplayName(result) + ")");
        }

        return result;
    }

    /**
     * 基于URL评分
     * 示例URL: https://www.pexels.com/video/person-playing-the-piano-2306150/
     * 可以提取关键词: person, playing, the, piano
     */
    private static void scoreByUrl(String url,
                                   Map<String, Integer> totalScores,
                                   Map<String, Integer> urlScores,
                                   Map<String, List<String>> scoreDetails) {
        if (url == null || url.isEmpty()) {
            if (DEBUG_MODE) {
                Log.d(TAG, "URL为空，不加分");
            }
            return;
        }

        int urlWeight = CATEGORY_WEIGHTS.get("URL_MATCH");

        if (DEBUG_MODE) {
            Log.d(TAG, "开始URL评分，权重=" + urlWeight);
            Log.d(TAG, "URL: " + url);
        }

        // 将URL转换为小写以便匹配
        String urlLower = url.toLowerCase();
        String decodedUrl = urlLower;

        try {
            // 解码URL中的特殊字符
            decodedUrl = URLDecoder.decode(urlLower, "UTF-8");
        } catch (Exception e) {
            if (DEBUG_MODE) {
                Log.e(TAG, "URL解码失败，使用原始URL: " + e.getMessage());
            }
        }

        // 检查URL中是否包含分类名
        for (String category : CATEGORY_KEYWORDS.keySet()) {
            if (decodedUrl.contains("/" + category + "/") ||
                    decodedUrl.contains(category + "-") ||
                    decodedUrl.contains("-" + category + "-") ||
                    decodedUrl.contains("-" + category + "/") ||
                    decodedUrl.endsWith("-" + category)) {

                addScoreWithDetail(category, urlWeight * 3, totalScores, urlScores, scoreDetails,
                        "URL路径包含分类名'" + category + "': +" + (urlWeight * 3) + "分");

                if (DEBUG_MODE) {
                    Log.d(TAG, "  " + category + "分类: URL包含分类名，+" + (urlWeight * 3) + "分");
                }
            }
        }

        // 检查URL中是否包含分类关键词
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (decodedUrl.contains(keyword)) {
                    addScoreWithDetail(category, urlWeight, totalScores, urlScores, scoreDetails,
                            "URL包含关键词'" + keyword + "': +" + urlWeight + "分");

                    if (DEBUG_MODE) {
                        Log.d(TAG, "  " + category + "分类: URL包含关键词'" + keyword + "': +" + urlWeight + "分");
                    }
                    break; // 一个URL匹配一个关键词即可
                }
            }
        }

        // 特殊分析：Pexels URL的视频标题部分
        // 格式：https://www.pexels.com/video/标题描述-视频ID/
        if (decodedUrl.contains("pexels.com/video/")) {
            extractVideoTitleFromUrl(decodedUrl, totalScores, urlScores, scoreDetails);
        }
    }

    /**
     * 从Pexels URL中提取视频标题进行分析
     * 示例：https://www.pexels.com/video/person-playing-the-piano-2306150/
     * 提取标题：person-playing-the-piano
     */
    private static void extractVideoTitleFromUrl(String url,
                                                 Map<String, Integer> totalScores,
                                                 Map<String, Integer> urlScores,
                                                 Map<String, List<String>> scoreDetails) {
        int urlWeight = CATEGORY_WEIGHTS.get("URL_MATCH");

        // 查找 "/video/" 后面的部分
        int videoIndex = url.indexOf("/video/");
        if (videoIndex == -1) {
            return;
        }

        // 获取 "/video/" 之后的内容
        String afterVideo = url.substring(videoIndex + 7); // "/video/".length = 7

        // 查找视频ID（通常是末尾的数字）
        int lastDash = afterVideo.lastIndexOf("-");
        if (lastDash == -1) {
            return;
        }

        // 提取标题部分（破折号前的部分）
        String titlePart = afterVideo.substring(0, lastDash);

        // 移除可能的多余破折号
        titlePart = titlePart.replaceAll("-+", " ");

        if (DEBUG_MODE) {
            Log.d(TAG, "从URL提取的标题: " + titlePart);
        }

        // 分析标题中的关键词
        String[] titleWords = titlePart.split("[\\s-]+");

        for (String word : titleWords) {
            // 跳过常见停用词
            if (isStopWord(word)) {
                continue;
            }

            // 检查每个分类的关键词
            for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();

                for (String keyword : keywords) {
                    if (word.contains(keyword) || keyword.contains(word)) {
                        addScoreWithDetail(category, urlWeight * 2, totalScores, urlScores, scoreDetails,
                                "URL标题包含'" + word + "'匹配关键词'" + keyword + "': +" + (urlWeight * 2) + "分");
                        break;
                    }
                }
            }
        }
    }

    /**
     * 判断是否为停用词（常见无意义词）
     */
    private static boolean isStopWord(String word) {
        String[] stopWords = {
                "the", "a", "an", "and", "or", "but", "in", "on", "at", "to",
                "for", "of", "with", "by", "as", "is", "are", "was", "were",
                "be", "been", "being", "have", "has", "had", "do", "does", "did",
                "this", "that", "these", "those", "i", "you", "he", "she", "it",
                "we", "they", "my", "your", "his", "her", "its", "our", "their",
                "me", "him", "her", "us", "them", "what", "which", "who", "whom",
                "whose", "where", "when", "why", "how", "all", "any", "both",
                "each", "few", "more", "most", "other", "some", "such", "no",
                "nor", "not", "only", "own", "same", "so", "than", "too", "very"
        };

        for (String stopWord : stopWords) {
            if (word.equals(stopWord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 基于标签评分
     */
    private static void scoreByTags(List<String> tags,
                                    Map<String, Integer> totalScores,
                                    Map<String, Integer> tagScores,
                                    Map<String, List<String>> scoreDetails) {
        if (tags == null || tags.isEmpty()) {
            if (DEBUG_MODE) {
                Log.d(TAG, "标签为空，不加分");
            }
            return;
        }

        int tagWeight = CATEGORY_WEIGHTS.get("TAG_MATCH");

        if (DEBUG_MODE) {
            Log.d(TAG, "开始标签评分，权重=" + tagWeight + "，标签数=" + tags.size());
        }

        for (String tag : tags) {
            String tagLower = tag.toLowerCase().trim();

            if (DEBUG_MODE) {
                Log.d(TAG, "处理标签: " + tag);
            }

            // 检查每个分类的关键词
            for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();

                // 检查标签是否完全匹配或包含关键词
                for (String keyword : keywords) {
                    if (tagLower.equals(keyword) || tagLower.contains(keyword) || keyword.contains(tagLower)) {
                        // 更新总分数
                        int oldTotal = totalScores.get(category);
                        totalScores.put(category, oldTotal + tagWeight);

                        // 更新标签维度分数
                        int oldTagScore = tagScores.get(category);
                        tagScores.put(category, oldTagScore + tagWeight);

                        // 记录详情
                        String detail = String.format("标签'%s'匹配'%s': +%d分",
                                tag, keyword, tagWeight);
                        scoreDetails.get(category).add(detail);

                        if (DEBUG_MODE) {
                            Log.d(TAG, "  " + category + "分类: " + detail);
                        }

                        // 如果完全匹配，额外加分
                        if (tagLower.equals(keyword)) {
                            totalScores.put(category, totalScores.get(category) + 2);
                            tagScores.put(category, tagScores.get(category) + 2);
                            scoreDetails.get(category).add("完全匹配额外+2分");

                            if (DEBUG_MODE) {
                                Log.d(TAG, "  " + category + "分类: 完全匹配额外+2分");
                            }
                        }
                        break; // 一个标签只匹配一个分类一次
                    }
                }
            }
        }
    }

    /**
     * 初始化分类权重
     */
    private static void initCategoryWeights() {
        // URL匹配的权重（新增，权重较高）
        CATEGORY_WEIGHTS.put("URL_MATCH", 10);
        // 标签匹配的权重（最高）
        CATEGORY_WEIGHTS.put("TAG_MATCH", 15);
        // 用户信息匹配的权重
        CATEGORY_WEIGHTS.put("USER_MATCH", 3);
        // 时长匹配的权重
        CATEGORY_WEIGHTS.put("DURATION_MATCH", 2);
        // 分辨率匹配的权重
        CATEGORY_WEIGHTS.put("RESOLUTION_MATCH", 1);
    }

    /**
     * 初始化分类关键词
     */
    private static void initCategoryKeywords() {
        // gaming 分类关键词
        CATEGORY_KEYWORDS.put("gaming", Arrays.asList(
                "gaming", "gameplay", "game", "twitch", "stream", "esports",
                "fortnite", "minecraft", "gamer", "playstation", "xbox", "nintendo",
                "steam", "valorant", "league", "overwatch", "pubg", "cod", "warzone",
                "rpg", "fps", "mmo", "racing", "sports game", "mobile game", "pc game",
                "console", "controller", "walkthrough", "speedrun"
        ));

        // music 分类关键词
        CATEGORY_KEYWORDS.put("music", Arrays.asList(
                "music", "song", "concert", "musician", "singer", "band", "guitar",
                "piano", "keyboard", "violin", "drums", "dj", "album", "live",
                "music video", "mv", "rap", "pop", "rock", "jazz", "hip hop",
                "hiphop", "classical", "electronic", "edm", "r&b", "blues",
                "instrumental", "orchestra", "festival", "performance", "vocal", "beat",
                "producer", "soundtrack", "karaoke"
        ));

        // movie 分类关键词
        CATEGORY_KEYWORDS.put("movie", Arrays.asList(
                "movie", "film", "cinema", "hollywood", "actor", "actress", "trailer",
                "scene", "director", "oscar", "blockbuster", "comedy", "action", "drama",
                "horror", "sci-fi", "science fiction", "thriller", "animation", "animated",
                "documentary", "short film", "film noir", "romance", "adventure", "fantasy",
                "suspense", "marvel", "disney", "netflix", "hollywood", "bollywood"
        ));

        // education 分类关键词
        CATEGORY_KEYWORDS.put("education", Arrays.asList(
                "education", "tutorial", "learn", "teaching", "course", "school",
                "university", "study", "online", "how to", "lecture", "lesson",
                "knowledge", "training", "workshop", "academic", "science", "math",
                "mathematics", "physics", "chemistry", "biology", "history", "art",
                "language", "programming", "coding", "technology", "business", "finance",
                "marketing", "design", "photography", "cooking class", "diy tutorial",
                "online course", "mooc", "educational"
        ));

        // lifestyle 分类关键词
        CATEGORY_KEYWORDS.put("lifestyle", Arrays.asList(
                "lifestyle", "travel", "food", "cooking", "recipe", "fitness", "workout",
                "yoga", "meditation", "health", "wellness", "fashion", "beauty", "makeup",
                "home", "interior", "design", "vlog", "daily", "routine", "gardening",
                "pets", "animals", "nature", "outdoor", "adventure", "sports", "exercise",
                "diy", "craft", "photography", "parenting", "relationship", "minimalism",
                "sustainable", "vegan", "vegetarian", "mindfulness", "selfcare"
        ));

        // funny 分类关键词
        CATEGORY_KEYWORDS.put("funny", Arrays.asList(
                "funny", "comedy", "hilarious", "humor", "joke", "meme", "prank",
                "laugh", "standup", "comic", "entertainment", "fun", "crazy", "silly",
                "fail", "moments", "skit", "parody", "satire", "blooper", "bloopers",
                "vine", "tiktok", "reels", "short", "compilation", "cat", "dogs", "pet",
                "animal", "cute", "wholesome", "reaction", "challenge", "trend"
        ));
    }

    /**
     * 打印详细评分结果
     */
    private static void printScoreDetails(PexelsVideo video,
                                          Map<String, Integer> finalScores,
                                          Map<String, Integer> tagScores,
                                          Map<String, Integer> urlScores,
                                          Map<String, Integer> userScores,
                                          Map<String, Integer> durationScores,
                                          Map<String, Integer> resolutionScores,
                                          Map<String, List<String>> scoreDetails) {

        Log.d(TAG, "\n=================== 视频分类评分详情 ===================");
        Log.d(TAG, "视频ID: " + video.getId());
        Log.d(TAG, "URL: " + video.getUrl());
        Log.d(TAG, "标签: " + formatTags(video.getTags()));
        Log.d(TAG, "时长: " + video.getDuration() + "秒");
        Log.d(TAG, "分辨率: " + video.getWidth() + "x" + video.getHeight());
        Log.d(TAG, "------------------------------------------------------");

        // 按分数排序
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(finalScores.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return Integer.compare(b.getValue(), a.getValue()); // 降序排序
            }
        });

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String category = entry.getKey();
            int totalScore = entry.getValue();

            // 跳过分数为0的分类（除了selected）
            if (totalScore == 0 && !"selected".equals(category)) {
                continue;
            }

            Log.d(TAG, "\n【" + getCategoryDisplayName(category) + "】(" + category + ")");
            Log.d(TAG, "总得分: " + totalScore);

            // 各项得分（新增URL得分）
            Log.d(TAG, "┌─ 得分明细:");
            Log.d(TAG, "│  URL得分: " + urlScores.get(category));
            Log.d(TAG, "│  标签得分: " + tagScores.get(category));
            Log.d(TAG, "│  用户得分: " + userScores.get(category));
            Log.d(TAG, "│  时长得分: " + durationScores.get(category));
            Log.d(TAG, "│  分辨率得分: " + resolutionScores.get(category));
            Log.d(TAG, "└─ 扣分项: " + getDeductionDetails(category, scoreDetails));

            // 详细说明
            List<String> details = scoreDetails.get(category);
            if (details != null && !details.isEmpty()) {
                Log.d(TAG, "详细说明:");
                for (String detail : details) {
                    Log.d(TAG, "  • " + detail);
                }
            }
        }

        Log.d(TAG, "======================================================\n");
    }

    /**
     * 格式化标签显示
     */
    private static String formatTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "无标签";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(tags.get(i));
        }
        return sb.toString();
    }

    /**
     * 添加分数并记录详情
     */
    private static void addScoreWithDetail(String category, int score,
                                           Map<String, Integer> totalScores,
                                           Map<String, Integer> dimensionScores,
                                           Map<String, List<String>> scoreDetails,
                                           String detail) {
        int oldTotal = totalScores.get(category);
        totalScores.put(category, oldTotal + score);

        int oldDimension = dimensionScores.get(category);
        dimensionScores.put(category, oldDimension + score);

        scoreDetails.get(category).add(detail);

        if (DEBUG_MODE) {
            Log.d(TAG, category + "分类: " + detail + "，总分从" + oldTotal + "变为" + totalScores.get(category));
        }
    }

    /**
     * 获取最佳分类
     */
    private static String getBestCategory(Map<String, Integer> scores) {
        String bestCategory = "selected";
        int maxScore = -1;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestCategory = entry.getKey();
            }
        }

        if (DEBUG_MODE) {
            Log.d(TAG, "最佳分类判断：最高分=" + maxScore + "，分类=" + bestCategory);
        }

        // 如果所有分数都很低，返回selected
        if (maxScore <= 1) { // selected有1分基础分
            return "selected";
        }

        return bestCategory;
    }

    /**
     * 基于用户信息评分
     */
    private static void scoreByUser(UserOnline user,
                                    Map<String, Integer> totalScores,
                                    Map<String, Integer> userScores,
                                    Map<String, List<String>> scoreDetails) {
        if (user == null) {
            return;
        }

        int userWeight = CATEGORY_WEIGHTS.get("USER_MATCH");
        String userName = user.getName() != null ? user.getName().toLowerCase() : "";


        // 检查用户名中是否包含分类关键词
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (userName.contains(keyword)) {
                    totalScores.put(category, totalScores.get(category) + userWeight);
                    userScores.put(category, userScores.get(category) + userWeight);

                    String detail = String.format("用户信息包含'%s': +%d分", keyword, userWeight);
                    scoreDetails.get(category).add(detail);

                    if (DEBUG_MODE) {
                        Log.d(TAG, category + "分类: " + detail);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 基于视频时长评分
     */
    private static void scoreByDuration(int duration,
                                        Map<String, Integer> totalScores,
                                        Map<String, Integer> durationScores,
                                        Map<String, List<String>> scoreDetails) {
        int durationWeight = CATEGORY_WEIGHTS.get("DURATION_MATCH");

        if (DEBUG_MODE) {
            Log.d(TAG, "开始时长评分，时长=" + duration + "秒，权重=" + durationWeight);
        }

        if (duration < 30) {
            // 超短视频：funny 或 social media
            addScoreWithDetail("funny", durationWeight * 2, totalScores, durationScores, scoreDetails,
                    "短视频(<30秒)倾向搞笑: +" + (durationWeight * 2) + "分");
            addScoreWithDetail("lifestyle", durationWeight, totalScores, durationScores, scoreDetails,
                    "短视频(<30秒)倾向生活: +" + durationWeight + "分");
        } else if (duration < 180) {
            // 30秒到3分钟：多种可能
            addScoreWithDetail("music", durationWeight, totalScores, durationScores, scoreDetails,
                    "中等视频(30-180秒)倾向音乐: +" + durationWeight + "分");
            addScoreWithDetail("gaming", durationWeight, totalScores, durationScores, scoreDetails,
                    "中等视频(30-180秒)倾向游戏: +" + durationWeight + "分");
            addScoreWithDetail("funny", durationWeight, totalScores, durationScores, scoreDetails,
                    "中等视频(30-180秒)倾向搞笑: +" + durationWeight + "分");
        } else if (duration < 600) {
            // 3-10分钟：education, lifestyle, movie剪辑
            addScoreWithDetail("education", durationWeight * 2, totalScores, durationScores, scoreDetails,
                    "长视频(3-10分钟)倾向教育: +" + (durationWeight * 2) + "分");
            addScoreWithDetail("lifestyle", durationWeight * 2, totalScores, durationScores, scoreDetails,
                    "长视频(3-10分钟)倾向生活: +" + (durationWeight * 2) + "分");
            addScoreWithDetail("movie", durationWeight, totalScores, durationScores, scoreDetails,
                    "长视频(3-10分钟)倾向影视: +" + durationWeight + "分");
        } else {
            // 10分钟以上：education, movie, documentary
            addScoreWithDetail("education", durationWeight * 3, totalScores, durationScores, scoreDetails,
                    "超长视频(>10分钟)倾向教育: +" + (durationWeight * 3) + "分");
            addScoreWithDetail("movie", durationWeight * 2, totalScores, durationScores, scoreDetails,
                    "超长视频(>10分钟)倾向影视: +" + (durationWeight * 2) + "分");
        }
    }

    /**
     * 基于视频分辨率/宽高比评分
     */
    private static void scoreByResolution(int width, int height,
                                          Map<String, Integer> totalScores,
                                          Map<String, Integer> resolutionScores,
                                          Map<String, List<String>> scoreDetails) {
        if (width == 0 || height == 0) {
            return;
        }

        int resolutionWeight = CATEGORY_WEIGHTS.get("RESOLUTION_MATCH");
        double aspectRatio = (double) width / height;

        if (DEBUG_MODE) {
            Log.d(TAG, "开始分辨率评分，分辨率=" + width + "x" + height +
                    "，宽高比=" + String.format("%.2f", aspectRatio) + "，权重=" + resolutionWeight);
        }

        // 判断视频比例
        if (aspectRatio < 0.7) {
            // 竖屏视频：常见于lifestyle, funny (短视频平台)
            addScoreWithDetail("lifestyle", resolutionWeight * 2, totalScores, resolutionScores, scoreDetails,
                    "竖屏视频(短视频平台): +" + (resolutionWeight * 2) + "分");
            addScoreWithDetail("funny", resolutionWeight * 2, totalScores, resolutionScores, scoreDetails,
                    "竖屏视频(短视频平台): +" + (resolutionWeight * 2) + "分");
        } else if (Math.abs(aspectRatio - 1.0) < 0.1) {
            // 正方形视频：常见于social media, lifestyle
            addScoreWithDetail("lifestyle", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "正方形视频: +" + resolutionWeight + "分");
            addScoreWithDetail("funny", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "正方形视频: +" + resolutionWeight + "分");
        } else if (Math.abs(aspectRatio - 1.78) < 0.2) {
            // 16:9标准比例：gaming, movie, education
            addScoreWithDetail("gaming", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "16:9标准比例(游戏/影视): +" + resolutionWeight + "分");
            addScoreWithDetail("movie", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "16:9标准比例(游戏/影视): +" + resolutionWeight + "分");
            addScoreWithDetail("education", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "16:9标准比例(教育): +" + resolutionWeight + "分");
        } else if (aspectRatio > 2.0) {
            // 超宽屏：movie, cinematic
            addScoreWithDetail("movie", resolutionWeight * 3, totalScores, resolutionScores, scoreDetails,
                    "超宽屏电影: +" + (resolutionWeight * 3) + "分");
        }

        // 分辨率判断
        if (width >= 1920 && height >= 1080) {
            // 高清视频：gaming, movie
            addScoreWithDetail("gaming", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "高清视频(1080p+): +" + resolutionWeight + "分");
            addScoreWithDetail("movie", resolutionWeight, totalScores, resolutionScores, scoreDetails,
                    "高清视频(1080p+): +" + resolutionWeight + "分");
        }
    }

    private static String getDeductionDetails(String category, Map<String, List<String>> scoreDetails) {
        return "无";
    }

    /**
     * 获取所有可用分类
     */
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>(CATEGORY_KEYWORDS.keySet());
        categories.add("selected");
        return categories;
    }

    /**
     * 获取分类的友好显示名称
     */
    public static String getCategoryDisplayName(String category) {
        if (category == null) {
            return "精选";
        }

        switch (category) {
            case "gaming": return "游戏";
            case "music": return "音乐";
            case "movie": return "影视";
            case "education": return "教育";
            case "lifestyle": return "生活";
            case "funny": return "搞笑";
            case "selected": return "精选";
            default: return category;
        }
    }

    /**
     * 获取分类的英文显示名称
     */
    public static String getCategoryEnglishName(String category) {
        if (category == null) {
            return "Featured";
        }

        switch (category) {
            case "gaming": return "Gaming";
            case "music": return "Music";
            case "movie": return "Movies";
            case "education": return "Education";
            case "lifestyle": return "Lifestyle";
            case "funny": return "Funny";
            case "selected": return "Featured";
            default: return category;
        }
    }

    /**
     * 验证分类是否有效
     */
    public static boolean isValidCategory(String category) {
        if (category == null) {
            return false;
        }
        List<String> allCategories = getAllCategories();
        return allCategories.contains(category);
    }

    /**
     * 简单版本的方法（兼容原始调用）
     */
    public static String simpleDetermineCategory(PexelsVideo pexelsVideo) {
        if (pexelsVideo == null) {
            return "selected";
        }

        List<String> tags = pexelsVideo.getTags();
        if (tags != null) {
            for (String tag : tags) {
                String tagLower = tag.toLowerCase();

                // 快速匹配
                if (tagLower.contains("game")) return "gaming";
                if (tagLower.contains("music")) return "music";
                if (tagLower.contains("movie") || tagLower.contains("film")) return "movie";
                if (tagLower.contains("educa") || tagLower.contains("tutorial") || tagLower.contains("learn")) return "education";
                if (tagLower.contains("lifestyle") || tagLower.contains("travel") || tagLower.contains("food")) return "lifestyle";
                if (tagLower.contains("funny") || tagLower.contains("comedy") || tagLower.contains("humor")) return "funny";
            }
        }

        return "selected";
    }

    /**
     * 启用或禁用调试模式
     */
    public static void enableDebugLog(boolean enable) {
        DEBUG_MODE = enable;
    }
}