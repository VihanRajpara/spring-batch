package com.springbatch.bigdata.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbatch.bigdata.jpa.entity.MarketData;

public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    // You can add custom query methods here if needed
}
