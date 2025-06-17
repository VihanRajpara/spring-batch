package com.springbatch.bigdata.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.springbatch.bigdata.jpa.entity.MarketData;
import com.springbatch.bigdata.jpa.repository.MarketDataRepository;

@Service
public class MarketDataService {
	
	@Autowired
	private MarketDataRepository repository;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd  HH:mm:ss");
	
	private static final ClassPathResource RESOURCE = new ClassPathResource("Data.csv");

	public String importCsvForJPA() throws Exception {
		truncateTable();
		long oneMinuteNs = 60L * 1_000_000_000L;

		
		int count = 0;
		
		long startTime = System.nanoTime();
		try (var reader = new BufferedReader(
				new InputStreamReader(RESOURCE.getInputStream(), StandardCharsets.UTF_8))) {
			reader.readLine(); // skip header
			String line;
			while ((line = reader.readLine()) != null) {
				if ((System.nanoTime() - startTime) > oneMinuteNs) {
					break;
				}

				String[] parts = line.split(",");
				MarketData data = new MarketData();
				data.setDate(LocalDateTime.parse(parts[1], formatter).toLocalDate());
				data.setOpen(new BigDecimal(parts[2]));
				data.setHigh(new BigDecimal(parts[3]));
				data.setLow(new BigDecimal(parts[4]));
				data.setClose(new BigDecimal(parts[5]));
				data.setVolume(Long.parseLong(parts[6]));
				data.setBarCount(Integer.parseInt(parts[7]));
				data.setAverage(new BigDecimal(parts[8]));

				repository.save(data);
				count++;
			}
		}

		long endTime = System.nanoTime();
		long durationMs = (endTime - startTime) / 1_000_000;

		return "Inserted " + count + " records in " + durationMs + " ms.";
	}
	
	
	
	
	public String importCsvForJDBC() throws Exception {
		truncateTable();
        
        long oneMinuteNs = 60L * 1_000_000_000L;

        
        int count = 0;
        long startTime = System.nanoTime();
        try (var reader = new BufferedReader(new InputStreamReader(RESOURCE.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return "CSV is empty.";

            String[] headers = headerLine.split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                if ((System.nanoTime() - startTime) > oneMinuteNs) break;

                String[] values = line.split(",");
                Map<String, String> map = IntStream.range(0, headers.length)
                        .boxed()
                        .collect(Collectors.toMap(i -> headers[i], i -> values[i]));

                insertRow(map);
                count++;
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        return "Inserted " + count + " records in " + durationMs + " ms.";
    }

    private void insertRow(Map<String, String> row) {
    	String sql = """
                INSERT INTO market_data 
                (date, open, high, low, close, volume, barCount, average)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                LocalDateTime.parse(row.get("date"), formatter).toLocalDate(),
                new BigDecimal(row.get("open")),
                new BigDecimal(row.get("high")),
                new BigDecimal(row.get("low")),
                new BigDecimal(row.get("close")),
                Long.parseLong(row.get("volume")),
                Integer.parseInt(row.get("barCount")),
                new BigDecimal(row.get("average"))
        );
    }
    
    
    
    public String importCsvForJDBCBatch() throws Exception {
        truncateTable();
        long oneMinuteNs = 60L * 1_000_000_000L;
        long startTime = System.nanoTime();
        long now;
        int totalInserted = 0;
        int totalProcessed = 0;

        
        String[] headers;

        try (var reader = new BufferedReader(new InputStreamReader(RESOURCE.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return "CSV is empty.";
            headers = headerLine.split(",");

            String line;
            List<Object[]> chunk = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                now = System.nanoTime();
                long elapsed = now - startTime;
                long remaining = oneMinuteNs - elapsed;

                if (remaining <= 0) break;

                totalProcessed++;

                String[] values = line.split(",");
                Map<String, String> row = IntStream.range(0, headers.length)
                    .boxed()
                    .collect(Collectors.toMap(i -> headers[i], i -> values[i]));

                Object[] params = {
                    LocalDateTime.parse(row.get("date"), formatter).toLocalDate(),
                    new BigDecimal(row.get("open")),
                    new BigDecimal(row.get("high")),
                    new BigDecimal(row.get("low")),
                    new BigDecimal(row.get("close")),
                    Long.parseLong(row.get("volume")),
                    Integer.parseInt(row.get("barCount")),
                    new BigDecimal(row.get("average"))
                };

                chunk.add(params);

                // Dynamically compute estimated time per row
                long avgTimePerRow = elapsed / totalProcessed;
                long safeChunkSize = Math.max(10, Math.min(200, remaining / avgTimePerRow));

                if (chunk.size() >= safeChunkSize) {
                    batchInsert(chunk);
                    totalInserted += chunk.size();
                    chunk.clear();
                }
            }

            // Final flush if time remains
            now = System.nanoTime();
            if (!chunk.isEmpty() && now - startTime < oneMinuteNs) {
                batchInsert(chunk);
                totalInserted += chunk.size();
            }
        }

        long totalTimeMs = (System.nanoTime() - startTime) / 1_000_000;
        return "Inserted " + totalInserted + " records in " + totalTimeMs + " ms.";
    }


    private void batchInsert(List<Object[]> batchArgs) {
        String sql = """
            INSERT INTO market_data 
            (date, open, high, low, close, volume, barCount, average)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    
//    public String importUsingLoadData() throws Exception {
//        truncateTable();
//
//        
//        File tempFile = File.createTempFile("market-data-", ".csv");
//        tempFile.deleteOnExit();
//
//        try (InputStream is = resource.getInputStream();
//             OutputStream os = new FileOutputStream(tempFile)) {
//            is.transferTo(os);
//        }
//
//        String sql = "LOAD DATA LOCAL INFILE ? INTO TABLE market_data " +
//                     "FIELDS TERMINATED BY ',' " +
//                     "IGNORE 1 LINES " +
//                     "(id, date, open, high, low, close, volume, barCount, average)";
//
//        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, tempFile.getAbsolutePath());
//            stmt.execute();
//
//        } catch (SQLException e) {
//            System.err.println("ERROR executing LOAD DATA LOCAL INFILE:");
//            System.err.println("Message: " + e.getMessage());
//            System.err.println("SQLState: " + e.getSQLState());
//            System.err.println("ErrorCode: " + e.getErrorCode());
//            e.printStackTrace();  // full stack trace
//            return "Import failed: " + e.getMessage();
//        }
//
//        return "Import completed using LOAD DATA LOCAL INFILE.";
//    }






    
    public void truncateTable() {
        jdbcTemplate.execute("TRUNCATE TABLE market_data");
    }
}
