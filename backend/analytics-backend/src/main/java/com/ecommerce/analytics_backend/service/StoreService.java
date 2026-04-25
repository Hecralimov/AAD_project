package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.model.Store;
import com.ecommerce.analytics_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Store> getStoreById(String id) {
        return storeRepository.findById(id);
    }

    @Transactional
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    @Transactional
    public Store updateStore(String id, Store store) {
        store.setId(id);
        return storeRepository.save(store);
    }

    @Transactional
    public Store updateStoreStatus(String id, String newStatus) {
        Store store = storeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Store not found: " + id));
        store.setStatus(newStatus);
        return storeRepository.save(store);
    }

    @Transactional
    public Store openStore(String id) {
        return updateStoreStatus(id, "OPEN");
    }

    @Transactional
    public Store closeStore(String id) {
        return updateStoreStatus(id, "CLOSED");
    }

    @Transactional
    public void deleteStore(String id) {
        storeRepository.deleteById(id);
    }
}
