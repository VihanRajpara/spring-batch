package com.springbatch.bigdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springbatch.bigdata.service.MarketDataService;

@RestController
@RequestMapping("/market-data")
public class MarketDataController {
	@Autowired
	private MarketDataService service;

	@PostMapping("/jpa-import")
	public String importCsvForJPA() {
		try {
			return service.importCsvForJPA();
		} catch (Exception e) {
			return "Import failed: " + e.getMessage();
		}
	}
	
	
	@PostMapping("/jdbc-import")
	public String importCsvForJDBC() {
		try {
			return service.importCsvForJDBC();
		} catch (Exception e) {
			return "Import failed: " + e.getMessage();
		}
	}
	
	@PostMapping("/jdbc-batch-import")
	public String importCsvForJDBCBatch() {
	    try {
	        return service.importCsvForJDBCBatch();
	    } catch (Exception e) {
	        return "Batch import failed: " + e.getMessage();
	    }
	}
	
//	@PostMapping("/load-data")
//    public String importCsvUsingLoadData() {
//        try {
//            return service.importUsingLoadData();
//        } catch (Exception e) {
//            return "Import failed: " + e.getMessage();
//        }
//    }

}
