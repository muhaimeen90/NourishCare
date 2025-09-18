package com.nourishcare.visionservice.service;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DJL Semantic matching service - ONLY uses vector embeddings and cosine similarity
 */
@Service
public class SemanticMatchingService {
    
    private static final Logger logger = LoggerFactory.getLogger(SemanticMatchingService.class);
    
    private ZooModel<String, float[]> model;
    private final Map<String, float[]> embeddingCache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initialize() {
        try {
            logger.info("🚀 Initializing DJL Semantic Matching Service...");
            
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
                    .optEngine("PyTorch")
                    .build();
            
            model = ModelZoo.loadModel(criteria);
            logger.info("✅ DJL model loaded successfully");
            
            // Test with sample
            float[] test = getEmbedding("apple");
            logger.info("✅ Model test successful, embedding dimension: {}", test.length);
            
        } catch (Exception e) {
            logger.error("❌ Failed to load DJL model: {}", e.getMessage(), e);
            throw new RuntimeException("DJL model loading failed", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (model != null) {
            model.close();
            logger.info("🧹 Model resources cleaned up");
        }
    }
    
    /**
     * Find best USDA match using ONLY vector embeddings and cosine similarity
     */
    public SemanticMatch findBestMatch(String detectedLabel, List<String> usdaCandidates) {
        try {
            logger.info("🔍 Computing embeddings for '{}' vs {} candidates", detectedLabel, usdaCandidates.size());
            
            float[] labelEmbedding = getEmbedding(detectedLabel);
            
            String bestMatch = null;
            double bestSimilarity = 0.0;
            
            for (String candidate : usdaCandidates) {
                float[] candidateEmbedding = getEmbedding(candidate);
                double similarity = cosineSimilarity(labelEmbedding, candidateEmbedding);
                
                logger.debug("  '{}' → {:.3f}", candidate, similarity);
                
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestMatch = candidate;
                }
            }
            
            logger.info("🎯 Best match: '{}' → '{}' (cosine: {:.3f})", 
                       detectedLabel, bestMatch, bestSimilarity);
            
            return new SemanticMatch(bestMatch, bestSimilarity, "embeddings");
            
        } catch (Exception e) {
            logger.error("❌ Embedding matching failed: {}", e.getMessage(), e);
            throw new RuntimeException("Semantic matching failed", e);
        }
    }
    
    /**
     * Get vector embedding using DJL model (with caching)
     */
    private float[] getEmbedding(String text) throws TranslateException, ModelException {
        if (embeddingCache.containsKey(text)) {
            return embeddingCache.get(text);
        }
        
        try (Predictor<String, float[]> predictor = model.newPredictor()) {
            float[] embedding = predictor.predict(text);
            embeddingCache.put(text, embedding);
            return embedding;
        }
    }
    
    /**
     * Calculate cosine similarity between two vectors
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vector dimensions must match");
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    /**
     * Semantic match result
     */
    public static class SemanticMatch {
        private final String bestMatch;
        private final double similarity;
        private final String method;
        
        public SemanticMatch(String bestMatch, double similarity, String method) {
            this.bestMatch = bestMatch;
            this.similarity = similarity;
            this.method = method;
        }
        
        public String getBestMatch() { return bestMatch; }
        public double getSimilarity() { return similarity; }
        public String getMethod() { return method; }
    }
}
