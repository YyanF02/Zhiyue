package com.ZhiyueSecondHand.service;

import java.util.List;

public interface EmbeddingService {
    void saveGoodsInVectorStore(List<String> texts);

    void deleteAllGoodsFromVectorStore();

    void updateGoodsInVectorStore(String text);

    void deleteGoodsFromVectorStore(String doc_id);
}
