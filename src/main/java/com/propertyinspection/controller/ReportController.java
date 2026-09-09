package com.propertyinspection.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertyinspection.dto.ReportRequest;
import com.propertyinspection.service.PdfReportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
 private final ObjectMapper mapper; private final PdfReportService pdf;
 public ReportController(ObjectMapper mapper,PdfReportService pdf){this.mapper=mapper;this.pdf=pdf;}

 @GetMapping("/health")
 public ResponseEntity<String> health(){return ResponseEntity.ok("Property Inspection Backend is running");}

 @PostMapping(value="/generate",consumes=MediaType.MULTIPART_FORM_DATA_VALUE,produces=MediaType.APPLICATION_PDF_VALUE)
 public ResponseEntity<byte[]> generate(
  @RequestParam String propertyName,
  @RequestParam String address,
  @RequestParam(required=false,defaultValue="") String unitNumber,
  @RequestParam(required=false,defaultValue="") String ownerName,
  @RequestParam String inspectorName,
  @RequestParam String inspectionDate,
  @RequestParam(defaultValue="[]") String problems,
  @RequestParam(required=false) List<MultipartFile> photos) {

  try {
   ReportRequest r=new ReportRequest();
   r.setPropertyName(propertyName);r.setAddress(address);r.setUnitNumber(unitNumber);r.setOwnerName(ownerName);
   r.setInspectorName(inspectorName);r.setInspectionDate(inspectionDate);
   r.setProblems(mapper.readValue(problems,mapper.getTypeFactory().constructCollectionType(List.class,com.propertyinspection.dto.ProblemDto.class)));
   byte[] result=pdf.generate(r,photos==null?List.of():photos);
   String safe=propertyName.replaceAll("[^a-zA-Z0-9._-]","_");
   return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"Property_Inspection_Report_"+safe+".pdf\"").contentType(MediaType.APPLICATION_PDF).body(result);
  } catch(Exception e) {
   throw new IllegalArgumentException("Could not generate report: "+e.getMessage(),e);
  }
 }
}