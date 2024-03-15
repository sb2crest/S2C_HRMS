package com.employee.management.controller;

import com.employee.management.DTO.*;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.filter.JWTAuthFilter;
import com.employee.management.service.AdminService;
import com.employee.management.service.EmployeeService;
import com.employee.management.service.PDFService;
import com.employee.management.service.PayRollService;
import com.employee.management.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @InjectMocks
    AdminController adminController;
    @MockBean
    AdminService adminService;
    @MockBean
    EmployeeService employeeService;
    @MockBean
    PDFService pdfService;
    @MockBean
    AmountToWordsConverter answerToWordsConverter;
    @MockBean
    JWTAuthFilter jwtFilter;
    @MockBean
    JWTService jwtService;
    @MockBean
    PayRollService payRollService;
    @Autowired
    WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void testAddEmployee() throws Exception {
        String resultStr="Employee Added";
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("EMP001");
        employeeDTO.setEmployeeName("John Doe");
        employeeDTO.setDesignation("Software Engineer");

        when(adminService.addNewEmployee(employeeDTO)).thenReturn(resultStr);
        mockMvc.perform(post("/admin/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    void testLoadData() throws Exception {
        AdminDashBoardData adminDashBoardData = new AdminDashBoardData();
        adminDashBoardData.setAverageSalary("50000");
        adminDashBoardData.setTodayDate("2024-03-01");
        adminDashBoardData.setNoOfEmployees(100);

        when(adminService.loadData()).thenReturn(adminDashBoardData);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(50000.00))
                .andExpect(jsonPath("$.noOfEmployees").value(100));

        verify(adminService, times(1)).loadData();
    }

    @Test
    void testFetchAllEmployee() throws Exception {
        EmployeeDTO employeeDTO1 = new EmployeeDTO();
        employeeDTO1.setEmployeeID("EMP001");
        employeeDTO1.setEmployeeName("John Doe");
        employeeDTO1.setDesignation("Software Engineer");

        EmployeeDTO employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setEmployeeID("EMP002");
        employeeDTO2.setEmployeeName("Jane Smith");
        employeeDTO2.setDesignation("QA Engineer");

        List<EmployeeDTO> employeeDTOList = Arrays.asList(employeeDTO1, employeeDTO2);
        when(adminService.fetchAllActiveEmployees()).thenReturn(employeeDTOList);

        mockMvc.perform(get("/admin/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employeeDTOList)))
                .andExpect(status().isOk());
    }

    @Test
    void editEmployee() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("EMP001");
        employeeDTO.setEmployeeName("John Doe");
        employeeDTO.setDesignation("Software Engineer");

        when(adminService.editEmployee(any(),any())).thenReturn(employeeDTO);

        mockMvc.perform(put("/admin/edit/EMP001")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isOk());

    }

    @Test
    void getEmployee() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("EMP001");
        employeeDTO.setEmployeeName("John Doe");
        employeeDTO.setDesignation("Software Engineer");

        when(employeeService.getEmployee(any())).thenReturn(employeeDTO);

        mockMvc.perform(get("/admin/get").param("empId","EMP001")
              .contentType(MediaType.APPLICATION_JSON)
              .content(new ObjectMapper().writeValueAsString(employeeDTO)))
              .andExpect(status().isFound());
    }

    @Test
    public void changeStatusOfEmployee() throws Exception {
        String empId = "S2C1";
        String status = "active";
        String expectedResponse = "Employee status changed successfully";

        when(adminService.changeEmployeeStatus(any(), any())).thenReturn(expectedResponse);
        mockMvc.perform(put("/admin/change-status")
                        .param("empId", empId)
                        .param("status", status)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(expectedResponse))
                .andExpect(status().isOk());

    }

    @Test
    void getEmployeeDesignation() throws Exception {
        String empId = "S2C1";
        String expectedResponse = "Software Engineer";

        when(adminService.fetchEmployeeDesignation(empId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/admin/get-designation/S2C1")
              .contentType(MediaType.TEXT_PLAIN))
              .andExpect(content().string(expectedResponse))
              .andExpect(status().isOk());
    }


    @Test
    void testFetchHikeRecommendations() throws Exception {
        List<HikeEntityDTO> hikeRecommendations = new ArrayList<>();
        HikeEntityDTO hike1 = new HikeEntityDTO();
        hike1.setId(1L);
        hike1.setEmployeeId("emp1");
        hike1.setPrevSalary("1000");
        hike1.setNewSalary("1500");
        hike1.setHikePercentage("50%");
        hike1.setReason("Reason1");
        hike1.setApprovedBy("Manager1");
        hike1.setApprovedDate("2024-03-01");
        hike1.setStatus(true);
        hike1.setEffectiveDate("2024-04-01");

        HikeEntityDTO hike2 = new HikeEntityDTO();
        hike2.setId(2L);
        hike2.setEmployeeId("emp2");
        hike2.setPrevSalary("1200");
        hike2.setNewSalary("1800");
        hike2.setHikePercentage("50%");
        hike2.setReason("Reason2");
        hike2.setApprovedBy("Manager2");
        hike2.setApprovedDate("2024-03-02");
        hike2.setStatus(false);
        hike2.setEffectiveDate("2024-04-02");

        when(adminService.hikeRecommendations()).thenReturn(hikeRecommendations);

        mockMvc.perform(get("/admin/hike-recommendations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(hikeRecommendations)))
            .andExpect(status().isOk());
    }

    @Test
    void testAddNewPayRoll() throws Exception {
        PayrollRequest request = new PayrollRequest();
        request.setEmployeeId("EMP001");
        request.setPayPeriod("January 2024");

        PayrollDTO payrollDTO = new PayrollDTO();
        payrollDTO.setEmployeeId(request.getEmployeeId());
        payrollDTO.setPayPeriod(request.getPayPeriod());

        when(adminService.addPayroll(any(), anyString())).thenReturn("Successfully send mail to"+payrollDTO.getEmployeeId());

        mockMvc.perform(post("/admin/add-payroll")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(new ObjectMapper().writeValueAsString(payrollDTO)))
              .andExpect(status().isOk());
    }

    @Test
    void testAddNewPayrollWithMinimalData() throws Exception {
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP001");
        request.setPayDate("January 2024");
        request.setPayDate("2024-03-01");
        request.setLopDays("1");
        PayrollDTO payrollDTO = new PayrollDTO();
        payrollDTO.setEmployeeId(request.getEmployeeId());
        payrollDTO.setPayDate(request.getPayDate());
        payrollDTO.setPayPeriod(request.getPayPeriod());

        when(adminService.addPayroll(any(), anyString())).thenReturn("Successfully send mail to"+payrollDTO.getEmployeeId());

        mockMvc.perform(post("/admin/add-new-payroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(payrollDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void fetchSixMonthData() throws Exception {
        List<AvgSalaryGraphResponse> avgSalaryGraphResponseList = List.of(new AvgSalaryGraphResponse("Jan","1000"),new AvgSalaryGraphResponse("Feb","1200"));
        when(adminService.getSalaryGraphDataForPastSixMonths()).thenReturn(avgSalaryGraphResponseList);

        mockMvc.perform(get("/admin/salary-graph")
              .contentType(MediaType.APPLICATION_JSON)
              .content(new ObjectMapper().writeValueAsString(avgSalaryGraphResponseList)))
              .andExpect(status().isOk());
    }
    @Test
    void testUpdatePFDetails() throws Exception {

        String responseMessage = "PF details updated successfully";
        when(adminService.updatePfDetails(any(PfNumberUpdateRequest.class))).thenReturn(responseMessage);
        PfNumberUpdateRequest request = new PfNumberUpdateRequest();
        request.setEmployeeId("EMP001");
        request.setPfNumber("PF654321");

        String requestJson = new ObjectMapper().writeValueAsString(request);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/admin/update-pf-no")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage))
                .andReturn();
        verify(adminService, times(1)).updatePfDetails(any(PfNumberUpdateRequest.class));
    }

    @Test
    void testUpdateHike() throws Exception {
        HikeUpdateRequest hikeUpdateRequest = new HikeUpdateRequest();
        hikeUpdateRequest.setEmployeeId("S2C1");
        hikeUpdateRequest.setPercentage("10%");
        hikeUpdateRequest.setNewPosition("Manager");
        hikeUpdateRequest.setReason("Performance");
        hikeUpdateRequest.setApprovedBy("HR");
        hikeUpdateRequest.setEffectiveDate("2024-03-01");

        String expectedResponse = "Hike Number updated successfully";

        when(adminService.updateHikeDetails(any())).thenReturn(expectedResponse);

        String requestJson = new ObjectMapper().writeValueAsString(hikeUpdateRequest);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/admin/update-hike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testReviewHike() throws Exception {
        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        when(adminService.previewHikeDetails(any(HikeUpdateRequest.class))).thenReturn(pdfBytes);

        byte[] pdfPreviewBytes = "PDF_PREVIEW_CONTENT".getBytes();
        when(pdfService.generatePdfPreviewResponse(pdfBytes)).thenReturn(ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfPreviewBytes));

        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId("EMP001");
        request.setPercentage("25");

        String requestJson = new ObjectMapper().writeValueAsString(request);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/admin/preview-hike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfPreviewBytes))
                .andReturn();

        verify(adminService, times(1)).previewHikeDetails(any(HikeUpdateRequest.class));

        verify(pdfService, times(1)).generatePdfPreviewResponse(pdfBytes);
    }
    @Test
    void testApproveHike() throws Exception {
        String requestJson = "{\"employeeId\":\"S2C1\",\"percentage\":\"10%\",\"newPosition\":\"Manager\",\"reason\":\"Performance\",\"approvedBy\":\"HR\",\"effectiveDate\":\"2024-03-01\"}";

        when(adminService.updateHikeDetails(any())).thenReturn("Mail sent Successfully");

        mockMvc.perform(post("/admin/approve-hike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Mail sent Successfully"));
    }
    @Test
    void testEditHike() throws Exception {

        HikeEntityDTO hikeEntityDTO = new HikeEntityDTO();
        hikeEntityDTO.setEmployeeId("S2C1");
        hikeEntityDTO.setHikePercentage("10%");
        hikeEntityDTO.setNewPosition("Manager");
        hikeEntityDTO.setReason("Performance");
        hikeEntityDTO.setApprovedBy("HR");
        hikeEntityDTO.setEffectiveDate("2024-03-01");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(hikeEntityDTO);

        HikeEntityDTO updatedHikeEntityDTO = new HikeEntityDTO();
        when(adminService.editHikeLetter(any())).thenReturn(updatedHikeEntityDTO);

        mockMvc.perform(post("/admin/edit-hike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPayrollDetails() throws Exception {
        String empId= "EMP01";
        when(payRollService.getPayrollDetails(eq(empId))).thenReturn(new CtcData());

        mockMvc.perform(get("/admin/view-payroll-by-id")
                        .param("empId", empId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPreviewNewPayRoll() throws Exception {
        PayrollDTO dto = new PayrollDTO();
        dto.setEmployeeId("1");
        EmployeeDTO employeeDto = new EmployeeDTO();
        byte[] data = new byte[10];
        PaySlip paySlip = new PaySlip();
        paySlip.setPayrollDTO(dto);
        paySlip.setEmployeeDTO(employeeDto);

        when(employeeService.getEmployee(any())).thenReturn(employeeDto);
        when(pdfService.generatePaySlipPdf(any(PaySlip.class))).thenReturn(data);


        mockMvc.perform(post("/admin/preview-payslip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":1}"))
               .andExpect(status().isOk());
    }

    @Test
    void testPreviewMonthlyPayroll() throws Exception {
        AddMonthlyPayRollRequest request = new AddMonthlyPayRollRequest();
        byte[] data = new byte[10];

        when(adminService.previewPayslipPdf(any(AddMonthlyPayRollRequest.class))).thenReturn(data);

        mockMvc.perform(post("/admin/preview-new-payslip")
                       .contentType(MediaType.APPLICATION_JSON)
                .content("{\"propertyName\":\"value\"}"))
              .andExpect(status().isOk());
    }
}