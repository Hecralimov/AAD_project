package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.model.Shipment;
import com.ecommerce.analytics_backend.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    @Transactional(readOnly = true)
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Shipment> getShipmentById(String id) {
        return shipmentRepository.findById(id);
    }

    @Transactional
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment updateShipment(String id, Shipment shipment) {
        shipment.setId(id);
        return shipmentRepository.save(shipment);
    }

    @Transactional(readOnly = true)
    public Shipment getShipmentTracking(String orderId) {
        return shipmentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("No shipment found for order: " + orderId));
    }

    @Transactional
    public void deleteShipment(String id) {
        shipmentRepository.deleteById(id);
    }
}
