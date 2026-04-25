package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.model.Shipment;
import com.ecommerce.analytics_backend.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable String id) {
        return shipmentService.getShipmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        return ResponseEntity.ok(shipmentService.createShipment(shipment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable String id, @RequestBody Shipment shipment) {
        return ResponseEntity.ok(shipmentService.updateShipment(id, shipment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable String id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok().build();
    }
}
