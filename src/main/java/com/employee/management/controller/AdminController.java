package com.employee.management.controller;

import com.employee.management.DTO.*;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.service.PDFService;
import com.employee.management.service.AdminService;
import com.employee.management.service.EmployeeService;
import com.employee.management.service.PayRollService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://hrm-service-fe-16185511.ap-south-1.elb.amazonaws.com/")
public class AdminController {
    @Autowired
    AdminService adminService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    PDFService pdfService;
    @Autowired
    PayRollService payRollService;

    @Autowired
    private AmountToWordsConverter amountToWordsConverter;

    private final SseEmitter emitter=new SseEmitter();


    //----------------------------------------------------------------
    //DashBoard
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminDashBoardData>loadData(){
        return new ResponseEntity<>(adminService.loadData(),HttpStatus.OK);
    }
    @GetMapping("/salary-graph")
    public ResponseEntity<List<AvgSalaryGraphResponse>> fetchSixMonthData(){
        return new ResponseEntity<>(adminService.getSalaryGraphDataForPastSixMonths(),HttpStatus.OK);
    }


    //----------------------------------------------------------------
    //Employee Operations
    @PostMapping("/add")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addEmployee(@RequestBody EmployeeDTO employeeDTO){
        return new ResponseEntity<>(adminService.addNewEmployee(employeeDTO), HttpStatus.CREATED);
    }
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeDTO>>fetchAllEmployee(){
        return new ResponseEntity<>(adminService.fetchAllActiveEmployees(),HttpStatus.OK);
    }
    @PutMapping("/edit/{empId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDTO> editEmployee(@PathVariable("empId") String empId,@RequestBody EmployeeDTO employeeDTO){
        return new ResponseEntity<>(adminService.editEmployee(empId,employeeDTO),HttpStatus.OK);
    }
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDTO>getEmployee(@RequestParam("empId")String empId){
        return new ResponseEntity<>(employeeService.getEmployee(empId),HttpStatus.FOUND);
    }
    @PutMapping("/change-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>changeStatusOfEmployee(@RequestParam("empId")String empId,
                                                        @RequestParam("status")String status){
        return new ResponseEntity<>(adminService.changeEmployeeStatus(empId,status),HttpStatus.OK);
    }
    @GetMapping("/get-designation/{id}")
    public ResponseEntity<String >getEmployeeDesignation(@PathVariable("id")String empId){
        return new ResponseEntity<>(adminService.fetchEmployeeDesignation(empId),HttpStatus.OK);
    }
    @PostMapping("/update-pf-no")
    public ResponseEntity<String> updatePFDetails(@RequestBody PfNumberUpdateRequest request){
        System.out.println(request);
        return new ResponseEntity<>(adminService.updatePfDetails(request),HttpStatus.OK);
    }



    //----------------------------------------------------------------
    //Payroll Operations
    @PostMapping("/add-payroll")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addNewPayRoll(@RequestBody PayrollDTO payrollDTO) {
        return new ResponseEntity<>(adminService.addPayroll(payrollDTO, payrollDTO.getEmployeeId()),HttpStatus.OK);
    }//Old
    @GetMapping("/view-payroll-by-id")
    public ResponseEntity<CtcData> getPayrollDetails(@RequestParam("empId")String empId){
        return new ResponseEntity<>(payRollService.getPayrollDetails(empId),HttpStatus.OK);
    }
    @PostMapping("/add-new-payroll")
    public ResponseEntity<String>addNewPayrollWithMinimalData(@RequestBody AddMonthlyPayRollRequest request){
        return new ResponseEntity<>(adminService.addMonthlyPayRoll(request),HttpStatus.OK);
    }
    @PostMapping("/preview-payslip")
    public ResponseEntity<byte[]> previewNewPayRoll(@RequestBody PayrollDTO payrollDTO) throws JRException {
        EmployeeDTO employee= employeeService.getEmployee(payrollDTO.getEmployeeId());
        PaySlip paySlip=new PaySlip();
        paySlip.setPayrollDTO(payrollDTO);
        paySlip.setEmployeeDTO(employee);
        return pdfService.generatePdfPreviewResponse(pdfService.generatePaySlipPdf(paySlip));
    }
    @PostMapping("/preview-new-payslip")
    public ResponseEntity<byte[]>previewMonthlyPayroll(@RequestBody AddMonthlyPayRollRequest request) throws JRException {
        return pdfService.generatePdfPreviewResponse(adminService.previewPayslipPdf(request));
    }

    @GetMapping("/leave-deduction")
    public ResponseEntity<CtcData>payDetailsWithLeaveDeduction(@RequestBody AddMonthlyPayRollRequest request){
        return new ResponseEntity<>(payRollService.getPayrollDetailsWithLeaveDeduction(request),HttpStatus.OK);
    }


    //----------------------------------------------------------------
    //Hike Operations
    @GetMapping("/hike-recommendations")
    public ResponseEntity<List<HikeEntityDTO>>fetchHikeRecommendations(){
        return new ResponseEntity<>(adminService.hikeRecommendations(),HttpStatus.OK);
    }
    @PostMapping("/approve-hike")
    public ResponseEntity<String>approveHike(@RequestBody HikeUpdateRequest request){
        return new ResponseEntity<>(adminService.updateHikeDetails(request),HttpStatus.OK);
    }

    @PostMapping("/update-hike")
    public ResponseEntity<HikeEntityDTO>giveHike(@RequestBody HikeUpdateRequest request){
        return new ResponseEntity<>(adminService.giveHike(request),HttpStatus.OK);
    }
    @PostMapping("/preview-hike")
    public ResponseEntity<byte[]>reviewHike(@RequestBody HikeUpdateRequest request){
        byte[] pdfBytes = adminService.previewHikeDetails(request);
        return pdfService.generatePdfPreviewResponse(pdfBytes);
    }
    @PostMapping("/edit-hike")
    public ResponseEntity<HikeEntityDTO>editHike(@RequestBody HikeEntityDTO hikeEntityDTO){
        return new ResponseEntity<>(adminService.editHikeLetter(hikeEntityDTO),HttpStatus.OK);
    }

}
