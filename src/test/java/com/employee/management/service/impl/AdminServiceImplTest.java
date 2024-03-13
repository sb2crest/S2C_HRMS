package com.employee.management.service.impl;

import com.employee.management.DTO.*;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.DateTimeConverter;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.*;
import com.employee.management.repository.*;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.PayRollService;
import com.employee.management.util.Formatters;
import com.employee.management.service.PDFService;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.EmailBodyBuilder;
import jakarta.mail.MessagingException;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static io.jsonwebtoken.lang.Assert.notNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {
    @InjectMocks
    private AdminServiceImpl adminService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PayrollRepository payrollRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private Mapper mapper;

    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private PDFService pdfService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DateTimeConverter dateTimeConverter;
    @Mock
    private HikeRepository hikeRepository;
    @Mock
    private CtcCalculator calculator;
    @Mock
    Formatters formatters;
    @Mock
    EmailBodyBuilder emailBodyBuilder;
    @Mock
    PayRollService payRollService;
    @Mock
    AmountToWordsConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminServiceImpl(employeeRepository, roleRepository,
                payrollRepository, statusRepository,
                hikeRepository, dateTimeConverter,
                mapper, emailSenderService,
                passwordEncoder, pdfService,
                emailBodyBuilder, calculator,
                formatters, converter);
    }

    @Test
    void testAddNewEmployee() {
        EmployeeDTO employeeDTO = getEmployeeDTO();
        Employee employee = getEmployee();
        Role role=new Role();
        role.setRoleId(2L);
        role.setName("ROLE_USER");
        Status status=new Status();
        status.setStatusID(1L);
        status.setName("active");
        when(mapper.convertToEmployeeEntity(any(EmployeeDTO.class))).thenReturn(employee);
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(statusRepository.findById(anyLong())).thenReturn(Optional.of(status));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(emailBodyBuilder.getBodyForAccountCreationMail(anyString(),anyString(),anyString()))
                .thenReturn("");
        String result = adminService.addNewEmployee(employeeDTO);

        assertEquals("Employee Added", result);
        verify(emailSenderService).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    private static EmployeeDTO getEmployeeDTO() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("EMP001");
        employeeDTO.setEmployeeName("John Doe");
        employeeDTO.setDesignation("Software Engineer");
        employeeDTO.setLocation("New York");
        employeeDTO.setBankName("ABC Bank");
        employeeDTO.setAccountNo("1234567890");
        employeeDTO.setDateOfJoin("2024-03-04");
        employeeDTO.setPassword("password");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setUanNumber("1234567890");
        employeeDTO.setPfNumber("PF12345");
        return employeeDTO;
    }
    static Employee getEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeID("EMP001");
        employee.setEmployeeName("John Doe");
        employee.setDesignation("Software Engineer");
        employee.setLocation("New York");
        employee.setBankName("ABC Bank");
        employee.setAccountNo("1234567890");
        employee.setPassword("password");
        employee.setGrossSalary(200000D);
        employee.setEmail("john.doe@example.com");
        employee.setUanNumber("1234567890");
        employee.setPfNumber("PF12345");
        return employee;
    }



    @Test
    public void testLoadData() {
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth previousYearMonth = currentYearMonth.minusMonths(1);
        String previousMonthFormatted = previousYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + previousYearMonth.getYear();
        List<Payroll> payrolls = Arrays.asList(testPayroll(),testPayroll());
        String averageSalary = "8,500.00";
        int noOfEmployees = 10;

        when(payrollRepository.getPayDetails(previousMonthFormatted)).thenReturn(Optional.of(payrolls));
        when(employeeRepository.findAll()).thenReturn(Collections.nCopies(noOfEmployees, new Employee()));
        when(Formatters.formatAmountWithCommas(anyDouble())).thenReturn(averageSalary);


        AdminDashBoardData result = adminService.loadData();


        verify(payrollRepository).getPayDetails(previousMonthFormatted);
        verify(employeeRepository).findAll();
       assertEquals(averageSalary, result.getAverageSalary());
        assertEquals(noOfEmployees, result.getNoOfEmployees());

    }
    @Test
    public void testLoadData_throwsException() {
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth previousYearMonth = currentYearMonth.minusMonths(1);
        String previousMonthFormatted = previousYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + previousYearMonth.getYear();
        List<Payroll> payrolls = Arrays.asList(testPayroll(),testPayroll());
        double averageSalary = 8500.0;
        int noOfEmployees = 10;

        when(payrollRepository.getPayDetails(previousMonthFormatted)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.loadData());

        verify(payrollRepository).getPayDetails(previousMonthFormatted);
    }

    private PayrollDTO testPayrollDTO() {
        PayrollDTO payrollDTO = new PayrollDTO();
        payrollDTO.setId(1L);
        payrollDTO.setPayPeriod("January 2024");
        payrollDTO.setPayDate("2024-01-31");
        payrollDTO.setEmployeeId("EMP001");

        payrollDTO.setGrossEarnings("8500");

        payrollDTO.setTotalDaysPaid(31);
        payrollDTO.setTotalLopDays(0);
        return payrollDTO;
    }
    private Payroll testPayroll() {
        Payroll payroll = new Payroll();
        payroll.setId(1L);
        payroll.setPayPeriod("January 2024");
        payroll.setPayDate(new Date());
        payroll.setEmployee(getEmployee());
        payroll.setGrossEarnings(8500D);
        payroll.setTotalPaidDays(31);
        payroll.setTotalLopDays(0);
        return payroll;
    }


    @Test
    void fetchAllActiveEmployees() {
        List<Employee> employees = new ArrayList<>();
        Status status = new Status();
        status.setName("active");
        Employee employee1 = getEmployee();
        Employee employee2 = getEmployee();
        employee1.setStatus(status);
        employee2.setStatus(status);
        Employee employee3 = getEmployee();
        status.setName("inactive");
        employee3.setStatus(status);
        employees=List.of(employee1,employee2,employee3);
        System.out.println(employee3);


        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeDTO> employeeDTOList=adminService.fetchAllActiveEmployees();
        System.out.println(employeeDTOList);
        verify(employeeRepository).findAll();

    }

    @Test
    void editEmployee() {
        String empId = "EMP01";
        EmployeeDTO employeeDTO=getEmployeeDTO();
        employeeDTO.setEmployeeName("Banner");
        Employee employee=getEmployee();
        employee.setEmployeeID(empId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(mapper.convertToEmployeeDTO(any(Employee.class))).thenReturn(employeeDTO);

        EmployeeDTO result= adminService.editEmployee(empId,employeeDTO);

        verify(employeeRepository).findById(empId);
        verify(employeeRepository).save(any(Employee.class));
        System.out.println(employeeDTO.getEmployeeName());
        assertEquals("Banner",result.getEmployeeName());
    }
    @Test
    void editEmployee_throwsException() {
        String empId = "EMP01";
        EmployeeDTO employeeDTO=getEmployeeDTO();
        employeeDTO.setEmployeeName("Banner");
        Employee employee=getEmployee();
        employee.setEmployeeID(empId);
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> adminService.editEmployee(empId, employeeDTO));
        verify(employeeRepository).findById(empId);
    }


    @Test
    void changeEmployeeStatus() {
        String resultStr ="Employee status changed successfully";
        String empId ="EMP001";
        String newStatus ="inactive";
        Employee employee =getEmployee();
        Status status = new Status();
        status.setName(newStatus);
        employee.setStatus(status);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(statusRepository.findByName(newStatus.toLowerCase())).thenReturn(Optional.of(status));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        String result = adminService.changeEmployeeStatus(empId,newStatus);
        assertEquals(resultStr,result);
        assertEquals("inactive",employee.getStatus().getName());
        verify(employeeRepository).findById(anyString());
    }

    @Test
    void changeEmployeeStatus_ThrowsEmployeeNotFound() {
        String resultStr ="Employee status changed successfully";
        String empId ="EMP01";
        String newStatus ="inactive";
        Employee employee =getEmployee();
        Status status = new Status();
        status.setName(newStatus);
        employee.setStatus(status);
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> adminService.changeEmployeeStatus(empId,newStatus));
        verify(employeeRepository).findById(anyString());
    }
    @Test
    void changeEmployeeStatus_throwsInvalidStatus() {
        String resultStr ="Employee status changed successfully";
        String empId ="EMP01";
        String newStatus ="inactive";
        Employee employee =getEmployee();
        Status status = new Status();
        status.setName(newStatus);
        employee.setStatus(status);

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(statusRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> adminService.changeEmployeeStatus(empId,newStatus));
        verify(statusRepository).findByName(anyString());
    }

    @Test
    void changeEmployeeStatus_catchBlock() {
        String resultStr ="Employee status changed successfully";
        String empId ="EMP001";
        String newStatus ="inactive";
        Employee employee =getEmployee();
        Status status = new Status();
        status.setName(newStatus);
        employee.setStatus(status);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(statusRepository.findByName(newStatus.toLowerCase())).thenReturn(Optional.of(status));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        String result = adminService.changeEmployeeStatus(empId,newStatus);
        assertEquals(resultStr,result);
        assertEquals("inactive",employee.getStatus().getName());
        verify(employeeRepository).findById(anyString());
    }

    @Test
    public void testAddPayroll() {
        String empId = "EMP001";
        PayrollDTO payroll = testPayrollDTO();
        Employee employee = getEmployee();
        employee.setEmployeeID(empId);
        employee.setGrossSalary(5000D);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(anyString(), any())).thenReturn(Optional.empty());


        PayrollDTO payrollDTO = new PayrollDTO();

        payrollDTO.setEmployeeId(empId);
        payrollDTO.setGrossEarnings("5000");
        payrollDTO.setPayDate("29-Feb-2024");
        payrollDTO.setPayPeriod("February 2024");
        payrollDTO.setLeaveDeduction("200");

        when(mapper.convertToPayroll(any())).thenReturn(new Payroll());


        String result = adminService.addPayroll(payrollDTO, empId);

        assertNotNull(result);
        assertEquals("Successfully send mail to "+employee.getEmail(), result);
        verify(employeeRepository).findById(empId);
        verify(payrollRepository).getPayPeriodDetails(anyString(), any());
        verify(payrollRepository).save(any(Payroll.class));

    }

    @Test
    public void testAddPayroll_throwsEmployeeNotFound() {
        String empId = "employeeId";
        PayrollDTO payrollDTO = new PayrollDTO();

        lenient().when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> {
            adminService.addPayroll(payrollDTO, empId);
        });
        verify(employeeRepository).findById(empId);
    }


    @Test
    public void testAddPayroll_throwsExistingPayPeriod() {
        String empId = "EMP001";
        String payPeriod = "January 2024";
        PayrollDTO payrollDTO = new PayrollDTO();
        payrollDTO.setPayPeriod(payPeriod);

        Employee employee = new Employee();
        employee.setEmployeeID(empId);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));

        when(payrollRepository.getPayPeriodDetails(anyString(), any())).thenReturn(Optional.of(new Payroll()));

        assertThrows(CompanyException.class, () -> adminService.addPayroll(payrollDTO, empId));

        verify(payrollRepository).getPayPeriodDetails(anyString(),any());
    }
    @Test
    public void testAddPayroll_catchBlock() throws JRException {
        String empId = "EMP001";
        PayrollDTO payroll = testPayrollDTO();
        Employee employee = getEmployee();
        employee.setEmployeeID(empId);
        employee.setGrossSalary(5000D);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(anyString(), any())).thenReturn(Optional.empty());


        PayrollDTO payrollDTO = new PayrollDTO();

        payrollDTO.setEmployeeId(empId);
        payrollDTO.setGrossEarnings("5000");
        payrollDTO.setPayDate("29-Feb-2024");
        payrollDTO.setPayPeriod("February 2024");
        payrollDTO.setLeaveDeduction("200");

        when(mapper.convertToPayroll(any())).thenReturn(new Payroll());
        when(pdfService.generatePaySlipPdf(any())).thenThrow(new JRException("Problem in sending mail"));

        CompanyException exception = assertThrows(CompanyException.class, () -> {
            adminService.addPayroll(payrollDTO,empId);
        });

        assertEquals(ResCodes.EMAIL_FAILED, exception.getResCodes());
        verify(payrollRepository).save(any(Payroll.class));
        verify(pdfService).generatePaySlipPdf(any());
    }

    @Test
    public void testGetSalaryGraphDataForPastSixMonths() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sixMonthsAgo = currentDate.minusMonths(6);
        String sixMonthAgo = sixMonthsAgo.format(formatter);
        Payroll payroll = new Payroll();
        payroll.setTotalNetPayable(10000.0);
        payroll.setPayPeriod("February 2024");
        List<Payroll> payrolls = List.of(payroll);

        when(payrollRepository.findByPayPeriodRange(sixMonthAgo)).thenReturn(payrolls);

        List<AvgSalaryGraphResponse> result = adminService.getSalaryGraphDataForPastSixMonths();

        assertNotNull(result);
        verify(payrollRepository).findByPayPeriodRange(sixMonthAgo);
        assertEquals(1, result.size());

    }


    @Test
    public void testUpdatePfDetails() {
        PfNumberUpdateRequest request = new PfNumberUpdateRequest();
        request.setEmployeeId("1");
        request.setUanNumber("1234567890");
        request.setPfNumber("123456");
        Employee employee = new Employee();

        when(employeeRepository.findById(request.getEmployeeId())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        String result = adminService.updatePfDetails(request);

        verify(employeeRepository).findById(request.getEmployeeId());
        verify(employeeRepository).save(any(Employee.class));
        assertEquals("Successfully Updated", result);
    }
    @Test
    public void testUpdatePfDetails_throwsEmployeeNotFoundException() {
        PfNumberUpdateRequest request = new PfNumberUpdateRequest();
        request.setEmployeeId("1");
        request.setUanNumber("1234567890");
        request.setPfNumber("123456");

        when(employeeRepository.findById(request.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.updatePfDetails(request));

        verify(employeeRepository).findById(request.getEmployeeId());
    }
    @Test
    public void testUpdatePfDetails_throwsUanIsNullException() {
        PfNumberUpdateRequest request = new PfNumberUpdateRequest();
        request.setEmployeeId("1");
        request.setUanNumber(null);
        request.setPfNumber("123456");
        Employee employee = new Employee();
        when(employeeRepository.findById(request.getEmployeeId())).thenReturn(Optional.of(employee));

        assertThrows(CompanyException.class,()->adminService.updatePfDetails(request));

    }
    @Test
    public void testUpdatePfDetails_throwsPfIsNullException() {
        PfNumberUpdateRequest request = new PfNumberUpdateRequest();
        request.setEmployeeId("1");
        request.setUanNumber("12345");
        request.setPfNumber(null);
        Employee employee = new Employee();
        when(employeeRepository.findById(request.getEmployeeId())).thenReturn(Optional.of(employee));

        assertThrows(CompanyException.class,()->adminService.updatePfDetails(request));
    }



    @Test
    public void testFetchEmployeeDesignation() {
        String empId = "1";
        Employee employee = new Employee();
        employee.setDesignation("Software Engineer");

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));

        String result = adminService.fetchEmployeeDesignation(empId);

        verify(employeeRepository).findById(empId);
        assertEquals("Software Engineer", result);
    }
    @Test
    public void testFetchEmployeeDesignation_throwException(){
        when(employeeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(CompanyException.class, () -> adminService.fetchEmployeeDesignation("1"));
        verify(employeeRepository).findById(any());
    }


    private HikeEntity getHikeEntity(){
        HikeEntity hikeEntity = new HikeEntity();
        hikeEntity.setId(1L);

        Employee employee = getEmployee();
        employee.setEmployeeID("EMP001");
        employee.setGrossSalary(50000D);
        employee.setDesignation("Software Engineer");
        hikeEntity.setEmployee(employee);

        hikeEntity.setPrevSalary(employee.getGrossSalary());
        hikeEntity.setHikePercentage(20D);
        hikeEntity.setReason("Performance appraisal");
        hikeEntity.setPrevPosition(employee.getDesignation());
        hikeEntity.setApprovedBy(employee);
        hikeEntity.setIsApproved(false);
        return hikeEntity;
    }

    @Test
    void testHikeRecommendations() {

        HikeEntity hikeEntity1 = new HikeEntity();
        HikeEntity hikeEntity2 = new HikeEntity();
        HikeEntityDTO hikeEntityDTO1 = new HikeEntityDTO();
        HikeEntityDTO hikeEntityDTO2 = new HikeEntityDTO();

        when(hikeRepository.findAllByStatusFalse()).thenReturn(Arrays.asList(hikeEntity1, hikeEntity2));
        when(mapper.convertToHikeEntityDto(hikeEntity1)).thenReturn(hikeEntityDTO1);
        when(mapper.convertToHikeEntityDto(hikeEntity2)).thenReturn(hikeEntityDTO2);

        List<HikeEntityDTO> result = adminService.hikeRecommendations();

        assertEquals(2, result.size());
        assertTrue(result.contains(hikeEntityDTO1));
        assertTrue(result.contains(hikeEntityDTO2));
    }

    @Test
    void testUpdateHikeDetails() throws IOException, JRException {
        String empID="EMP001";
        String resultStr="Mail sent Successfully";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("14-Feb-2024");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();


        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.of(approvedBy));
        when(hikeRepository.findByStatusAndEmployee(false, employee)).thenReturn(Optional.of(hike));
        when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
        when(pdfService.generateHikeLetter(any(), any(),anyString())).thenReturn(new byte[0]);

        String result = adminService.updateHikeDetails(request);

        assertEquals(resultStr, result);
        verify(hikeRepository).save(hike);
        verify(pdfService).generateHikeLetter(any(), any(),anyString());
    }

    @Test
    void testUpdateHikeDetails_newPositionNull() throws IOException, JRException {
        String empID="EMP001";
        String resultStr="Mail sent Successfully";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("14-Feb-2024");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setNewPosition(null);

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.of(approvedBy));
        when(hikeRepository.findByStatusAndEmployee(false, employee)).thenReturn(Optional.of(hike));
        when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
        when(pdfService.generateHikeLetter(any(), any(),anyString())).thenReturn(new byte[0]);

        String result = adminService.updateHikeDetails(request);

        assertEquals(resultStr, result);
        verify(hikeRepository).save(hike);
        verify(pdfService).generateHikeLetter(any(), any(),anyString());
    }
    @Test
    void testUpdateHikeDetails_newPositionIsNone() throws IOException, JRException {
        String empID="EMP001";
        String resultStr="Mail sent Successfully";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("14-Feb-2024");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setNewPosition("None");

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.of(approvedBy));
        when(hikeRepository.findByStatusAndEmployee(false, employee)).thenReturn(Optional.of(hike));
        when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
        when(pdfService.generateHikeLetter(any(), any(),anyString())).thenReturn(new byte[0]);

        String result = adminService.updateHikeDetails(request);

        assertEquals(resultStr, result);
        verify(hikeRepository).save(hike);
        verify(pdfService).generateHikeLetter(any(), any(),anyString());
    }
    @Test
    void testUpdateHikeDetails_ThrowEmpNotFound() throws IOException, JRException {
        String empID="EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> adminService.updateHikeDetails(request));
        verify(employeeRepository).findById(eq(empID));
    }
    @Test
    void testUpdateHikeDetails_approvedEmpNotFound() throws IOException, JRException {
        String empID="EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> adminService.updateHikeDetails(request));
        verify(employeeRepository).findById(eq(request.getApprovedBy()));
    }
    @Test
    void testUpdateHikeDetails_HikeNotFound() throws IOException, JRException {
        String empID="EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.of(approvedBy));
        when(hikeRepository.findByStatusAndEmployee(false, employee)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> adminService.updateHikeDetails(request));

        verify(hikeRepository).findByStatusAndEmployee(false, employee);
    }

    @Test
    void testUpdateHikeDetails_catchBlock() throws IOException, JRException {
        String empID="EMP001";
        String resultStr="Mail sent Successfully";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("12-Feb-2024");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setIsApproved(false);
        hike.setIsPromoted(false);

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.of(approvedBy));
        when(hikeRepository.findByStatusAndEmployee(anyBoolean(),any())).thenReturn(Optional.of(hike));
        when(mapper.convertToEmployeeDTO(any())).thenReturn(new EmployeeDTO());
        when(pdfService.generateHikeLetter(any(), any(), anyString())).thenThrow(new JRException("Problem in sending mail"));

        CompanyException exception = assertThrows(CompanyException.class, () -> {
            adminService.updateHikeDetails(request);
        });

        assertEquals(ResCodes.EMAIL_FAILED, exception.getResCodes());
        verify(pdfService).generateHikeLetter(any(), any(), anyString());
    }
    @Test
    void testUpdateHikeDetails_hikeAlreadyApproved() throws IOException, JRException {
        String empID="EMP001";
        String resultStr="Mail sent Successfully";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("12-Feb-2024");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setIsApproved(true);
        hike.setIsPromoted(true);

        when(employeeRepository.findById(eq(empID))).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(request.getApprovedBy()))).thenReturn(Optional.of(approvedBy));
        when(hikeRepository.findByStatusAndEmployee(anyBoolean(),any())).thenReturn(Optional.of(hike));

        CompanyException exception = assertThrows(CompanyException.class, () -> {
            adminService.updateHikeDetails(request);
        });

        assertEquals(ResCodes.HIKE_APPROVED_ALREADY, exception.getResCodes());
    }


    @Test
    void testGiveHike() throws IOException, JRException {
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setIsApproved(true);
        hike.setApprovedBy(approvedBy);

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById((anyString()))).thenReturn(Optional.of(approvedBy));
        when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
        when(hikeRepository.save(any())).thenReturn(hike);
        when(mapper.convertToEmployeeDTO(any())).thenReturn(new EmployeeDTO());
        when(pdfService.generateHikeLetter(any(), any(),anyString())).thenReturn(new byte[10]);
        when(mapper.convertToHikeEntityDto(any())).thenReturn(new HikeEntityDTO());

        HikeEntityDTO dto = adminService.giveHike(request);

        assertNotNull(dto);
        verify(pdfService).generateHikeLetter(any(), any(),anyString());
    }

    @Test
    void testGiveHike_employeeNotFound() throws IOException, JRException {
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class ,()->adminService.giveHike(request));
    }
    @Test
    void testGiveHike_approvedEmployeeNotFound() throws IOException, JRException {
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(approvedBy.getEmployeeID()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class ,()->adminService.giveHike(request));

    }
    @Test
    void testGiveHike_catchBlock() throws IOException, JRException {
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setIsApproved(true);
        hike.setApprovedBy(approvedBy);

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById((anyString()))).thenReturn(Optional.of(approvedBy));
        when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
        when(hikeRepository.save(any())).thenReturn(hike);
        when(mapper.convertToEmployeeDTO(any())).thenReturn(new EmployeeDTO());
        doThrow(new RuntimeException("Something went wrong")).when(pdfService).generateHikeLetter(any(), any(), anyString());
        when(mapper.convertToHikeEntityDto(any())).thenReturn(new HikeEntityDTO());

        HikeEntityDTO result= adminService.giveHike(request);

        assertNotNull(result);

        verify(pdfService, times(1)).generateHikeLetter(any(), any(),anyString());
    }

    @Test
    public void testSendHikeLetter_Success() throws JRException, IOException {
        HikeEntity hike = getHikeEntity();
        hike.setId(1L);

        Employee employee = getEmployee();
        employee.setEmployeeID("EMP01");
        employee.setEmail("test@example.com");
        hike.setEmployee(employee);

        when(hikeRepository.findById(1L)).thenReturn(Optional.of(hike));
        when(employeeRepository.findById("EMP01")).thenReturn(Optional.of(employee));

        String result = adminService.sendHikeLetter(1L);

        assertEquals("Email send Successfully", result);

        verify(pdfService, times(1)).generateHikeLetter(any(), any(),anyString());
    }

    @Test
    public void testSendHikeLetter_HikeNotFound() {
        when(hikeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->  adminService.sendHikeLetter(1L));
        verify(hikeRepository).findById(1L);
    }
    @Test
    public void testSendHikeLetter_EmployeeNotFound() {
        HikeEntity hike = getHikeEntity();
        hike.setId(1L);

        when(hikeRepository.findById(1L)).thenReturn(Optional.of(hike));

        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->  adminService.sendHikeLetter(1L));
        verify(employeeRepository).findById(anyString());
    }

    @Test
    public void testSendHikeLetter_CatchBlock() throws JRException, IOException {
        HikeEntity hike = getHikeEntity();
        hike.setId(1L);

        Employee employee = getEmployee();
        employee.setEmployeeID("EMP01");
        employee.setEmail("test@example.com");
        hike.setEmployee(employee);
        EmployeeDTO employeeDTO=getEmployeeDTO();

        when(hikeRepository.findById(1L)).thenReturn(Optional.of(hike));
        when(employeeRepository.findById("EMP01")).thenReturn(Optional.of(employee));
        when(pdfService.generateHikeLetter(any(),any(),anyString())).thenThrow(new RuntimeException("Something went wrong"));

        String result = adminService.sendHikeLetter(1L);
        assertEquals("Something went wrong", result);

        verify(pdfService, times(1)).generateHikeLetter(any(), any(),anyString());
    }

    @Test
    void editHikeLetter() throws JRException, IOException {
        HikeEntityDTO hikeEntityDTO=new HikeEntityDTO();
        hikeEntityDTO.setId(1L);
        hikeEntityDTO.setHikePercentage("25");
        hikeEntityDTO.setNewPosition("New");
        hikeEntityDTO.setReason("Performance Excellence");
        hikeEntityDTO.setEffectiveDate("2023-05-01");
        hikeEntityDTO.setPrevPosition("Old");
        hikeEntityDTO.setPrevSalary("100000");
        hikeEntityDTO.setNewSalary("140000");

        HikeEntity hike = getHikeEntity();

        Employee employee = getEmployee();
        Employee approvedBy=getEmployee();
        approvedBy.setEmployeeID("EMP02");
        employee.setEmployeeID("EMP01");

        hike.setEmployee(employee);
        hike.setApprovedBy(approvedBy);

        when(hikeRepository.findById(any())).thenReturn(Optional.of(hike));
        when(employeeRepository.findById(any())).thenReturn(Optional.of(employee));
        when(dateTimeConverter.stringToLocalDateTimeConverter(any())).thenReturn(new Date());
        when(Formatters.convertStringToDoubleAmount(anyString())).thenReturn(20D);
        when(hikeRepository.save(any())).thenReturn((hike));
        when(mapper.convertToHikeEntityDto(any())).thenReturn(new HikeEntityDTO());

        HikeEntityDTO result = adminService.editHikeLetter(hikeEntityDTO);

        assertNotNull(result);
        verify(hikeRepository).save(any());
    }

    @Test
    public void testEditHikeLetter_HikeNotFound() {
        HikeEntityDTO hikeEntityDTO=new HikeEntityDTO();
        hikeEntityDTO.setId(1L);
        hikeEntityDTO.setHikePercentage("25");
        hikeEntityDTO.setNewPosition("New");
        hikeEntityDTO.setReason("Performance Excellence");
        hikeEntityDTO.setEffectiveDate("2023-05-01");
        hikeEntityDTO.setPrevPosition("Old");
        hikeEntityDTO.setPrevSalary("100000");
        hikeEntityDTO.setNewSalary("140000");

        HikeEntity hike = getHikeEntity();

        Employee employee = getEmployee();
        Employee approvedBy=getEmployee();
        approvedBy.setEmployeeID("EMP02");
        employee.setEmployeeID("EMP01");

        hike.setEmployee(employee);
        hike.setApprovedBy(approvedBy);

        when(hikeRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.editHikeLetter(hikeEntityDTO));
    }

    @Test
    public void testEditHikeLetter_employeeNotFound() {
        HikeEntityDTO hikeEntityDTO=new HikeEntityDTO();
        hikeEntityDTO.setId(1L);
        hikeEntityDTO.setHikePercentage("25");
        hikeEntityDTO.setNewPosition("New");
        hikeEntityDTO.setReason("Performance Excellence");
        hikeEntityDTO.setEffectiveDate("2023-05-01");
        hikeEntityDTO.setPrevPosition("Old");
        hikeEntityDTO.setPrevSalary("100000");
        hikeEntityDTO.setNewSalary("140000");

        HikeEntity hike = getHikeEntity();

        Employee employee = getEmployee();
        Employee approvedBy=getEmployee();
        approvedBy.setEmployeeID("EMP02");
        employee.setEmployeeID("EMP01");

        hike.setEmployee(employee);
        hike.setApprovedBy(approvedBy);

        when(hikeRepository.findById(any())).thenReturn(Optional.of(hike));
        when(employeeRepository.findById(eq(hikeEntityDTO.getEmployeeId()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.editHikeLetter(hikeEntityDTO));
    }
    @Test
    public void testEditHikeLetter_approvedEmployeeNotFound() {

        HikeEntity hike = getHikeEntity();
        Employee employee = getEmployee();
        Employee approvedBy=getEmployee();
        approvedBy.setEmployeeID("EMP02");
        employee.setEmployeeID("EMP01");
        hike.setEmployee(employee);
        hike.setApprovedBy(approvedBy);
        HikeEntityDTO hikeEntityDTO=new HikeEntityDTO();
        hikeEntityDTO.setId(1L);
        hikeEntityDTO.setHikePercentage("25");
        hikeEntityDTO.setNewPosition("New");
        hikeEntityDTO.setReason("Performance Excellence");
        hikeEntityDTO.setEffectiveDate("2023-05-01");
        hikeEntityDTO.setPrevPosition("Old");
        hikeEntityDTO.setPrevSalary("100000");
        hikeEntityDTO.setNewSalary("140000");
        hikeEntityDTO.setEmployeeId(employee.getEmployeeID());
        hikeEntityDTO.setApprovedBy(approvedBy.getEmployeeID());

        when(hikeRepository.findById(any())).thenReturn(Optional.of(hike));
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(eq(approvedBy.getEmployeeID()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.editHikeLetter(hikeEntityDTO));
    }
    @Test
    void testPreviewPayslip() throws JRException {
        byte[] paySlipPDF = new byte[10];
        AddMonthlyPayRollRequest request=new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP01");
        request.setPayDate("2015-01-01");
        request.setPayPeriod("February 2023");
        request.setLopDays("2");
        Employee employee=getEmployee();
        employee.setEmployeeID(request.getEmployeeId());

        PayrollDTO payrollDTO=new PayrollDTO();
        EmployeeDTO employeeDTO=getEmployeeDTO();
        PaySlip paySlip=new PaySlip();
        paySlip.setEmployeeDTO(employeeDTO);
        paySlip.setPayrollDTO(payrollDTO);

        when(employeeRepository.findById(eq(employee.getEmployeeID()))).thenReturn(Optional.of(employee));
        when(mapper.mapCtcDataToPayroll(any(),any())).thenReturn(new Payroll());
        when(mapper.convertToEmployeeDTO(any())).thenReturn(employeeDTO);
        when(mapper.convertToPayRollDTO(any())).thenReturn(payrollDTO);
        when(pdfService.generatePaySlipPdf(any())).thenReturn(paySlipPDF);

        byte[] result=adminService.previewPayslipPdf(request);

        assertNotNull(result);
        verify(pdfService).generatePaySlipPdf(paySlip);
    }

    @Test
    void testPreviewPayslip_throwsEmployeeNotFound() throws JRException {
        byte[] paySlipPDF = new byte[10];
        AddMonthlyPayRollRequest request=new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP01");
        request.setPayDate("2015-01-01");
        request.setPayPeriod("February 2023");
        request.setLopDays("2");
        Employee employee=getEmployee();
        employee.setEmployeeID(request.getEmployeeId());

        when(employeeRepository.findById(eq(employee.getEmployeeID()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.previewPayslipPdf(request));
        verify(employeeRepository).findById(eq(employee.getEmployeeID()));
    }

    @Test
    void testAddMonthlyPayRoll() throws MessagingException, IOException, JRException {
        byte[] paySlipPDF = new byte[10];
        String resultStr ="Successfully send mail to ";
        AddMonthlyPayRollRequest request=new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP01");
        request.setPayDate("2015-01-01");
        request.setPayPeriod("February 2023");
        request.setLopDays("2");
        Employee employee=getEmployee();
        employee.setEmployeeID(request.getEmployeeId());

        PayrollDTO payrollDTO=new PayrollDTO();
        EmployeeDTO employeeDTO=getEmployeeDTO();
        PaySlip paySlip=new PaySlip();
        paySlip.setEmployeeDTO(employeeDTO);
        paySlip.setPayrollDTO(payrollDTO);
        CtcData ctcData=new CtcData();
        Payroll payroll=new Payroll();
        payroll.setEmployee(employee);
        payroll.setGrossEarnings(employee.getGrossSalary());
        when(employeeRepository.findById(eq(employee.getEmployeeID()))).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(eq(request.getPayPeriod()), eq(employee))).thenReturn(Optional.empty());
        when(calculator.compensationDetails(anyDouble())).thenReturn(ctcData);
        when(mapper.mapCtcDataToPayroll(any(),any())).thenReturn(new Payroll());
        when(payrollRepository.save(any(Payroll.class))).thenReturn(new Payroll());
        when(mapper.convertToEmployeeDTO(any())).thenReturn(employeeDTO);
        when(mapper.convertToPayRollDTO(any())).thenReturn(payrollDTO);
        when(pdfService.generatePaySlipPdf(any())).thenReturn(paySlipPDF);
        doNothing().when(emailSenderService).sendEmailWithAttachment(anyString(), anyString(), anyString(), eq(paySlipPDF));

        String result=adminService.addMonthlyPayRoll(request);

        assertEquals(resultStr+employee.getEmail(),result);
        verify(payrollRepository).save(any(Payroll.class));
        verify(emailSenderService).sendEmailWithAttachment(anyString(), anyString(), anyString(), eq(paySlipPDF));

    }
    @Test
    void testAddMonthlyPayRoll_throwsEmployeeNotFound() throws MessagingException, IOException, JRException {
        byte[] paySlipPDF = new byte[10];
        String resultStr ="Successfully send mail to ";
        AddMonthlyPayRollRequest request=new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP01");
        request.setPayDate("2015-01-01");
        request.setPayPeriod("February 2023");
        request.setLopDays("2");
        Employee employee=getEmployee();
        employee.setEmployeeID(request.getEmployeeId());

        when(employeeRepository.findById(eq(employee.getEmployeeID()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.addMonthlyPayRoll(request));
    }
    @Test
    void testAddMonthlyPayRoll_throwsDuplicatePayrollFound() throws MessagingException, IOException, JRException {
        byte[] paySlipPDF = new byte[10];
        String resultStr ="Successfully send mail to ";
        AddMonthlyPayRollRequest request=new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP01");
        request.setPayDate("2015-01-01");
        request.setPayPeriod("February 2023");
        request.setLopDays("2");
        Employee employee=getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Payroll payroll=new Payroll();
        payroll.setEmployee(employee);
        payroll.setGrossEarnings(employee.getGrossSalary());

        when(employeeRepository.findById(eq(employee.getEmployeeID()))).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(anyString(), any())).thenReturn(Optional.of(new Payroll()));

        assertThrows(CompanyException.class,()->adminService.addMonthlyPayRoll(request));
    }

    @Test
    void testAddMonthlyPayRoll_catchBlock() throws MessagingException, IOException, JRException {
        byte[] paySlipPDF = new byte[10];
        String resultStr ="Successfully send mail to ";
        AddMonthlyPayRollRequest request=new AddMonthlyPayRollRequest();
        request.setEmployeeId("EMP01");
        request.setPayDate("2015-01-01");
        request.setPayPeriod("February 2023");
        request.setLopDays("2");
        Employee employee=getEmployee();
        employee.setEmployeeID(request.getEmployeeId());

        PayrollDTO payrollDTO=new PayrollDTO();
        EmployeeDTO employeeDTO=getEmployeeDTO();
        PaySlip paySlip=new PaySlip();
        paySlip.setEmployeeDTO(employeeDTO);
        paySlip.setPayrollDTO(payrollDTO);
        CtcData ctcData=new CtcData();
        Payroll payroll=new Payroll();
        payroll.setEmployee(employee);
        payroll.setGrossEarnings(employee.getGrossSalary());
        when(employeeRepository.findById(eq(employee.getEmployeeID()))).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(eq(request.getPayPeriod()), eq(employee))).thenReturn(Optional.empty());
        when(calculator.compensationDetails(anyDouble())).thenReturn(ctcData);
        when(mapper.mapCtcDataToPayroll(any(),any())).thenReturn(new Payroll());
        when(payrollRepository.save(any(Payroll.class))).thenReturn(new Payroll());
        when(mapper.convertToEmployeeDTO(any())).thenReturn(employeeDTO);
        when(mapper.convertToPayRollDTO(any())).thenReturn(payrollDTO);
        when(pdfService.generatePaySlipPdf(any())).thenThrow(new JRException("Problem in sending mail"));

        CompanyException exception = assertThrows(CompanyException.class, () -> {
            adminService.addMonthlyPayRoll(request);
        });

        assertEquals(ResCodes.EMAIL_FAILED, exception.getResCodes());
        verify(payrollRepository).save(any(Payroll.class));
        verify(pdfService).generatePaySlipPdf(any());
    }

    @Test
    void testPreviewHikeDetails() throws JRException, IOException {
        byte[] output=new byte[10];
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");
        Employee employee = getEmployee();
        employee.setEmployeeID(request.getEmployeeId());
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());
        HikeEntity hike = getHikeEntity();
        hike.setIsApproved(true);
        hike.setApprovedBy(approvedBy);

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById((anyString()))).thenReturn(Optional.of(approvedBy));
        when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
        when(pdfService.generateHikeLetter(any(), any(),anyString())).thenReturn(new byte[10]);

         byte[] result = adminService.previewHikeDetails(request);

        assertNotNull(result);
        verify(pdfService).generateHikeLetter(any(), any(),anyString());

    }
    @Test
    void testPreviewHikeDetails_employeeNotFound() throws JRException, IOException {
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.previewHikeDetails(request));
    }
    @Test
    void testPreviewHikeDetails_ApprovedEmployeeNotFound() throws Exception {
        String empID = "EMP001";
        HikeUpdateRequest request = new HikeUpdateRequest();
        request.setEmployeeId(empID);
        request.setNewPosition("New");
        request.setReason("Performance Excellence");
        request.setPercentage("10");
        request.setEffectiveDate("2023-05-01");
        request.setApprovedBy("EMP002");
        request.setIssuedDate("2023-05-01");
        request.setApprovedDate("2023-05-01");
        Employee approvedBy = getEmployee();
        approvedBy.setEmployeeID(request.getApprovedBy());

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(new Employee()));
        when(employeeRepository.findById(eq(approvedBy.getEmployeeID()))).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,()->adminService.previewHikeDetails(request));
    }

    @Test
    void testPreviewHikeDetails_catchBlock() throws JRException, IOException {
        {
            String empID = "EMP001";
            HikeUpdateRequest request = new HikeUpdateRequest();
            request.setEmployeeId(empID);
            request.setNewPosition("New");
            request.setReason("Performance Excellence");
            request.setPercentage("10");
            request.setEffectiveDate("2023-05-01");
            request.setApprovedBy("EMP002");
            request.setIssuedDate("2023-05-01");
            request.setApprovedDate("2023-05-01");
            Employee employee = getEmployee();
            employee.setEmployeeID(request.getEmployeeId());
            Employee approvedBy = getEmployee();
            approvedBy.setEmployeeID(request.getApprovedBy());
            HikeEntity hike = getHikeEntity();
            hike.setIsApproved(true);
            hike.setApprovedBy(approvedBy);

            when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
            when(employeeRepository.findById((anyString()))).thenReturn(Optional.of(approvedBy));
            when(dateTimeConverter.stringToLocalDateTimeConverter(anyString())).thenReturn(new Date());
            when(pdfService.generateHikeLetter(any(), any(), anyString())).thenThrow(new JRException("Error in generating Hike letter"));

            CompanyException exception = assertThrows(CompanyException.class, () -> {
                adminService.previewHikeDetails(request);
            });

            assertEquals(ResCodes.SOMETHING_WENT_WRONG, exception.getResCodes());
            verify(pdfService).generateHikeLetter(any(), any(), anyString());

        }
    }

}