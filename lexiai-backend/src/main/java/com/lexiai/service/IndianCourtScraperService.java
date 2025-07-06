package com.lexiai.service;

import com.lexiai.exception.WebScrapingException;
import com.lexiai.model.LegalCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IndianCourtScraperService {
    
    private static final String ECOURTS_BASE_URL = "https://ecourts.gov.in/ecourts_home/";
    private static final String SUPREME_COURT_URL = "https://main.sci.gov.in/";
    private static final String DELHI_HC_URL = "http://delhihighcourt.nic.in/";
    
    public List<LegalCase> scrapeECourts(String caseNumber, String courtName) {
        List<LegalCase> cases = new ArrayList<>();
        WebDriver driver = null;
        
        try {
            driver = setupWebDriver();
            
            // Navigate to eCourts website
            driver.get(ECOURTS_BASE_URL);
            
            // Wait for page to load and search for case
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Look for search functionality (this varies by court)
            if (searchCaseInECourts(driver, wait, caseNumber, courtName)) {
                LegalCase legalCase = extractCaseDetailsFromECourts(driver);
                if (legalCase != null) {
                    cases.add(legalCase);
                }
            }
            
        } catch (Exception e) {
            throw new WebScrapingException("Error scraping eCourts: " + e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        
        return cases;
    }
    
    public List<LegalCase> scrapeSupremeCourt(String caseNumber) {
        List<LegalCase> cases = new ArrayList<>();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String searchUrl = SUPREME_COURT_URL + "case_status";
            HttpGet request = new HttpGet(searchUrl);
            request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String html = EntityUtils.toString(response.getEntity());
                Document doc = Jsoup.parse(html);
                
                // Supreme Court specific scraping logic
                LegalCase legalCase = extractFromSupremeCourt(doc, caseNumber);
                if (legalCase != null) {
                    cases.add(legalCase);
                }
            }
            
        } catch (Exception e) {
            throw new WebScrapingException("Error scraping Supreme Court: " + e.getMessage(), e);
        }
        
        return cases;
    }
    
    public List<LegalCase> scrapeHighCourt(String caseNumber, String courtName) {
        List<LegalCase> cases = new ArrayList<>();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String baseUrl = getHighCourtUrl(courtName);
            if (baseUrl != null) {
                HttpGet request = new HttpGet(baseUrl);
                request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String html = EntityUtils.toString(response.getEntity());
                    Document doc = Jsoup.parse(html);
                    
                    LegalCase legalCase = extractFromHighCourt(doc, caseNumber, courtName);
                    if (legalCase != null) {
                        cases.add(legalCase);
                    }
                }
            }
            
        } catch (Exception e) {
            throw new WebScrapingException("Error scraping High Court: " + e.getMessage(), e);
        }
        
        return cases;
    }
    
    private WebDriver setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        return new ChromeDriver(options);
    }
    
    private boolean searchCaseInECourts(WebDriver driver, WebDriverWait wait, String caseNumber, String courtName) {
        try {
            // Look for case number input field
            WebElement caseNumberInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[contains(@placeholder, 'case') or contains(@name, 'case')]")
                )
            );
            
            caseNumberInput.clear();
            caseNumberInput.sendKeys(caseNumber);
            
            // Find and click search button
            WebElement searchButton = driver.findElement(
                By.xpath("//button[contains(text(), 'Search') or contains(text(), 'Submit')]")
            );
            searchButton.click();
            
            // Wait for results
            Thread.sleep(2000);
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private LegalCase extractCaseDetailsFromECourts(WebDriver driver) {
        try {
            LegalCase legalCase = new LegalCase();
            
            // Extract case details (this is a generic implementation)
            List<WebElement> detailElements = driver.findElements(By.xpath("//table//tr"));
            
            for (WebElement element : detailElements) {
                String text = element.getText().toLowerCase();
                
                if (text.contains("case number")) {
                    legalCase.setCaseNumber(extractValue(element.getText()));
                } else if (text.contains("case title") || text.contains("title")) {
                    legalCase.setTitle(extractValue(element.getText()));
                } else if (text.contains("petitioner")) {
                    legalCase.setPlaintiff(extractValue(element.getText()));
                } else if (text.contains("respondent")) {
                    legalCase.setDefendant(extractValue(element.getText()));
                } else if (text.contains("judge")) {
                    legalCase.setJudgeName(extractValue(element.getText()));
                } else if (text.contains("status")) {
                    legalCase.setCaseStatus(extractValue(element.getText()));
                } else if (text.contains("filing date")) {
                    String dateStr = extractValue(element.getText());
                    legalCase.setFilingDate(parseDate(dateStr));
                }
            }
            
            legalCase.setSourceType("Web Scraping");
            legalCase.setSourceUrl(driver.getCurrentUrl());
            
            return legalCase.getTitle() != null ? legalCase : null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private LegalCase extractFromSupremeCourt(Document doc, String caseNumber) {
        try {
            LegalCase legalCase = new LegalCase();
            
            // Supreme Court specific extraction logic
            Elements titleElements = doc.select("h1, h2, .case-title");
            if (!titleElements.isEmpty()) {
                legalCase.setTitle(titleElements.first().text());
            }
            
            // Extract other details based on Supreme Court website structure
            Elements detailsTable = doc.select("table tr");
            for (Element row : detailsTable) {
                String rowText = row.text().toLowerCase();
                if (rowText.contains("petitioner")) {
                    legalCase.setPlaintiff(extractValueFromRow(row));
                } else if (rowText.contains("respondent")) {
                    legalCase.setDefendant(extractValueFromRow(row));
                }
            }
            
            legalCase.setCaseNumber(caseNumber);
            legalCase.setCourtName("Supreme Court of India");
            legalCase.setJurisdiction("National");
            legalCase.setSourceType("Web Scraping");
            legalCase.setSourceUrl(SUPREME_COURT_URL);
            
            return legalCase.getTitle() != null ? legalCase : null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private LegalCase extractFromHighCourt(Document doc, String caseNumber, String courtName) {
        try {
            LegalCase legalCase = new LegalCase();
            
            // Generic High Court extraction logic
            Elements titleElements = doc.select("h1, h2, .case-title, .title");
            if (!titleElements.isEmpty()) {
                legalCase.setTitle(titleElements.first().text());
            }
            
            legalCase.setCaseNumber(caseNumber);
            legalCase.setCourtName(courtName);
            legalCase.setJurisdiction("State");
            legalCase.setSourceType("Web Scraping");
            legalCase.setSourceUrl(getHighCourtUrl(courtName));
            
            return legalCase.getTitle() != null ? legalCase : null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String getHighCourtUrl(String courtName) {
        String normalizedName = courtName.toLowerCase();
        
        if (normalizedName.contains("delhi")) {
            return DELHI_HC_URL;
        } else if (normalizedName.contains("bombay") || normalizedName.contains("mumbai")) {
            return "http://bombayhighcourt.nic.in/";
        } else if (normalizedName.contains("madras") || normalizedName.contains("chennai")) {
            return "http://hcmadras.tn.nic.in/";
        } else if (normalizedName.contains("calcutta") || normalizedName.contains("kolkata")) {
            return "http://calcuttahighcourt.nic.in/";
        }
        
        // Add more High Court URLs as needed
        return ECOURTS_BASE_URL; // Fallback to eCourts
    }
    
    private String extractValue(String text) {
        String[] parts = text.split(":");
        return parts.length > 1 ? parts[1].trim() : text.trim();
    }
    
    private String extractValueFromRow(Element row) {
        Elements cells = row.select("td");
        return cells.size() > 1 ? cells.get(1).text() : "";
    }
    
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        // Common Indian date formats
        String[] patterns = {
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd.MM.yyyy",
            "yyyy-MM-dd",
            "MM/dd/yyyy"
        };
        
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Try next pattern
            }
        }
        
        return null;
    }
}
