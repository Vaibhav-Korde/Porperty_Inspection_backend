package com.propertyinspection.dto;
import java.util.List;
public class ReportRequest {
 private String propertyName,address,unitNumber,ownerName,inspectorName,inspectionDate;
 private List<ProblemDto> problems;
 public String getPropertyName(){return propertyName;} public void setPropertyName(String v){propertyName=v;}
 public String getAddress(){return address;} public void setAddress(String v){address=v;}
 public String getUnitNumber(){return unitNumber;} public void setUnitNumber(String v){unitNumber=v;}
 public String getOwnerName(){return ownerName;} public void setOwnerName(String v){ownerName=v;}
 public String getInspectorName(){return inspectorName;} public void setInspectorName(String v){inspectorName=v;}
 public String getInspectionDate(){return inspectionDate;} public void setInspectionDate(String v){inspectionDate=v;}
 public List<ProblemDto> getProblems(){return problems;} public void setProblems(List<ProblemDto> v){problems=v;}
}